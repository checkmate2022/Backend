package com.checkmate.backend.entity.avatar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmoticonType {
	SAD("SAD", "슬픔"),
	HAPPY("HAPPY", "기쁨"),
	WINK("WINK", "윙크"),
	ANGRY("ANGRY", "화남");

	private final String code;
	private final String displayName;
}

