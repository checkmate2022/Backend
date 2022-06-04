package com.checkmate.backend.oauth.api.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.checkmate.backend.oauth.model.TeamDto;
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
@Table(name = "TEAM")
public class Team {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "TEAM_SEQ")
	private Long teamSeq;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	@Column(name = "TEAM_NAME")
	private String teamName;

	@Column(name = "TEAM_DESCRIPTION")
	private String teamDescription;

	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Schedule> schedules = new ArrayList<>();

	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<TeamParticipant> participants = new ArrayList<>();

	public Team(TeamDto teamDto) {
		this.teamName = teamDto.getTeamName();
		this.teamDescription = teamDto.getTeamDescription();

	}

	public void update(TeamDto teamDto) {
		this.teamName = teamDto.getTeamName();
		this.teamDescription = teamDto.getTeamDescription();

	}

	//user설정
	public void setUser(User user) {
		this.user = user;
		user.getTeam().add(this);
	}

	//participant 추가
	public void addParticipant(TeamParticipant participant) {
		participants.add(participant);
		participant.setTeam(this);
	}

	public void deleteAllParticipants() {
		for (int i = 0; i < participants.size(); i++) {
			participants.remove(i);
		}
	}

}
