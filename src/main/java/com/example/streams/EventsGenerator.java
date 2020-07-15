/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.streams;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.example.streams.domain.EngagementActivity;
import com.example.streams.domain.MediaConsumer;
import com.example.streams.domain.MediaPost;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EventsGenerator {
	
	public static final Log logger = LogFactory.getLog(EventsGenerator.class);

	public static void main(String... args) throws Exception {
		
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		DefaultKafkaProducerFactory<Long, MediaPost> pf = new DefaultKafkaProducerFactory<>(props);
		KafkaTemplate<Long, MediaPost> template = new KafkaTemplate<>(pf, true);
		template.setDefaultTopic("post-topic");

		DefaultKafkaProducerFactory<Long, MediaConsumer> pf1 = new DefaultKafkaProducerFactory<>(props);
		KafkaTemplate<Long, MediaConsumer> template1 = new KafkaTemplate<>(pf1, true);
		template1.setDefaultTopic("consumer-topic");

		DefaultKafkaProducerFactory<Long, EngagementActivity> pf2 = new DefaultKafkaProducerFactory<>(props);
		KafkaTemplate<Long, EngagementActivity> template2 = new KafkaTemplate<>(pf2, true);
		template2.setDefaultTopic("engagement-activity-topic");


		final List<MediaPost> posts = Arrays.asList(new MediaPost(1L,"Recipe of the Day", "Food and Living"),
				new MediaPost(2L, "The Funniest Pokeman Game", "Gaming"),
				new MediaPost(3L, "Fortnite", "Gaming"),new MediaPost(4L, "Agile Humor", "Technology Trends"),
				new MediaPost(5L, "Agile Vs Non-Agile", "Technology Trends"),
				new MediaPost(6L, "Ziggy Chen Spring Summer", "Fashion and Beauty"), 
				new MediaPost(7L, "Dolce and Gabbana alta Moda", "Fashion and Beauty")
				);			
				
		final List<MediaConsumer> consumers = Arrays.asList(new MediaConsumer(1L,"Anonymous"),
				new MediaConsumer(2L, "Bob"), new MediaConsumer(3L, "Jean"), new MediaConsumer(4L, "Doe"),
				new MediaConsumer(5L, "Billy"), new MediaConsumer(6L, "Kate"), new MediaConsumer(7L, "Jill")
				);
		
		posts.forEach(post -> {
			logger.info("Writing post information for '" + post.getName() + "' to input topic " +
					"post-topic");
			template.sendDefault(post.getPostId(), post);
		});
		
		consumers.forEach(consumer -> {
			logger.info("Writing consumer information for '" + consumer.getDescription() + "' to input topic " +
					"consumer-topic");
			template1.sendDefault(consumer.getConsumerId(), consumer);
		});
		
		final Random random = new Random();
		long engagementActivityId =0;

		// send an engagement activity event every 100 milliseconds
		while (true) {
			final MediaPost post = posts.get(random.nextInt(posts.size()));
			final MediaConsumer consumer = consumers.get(random.nextInt(consumers.size()));
			logger.info("Writing engagement for post " + post.getName() + " to input topic " +
					KafkaStreamsInteractiveQueryDemo.ENGAGEMENT_ACTIVITY_EVENTS);
			template2.sendDefault(engagementActivityId++, 
					new EngagementActivity(engagementActivityId, post.getPostId(), random.nextInt(9)*10000L, 
							consumer.getConsumerId()));

			Thread.sleep(100L);
		}
		
	}

}
