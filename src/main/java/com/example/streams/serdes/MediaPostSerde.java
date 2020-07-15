package com.example.streams.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import com.example.streams.domain.MediaPost;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MediaPostSerde<MediaPost> implements Serde<MediaPost> {

	@Override
	public Serializer<MediaPost> serializer() {

		return new Serializer<MediaPost>() {
			  @Override 
			  public void configure(Map<String, ?> map, boolean b) {
			  }
			  
			  @Override
			  public byte[] serialize(String arg0, MediaPost arg1) {
			    byte[] retVal = null;
			    ObjectMapper objectMapper = new ObjectMapper();
			    try {
			      retVal = objectMapper.writeValueAsString(arg1).getBytes();
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			    return retVal;
			  }
			  @Override 
			  public void close() {
			  }
		};
	}
		
		
	@Override
	public Deserializer<MediaPost> deserializer() {

		return (Deserializer<MediaPost>) new MediaPostDeserializer() ;
	}

}
