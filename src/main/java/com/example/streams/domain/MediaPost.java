package com.example.streams.domain;

public class MediaPost {
	Long postId;
	String name;
	String category;
	
	public MediaPost() {
		super();
	}
	public MediaPost(Long postId) {
		super();
		this.postId = postId;
	}
	public MediaPost(Long postId, String name) {
		super();
		this.postId = postId;
		this.name = name;
	}
	public MediaPost(Long postId, String name, String category) {
		this(postId, name);
		this.category = category;
	}
	public MediaPost(String name) {
		super();
		this.name = name;
	}
	public Long getPostId() {
		return postId;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override public String toString() {
	    return "MediaPost(" + postId + ", " + name + ", " + category + ")";
	  }
}
