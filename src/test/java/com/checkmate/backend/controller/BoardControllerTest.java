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

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.BoardDto;
import com.checkmate.backend.model.dto.TeamDto;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("BoardControllerTest 테스트")
class BoardControllerTest {

	@Autowired
	MockMvc mvc;
	String url = "http://localhost:8080/api/v1/board";
	String token;
	Team savedTeam;
	Channel savedChannel;
	User savedUser;
	Board savedBoard;
	@Autowired
	private WebApplicationContext ctx;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private BoardRepository boardRepository;

	@BeforeEach
	void setUp() throws Exception {
		mvc = MockMvcBuilders
			.webAppContextSetup(ctx)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();

		token = getToken();
		TeamDto teamDto = new TeamDto("팀이름", "팀설명");

		Team team = new Team(teamDto);
		savedTeam = teamRepository.save(team);
		Channel channel1 = new Channel("channel1", savedTeam);
		savedChannel = channelRepository.save(channel1);
		Board board = new Board("제목", "내용", savedChannel, savedTeam.getTeamSeq(), savedUser.getUserSeq());
		savedBoard = boardRepository.save(board);
	}

	private String getToken() throws Exception {
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
			.password(passwordEncoder.encode("1234"))
			.userId("board")
			.username("repo1")
			.providerType(ProviderType.LOCAL)
			.roleType(RoleType.USER)
			.createdAt(now)
			.modifiedAt(now)
			.build();

		savedUser = userRepository.save(user);
		MvcResult result = mvc.perform(post("http://localhost:8080/api/v1/auth/login")
			.content("{"
				+ "  \"id\" : \"board\", "
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
	void findById() throws Exception {
		ResultActions actions = mvc.perform(
			get(url + "/{boardId}", savedBoard.getBoardSeq())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.title").value("제목"));
	}

	@Test
	void getBoardsByChannel() throws Exception {
		ResultActions actions = mvc.perform(
			get(url + "/channel/{channelId}", savedChannel.getChannelSeq())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.list[0].title").value("제목"));

	}

	// @Test
	// void getBoardsByTeam() throws Exception {
	// 	ResultActions actions = mvc.perform(
	// 		get(url)
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.accept(MediaType.APPLICATION_JSON)
	// 			.param("teamId", String.valueOf(savedTeam.getTeamSeq()))
	// 			.characterEncoding("UFT-8"));
	//
	// 	actions.andExpect(status().is2xxSuccessful())
	// 		.andExpect(jsonPath("$.list[0].title").value("제목"));
	// }

	@Test
	void create() throws Exception {
		BoardDto boardDto = new BoardDto("create", "create");
		ResultActions actions = mvc.perform(
			post(url)
				.content(toJson(boardDto))
				.param("channelId", String.valueOf(savedChannel.getChannelSeq()))
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.title").value("create"));

	}

	@Test
	void modify() throws Exception {
		BoardDto boardDto = new BoardDto("create1", "create1");
		ResultActions actions = mvc.perform(
			put(url + "/{boardId}", savedBoard.getBoardSeq())
				.content(toJson(boardDto))
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.title").value("create1"));
	}

	@Test
	void deleteBoard() throws Exception {
		ResultActions actions = mvc.perform(
			delete(url + "/{boardId}", savedBoard.getBoardSeq())
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful());

	}

	@AfterEach
	void deleteAll() {
		boardRepository.deleteAll();
		channelRepository.deleteAll();
		teamRepository.deleteAll();
		userRepository.deleteAll();
	}

	private <T> String toJson(T data) throws JsonProcessingException {
		return objectMapper.writeValueAsString(data);
	}
}