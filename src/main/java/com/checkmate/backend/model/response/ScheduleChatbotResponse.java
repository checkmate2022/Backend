package com.checkmate.backend.model.response;

import java.time.LocalDateTime;

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
public class ScheduleChatbotResponse {

	private String userId;

	private String scheduleName;

	private String scheduleDescription;

	private ScheduleType scheduleType;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime scheduleStartDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime scheduleEndDate;

	private String teamName;
}
