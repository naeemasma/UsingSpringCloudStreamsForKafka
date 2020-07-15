package com.example.streams.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import com.example.streams.domain.TopMediaPosts;

public class TopMediaPostsSerde implements Serde<TopMediaPosts> {
	
	@Override
	public Serializer<TopMediaPosts> serializer() {
		return new TopMediaPostsSerializer();
	}		
		
	@Override
	public Deserializer<TopMediaPosts> deserializer() {

		return new TopMediaPostsDeserializer() ;
	}
}
