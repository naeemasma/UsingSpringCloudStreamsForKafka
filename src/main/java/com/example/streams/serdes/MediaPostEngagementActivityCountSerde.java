package com.example.streams.serdes;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MediaPostEngagementActivityCountSerde<MediaPostEngagementActivityCount> implements Serde<MediaPostEngagementActivityCount> {

	@Override
	public Serializer<MediaPostEngagementActivityCount> serializer() {

		return new Serializer<MediaPostEngagementActivityCount>() {
			  @Override 
			  public void configure(Map<String, ?> map, boolean b) {
			  }
			  
			  @Override
			  public byte[] serialize(String arg0, MediaPostEngagementActivityCount arg1) {
			    byte[] retVal = null;
			    ObjectMapper objectMapper = new ObjectMapper();
			    try {
			      retVal = objectMapper.writeValueAsString(arg1).getBytes();
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			    return retVal;
			  }
			  @Override public void close() {
			  }
		};
	}
		
		
	@Override
	public Deserializer<MediaPostEngagementActivityCount> deserializer() {

		return (Deserializer<MediaPostEngagementActivityCount>) new MediaPostEngagementActivityCountDeserializer() ;
	}

}
