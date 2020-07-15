package com.example.streams.domain;

import com.example.streams.util.Utilities;

public class EngagementActivity {
	public Long id;
	public Long postId;
	public Long engagementDuration;
	public String engagementDateTime;
	public Long consumerId;

	public EngagementActivity() {
		super();
	}

	public EngagementActivity(Long id, Long postId, Long duration, Long consumerId) {
		super();
		this.id = id;
		this.postId = postId;
		this.engagementDuration = duration;
		this.engagementDateTime=Utilities.getCurrentTimeString();
		this.consumerId = consumerId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Long getEngagementDuration() {
		return engagementDuration;
	}

	public void setEngagementDuration(Long engagementDuration) {
		this.engagementDuration = engagementDuration;
	}	

	public String getEngagementDateTime() {
		return engagementDateTime;
	}

	public void setEngagementDateTime(String engagementDateTime) {
		this.engagementDateTime = engagementDateTime;
	}

	public Long getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(Long consumerId) {
		this.consumerId = consumerId;
	}
}
