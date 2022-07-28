package com.checkmate.backend.entity.meeting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetingParticipantType {
	HOST("HOST", "호스트"),
	PARTICIPANT("PARTICIPANT", "참여자");

	private final String code;
	private final String displayName;
}
