package com.example.streams.serdes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.example.streams.domain.TopMediaPosts;
import com.example.streams.domain.MediaPostEngagementActivityCount;

public class TopMediaPostsSerializer implements Serializer<TopMediaPosts>{
			
			public void configure(final Map<String, ?> map, final boolean b) {
			}

			public byte[] serialize(final String s, final TopMediaPosts topMediaPosts) {

				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final DataOutputStream
						dataOutputStream =
						new DataOutputStream(out);
				try {
					for (MediaPostEngagementActivityCount mediaPostEngagementActivityCount : topMediaPosts) {
						dataOutputStream.writeLong(mediaPostEngagementActivityCount.getPostId());
						dataOutputStream.writeLong(mediaPostEngagementActivityCount.getEngagementActivityCount());
					}
					dataOutputStream.flush();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				return out.toByteArray();
			}
}
