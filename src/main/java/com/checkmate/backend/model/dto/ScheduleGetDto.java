package com.checkmate.backend.model.dto;

import java.time.LocalDateTime;
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
public class ScheduleGetDto {

	private Long scheduleSeq;

	private String userId;

	private int meetingId;

	private String scheduleName;

	private String scheduleDescription;

	private LocalDateTime scheduleStartDate;

	private LocalDateTime scheduleEndDate;

	private List<String> participants;

	private Long teamId;


	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}
}
