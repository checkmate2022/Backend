package com.checkmate.backend.model.dto;

import java.time.LocalDateTime;

import com.checkmate.backend.entity.schedule.ScheduleType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDto {

	private String scheduleName;

	private String scheduleDescription;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime scheduleStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime scheduleEndDate;

	private ScheduleType scheduleType;

	private int notificationTime;

	public ScheduleDto(String scheduleName, String scheduleDescription, ScheduleType scheduleType,int notificationTime,
		LocalDateTime scheduleStartDate,
		LocalDateTime scheduleEndDate) {
		this.scheduleName = scheduleName;
		this.scheduleDescription = scheduleDescription;
		this.scheduleType = scheduleType;
		this.notificationTime=notificationTime;
		this.scheduleStartDate = scheduleStartDate;
		this.scheduleEndDate = scheduleEndDate;
	}
}
