package com.checkmate.backend.entity.meeting;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "MEETING")
public class Meeting {

	@Id
	@Column(name = "MEETING_SEQ")
	private String id;

	//회의 타입 (진행중, 예정, 종료)
	@Column(name = "MEETING_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private MeetingType meetingType;

	//회의 host
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User host;

	@OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<MeetingParticipant> participants = new ArrayList<>();

	public Meeting(String id, MeetingType meetingType, User host) {
		this.id = id;
		this.meetingType = meetingType;
		this.host = host;
	}

	public void setMeetingType(MeetingType meetingType) {
		this.meetingType = meetingType;
	}

	public void addParticipant(MeetingParticipant participant) {
		participants.add(participant);
		participant.setMeeting(this);
	}

}
