package com.checkmate.backend.entity.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamRoleType {
	LEADER("LEADER", "일반 사용자 권한"),
	MEMBER("MEMBER", "관리자 권한");

	private final String code;
	private final String displayName;

}
