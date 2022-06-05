package com.checkmate.backend.model.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class ScheduleResponse {

	private Long scheduleSeq;

	private User user;

	private int meetingId;

	private String scheduleName;

	private String scheduleDescription;

	private LocalDateTime scheduleStartDate;

	private LocalDateTime scheduleEndDate;

	private List<String> participants;

	private String team;

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public void setTeam(String team) {
		this.team = team;
	}
}
