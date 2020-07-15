package com.example.streams.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import com.example.streams.domain.MediaPost;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MediaPostDeserializer implements Deserializer<MediaPost> {
    @Override
	public void close() {
    }
    
    @Override 
    public void configure(Map<String, ?> arg0, boolean arg1) {
    }
    
    @Override 
    public MediaPost deserialize(String arg0, byte[] arg1) {
    ObjectMapper mapper = new ObjectMapper();
    MediaPost mediaPost = null;
    try {
    	mediaPost = mapper.readValue(arg1, MediaPost.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mediaPost;
  }
}