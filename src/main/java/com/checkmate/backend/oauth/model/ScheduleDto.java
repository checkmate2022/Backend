package com.checkmate.backend.oauth.model;

import java.time.LocalDateTime;
import java.util.List;

import com.checkmate.backend.oauth.api.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDto {

	private String schedule_name;

	private String schedule_description;

	//private List<String> schedule_participants;

	private LocalDateTime schedule_startdate;

	private LocalDateTime schedule_enddate;

	public ScheduleDto(String schedule_name,String schedule_description,LocalDateTime schedule_startdate,LocalDateTime schedule_enddate){
		this.schedule_name = schedule_name;
		this. schedule_description =  schedule_description;

		this.schedule_startdate = schedule_startdate;
		this.schedule_enddate = schedule_enddate;
	}
}
