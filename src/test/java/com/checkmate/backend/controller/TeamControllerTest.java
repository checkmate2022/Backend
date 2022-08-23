package com.checkmate.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.request.TeamRequest;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("TeamControllerTest 테스트")
class TeamControllerTest {

	@Autowired
	MockMvc mvc;
	String url = "http://localhost:8080/api/v1/team";
	String token;
	@Autowired
	private WebApplicationContext ctx;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() throws Exception {
		mvc = MockMvcBuilders
			.webAppContextSetup(ctx)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();

		token = getToken();

		TeamRequest teamRequest = TeamRequest.builder()
			.teamName("team")
			.teamDescription("team")
			.participantName(new ArrayList<>())
			.build();

		ResultActions actions = mvc.perform(
			post(url)
				.content(toJson(teamRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token));

	}

	private String getToken() throws Exception {
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
			.userSeq(1L)
			.password(passwordEncoder.encode("1234"))
			.userId("test@gmail.com")
			.username("repo1")
			.providerType(ProviderType.LOCAL)
			.roleType(RoleType.USER)
			.createdAt(now)
			.modifiedAt(now)
			.build();

		userRepository.save(user);
		MvcResult result = mvc.perform(post("http://localhost:8080/api/v1/auth/login")
			.content("{"
				+ "  \"id\" : \"test@gmail.com\", "
				+ "  \"password\": \"1234\" "
				+ "}")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn();

		String resultTostring = result.getResponse().getContentAsString();
		JacksonJsonParser jsonParser = new JacksonJsonParser();
		String token = jsonParser.parseMap(resultTostring).get("data").toString();
		return token;
	}

	@Test
	@DisplayName("전체 팀 조회")
	void getTeams() throws Exception {

		ResultActions actions = mvc.perform(
			get(url)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.list[0].teamName").value("team"));
	}

	@Test
	@DisplayName("단건 팀 조회")
	void getTeam() throws Exception {

		ResultActions actions = mvc.perform(
			get(url + "/{teamId}", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.teamName").value("team"));
	}

	@Test
	@DisplayName("사용자별 팀 조회")
	void getTeamByUser() throws Exception {
		ResultActions actions = mvc.perform(
			get(url)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.list[0].teamName").value("team"));
	}

	@Test
	@DisplayName("팀별 사용자 조회")
	void findUserByTeam() throws Exception {
		ResultActions actions = mvc.perform(
			get(url + "/{teamId}/user", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.list[0].userId").value("test@gmail.com"));

	}

	@Test
	void createTeam() throws Exception {
		TeamRequest teamRequest = TeamRequest.builder()
			.teamName("team")
			.teamDescription("team")
			.participantName(new ArrayList<>())
			.build();

		ResultActions actions = mvc.perform(
			post(url)
				.content(toJson(teamRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.teamName").value("team"));

	}

	@Test
	void updateTeam() throws Exception {
		TeamRequest teamRequest = TeamRequest.builder()
			.teamName("team")
			.teamDescription("team1")
			.participantName(new ArrayList<>())
			.build();

		ResultActions actions = mvc.perform(
			put(url + "/{teamId}", 1L)
				.content(toJson(teamRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.teamDescription").value("team1"));
	}

	private <T> String toJson(T data) throws JsonProcessingException {
		return objectMapper.writeValueAsString(data);
	}

	@Test
	void deleteTeam() throws Exception {
		ResultActions actions = mvc.perform(
			delete(url + "/{teamId}", 1L)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful());
	}
}