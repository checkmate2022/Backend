package com.checkmate.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingParticipant;
import com.checkmate.backend.entity.meeting.MeetingParticipantType;
import com.checkmate.backend.entity.meeting.MeetingType;
import com.checkmate.backend.entity.participant.Participant;
import com.checkmate.backend.entity.schedule.Notification;
import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.schedule.ScheduleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.ScheduleDto;
import com.checkmate.backend.model.dto.ScheduleGetDto;
import com.checkmate.backend.model.request.ScheduleRequest;
import com.checkmate.backend.model.response.ScheduleChatbotResponse;
import com.checkmate.backend.repo.MeetingParticipantRepository;
import com.checkmate.backend.repo.MeetingRepository;
import com.checkmate.backend.repo.NotificationRepository;
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
	private final MeetingRepository meetingRepository;
	private final MeetingParticipantRepository meetingParticipantRepository;
	private final NotificationRepository notificationRepository;
	/*
	// 전체 일정 조회
	@Transactional(readOnly = true)
	public List<Schedule> findSchedules() {
		List<Schedule> schedules = scheduleRepository.findAll();
		return schedules;
	}*/

	//챗봇 알림 설정
	public void updateNotification(long id, int notificationTime, User user) {
		Schedule schedule = scheduleRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당 schedule은 존재하지 않습니다.")
		);

		schedule.setNotificationTime(notificationTime);
		Notification notification = new Notification(schedule.getScheduleName(), schedule.getScheduleDescription(),
			user.getUserId(), schedule.getScheduleStartdate().minusMinutes(notificationTime), false);
		notificationRepository.save(notification);

	}

	//챗봇 -> 알람 설정 안된 일정 반환
	public List<ScheduleChatbotResponse> getSchedulesForChatBotNotification(User user) {
		List<Schedule> schedules = scheduleRepository.findSchedulesByNotificationTimeAndAndScheduleStartdateAndUser(
			user);

		return getScheduleChatbotResponses(schedules);
	}

	//챗봇 일정 조회(날짜 받으면 일정 조회)
	public List<ScheduleChatbotResponse> getSchedulesForChatbot(LocalDateTime time, User user) {
		List<Schedule> schedules = scheduleRepository.findSchedulesByScheduleStartdateBetweenAndScheduleEnddate(time,
			user);

		return getScheduleChatbotResponses(schedules);
	}

	@NotNull
	private List<ScheduleChatbotResponse> getScheduleChatbotResponses(List<Schedule> schedules) {
		List<ScheduleChatbotResponse> response = new ArrayList<>();

		for (Schedule s : schedules) {

			ScheduleChatbotResponse scheduleChatbotResponse = ScheduleChatbotResponse.builder()
				.scheduleSeq(s.getScheduleSeq())
				.scheduleName(s.getScheduleName())
				.scheduleDescription(s.getScheduleDescription())
				.scheduleType(s.getScheduleType())
				.scheduleStartDate(s.getScheduleStartdate())
				.scheduleEndDate(s.getScheduleEnddate())
				.teamName(s.getTeam().getTeamName())
				.userId(s.getUser().getUserId())
				.build();

			//List 담기
			response.add(scheduleChatbotResponse);
		}
		return response;
	}

	// 일정 단건 조회
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
			.scheduleType(schedule.getScheduleType())
			.scheduleStartDate(schedule.getScheduleStartdate())
			.scheduleEndDate(schedule.getScheduleEnddate())
			.teamId(schedule.getTeam().getTeamSeq())
			.userId(schedule.getUser().getUserId())
			.notificationTime(schedule.getNotificationTime())
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
				.scheduleType(s.getScheduleType())
				.scheduleStartDate(s.getScheduleStartdate())
				.scheduleEndDate(s.getScheduleEnddate())
				.teamId(s.getTeam().getTeamSeq())
				.userId(s.getUser().getUserId())
				.notificationTime(s.getNotificationTime())
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
		LocalDateTime start = scheduleReq.getScheduleStartDate();

		ScheduleDto scheduleDto = new ScheduleDto(scheduleReq.getScheduleName(), scheduleReq.getScheduleDescription(),
			scheduleReq.getScheduleType(), scheduleReq.getNotificationTime()
			, scheduleReq.getScheduleStartDate(), scheduleReq.getScheduleEndDate());

		Schedule schedule = new Schedule(scheduleDto);
		List<String> participants = scheduleReq.getParticipantName();
		Meeting meeting = null;
		Schedule save = scheduleRepository.save(schedule);
		//작성자 설정
		save.setUser(user);

		//team 설정
		Team team = teamRepository.findById(scheduleReq.getTeamId()).orElseThrow(
			() -> new IllegalArgumentException("해당 team은 존재하지 않습니다.")
		);
		save.setTeam(team);

		//회의면 회의 생성
		if (save.getScheduleType() == ScheduleType.CONFERENCE) {
			save.makeMeetingId();
			meeting = new Meeting(save.getMeetingId(), MeetingType.PLAN, save.getUser(), save.getTeam(),
				save.getScheduleStartdate());
			meetingRepository.save(meeting);
			MeetingParticipant meetingParticipant = new MeetingParticipant(save.getUser(), meeting,
				MeetingParticipantType.HOST);
			meetingParticipantRepository.save(meetingParticipant);
		}

		//participant 닉네임으로 담음
		for (String p : participants) {
			//닉네임으로 User 찾음
			User findUser = userRepository.findByUsername(p);
			//participant 설정
			Participant participant = new Participant(findUser, save);
			if (save.getScheduleType() == ScheduleType.CONFERENCE) {
				MeetingParticipant meetingParticipant = new MeetingParticipant(findUser, meeting,
					MeetingParticipantType.PARTICIPANT);
				meetingParticipantRepository.save(meetingParticipant);
			}
			participant = participantRepository.save(participant);
			save.addParticipant(participant);
		}
		//작성자도 참여자로 넣음
		Participant participant = new Participant(user, save);
		participant = participantRepository.save(participant);

		save.addParticipant(participant);

		if (scheduleReq.getNotificationTime() != 0) {
			// //알림
			Notification notification = new Notification(save.getScheduleName(), save.getScheduleDescription(),
				user.getUserId(), save.getScheduleStartdate().minusMinutes(scheduleReq.getNotificationTime()), false);
			notificationRepository.save(notification);
		}

		return save;
	}

	// 일정 수정
	public Schedule update(Long scheduleId, ScheduleRequest scheduleReq) {

		ScheduleDto scheduleDto = new ScheduleDto(scheduleReq.getScheduleName(), scheduleReq.getScheduleDescription()
			, scheduleReq.getScheduleType(), scheduleReq.getNotificationTime(), scheduleReq.getScheduleStartDate(),
			scheduleReq.getScheduleEndDate());
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
