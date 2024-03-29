package com.checkmate.backend.entity.avatar;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.checkmate.backend.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "AVATAR")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Avatar {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "AVATAR_ID")
	private Long avatarSeq;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	@JsonIgnore
	private User user;

	@Column(name = "AVATAR_NAME", length = 100)
	@NotNull
	@Size(max = 100)
	private String avatarName;

	@Column(name = "AVATAR_DESCRIPTION", length = 300)
	@NotNull
	private String avatarDescription;

	@Column(name = "AVATAR_ORIGIN_URL", length = 100)
	@NotNull
	private String avatarOriginUrl;

	@Column(name = "AVATAR_CREATED_URL", length = 100)
	@NotNull
	private String avatarCreatedUrl;

	@Column(name = "AVATAR_STYLE", length = 100)
	@Enumerated(EnumType.STRING)
	private AvatarType avatarStyle;

	@Column(name = "AVATAR_STYLE_ID", length = 100)
	private Long avatarStyleId;

	@Column(name = "AVATAR_DATE", length = 100)
	@NotNull
	private LocalDateTime avatarDate;

	@Column(name = "AVATAR_BASIC", length = 100)
	private Boolean isBasic = false;

	@OneToMany(mappedBy = "avatar", cascade = CascadeType.ALL)
	private List<Emoticon> emoticons = new ArrayList<>();

	public Avatar(User user, String avatarName, String avatarDescription, AvatarType avatarStyle, Long avatarStyleId,
		String originFileUrl, String createdFileUrl, LocalDateTime dateTime) {
		this.user = user;
		this.avatarName = avatarName;
		this.avatarDescription = avatarDescription;
		this.avatarStyle = avatarStyle;
		this.avatarStyleId = avatarStyleId;
		this.avatarOriginUrl = originFileUrl;
		this.avatarCreatedUrl = createdFileUrl;
		this.avatarDate = dateTime;
	}

	public void update(String avatarName, String avatarDescription, AvatarType avatarStyle, Long avatarStyleId,
		String originFileUrl, String createdFileUrl, LocalDateTime dateTime) {
		this.avatarName = avatarName;
		this.avatarDescription = avatarDescription;
		this.avatarStyle = avatarStyle;
		this.avatarStyleId = avatarStyleId;
		this.avatarOriginUrl = originFileUrl;
		this.avatarCreatedUrl = createdFileUrl;
		this.avatarDate = dateTime;

	}

	public void setIsBasic() {
		this.isBasic = true;
	}

	public void setIsBasicFalse() {
		this.isBasic = false;
	}

	//user설정
	public void setUser(User user) {
		this.user = user;
		user.getAvatar().add(this);
	}

	public void setEmoticon(Emoticon emotion) {
		this.emoticons.add(emotion);
	}
}
