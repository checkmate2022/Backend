package com.checkmate.backend.entity.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

import com.checkmate.backend.entity.participant.Participant;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.ScheduleDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SCHEDULE")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Schedule {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "SCHEDULE_SEQ")
	private Long scheduleSeq;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEAM_ID")
	private Team team;

	@Column(name = "MEETING_ID")
	private String meetingId;

	@Column(name = "SCHEDULE_NAME")
	private String scheduleName;

	@Column(name = "SCHEDULE_DESCRIPTION")
	private String scheduleDescription;

	@Column(name = "SCHEDULE_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	private ScheduleType scheduleType;

	@Column(name = "SCHEDULE_NOTIFICATION_TIME")
	private int notificationTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "SCHEDULE_STARTDATE")
	private LocalDateTime scheduleStartdate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "SCHEDULE_ENDDATE")
	private LocalDateTime scheduleEnddate;

	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Participant> participants = new ArrayList<>();

	public Schedule(ScheduleDto scheduleDto) {
		this.scheduleName = scheduleDto.getScheduleName();
		this.scheduleDescription = scheduleDto.getScheduleDescription();
		this.scheduleType = scheduleDto.getScheduleType();
		this.notificationTime = scheduleDto.getNotificationTime();
		this.scheduleStartdate = scheduleDto.getScheduleStartDate();
		this.scheduleEnddate = scheduleDto.getScheduleEndDate();
	}

	public void update(ScheduleDto scheduleDto) {
		this.scheduleName = scheduleDto.getScheduleName();
		this.scheduleDescription = scheduleDto.getScheduleDescription();
		this.scheduleType = scheduleDto.getScheduleType();
		this.notificationTime = scheduleDto.getNotificationTime();
		this.scheduleStartdate = scheduleDto.getScheduleStartDate();
		this.scheduleEnddate = scheduleDto.getScheduleEndDate();
	}

	//meetingId 랜덤생성
	public void makeMeetingId() {
		Random random = new Random();
		int meetingId = random.nextInt(888888) + 111111;
		this.meetingId = String.valueOf(meetingId);
	}

	//user설정
	public void setUser(User user) {
		this.user = user;
		// user.getSchedule().add(this);
	}

	//team설정
	public void setTeam(Team team) {
		this.team = team;
		team.getSchedules().add(this);
	}

	public void setNotificationTime(int notificationTime) {
		this.notificationTime = notificationTime;
	}

	//participant 추가
	public void addParticipant(Participant participant) {
		participants.add(participant);
		participant.setSchedule(this);
	}

	public void deleteAllParticipants() {
		for (int i = 0; i < participants.size(); i++) {
			participants.remove(i);
		}
	}
}

