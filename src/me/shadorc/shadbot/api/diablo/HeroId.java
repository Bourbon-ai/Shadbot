package me.shadorc.shadbot.api.diablo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HeroId {

	@JsonProperty("id")
	private long id;

	public long getId() {
		return id;
	}

}