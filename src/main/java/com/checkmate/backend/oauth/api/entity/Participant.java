package com.checkmate.backend.oauth.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "PATICIPANT")
public class Participant {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "PARTICIPANT_ID")
	private Long participantSeq;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_SEQ")
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCHEDULE_SEQ")
	private Schedule schedule;

	public Participant(User user, Schedule schedule) {
		this.user = user;
		this.schedule = schedule;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}
}
