package com.checkmate.backend.oauth.api.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.checkmate.backend.oauth.entity.ProviderType;
import com.checkmate.backend.oauth.entity.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER")
public class User {
	@JsonIgnore
	@Id
	@Column(name = "USER_SEQ")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userSeq;

	@Column(name = "USER_ID", length = 64, unique = true)
	@NotNull
	@Size(max = 64)
	private String userId;

	@Column(name = "USERNAME", length = 100)
	@NotNull
	@Size(max = 100)
	private String username;

	@JsonIgnore
	@Column(name = "PASSWORD", length = 128)
	@NotNull
	@Size(max = 128)
	private String password;

	@Column(name = "PROVIDER_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ProviderType providerType;

	@Column(name = "ROLE_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	@NotNull
	private RoleType roleType;

	@Column(name = "CREATED_AT")
	@NotNull
	private LocalDateTime createdAt;

	@Column(name = "MODIFIED_AT")
	@NotNull
	private LocalDateTime modifiedAt;

	@Column(name = "USER_IMAGE")
	private String userImage;

	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Schedule> schedule = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Avatar> avatar = new ArrayList<>();

	public User(
		@NotNull @Size(max = 64) String userId,
		@NotNull @Size(max = 100) String username,
		@NotNull @Size(max = 128) String password,
		@NotNull ProviderType providerType,
		@NotNull RoleType roleType,
		@NotNull LocalDateTime createdAt,
		@NotNull LocalDateTime modifiedAt
	) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.providerType = providerType;
		this.roleType = roleType;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	//userImage 설정
	public void setUserImage(String imageUrl) {
		this.userImage = imageUrl;
	}

	// //participant 설정
	// public void addParticipant(Participant participant) {
	// 	participants.add(participant);
	// 	participant.setUser(this);
	// }
}
