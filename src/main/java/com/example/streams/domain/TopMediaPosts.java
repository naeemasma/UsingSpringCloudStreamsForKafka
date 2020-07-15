package com.example.streams.domain;

import com.example.streams.KafkaStreamsInteractiveQueryDemo;
import com.example.streams.domain.MediaPostEngagementActivityCount;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TopMediaPosts implements Iterable<MediaPostEngagementActivityCount>{

	private Log logger = LogFactory.getLog(getClass());
	private final Map<Long, MediaPostEngagementActivityCount> currentPosts = new HashMap<>();
	private final TreeSet<MediaPostEngagementActivityCount> topPosts = new TreeSet<>((o1, o2) -> {
		final int result = o2.getEngagementActivityCount().compareTo(o1.getEngagementActivityCount());
		if (result != 0) {
			return result;
		}
		return o1.getPostId().compareTo(o2.getPostId());
	});

	public void add(final MediaPostEngagementActivityCount mediaPostEngagementActivityCount) {
		logger.info("add: " + mediaPostEngagementActivityCount);
		if(currentPosts.containsKey(mediaPostEngagementActivityCount.getPostId())) {
			topPosts.remove(currentPosts.remove(mediaPostEngagementActivityCount.getPostId()));
		}
		topPosts.add(mediaPostEngagementActivityCount);
		currentPosts.put(mediaPostEngagementActivityCount.getPostId(), mediaPostEngagementActivityCount);
		if (topPosts.size() > KafkaStreamsInteractiveQueryDemo.TOP_LIST_SIZE) {
			final MediaPostEngagementActivityCount last = topPosts.last();
			currentPosts.remove(last.getPostId());
			topPosts.remove(last);
		}
	}

	public void remove(final MediaPostEngagementActivityCount mediaPostEngagementActivityCount) {
		logger.info("remove: " + mediaPostEngagementActivityCount);
		
		topPosts.remove(mediaPostEngagementActivityCount);
		currentPosts.remove(mediaPostEngagementActivityCount.getPostId());
	}

	@Override
	public Iterator<MediaPostEngagementActivityCount> iterator() {
		return topPosts.iterator();
	}
	
	@Override 
	public String toString() {
		StringBuilder ret=new StringBuilder();
		for(int i=0;i<topPosts.size();i++) {
			MediaPostEngagementActivityCount mediaPost = iterator().next();
	             ret.append("(MediaPost: " + mediaPost.getPostId() 
	             + ", engagement count: " + mediaPost.getEngagementActivityCount() +") ");
	    }
	   return ret.toString();
	}
	
	public TreeSet<MediaPostEngagementActivityCount> mediaPostEngagementActivities() {		
		return topPosts;
	}
}