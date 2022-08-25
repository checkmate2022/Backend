package com.checkmate.backend.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.model.dto.TeamDto;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ChannelRepositoryTest {
	Team save;
	@Autowired
	ChannelRepository channelRepository;
	@Autowired
	TeamRepository teamRepository;

	@BeforeEach
	void 채널생성() {
		TeamDto teamDto = new TeamDto("팀이름", "팀설명");
		Team team = new Team(teamDto);

		save = teamRepository.save(team);

		Channel channel1 = new Channel("channel1", save);
		Channel savedChannel1 = channelRepository.save(channel1);
		Channel channel2 = new Channel("channel2", save);
		Channel savedChannel2 = channelRepository.save(channel2);

		//then
		assertEquals(savedChannel1.getTeam(), save);
		assertEquals(savedChannel2.getChannelName(), "channel2");
	}

	@Test
	@DisplayName("팀안에 모든 채널 가져오기")
	void findAllByTeam() {
		List<Channel> channels = channelRepository.findAllByTeam(save);

		for (int i = 1; i < channels.size() + 1; i++) {
			assertEquals(channels.get(i - 1).getChannelName(), "channel" + i);
		}

	}
}