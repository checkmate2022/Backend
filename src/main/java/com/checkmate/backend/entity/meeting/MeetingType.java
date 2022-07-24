package com.checkmate.backend.entity.meeting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetingType {
	END("END", "종료"),
	DOING("DOING", "진행중"),
	PLAN("PLAN", "예정");

	private final String code;
	private final String displayName;
}
