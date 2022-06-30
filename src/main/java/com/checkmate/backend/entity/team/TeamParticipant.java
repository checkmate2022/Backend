package com.checkmate.backend.entity.team;

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
import javax.persistence.Table;

import com.checkmate.backend.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TEAMPATICIPANT")
public class TeamParticipant {
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "TEAMPARTICIPANT_ID")
	private Long teamparticipantSeq;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_SEQ")
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEAM_SEQ")
	private Team team;

	@Column(name = "TEAM_ROLE_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private TeamRoleType teamRoleType;

	public TeamParticipant(User user, Team team, TeamRoleType teamRoleType) {
		this.user = user;
		this.team = team;
		this.teamRoleType = teamRoleType;
	}

	public Team getTeam() {
		return this.team;
	}
}
