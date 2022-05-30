package com.checkmate.backend.oauth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.User;
import com.checkmate.backend.oauth.api.repo.ScheduleRepository;
import com.checkmate.backend.oauth.api.repo.UserRepository;
import com.checkmate.backend.oauth.model.ScheduleDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final UserRepository userRepository;

	// 전체 일정 조회
	@Transactional(readOnly = true)
	public List<Schedule> findSchedules() {
		List<Schedule> schedules = scheduleRepository.findAll();

		return schedules;
	}

	// 일정 단건 조회
	public Optional<Schedule> findOne(Long scheduleId) {
		Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
		return schedule;
	}

	// 일정 등록
	public Schedule make(Schedule schedule, User user) {
		schedule.setUser(user);


		//List<String> participants= participnats;
		/*Participant newPart = new Participant();

		schedule.addParticipant(newPart);

		for(String participant: participants ){
			User findUser =userRepository.findByUserId(participant);
			findUser.addParticipant(newPart);
		}*/

		schedule.makeMeetingId();
		Schedule save = scheduleRepository.save(schedule);
		return save;
	}



	// 일정 수정
	public Schedule update(Long scheduleId, ScheduleDto scheduleDto) {
		Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
			() -> new IllegalArgumentException("해당 일정은 존재하지 않습니다.")
		);
		schedule.update(scheduleDto);
		return schedule;
	}

	// 일정 삭제
	public void delete(Long scheduleId) {
		scheduleRepository.deleteById(scheduleId);
	}

}
