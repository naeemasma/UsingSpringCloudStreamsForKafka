package com.example.streams.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.example.streams.KafkaStreamsInteractiveQueryDemo;
import com.example.streams.domain.MediaConsumer;
import com.example.streams.domain.MediaPost;
import com.example.streams.domain.MediaPostEngagementActivityCount;
import com.example.streams.domain.TopMediaPosts;

@RestController
public class Controller {
	private final Log logger = LogFactory.getLog(getClass());	
	@Autowired
	private InteractiveQueryService interactiveQueryService;
			
	@RequestMapping("/consumer")
	public MediaConsumer consumer(@RequestParam(value="id") Long id) {
		HostInfo hostInfo = interactiveQueryService.getHostInfo(KafkaStreamsInteractiveQueryDemo.ALL_CONSUMERS_STORE,
				KafkaStreamsInteractiveQueryDemo.ALL_CONSUMERS_STORE, new StringSerializer());

		if (interactiveQueryService.getCurrentHostInfo().equals(hostInfo)) {
			logger.info("Request served from same host: " + hostInfo);
			
			final ReadOnlyKeyValueStore<Long, MediaConsumer> customerStore =
					interactiveQueryService.getQueryableStore(
							KafkaStreamsInteractiveQueryDemo.ALL_CONSUMERS_STORE, 
							QueryableStoreTypes.<Long, MediaConsumer>keyValueStore());

			final MediaConsumer customer = customerStore.get(id);
			if (customer == null) {
				throw new IllegalArgumentException("Consumer not found");
			}
			return customer;
		}
		else {
			//find the store from the proper instance.
			logger.info("Consumer request served from different host: " + hostInfo);
			System.out.println(String.format("http://%s:%d/%s", hostInfo.host(),
					hostInfo.port(), "/consumer?id="+id));
			RestTemplate restTemplate = new RestTemplate();
			return restTemplate.postForObject(
					String.format("http://%s:%d/%s", hostInfo.host(),
							hostInfo.port(), "/consumer?id="+id), "id", MediaConsumer.class);
		}		
	}
	
	@RequestMapping("/post")
	public MediaPost post(@RequestParam(value="id") Long id) {
		final ReadOnlyKeyValueStore<Long, MediaPost> mediaPostStore =
				interactiveQueryService.getQueryableStore(KafkaStreamsInteractiveQueryDemo.ALL_POSTS_STORE, QueryableStoreTypes.<Long, MediaPost>keyValueStore());

		final MediaPost mediaPost = mediaPostStore.get(id);
		if (mediaPost == null) {
			throw new IllegalArgumentException("IllegalArgumentException");
		}
		return new MediaPost(mediaPost.getName());
	}
	
	@RequestMapping("/trends/top-posts")
	@SuppressWarnings("unchecked")
	public List<MediaPostEngagementActivityCount> topPosts(@RequestParam(value="category") String category) {

		HostInfo hostInfo = interactiveQueryService.getHostInfo(KafkaStreamsInteractiveQueryDemo.TOP_POSTS_STORE,
				KafkaStreamsInteractiveQueryDemo.TOP_POSTS_KEY, new StringSerializer());

		if (interactiveQueryService.getCurrentHostInfo().equals(hostInfo)) {
			logger.info("Top Posts request served from same host: " + hostInfo);
			return topMediaPosts(KafkaStreamsInteractiveQueryDemo.TOP_POSTS_KEY, KafkaStreamsInteractiveQueryDemo.TOP_POSTS_STORE);
		}
		else {
			//find the store from the proper instance.
			logger.info("Top Posts request served from different host: " + hostInfo);
			RestTemplate restTemplate = new RestTemplate();
			return restTemplate.postForObject(
					String.format("http://%s:%d/%s", hostInfo.host(),
							hostInfo.port(), "trends/top-posts?category=all"), "all", List.class);
		}
    }

	private List<MediaPostEngagementActivityCount> topMediaPosts(final String key,
												 final String storeName) {
		final ReadOnlyKeyValueStore<String, TopMediaPosts> topPostsStore =
				interactiveQueryService.getQueryableStore(storeName, QueryableStoreTypes.<String, TopMediaPosts>keyValueStore());

		// Get the value from the store
		final TopMediaPosts value = topPostsStore.get(key);
		if (value == null) {
			throw new IllegalArgumentException(String.format("Unable to find value in %s for key %s", storeName, key + " - size: " + topPostsStore.approximateNumEntries()));
		}
		final List<MediaPostEngagementActivityCount> results = new ArrayList<>();
		TreeSet<MediaPostEngagementActivityCount> mediaPostEngagementActivities =
				value.mediaPostEngagementActivities();	
		for(int i=0;i<mediaPostEngagementActivities.size();i++)
		{
			MediaPostEngagementActivityCount postEngagementCount = mediaPostEngagementActivities.iterator().next();
			HostInfo hostInfo = interactiveQueryService.getHostInfo(KafkaStreamsInteractiveQueryDemo.ALL_POSTS_STORE,
					postEngagementCount.getPostId(), new LongSerializer());

			if (interactiveQueryService.getCurrentHostInfo().equals(hostInfo)) {
				logger.info("Post info request served from same host: " + hostInfo);

				final ReadOnlyKeyValueStore<Long, MediaPost> postsStore =
						interactiveQueryService.getQueryableStore(KafkaStreamsInteractiveQueryDemo.ALL_POSTS_STORE, QueryableStoreTypes.<Long, MediaPost>keyValueStore());

				MediaPost mediaPost = postsStore.get(postEngagementCount.getPostId());
				results.add(new MediaPostEngagementActivityCount(mediaPost.getPostId(), postEngagementCount.getEngagementActivityCount()));
			}
			else {
				logger.info("Post info request served from different host: " + hostInfo);
				RestTemplate restTemplate = new RestTemplate();
				MediaPost mediaPost = restTemplate.postForObject(
						String.format("http://%s:%d/%s", hostInfo.host(),
								hostInfo.port(), "post?id=" + postEngagementCount.getPostId()),  "id", MediaPost.class);
				results.add(new MediaPostEngagementActivityCount(mediaPost.getPostId(), postEngagementCount.getEngagementActivityCount()));
			}
		}
		return results;								
	}
}