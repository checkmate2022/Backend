package com.checkmate.backend.oauth.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleRequest {

	private String scheduleName;

	private String scheduleDescription;

	private LocalDateTime scheduleStartDate;

	private LocalDateTime scheduleEndDate;

	private List<String> participantName;

	private Long teamId;

	public ScheduleRequest(String scheduleName, String scheduleDescription, LocalDateTime scheduleStartDate,
		LocalDateTime scheduleEndDate, List<String> participantName,Long teamId) {
		this.scheduleName = scheduleName;
		this.scheduleDescription = scheduleDescription;
		this.scheduleStartDate = scheduleStartDate;
		this.scheduleEndDate = scheduleEndDate;
		this.participantName = participantName;
		this.teamId = teamId;
	}
}
