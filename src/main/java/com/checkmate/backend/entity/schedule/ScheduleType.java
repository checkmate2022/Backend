package com.checkmate.backend.entity.schedule;

import java.util.Arrays;

import com.checkmate.backend.entity.oauth.RoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduleType {
	BASIC("BASIC", "기본"),
	CONFERENCE("CONFERENCE", "회의");

	private final String code;
	private final String displayName;
}
