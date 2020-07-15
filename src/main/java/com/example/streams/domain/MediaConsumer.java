package com.example.streams.domain;

public class MediaConsumer {
	public Long consumerId;
	public String description;

	public MediaConsumer() {
		super();
	}

	public MediaConsumer(Long consumerId, String description) {
		super();
		this.consumerId = consumerId;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(Long consumerId) {
		this.consumerId = consumerId;
	}

	public MediaConsumer(Long consumerId) {
		super();
		this.consumerId = consumerId;
	}

}
