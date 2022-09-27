package com.checkmate.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingType;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.TeamDto;
import com.checkmate.backend.model.response.MeetingResponse;
import com.checkmate.backend.repo.MeetingRepository;
import com.checkmate.backend.repo.TeamRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingService 테스트")
class MeetingServiceTest {
	MeetingService meetingService;
	@Mock
	MeetingRepository meetingRepository;
	@Mock
	TeamRepository teamRepository;

	@BeforeEach
	void setUp() {
		this.meetingService = new MeetingService(this.meetingRepository, this.teamRepository);
	}

	@Test
	void endMeeting() {

		Meeting mockMeeting = Meeting.builder()
			.id("랜덤id")
			.meetingType(MeetingType.DOING)
			.build();

		given(meetingRepository.findById(any())).willReturn(java.util.Optional.of(mockMeeting));
		meetingService.endMeeting("랜덤id");

		assertEquals(mockMeeting.getMeetingType(), MeetingType.END);
	}

	@Test
	void startMeeting() {
		Meeting mockMeeting = Meeting.builder()
			.id("랜덤id")
			.meetingType(MeetingType.PLAN)
			.build();

		given(meetingRepository.findById(any())).willReturn(java.util.Optional.of(mockMeeting));
		meetingService.startMeeting("랜덤id");

		assertEquals(mockMeeting.getMeetingType(), MeetingType.DOING);
	}

	@Test
	void findById() {
		LocalDateTime now = LocalDateTime.now();
		Meeting mockMeeting = Meeting.builder()
			.id("랜덤id")
			.meetingType(MeetingType.PLAN)
			.meetingStartdate(now)
			.participants(new ArrayList<>())
			.build();

		given(meetingRepository.findById(any())).willReturn(java.util.Optional.of(mockMeeting));

		MeetingResponse meetingResponse = meetingService.findById("랜덤id");

		assertEquals(meetingResponse.getMeetingId(), mockMeeting.getId());
		assertEquals(meetingResponse.getScheduleStartDate(), mockMeeting.getMeetingStartdate());

	}

	@Test
	void findMeetingsByTeam() {
		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo1", "repo1", "repo1", ProviderType.LOCAL, RoleType.USER, now, now);
		TeamDto teamDto = new TeamDto("팀이름", "팀설명");
		Team team = new Team(teamDto);

		Meeting meeting = new Meeting("랜덤id", MeetingType.PLAN, user, team, now);
		List<Meeting> meetings = new ArrayList<>();
		meetings.add(meeting);

		given(teamRepository.findById(any())).willReturn(java.util.Optional.of(team));
		given(meetingRepository.findMeetingsByTeam(any())).willReturn(meetings);

		List<MeetingResponse> meetingResponses = meetingService.findMeetingsByTeam(1L);

		assertEquals(meetingResponses.get(0).getMeetingId(), meetings.get(0).getId());
		assertEquals(meetingResponses.get(0).getScheduleStartDate(), meetings.get(0).getMeetingStartdate());
	}

}