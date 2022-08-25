package com.checkmate.backend.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.team.TeamParticipant;
import com.checkmate.backend.entity.team.TeamRoleType;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.TeamDto;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class TeamParticipantRepositoryTest {

	Team save;
	User savedUser;
	TeamParticipant savedParticipant;
	@Autowired
	private TeamParticipantRepository teamParticipantRepository;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	@DisplayName("팀 참가자 저장")
	void Save() {
		//given
		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo1", "repo1", "repo1", ProviderType.LOCAL, RoleType.USER, now, now);
		TeamDto teamDto = new TeamDto("팀이름", "팀설명");
		Team team = new Team(teamDto);

		savedUser = userRepository.save(user);
		save = teamRepository.save(team);
		save.setUser(savedUser);

		TeamParticipant participant = new TeamParticipant(savedUser, save, TeamRoleType.MEMBER);
		savedParticipant = teamParticipantRepository.save(participant);

		assertEquals(savedParticipant.getUser(), participant.getUser());
	}

	@AfterEach
	void delete() {
		userRepository.delete(savedUser);
		teamRepository.delete(save);
		teamParticipantRepository.delete(savedParticipant);
	}

	@Test
	@DisplayName("팀 삭제 -> 팀참가자 모두 삭제")
	void deleteAllByTeam() {
		teamParticipantRepository.deleteAllByTeam(save);
	}

	@Test
	@DisplayName("사용자별 팀 조회")
	void findAllByUser() {
		List<TeamParticipant> participants = teamParticipantRepository.findAllByUser(savedUser);

		assertEquals(participants.get(0), savedParticipant);
	}

	@Test
	@DisplayName("팀별 팀참가자 조회")
	void findAllByTeam() {
		List<TeamParticipant> participants = teamParticipantRepository.findAllByTeam(save);

		assertEquals(participants.get(0), savedParticipant);
	}
}