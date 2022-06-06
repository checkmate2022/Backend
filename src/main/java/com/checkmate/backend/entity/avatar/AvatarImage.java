package com.checkmate.backend.entity.avatar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class AvatarImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "file_id")
	private Long id;

	private String file_name;

	private String saved_path;

	@Builder
	public AvatarImage(Long id, String file_name, String saved_path) {
		this.id = id;
		this.file_name = file_name;
		this.saved_path = saved_path;
	}
}
