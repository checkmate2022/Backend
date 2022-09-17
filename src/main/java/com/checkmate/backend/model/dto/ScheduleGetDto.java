package com.checkmate.backend.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.checkmate.backend.entity.schedule.ScheduleType;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class ScheduleGetDto {

	private Long scheduleSeq;

	private String userId;

	private String meetingId;

	private String scheduleName;

	private String scheduleDescription;

	private int notificationTime;

	private ScheduleType scheduleType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime scheduleStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime scheduleEndDate;

	private List<String> participants;

	private Long teamId;

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}
}
