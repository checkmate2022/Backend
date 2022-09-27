package com.checkmate.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingType;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.TeamDto;
import com.checkmate.backend.repo.MeetingRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("MeetingControllerTest 테스트")
class MeetingControllerTest {
	User savedUser;
	Team savedTeam;
	Meeting savedMeeting;
	@Autowired
	MockMvc mvc;
	String url = "http://localhost:8080/api/v1/meeting";
	@Autowired
	private MeetingRepository meetingRepository;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WebApplicationContext ctx;
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() throws Exception {
		mvc = MockMvcBuilders
			.webAppContextSetup(ctx)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();

		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo1", "repo1", "repo1", ProviderType.LOCAL, RoleType.USER, now, now);
		TeamDto teamDto = new TeamDto("팀이름", "팀설명");
		Team team = new Team(teamDto);

		savedUser = userRepository.save(user);
		savedTeam = teamRepository.save(team);
		savedTeam.setUser(savedUser);
		Meeting meeting = new Meeting("랜덤id", MeetingType.PLAN, savedUser, savedTeam, now);
		savedMeeting = meetingRepository.save(meeting);
	}

	@Test
	void getMeeting() throws Exception {
		ResultActions actions = mvc.perform(
			get(url + "/{meetingId}", savedMeeting.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.meetingId").value("랜덤id"));
	}

	@Test
	void getSchedulesByTeam() throws Exception {
		ResultActions actions = mvc.perform(
			get(url + "/team/{teamId}", savedTeam.getTeamSeq())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.list[0].meetingId").value("랜덤id"));
	}

	@AfterEach
	void delete() {
		meetingRepository.delete(savedMeeting);
		teamRepository.delete(savedTeam);
		userRepository.delete(savedUser);
	}
}