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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.checkmate.backend.entity.board.Board;
import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.comment.Comment;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.TeamDto;
import com.checkmate.backend.repo.BoardRepository;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.CommentRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("CommentControllerTest 테스트")
class CommentControllerTest {
	@Autowired
	MockMvc mvc;
	String url = "http://localhost:8080/api/v1/comment";
	String token;
	Team savedTeam;
	Channel savedChannel;
	User savedUser;
	Board savedBoard;
	Comment savedComment;
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
	@Autowired
	private CommentRepository commentRepository;

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
		Comment comment = new Comment("내용", savedBoard, savedUser.getUserSeq(), "emoticon");
		savedComment = commentRepository.save(comment);
	}

	private String getToken() throws Exception {
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
			.password(passwordEncoder.encode("1234"))
			.userId("test")
			.username("repo1")
			.providerType(ProviderType.LOCAL)
			.roleType(RoleType.USER)
			.createdAt(now)
			.modifiedAt(now)
			.build();

		savedUser = userRepository.save(user);
		MvcResult result = mvc.perform(post("http://localhost:8080/api/v1/auth/login")
			.content("{"
				+ "  \"id\" : \"test\", "
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
	void comments() throws Exception {
		ResultActions actions = mvc.perform(
			get(url + "/{boardId}", savedBoard.getBoardSeq())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.list[0].content").value("내용"));
	}

	@Test
	void create() throws Exception {
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("boardId", String.valueOf(savedBoard.getBoardSeq()));
		info.add("content", "create");
		info.add("emoticonUrl", "emoticon");
		ResultActions actions = mvc.perform(
			post(url)
				.params(info)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.content").value("create"));
	}

	@Test
	void update() throws Exception {
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("commentSeq", String.valueOf(savedComment.getCommentSeq()));
		info.add("content", "update");
		info.add("emoticonUrl", "emoticon");
		ResultActions actions = mvc.perform(
			put(url)
				.params(info)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data.content").value("update"));
	}

	@Test
	void deleteComment() throws Exception {
		ResultActions actions = mvc.perform(
			delete(url)
				.param("commentSeq", String.valueOf(savedComment.getCommentSeq()))
				.header("Authorization", "Bearer " + token));

		actions.andExpect(status().is2xxSuccessful());
	}

	@AfterEach
	void deleteAll() {
		commentRepository.deleteAll();
		boardRepository.deleteAll();
		channelRepository.deleteAll();
		teamRepository.deleteAll();
		userRepository.deleteAll();
	}
}