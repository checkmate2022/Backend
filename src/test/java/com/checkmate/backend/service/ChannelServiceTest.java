package com.checkmate.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.TeamRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChannelServiceTest 테스트")
class ChannelServiceTest {
	ChannelService channelService;
	@Mock
	TeamRepository teamRepository;
	@Mock
	ChannelRepository channelRepository;

	@BeforeEach
	void setUp() {
		this.channelService = new ChannelService(this.channelRepository, this.teamRepository);
	}

	@Test
	void findAllByTeam() {
		long teamId = 1L;
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		List<Channel> channels = new ArrayList<>();
		Channel channel1 = new Channel("channel1", mockTeam);
		Channel channel2 = new Channel("channel2", mockTeam);
		channels.add(channel1);
		channels.add(channel2);

		given(teamRepository.findById(teamId)).willReturn(java.util.Optional.ofNullable(mockTeam));
		given(channelRepository.findAllByTeam(mockTeam)).willReturn(channels);

		List<Channel> channelList = channelService.findAllByTeam(teamId);

		for (int i = 0; i < channelList.size(); i++) {
			assertEquals(channelList.get(i).getChannelName(), channels.get(i).getChannelName());
		}
	}

	@Test
	void create() {
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);

		given(teamRepository.findById(1L)).willReturn(java.util.Optional.ofNullable(mockTeam));
		given(channelRepository.save(any())).willReturn(mockChannel);

		Channel channel = channelService.create(1L, "채널");

		assertEquals(mockTeam, channel.getTeam());
		assertEquals("채널", channel.getChannelName());

	}

	@Test
	void modify() {
		Team mockTeam = Team.builder()
			.teamSeq(1L)
			.teamName("test")
			.teamDescription("test")
			.build();
		Channel mockChannel = new Channel("채널", mockTeam);
		given(channelRepository.findById(1L)).willReturn(java.util.Optional.of(mockChannel));

		Channel channel = channelService.modify(1L, "수정");

		assertEquals(channel.getChannelName(), "수정");
	}
}