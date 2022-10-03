package com.checkmate.backend.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduleType {
	BASIC("basic", "basic"),
	CONFERENCE("conference", "conference");

	private final String code;
	private final String displayName;
}