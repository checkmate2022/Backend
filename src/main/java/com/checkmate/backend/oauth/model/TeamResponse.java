package com.checkmate.backend.oauth.model;

import java.util.List;

import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.User;

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

	private User user;

	private String teamName;

	private String teamDescription;

	private List<Schedule> schedules;

	private List<String> participants;

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}
}
