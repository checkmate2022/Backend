package com.checkmate.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

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
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("UserController 테스트")
class UserControllerTest {

	@Autowired
	MockMvc mvc;
	String url = "http://localhost:8080/api/v1/users";
	@Autowired
	private WebApplicationContext ctx;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
			.webAppContextSetup(ctx)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
	}

	@Test
	void getUser() throws Exception {
		String token = getToken();

		ResultActions actions = mvc.perform(
			get(url)
				.header("Authorization", "Bearer " + token))
			.andDo(print());

		// actions
		// 	.andExpect(status().is2xxSuccessful())
		// 	.andExpect(jsonPath("$.data.userId").value("test@gmail.com"));

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
	@DisplayName("회원가입 테스트")
	void join() throws Exception {
		ResultActions actions = mvc.perform(
			post(url + "/join")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content("{"
					+ "  \"userId\" : \"test1@gmail.com\", "
					+ "  \"username\" : \"Test User\", "
					+ "  \"password\": \"1234\" "
					+ "}"));

		actions
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("data.userId").value("test1@gmail.com"));

	}

	@Test
	void checkName() throws Exception {
		String username = "test";
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("username", username);

		ResultActions actions = mvc.perform(
			post(url + "/check/name")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8")
				.params(info));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data").value(0));
	}

	@Test
	void checkId() throws Exception {
		String userId = "test";
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("userId", userId);

		ResultActions actions = mvc.perform(
			post(url + "/check/userId")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8")
				.params(info));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data").value(0));
	}

	@Test
	void checkPassword() throws Exception {
		String password = "test";
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("password", password);

		String token = getToken();

		ResultActions actions = mvc.perform(
			post(url + "/check/password")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8")
				.params(info));

		actions.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.data").value(false));
	}
}