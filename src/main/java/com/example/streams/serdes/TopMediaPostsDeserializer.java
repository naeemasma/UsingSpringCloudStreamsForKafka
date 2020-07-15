package com.example.streams.serdes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import com.example.streams.domain.MediaPostEngagementActivityCount;
import com.example.streams.domain.TopMediaPosts;

public class TopMediaPostsDeserializer implements Deserializer<TopMediaPosts> {
	@Override
	public void close() {
    }
    
    @Override 
    public void configure(Map<String, ?> arg0, boolean arg1) {
    }
    
    @Override 
    public TopMediaPosts deserialize(String arg0, byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		final TopMediaPosts result = new TopMediaPosts();

		final DataInputStream
				dataInputStream =
				new DataInputStream(new ByteArrayInputStream(bytes));

		try {
			while(dataInputStream.available() > 0) {
				result.add(new MediaPostEngagementActivityCount(dataInputStream.readLong(),
						dataInputStream.readLong()));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	};
}