package com.checkmate.backend.model.request;

import java.time.LocalDateTime;
import java.util.List;

import com.checkmate.backend.entity.schedule.ScheduleType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleRequest {

	private String scheduleName;

	private String scheduleDescription;

	private ScheduleType scheduleType;

	private LocalDateTime scheduleStartDate;

	private LocalDateTime scheduleEndDate;

	private List<String> participantName;

	private Long teamId;

	private int notificationTime;

	public ScheduleRequest(String scheduleName, String scheduleDescription, ScheduleType scheduleType,
		LocalDateTime scheduleStartDate,
		LocalDateTime scheduleEndDate, List<String> participantName, Long teamId) {
		this.scheduleName = scheduleName;
		this.scheduleDescription = scheduleDescription;
		this.scheduleType = scheduleType;
		this.scheduleStartDate = scheduleStartDate;
		this.scheduleEndDate = scheduleEndDate;
		this.participantName = participantName;
		this.teamId = teamId;
	}
}
