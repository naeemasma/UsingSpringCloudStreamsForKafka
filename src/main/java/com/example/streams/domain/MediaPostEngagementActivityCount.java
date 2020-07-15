package com.example.streams.domain;

public class MediaPostEngagementActivityCount{
	Long postId;
	Long engagementActivityCount;
	
	public MediaPostEngagementActivityCount() {
		super();
	}
	public MediaPostEngagementActivityCount(Long postId) {
		super();
		this.postId = postId;
	}
	public MediaPostEngagementActivityCount(Long postId, String name) {
		this(postId);
	}
	public MediaPostEngagementActivityCount(Long postId, String name, Long engagementActivityCount) {
		this(postId, name);
		this.engagementActivityCount = engagementActivityCount;
	}
	public MediaPostEngagementActivityCount(Long postId, Long engagementActivityCount) {
		this(postId);
		this.engagementActivityCount = engagementActivityCount;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public Long getEngagementActivityCount() {
		return engagementActivityCount;
	}
	public void setEngagementActivityCount(Long engagementActivityCount) {
		this.engagementActivityCount = engagementActivityCount;
	}
	@Override 
	public String toString() {
	    return "EngagementActivityCount(" + postId + ", " + engagementActivityCount.toString() + ")";
	  }
}
