package com.checkmate.backend.oauth.api.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="Avatar")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Avatar {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "avatar_id")
	private Long id;

	/*
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
*/
	private String user_id;

	private String avatar_name;

	private String avatar_description;

	private String avatar_origin_url;

	private String avatar_created_url;

	private LocalDateTime avatar_date;

	private Boolean isBasic;

	public Avatar(String user_id,String avatar_name,String avatar_description,
		String originFileUrl,String createdFileUrl, LocalDateTime dateTime) {
		this.user_id =user_id;
		this.avatar_name =  avatar_name;
		this.avatar_description =  avatar_description;
		this.avatar_origin_url =  originFileUrl;
		this.avatar_created_url = createdFileUrl;
		this.avatar_date = dateTime;
	}

	public void update(String user_id,String avatar_name,String avatar_description,
		String originFileUrl,String createdFileUrl, LocalDateTime dateTime) {
		this.user_id =user_id;
		this.avatar_name =  avatar_name;
		this.avatar_description =  avatar_description;
		this.avatar_origin_url =  originFileUrl;
		this.avatar_created_url = createdFileUrl;
		this.avatar_date = dateTime;
	}

	public void setIsBasic() {
		if (this.isBasic == false)
			this.isBasic = true;
		else
			this.isBasic = false;
	}

}
