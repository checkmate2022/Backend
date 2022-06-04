package com.checkmate.backend.oauth.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamRequest {

	private String teamName;

	private String teamDescription;

	private List<String> participantName;

	public TeamRequest(String teamName, String teamDescription, List<String> participantName) {
		this.teamName = teamName;
		this.teamDescription = teamDescription;
		this.participantName = participantName;
	}
}
