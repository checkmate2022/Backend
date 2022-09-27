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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.checkmate.backend.entity.avatar.AvatarType;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("AvatarController 테스트")
class AvatarControllerTest {
	@Autowired
	MockMvc mvc;
	String url = "http://localhost:8080/api/v1/avatar";
	@Autowired
	private WebApplicationContext ctx;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ObjectMapper mapper;

	@BeforeEach
	void setUp() throws Exception {
		mvc = MockMvcBuilders
			.webAppContextSetup(ctx)
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
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
	@DisplayName("전체 캐릭터 조회")
	void getAvatars() throws Exception {
		ResultActions actions = mvc.perform(
			get(url)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UFT-8"));
		actions
			.andExpect(status().is2xxSuccessful());
		// .andExpect(jsonPath("$.list[0].username").value("test@gmail.com"));

	}

	@Test
	@DisplayName("캐릭터 등록")
	void createAatar() throws Exception {
		MockMultipartFile originfile
			= new MockMultipartFile(
			"file",
			"hello.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"Hello, World!".getBytes()
		);

		MockMultipartFile createdfile
			= new MockMultipartFile(
			"file",
			"hello.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"Hello, World!".getBytes()
		);
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("avatarName", "avatarName");
		info.add("avatarDescription", "avatarDescription");
		info.add("avatarStyle", "cartoon");
		info.add("avatarStyleId", "1L");

		MultipartHttpServletRequest request = (MultipartHttpServletRequest)
			mvc.perform(multipart(url)
				.file("originfile", originfile.getBytes())
				.file("createdfile", createdfile.getBytes())
				.param("avatarName", "avatarName")
				.param("avatarDescription", "avatarDescription")
				.param("avatarStyle", AvatarType.cartoon.getDisplayName())
				.param("avatarStyleId", "1L"))
				// .andExpect(status().isOk())
				.andReturn().getRequest();

	}

}