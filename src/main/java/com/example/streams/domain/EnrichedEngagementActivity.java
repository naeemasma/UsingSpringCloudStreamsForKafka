package com.example.streams.domain;

public class EnrichedEngagementActivity {
	public EngagementActivity engagementActivity;
	public MediaPost post;
	public MediaConsumer consumer;
	public EnrichedEngagementActivity() {
	}
	public EnrichedEngagementActivity(EngagementActivity engagementActivity, MediaPost post, MediaConsumer consumer) {
		super();
		this.post = post;
		this.consumer = consumer;
		this.engagementActivity = engagementActivity;
	}
	public EngagementActivity getEngagementActivity() {
		return engagementActivity;
	}
	public void setEngagementActivity(EngagementActivity engagementActivity) {
		this.engagementActivity = engagementActivity;
	}
	public MediaPost getPost() {
		return post;
	}
	public void setPost(MediaPost post) {
		this.post = post;
	}
	public MediaConsumer getConsumer() {
		return consumer;
	}
	public void setConsumer(MediaConsumer consumer) {
		this.consumer = consumer;
	}
}
