package com.checkmate.backend.oauth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.oauth.api.entity.Participant;
import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.User;
import com.checkmate.backend.oauth.api.repo.ParticipantRepository;
import com.checkmate.backend.oauth.api.repo.ScheduleRepository;
import com.checkmate.backend.oauth.api.repo.UserRepository;
import com.checkmate.backend.oauth.model.ScheduleDto;
import com.checkmate.backend.oauth.model.ScheduleRequest;
import com.checkmate.backend.oauth.model.ScheduleResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final UserRepository userRepository;
	private final ParticipantRepository participantRepository;

	// 전체 일정 조회
	@Transactional(readOnly = true)
	public List<Schedule> findSchedules() {
		List<Schedule> schedules = scheduleRepository.findAll();

		return schedules;
	}

	// 일정 단건 조회
	@Transactional(readOnly = true)
	public Optional<Schedule> findOne(Long scheduleId) {
		Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
		return schedule;
	}

	// 사용자별 스케쥴 조회
	public List<ScheduleResponse> findScheduleByUser(User user) {
		//user에 따라 participant 찾음
		List<Participant> participants = participantRepository.findAllByUser(user);

		List<ScheduleResponse> schedules = new ArrayList<>();
		//반복문
		for (Participant p : participants) {
			List<String> users = new ArrayList<>();
			//response 객체 생성
			Optional<Schedule> schedule = scheduleRepository.findById(p.getSchedule().getScheduleSeq());
			ScheduleResponse response = ScheduleResponse.builder().
				scheduleSeq(schedule.get().getScheduleSeq())
				.meetingId(schedule.get().getMeetingId())
				.scheduleDescription(schedule.get().getScheduleDescription())
				.scheduleStartDate(schedule.get().getScheduleStartdate())
				.scheduleEndDate(schedule.get().getScheduleEnddate())
				.user(schedule.get().getUser())
				.build();
			//참여자 정보 담아줌
			for (Participant scheduleP : schedule.get().getParticipants()) {
				users.add(scheduleP.getUser().getUsername());
			}
			response.setParticipants(users);
			//List 담기
			schedules.add(response);
		}

		return schedules;
	}

	// 일정 등록
	public Schedule make(ScheduleRequest scheduleReq, User user) {

		ScheduleDto scheduleDto = new ScheduleDto(scheduleReq.getScheduleName(), scheduleReq.getScheduleDescription()
			, scheduleReq.getScheduleStartDate(), scheduleReq.getScheduleEndDate());

		Schedule schedule = new Schedule(scheduleDto);
		List<String> participants = scheduleReq.getParticipantName();
		Schedule save = scheduleRepository.save(schedule);
		//작성자 설정
		save.setUser(user);
		//participant 닉네임으로 담음
		for (String p : participants) {
			//닉네임으로 User 찾음
			User findUser = userRepository.findByUsername(p);
			//participant 설정
			Participant participant = new Participant(findUser, save);
			participant = participantRepository.save(participant);
			save.addParticipant(participant);
		}
		//작성자도 참여자로 넣음
		Participant participant = new Participant(user, save);
		participant = participantRepository.save(participant);

		save.addParticipant(participant);
		save.makeMeetingId();
		return save;
	}

	// 일정 수정
	public Schedule update(Long scheduleId, ScheduleRequest scheduleReq) {

		ScheduleDto scheduleDto = new ScheduleDto(scheduleReq.getScheduleName(), scheduleReq.getScheduleDescription()
			, scheduleReq.getScheduleStartDate(), scheduleReq.getScheduleEndDate());
		Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
			() -> new IllegalArgumentException("해당 일정은 존재하지 않습니다.")
		);
		//기존 participants 삭제
		participantRepository.deleteAllBySchedule(schedule);
		schedule.deleteAllParticipants();

		//새롭게 participants 추가
		List<String> participants = scheduleReq.getParticipantName();
		for (var p : participants) {
			User findUser = userRepository.findByUsername(p);
			Participant participant = new Participant(findUser, schedule);
			participant = participantRepository.save(participant);
			schedule.addParticipant(participant);
		}

		Participant participant = new Participant(schedule.getUser(), schedule);
		participant = participantRepository.save(participant);
		schedule.addParticipant(participant);

		schedule.update(scheduleDto);

		schedule = scheduleRepository.save(schedule);

		return schedule;
	}

	// 일정 삭제
	public void delete(Long scheduleId) {
		scheduleRepository.deleteById(scheduleId);
	}

}
