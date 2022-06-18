package com.checkmate.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.participant.Participant;
import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.ScheduleDto;
import com.checkmate.backend.model.dto.ScheduleGetDto;
import com.checkmate.backend.model.request.ScheduleRequest;
import com.checkmate.backend.repo.ParticipantRepository;
import com.checkmate.backend.repo.ScheduleRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final UserRepository userRepository;
	private final ParticipantRepository participantRepository;
	private final TeamRepository teamRepository;

	/*
	// 전체 일정 조회
	@Transactional(readOnly = true)
	public List<Schedule> findSchedules() {
		List<Schedule> schedules = scheduleRepository.findAll();
		return schedules;
	}*/

	// 일정 단건 조회
	@Transactional(readOnly = true)
	public ScheduleGetDto findOne(Long scheduleId) {
		Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
			() -> new IllegalArgumentException("해당 schedule은 존재하지 않습니다.")
		);
		ScheduleGetDto response = new ScheduleGetDto();

		List<String> users = new ArrayList<>();

		response = ScheduleGetDto.builder().
			scheduleSeq(schedule.getScheduleSeq())
			.meetingId(schedule.getMeetingId())
			.scheduleName(schedule.getScheduleName())
			.scheduleDescription(schedule.getScheduleDescription())
			.scheduleStartDate(schedule.getScheduleStartdate())
			.scheduleEndDate(schedule.getScheduleEnddate())
			.teamId(schedule.getTeam().getTeamSeq())
			.userId(schedule.getUser().getUserId())
			.build();
		//참여자 정보 담아줌
		for (Participant scheduleP : schedule.getParticipants()) {
			users.add(scheduleP.getUser().getUsername());
		}
		response.setParticipants(users);
		return response;
	}

	//팀별 스케쥴 조회
	public List<ScheduleGetDto> findScheduleByTeam(Long teamId) {
		//팀찾기
		Optional<Team> team = teamRepository.findById(teamId);
		//팀에 해당하는 스케쥴 찾기
		List<Schedule> schedules = scheduleRepository.findAllByTeam(team);
		//반환 리스트 생성
		List<ScheduleGetDto> response = new ArrayList<>();

		for (Schedule s : schedules) {

			List<String> users = new ArrayList<>();

			ScheduleGetDto scheduleGetDto = ScheduleGetDto.builder().
				scheduleSeq(s.getScheduleSeq())
				.meetingId(s.getMeetingId())
				.scheduleName(s.getScheduleName())
				.scheduleDescription(s.getScheduleDescription())
				.scheduleStartDate(s.getScheduleStartdate())
				.scheduleEndDate(s.getScheduleEnddate())
				.teamId(s.getTeam().getTeamSeq())
				.userId(s.getUser().getUserId())
				.build();

			//참여자 정보 담아줌
			for (Participant scheduleP : s.getParticipants()) {
				users.add(scheduleP.getUser().getUsername());
			}
			scheduleGetDto.setParticipants(users);

			//List 담기
			response.add(scheduleGetDto);
		}
		return response;
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
		//team 설정
		Team team = teamRepository.findById(scheduleReq.getTeamId()).orElseThrow(
			() -> new IllegalArgumentException("해당 team은 존재하지 않습니다.")
		);
		save.setTeam(team);
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

		/*
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
				.scheduleName(schedule.get().getScheduleName())
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
			response.setTeam(schedule.get().getTeam());
			//List 담기
			schedules.add(response);
		}

		return schedules;
	}
*/
}
