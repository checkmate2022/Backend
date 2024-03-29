package com.checkmate.backend.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponse {

	private Long teamSeq;

	private String teamName;

	private String teamDescription;

	private String userId;

	private List<String> participants;

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

}
