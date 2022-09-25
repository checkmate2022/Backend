package com.checkmate.backend.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.meeting.MeetingType;
import com.checkmate.backend.entity.oauth.ProviderType;
import com.checkmate.backend.entity.oauth.RoleType;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;
import com.checkmate.backend.model.dto.TeamDto;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MeetingRepositoryTest {
	User savedUser;
	Team savedTeam;
	Meeting savedMeeting;
	@Autowired
	private MeetingRepository meetingRepository;
	@Autowired
	private TeamRepository teamRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	@DisplayName("미팅 저장")
	void setUp() {
		//given
		LocalDateTime now = LocalDateTime.now();
		User user = new User("repo1", "repo1", "repo1", ProviderType.LOCAL, RoleType.USER, now, now);
		TeamDto teamDto = new TeamDto("팀이름", "팀설명");
		Team team = new Team(teamDto);

		savedUser = userRepository.save(user);
		savedTeam = teamRepository.save(team);
		savedTeam.setUser(savedUser);
		Meeting meeting = new Meeting("랜덤id", MeetingType.PLAN, user, team, now);
		savedMeeting = meetingRepository.save(meeting);

		assertEquals(savedMeeting.getHost(), savedUser);
		assertEquals(savedMeeting.getId(), "랜덤id");
	}

	@Test
	void findById() {
		Optional<Meeting> meetingOptional = meetingRepository.findById("랜덤id");
		assertEquals(meetingOptional.get().getId(), "랜덤id");
		assertEquals(meetingOptional.get().getHost(), savedUser);
	}

	@Test
	void findMeetingsByTeam() {
		List<Meeting> meetings = meetingRepository.findMeetingsByTeam(savedTeam);

		assertEquals(meetings.get(0), savedMeeting);
	}

	@AfterEach
	void delete() {
		userRepository.delete(savedUser);
		teamRepository.delete(savedTeam);
		meetingRepository.delete(savedMeeting);
	}
}