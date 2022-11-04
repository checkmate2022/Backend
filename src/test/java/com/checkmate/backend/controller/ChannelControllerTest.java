package com.checkmate.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.request.TeamRequest;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("ChannelControllerTest 테스트")
class ChannelControllerTest {
	@Autowired
	MockMvc mvc;
	String token;
	String url = "http://localhost:8080/api/v1/channel";
	Integer teamId;
	Integer channelId;
	@Autowired
	private WebApplicationContext ctx;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

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

		MvcResult result = mvc.perform(
			post("http://localhost:8080/api/v1/team")
				.content(toJson(teamRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)).andReturn();

		String resultTostring = result.getResponse().getContentAsString();
		teamId = JsonPath.parse(resultTostring).read("$.data.teamSeq");

		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("teamId", String.valueOf(teamId));
		info.add("channelName", "channel");
		MvcResult actions = mvc.perform(
			post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.param("teamId", String.valueOf(teamId))
				.param("channelName", "channel")).andReturn();

		String channelTostring = actions.getResponse().getContentAsString();
		channelId = JsonPath.parse(channelTostring).read("$.data.channelSeq");

	}

	@AfterEach
	void deleteAll() {
		channelRepository.deleteAll();
		teamRepository.deleteById(Long.valueOf(teamId));
		userRepository.deleteAll();
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
	void getChannelsByTeam() throws Exception {
		ResultActions actions = mvc.perform(
			get(url + "/team/{teamId}", teamId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.list[0].channelName").value("channel"));
	}

	@Test
	void create() throws Exception {
		ResultActions actions = mvc.perform(
			post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token)
				.param("teamId", String.valueOf(teamId))
				.param("channelName", "channel1"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.channelName").value("channel1"));
	}

	@Test
	void deleteChannel() throws Exception {
		ResultActions actions = mvc.perform(
			delete(url + "/{channelId}", channelId));

		actions.andExpect(status().is2xxSuccessful());
	}

	@Test
	void updateChannel() throws Exception {
		ResultActions actions = mvc.perform(
			put(url + "/{channelId}", channelId)
				.contentType(MediaType.APPLICATION_JSON)
				.param("channelName", "channel1")
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.channelName").value("channel1"));

	}

	private <T> String toJson(T data) throws JsonProcessingException {
		return objectMapper.writeValueAsString(data);
	}
}