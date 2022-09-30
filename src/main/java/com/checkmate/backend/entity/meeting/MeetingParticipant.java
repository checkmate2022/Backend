package com.checkmate.backend.entity.meeting;

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

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MEETING_PARTICIPANT")
public class MeetingParticipant {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "PARTICIPANT_ID")
	private Long participantSeq;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_SEQ")
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MEETING_SEQ")
	private Meeting meeting;

	@Column(name = "MEETINGPARTICIPANT_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private MeetingParticipantType meetingParticipantType;

	public MeetingParticipant(User user, Meeting meeting, MeetingParticipantType meetingParticipantType) {
		this.user = user;
		this.meeting = meeting;
		this.meetingParticipantType = meetingParticipantType;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

}
