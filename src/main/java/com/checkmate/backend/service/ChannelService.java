package com.checkmate.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.repo.ChannelRepository;
import com.checkmate.backend.repo.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {

	private final ChannelRepository channelRepository;
	private final TeamRepository teamRepository;

	@Transactional(readOnly = true)
	public List<Channel> findAllByTeam(long teamId) {
		Team team = teamRepository.findById(teamId).orElseThrow();
		return channelRepository.findAllByTeam(team);
	}

	//채널 생성
	public Channel create(long teamId, String channelName) {
		Team team = teamRepository.findById(teamId).orElseThrow();
		LocalDateTime now = LocalDateTime.now();
		Channel channel = new Channel(channelName, team, now, now);
		return channelRepository.save(channel);
	}

	//채널 삭제
	public void delete(long channelSeq) {
		channelRepository.deleteById(channelSeq);
	}

	//채널 수정
	public Channel modify(long channelSeq, String newName) {
		Channel modifiedChannel = channelRepository.findById(channelSeq).orElseThrow();
		modifiedChannel.setChannelName(newName);
		return modifiedChannel;
	}
}