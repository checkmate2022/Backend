package com.checkmate.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingParticipant;
import com.checkmate.backend.entity.meeting.MeetingType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.model.response.MeetingParticipantResponse;
import com.checkmate.backend.model.response.MeetingResponse;
import com.checkmate.backend.repo.MeetingRepository;
import com.checkmate.backend.repo.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

	private final MeetingRepository meetingRepository;
	private final TeamRepository teamRepository;

	//host가 회의 종료 로직 추가 필요(알림이나 sessionController에서 host면 회의 종료 등등)
	public void endMeeting(String meetingId) {
		Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(
			() -> new IllegalArgumentException("회의가 없습니다.")
		);
		meeting.setMeetingType(MeetingType.END);
	}

	public void startMeeting(String meetingId) {
		Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(
			() -> new IllegalArgumentException("회의가 없습니다.")
		);
		meeting.setMeetingType(MeetingType.DOING);
	}

	//단건조회
	public MeetingResponse findById(String meetingId) {
		Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(
			() -> new IllegalArgumentException("회의가 없습니다.")
		);

		List<MeetingParticipantResponse> users = new ArrayList<>();

		MeetingResponse meetingResponse = MeetingResponse.builder()
			.meetingId(meeting.getId())
			.scheduleStartDate(meeting.getMeetingStartdate())
			.build();

		for (MeetingParticipant meetingParticipant : meeting.getParticipants()) {
			users.add(new MeetingParticipantResponse(meetingParticipant.getUser().getUsername(),
				meetingParticipant.getUser().getUserImage(), meetingParticipant.getMeetingParticipantType()));
		}

		meetingResponse.setParticipants(users);
		return meetingResponse;
	}

	//팀별 조회
	public List<MeetingResponse> findMeetingsByTeam(long teamId) {
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("팀이 없습니다.")
		);
		List<Meeting> meetings = meetingRepository.findMeetingsByTeam(team);

		List<MeetingResponse> meetingResponses = getMeetingResponses(meetings);

		return meetingResponses;
	}

	private List<MeetingResponse> getMeetingResponses(List<Meeting> meetings) {
		List<MeetingResponse> meetingResponses = new ArrayList<>();

		for (Meeting meeting : meetings) {
			List<MeetingParticipantResponse> users = new ArrayList<>();
			MeetingResponse meetingResponse = MeetingResponse.builder()
				.meetingId(meeting.getId())
				.scheduleStartDate(meeting.getMeetingStartdate())
				.build();

			for (MeetingParticipant meetingParticipant : meeting.getParticipants()) {
				users.add(new MeetingParticipantResponse(meetingParticipant.getUser().getUsername(),
					meetingParticipant.getUser().getUserImage(), meetingParticipant.getMeetingParticipantType()));
			}
			meetingResponse.setParticipants(users);

			meetingResponses.add(meetingResponse);
		}
		return meetingResponses;
	}

}
