package com.example.streams.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import com.example.streams.domain.MediaPostEngagementActivityCount;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MediaPostEngagementActivityCountDeserializer implements Deserializer<MediaPostEngagementActivityCount> {
	    @Override
		public void close() {
	    }
	    
	    @Override 
	    public void configure(Map<String, ?> arg0, boolean arg1) {
	    }
	    
	    @Override 
	    public MediaPostEngagementActivityCount deserialize(String arg0, byte[] arg1) {
		    ObjectMapper mapper = new ObjectMapper();
		    MediaPostEngagementActivityCount mediaPostEngagementActivityCount = null;
		    try {
		    	mediaPostEngagementActivityCount = mapper.readValue(arg1, MediaPostEngagementActivityCount.class);
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    return mediaPostEngagementActivityCount;
	  }
}