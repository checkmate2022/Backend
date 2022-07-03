package com.checkmate.backend.model.response;

import com.checkmate.backend.entity.avatar.Avatar;
import com.checkmate.backend.entity.team.TeamRoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantResponse {

	private Long userSeq;

	private Avatar avatar;

	private String userId;

	private String username;

	private String userImg;

	private TeamRoleType teamRoleType;

}
