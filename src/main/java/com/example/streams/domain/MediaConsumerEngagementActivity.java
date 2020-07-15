package com.example.streams.domain;

public class MediaConsumerEngagementActivity {
	public MediaConsumer consumer;
	public EngagementActivity engagementActivity;

	public MediaConsumerEngagementActivity() {
		super();
	}
	public MediaConsumerEngagementActivity(MediaConsumer consumer, EngagementActivity engagementActivity) {
		super();
		this.consumer = consumer;
		this.engagementActivity = engagementActivity;
	}
	public MediaConsumer getConsumer() {
		return consumer;
	}
	public void setConsumer(MediaConsumer consumer) {
		this.consumer = consumer;
	}
	public EngagementActivity getEngagementActivity() {
		return engagementActivity;
	}
	public void setEngagementActivity(EngagementActivity engagementActivity) {
		this.engagementActivity = engagementActivity;
	}
}
