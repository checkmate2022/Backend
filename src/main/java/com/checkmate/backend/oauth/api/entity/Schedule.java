package com.checkmate.backend.oauth.api.entity;

import java.time.LocalDateTime;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.checkmate.backend.oauth.model.ScheduleDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "SCHEDULE")
public class Schedule {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@JsonIgnore
	@Column(name = "SCHEDULE_SEQ")
	private Long scheduleSeq;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	@Column(name = "MEETING_ID")
	private int meetingId;

	@Column(name = "SCHEDULE_NAME")
	private String scheduleName;

	@Column(name = "SCHEDULE_DESCRIPTION")
	private String scheduleDescription;

	@Column(name = "SCHEDULE_STARTDATE")
	private LocalDateTime scheduleStartdate;

	@Column(name = "SCHEDULE_ENDDATE")
	private LocalDateTime scheduleEnddate;

	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	// @Column(name = "SCHEDULE_PARTICIPANTS")
	// private List<User> scheduleParticipants = new ArrayList<User>();

	public Schedule(ScheduleDto scheduleDto) {
		this.scheduleName = scheduleDto.getScheduleName();
		this.scheduleDescription = scheduleDto.getScheduleDescription();
		this.scheduleStartdate = scheduleDto.getScheduleStartDate();
		this.scheduleEnddate = scheduleDto.getScheduleEndDate();
	}

	public void update(ScheduleDto scheduleDto) {
		this.scheduleName = scheduleDto.getScheduleName();
		this.scheduleDescription = scheduleDto.getScheduleDescription();
		this.scheduleStartdate = scheduleDto.getScheduleStartDate();
		this.scheduleEnddate = scheduleDto.getScheduleEndDate();
	}

	//meetingId 랜덤생성
	public void makeMeetingId() {
		Random random = new Random();
		int meetingId = random.nextInt(888888) + 111111;
		this.meetingId = meetingId;
	}

	//user설정
	public void setUser(User user) {
		this.user = user;
		user.getSchedule().add(this);
	}

}
