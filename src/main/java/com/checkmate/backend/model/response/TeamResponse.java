package com.checkmate.backend.model.response;

import java.util.List;

import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.user.User;

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

	private List<String> participants;

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

}
