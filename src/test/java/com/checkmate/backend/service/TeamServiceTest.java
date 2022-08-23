package com.checkmate.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkmate.backend.advice.exception.MisMatchException;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.team.TeamParticipant;
import com.checkmate.backend.entity.team.TeamRoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.request.TeamRequest;
import com.checkmate.backend.model.response.ParticipantResponse;
import com.checkmate.backend.model.response.TeamResponse;
import com.checkmate.backend.repo.TeamParticipantRepository;
import com.checkmate.backend.repo.TeamRepository;
import com.checkmate.backend.repo.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class TeamServiceTest {
	TeamService teamService;
	@Mock
	UserRepository userRepository;

	@Mock
	TeamRepository teamRepository;

	@Mock
	TeamParticipantRepository teamParticipantRepository;

	@BeforeEach
	void setUp() {
		this.teamService = new TeamService(this.teamRepository, this.userRepository, this.teamParticipantRepository);
	}

	@Test
	void findTeams() {
		List<Team> mockTeams = new ArrayList<>();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		mockTeams.add(mockTeam);
		given(teamRepository.findAll()).willReturn(mockTeams);

		List<Team> teams = teamService.findTeams();

		assertEquals(teams, mockTeams);
	}

	@Test
	void findOne() {
		Long teamId = 1L;
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();

		given(teamRepository.findById(teamId)).willReturn(java.util.Optional.ofNullable(mockTeam));

		Team team = teamService.findOne(teamId);

		assertEquals(team, mockTeam);
	}

	@Test
	void findTeamByUser() {
		LocalDateTime now = LocalDateTime.now();
		User mockUser = new User("repo1", "repo1", "repo1", ProviderType.LOCAL, RoleType.USER, now, now);
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("team")
			.teamDescription("team")
			.participants(new ArrayList<>()).build();
		TeamParticipant teamParticipant = new TeamParticipant(mockUser, mockTeam, TeamRoleType.MEMBER);

		mockTeam.addParticipant(teamParticipant);

		List<TeamParticipant> mockParticipants = new ArrayList<>();
		mockParticipants.add(teamParticipant);

		given(teamParticipantRepository.findAllByUser(mockUser)).willReturn(mockParticipants);
		given(teamRepository.findById(any())).willReturn(Optional.ofNullable(mockTeam));

		List<String> participants = new ArrayList<>();
		participants.add("repo1");

		TeamResponse teamResponse = TeamResponse.builder()
			.teamSeq(1L)
			.teamName("team")
			.teamDescription("team")
			.participants(participants)
			.build();

		List<TeamResponse> findTeamByUser = teamService.findTeamByUser(mockUser);

		assertEquals(findTeamByUser.get(0).getTeamSeq(), teamResponse.getTeamSeq());
		assertEquals(findTeamByUser.get(0).getTeamName(), teamResponse.getTeamName());
		assertEquals(findTeamByUser.get(0).getParticipants(), teamResponse.getParticipants());

	}

	@Test
	void findUserByTeam() {
		LocalDateTime now = LocalDateTime.now();
		User mockUser = User.builder()
			.userSeq(1L)
			.avatar(new ArrayList<>())
			.userId("test")
			.username("test").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("team")
			.teamDescription("team")
			.participants(new ArrayList<>()).build();
		TeamParticipant teamParticipant = new TeamParticipant(mockUser, mockTeam, TeamRoleType.MEMBER);

		mockTeam.addParticipant(teamParticipant);

		List<TeamParticipant> mockParticipants = new ArrayList<>();
		mockParticipants.add(teamParticipant);

		given(teamParticipantRepository.findAllByTeam(mockTeam)).willReturn(mockParticipants);
		given(teamRepository.findById(any())).willReturn(Optional.ofNullable(mockTeam));
		given(userRepository.findById(any())).willReturn(Optional.ofNullable(mockUser));

		ParticipantResponse participantResponse = ParticipantResponse.builder()
			.userSeq(1L)
			.userId("test")
			.teamRoleType(TeamRoleType.MEMBER)
			.username("test")
			.build();

		List<ParticipantResponse> responses = teamService.findUserByTeam(1L);

		assertEquals(responses.get(0).getUserId(), participantResponse.getUserId());
		assertEquals(responses.get(0).getUsername(), participantResponse.getUsername());
		assertEquals(responses.get(0).getTeamRoleType(), participantResponse.getTeamRoleType());
	}

	@Test
	void make() {

		TeamRequest teamRequest = TeamRequest.builder()
			.teamName("team")
			.teamDescription("team")
			.participantName(new ArrayList<>())
			.build();

		User mockUser = User.builder()
			.userSeq(1L)
			.avatar(new ArrayList<>())
			.userId("test")
			.team(new ArrayList<>())
			.username("test").build();

		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("team")
			.teamDescription("team")
			.participants(new ArrayList<>()).build();

		TeamParticipant teamParticipant = new TeamParticipant(mockUser, mockTeam, TeamRoleType.MEMBER);

		given(teamRepository.save(any())).willReturn(mockTeam);
		given(teamParticipantRepository.save(any())).willReturn(teamParticipant);

		Team team = teamService.make(teamRequest, mockUser);

		assertEquals(mockUser, team.getParticipants().get(0).getUser());
		assertEquals(1L, team.getTeamSeq());
		assertEquals("team", team.getTeamName());

	}

	@Test
	void update() {
		TeamRequest teamRequest = TeamRequest.builder()
			.teamName("team1")
			.teamDescription("team1")
			.participantName(new ArrayList<>())
			.build();
		User mockUser = User.builder()
			.userSeq(1L)
			.avatar(new ArrayList<>())
			.userId("test")
			.team(new ArrayList<>())
			.username("test").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("team")
			.teamDescription("team")
			.user(mockUser)
			.participants(new ArrayList<>()).build();
		TeamParticipant teamParticipant = new TeamParticipant(mockUser, mockTeam, TeamRoleType.MEMBER);
		given(teamRepository.findById(1L)).willReturn(Optional.ofNullable(mockTeam));
		given(teamRepository.save(any())).willReturn(mockTeam);
		given(teamParticipantRepository.save(any())).willReturn(teamParticipant);

		Team team = teamService.update(1L, teamRequest, mockUser);

		assertEquals(1L, team.getTeamSeq());
		assertEquals("team1", team.getTeamName());
		assertEquals("team1", team.getTeamDescription());

	}

	@Test
	@DisplayName("팀수정시 리더가 아닌 다른 사람이 수정시 MisMatchException")
	void notLeaderThrowMisMatchException() {
		TeamRequest teamRequest = TeamRequest.builder()
			.teamName("team1")
			.teamDescription("team1")
			.participantName(new ArrayList<>())
			.build();
		User mockUser = User.builder()
			.userSeq(1L)
			.avatar(new ArrayList<>())
			.userId("test")
			.team(new ArrayList<>())
			.username("test").build();
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("team")
			.teamDescription("team")
			.user(mock(User.class))
			.participants(new ArrayList<>()).build();

		when(teamRepository.findById(1L)).thenReturn(Optional.ofNullable(mockTeam));

		assertThrows(MisMatchException.class, () -> teamService.update(1L, teamRequest, mockUser));
	}

}