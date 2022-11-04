package com.checkmate.backend.entity.avatar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "EMOTICON")
public class Emoticon {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "EMOTICON")
	private Long emoticonSeq;

	@Column(name = "EMOTICON_IMAGE_URL", length = 100)
	@NotNull
	private String emoticonUrl;

	@Column(name = "EMOTICON_TYPE", length = 100)
	private EmoticonType emoticonType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AVATAR_SEQ")
	private Avatar avatar;

	public Emoticon(String emoticonUrl, EmoticonType emoticonType) {
		this.emoticonUrl = emoticonUrl;
		this.emoticonType = emoticonType;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

}
