package com.checkmate.backend.oauth.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AvatarCreateDto {
	private String user_id;

	private String avatar_name;

	private String avatar_description;

	private LocalDateTime avatar_date;

	public AvatarCreateDto(String user_id, String avatar_name, String avatar_description, LocalDateTime avatar_date) {
		this.user_id = user_id;
		this.avatar_name = avatar_name;
		this.avatar_description = avatar_description;
		this.avatar_date = avatar_date;
	}
}
