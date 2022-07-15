package com.checkmate.backend.entity.avatar;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AvatarType {
	cartoon("cartoon", "만화"),
	caricature("caricature", "캐리커쳐"),
	anime("anime", "애니"),
	arcane("arcane", "아케인"),
	comic("comic", "코믹"),
	pixar("pixar", "픽사"),
	slamdunk("slamdunk", "슬램덩크");

	private final String code;
	private final String displayName;
}
