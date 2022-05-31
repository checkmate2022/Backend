package com.checkmate.backend.oauth.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDto {

	private String scheduleName;

	private String scheduleDescription;

	private LocalDateTime scheduleStartDate;

	private LocalDateTime scheduleEndDate;

	public ScheduleDto(String scheduleName, String scheduleDescription, LocalDateTime scheduleStartDate,
		LocalDateTime scheduleEndDate) {
		this.scheduleName = scheduleName;
		this.scheduleDescription = scheduleDescription;
		this.scheduleStartDate = scheduleStartDate;
		this.scheduleEndDate = scheduleEndDate;
	}
}
