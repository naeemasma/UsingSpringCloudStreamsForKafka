package com.example.streams;

import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import com.example.streams.domain.MediaConsumer;
import com.example.streams.domain.MediaConsumerEngagementActivity;
import com.example.streams.domain.EnrichedEngagementActivity;
import com.example.streams.domain.EngagementActivity;
import com.example.streams.domain.MediaPost;
import com.example.streams.domain.MediaPostEngagementActivityCount;
import com.example.streams.domain.TopMediaPosts;
import com.example.streams.serdes.MediaPostEngagementActivityCountSerde;
import com.example.streams.serdes.MediaPostSerde;
import com.example.streams.serdes.TopMediaPostsSerde;
import com.example.streams.util.Utilities;
@SpringBootApplication
public class KafkaStreamsInteractiveQueryDemo {
	public static final Long MIN_COUNTABLE_ENGAGEMENT_DURATION = 30 * 1000L;
	public static final String ALL_CONSUMERS_STORE = "all-consumers-store";
	public static final String ENGANGEMENT_ACTIVITY_STORE = "engagement-activity-store";
	public static final String TOP_POSTS_STORE = "top-posts-store";
	public static final String TOP_POSTS_BY_CATEGORY_STORE = "top-posts-by-category-store";
	public static final String TOP_POSTS_KEY = "all";
	public static final Integer TOP_LIST_SIZE = 1;
	public static final String ALL_POSTS_STORE = "all-posts-store";
	public static final String ENGAGEMENT_ACTIVITY_EVENTS = "engagement-activity-topic";
	@Autowired
	private InteractiveQueryService queryService;
	
	public static final Log logger = LogFactory.getLog(KafkaStreamsInteractiveQueryDemo.class);

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsInteractiveQueryDemo.class, args);
	}
	
	@EnableScheduling	
	public static class InteractivePostCountApplication {
		
	    @Bean
		public Function<KStream<Long, EngagementActivity>,
		        Function<GlobalKTable<Long, MediaConsumer>,
		                Function<GlobalKTable<Long, MediaPost>, KStream<Long, EnrichedEngagementActivity>
		                >>> process() {

		    return engagementActivities -> (
		              consumers -> (
		                    posts -> {		                    	
		                    	// Accept engagement activities that have a duration >= the minimum
		        				final KStream<Long, EngagementActivity> acceptableEngagementActivities =
		        						engagementActivities.filter((key, activity) -> activity.getEngagementDuration() >= 
		        						MIN_COUNTABLE_ENGAGEMENT_DURATION)
		        								// repartition based on song id
		        								.map((key, value) -> KeyValue.pair(value.getId(), value));
		        				
		        				// join the engagement activities with media post as we will use it later for finding trends
		        				final KStream<Long, EnrichedEngagementActivity> mediaPostsEngagementActivities =
		        						acceptableEngagementActivities.join(posts, 
		        			                            (key, engagementActivity) -> engagementActivity.getPostId(),
		        			                                (engagementActivity, post) -> {		        			                                	
		        			                                	EnrichedEngagementActivity mediaPostEnagementActivity = 
		        			                                			new EnrichedEngagementActivity(engagementActivity, post, null);
		        			                                	return mediaPostEnagementActivity;
		        			                                });  				
		                    	
		                    	// create a state store to track engagement activity counts
		                    	MediaPostSerde mediaPostSerde = new MediaPostSerde();
		                    	final KTable<MediaPost, Long> postEngagementCounts = mediaPostsEngagementActivities.groupBy(
		        				(key, value) ->  value.getPost(),
		        				        Grouped.with(mediaPostSerde, new JsonSerde<>(EnrichedEngagementActivity.class)))
		        				.count(
		        						Materialized.<MediaPost, Long, KeyValueStore<Bytes, byte[]>>as(ENGANGEMENT_ACTIVITY_STORE)
			        					.withKeySerde(mediaPostSerde)
			        					.withValueSerde(Serdes.Long())
		        						);
		        						                    	
		        				// Compute the top trending post. The results of this computation will continuously update the state
		        				// store "top-posts", and this state store can then be queried interactively via a REST API 
		        				// for the latest trends.
		        				
		        				TopMediaPostsSerde topMediaPostsSerde = new TopMediaPostsSerde();
		        				MediaPostEngagementActivityCountSerde postEngagementCountSerde = new MediaPostEngagementActivityCountSerde();
		        				
		        				postEngagementCounts.groupBy((post, engagements) ->
		        								KeyValue.pair(TOP_POSTS_KEY,
		        										new MediaPostEngagementActivityCount(post.getPostId(), engagements))
		        						,Serialized.with(Serdes.String(), postEngagementCountSerde))
		        						.aggregate(TopMediaPosts::new,
		        								(aggKey, newValue, aggValue) -> {		        									
		        									((TopMediaPosts)aggValue).add((MediaPostEngagementActivityCount)newValue);
		        									return aggValue;
		        								},
		        								(aggKey, oldValue, aggValue) -> {
		        									((TopMediaPosts)aggValue).remove((MediaPostEngagementActivityCount)oldValue);
		        									return aggValue;
		        							},
		        								Materialized.<String, TopMediaPosts, KeyValueStore<Bytes, byte[]>>as(TOP_POSTS_STORE)
		        										.withKeySerde(Serdes.String())
		        										.withValueSerde(topMediaPostsSerde)
		        						);
		        				
		        				KStream<Long, EnrichedEngagementActivity> enrichedEngagementActivities =
				                    	engagementActivities.join(consumers,
					                            (engagementActivityId, engagementActivity) -> engagementActivity.getConsumerId(),
					                                (engagementActivity, consumer) -> new MediaConsumerEngagementActivity(consumer, engagementActivity))
					                                .leftJoin(posts,
					                                        (engagementActivityId, consumerEngagementActivity) -> consumerEngagementActivity.engagementActivity.postId,
					                                        (consumerEngagementActivity, post) -> {
					                                            EnrichedEngagementActivity enrichedEngagementActivity = new EnrichedEngagementActivity();
					                                            enrichedEngagementActivity.setPost(post);
					                                            enrichedEngagementActivity.setConsumer(consumerEngagementActivity.consumer);
					                                            enrichedEngagementActivity.setEngagementActivity(consumerEngagementActivity.engagementActivity);
					                                            return enrichedEngagementActivity;
					                                        });
		        				
		                    	return enrichedEngagementActivities;
		                    }));}								
	}
	
	@Scheduled(fixedRate = 30000, initialDelay = 5000)
	public void printEngagementActivities() {	
		final ReadOnlyKeyValueStore<MediaPost, Long> activityStore =
				queryService.getQueryableStore(KafkaStreamsInteractiveQueryDemo.ENGANGEMENT_ACTIVITY_STORE, 
						QueryableStoreTypes.<MediaPost, Long>keyValueStore());
		KeyValueIterator<MediaPost, Long> activityIterator = activityStore.all();
		
		while (activityIterator.hasNext()) {
			KeyValue<MediaPost, Long> activityKeyValue = activityIterator.next();
			MediaPost post = activityKeyValue.key;
			Long engagementCount = (Long)activityKeyValue.value;
			logger.info("Post ID: "  + post.toString()
					+ ", Engagement Count: " + engagementCount
					+ ", Current Date Time: " + Utilities.getCurrentTimeString()
					);
		}
	}
}
