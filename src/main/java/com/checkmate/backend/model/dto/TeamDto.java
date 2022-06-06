package com.checkmate.backend.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamDto {

	private String teamName;

	private String teamDescription;

	public TeamDto(String teamName, String teamDescription) {
		this.teamName = teamName;
		this.teamDescription = teamDescription;

	}
}
