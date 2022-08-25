package com.checkmate.backend.service;

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
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("팀이 존재하지 않습니다.")
		);
		return channelRepository.findAllByTeam(team);
	}

	//채널 생성
	public Channel create(long teamId, String channelName) {
		Team team = teamRepository.findById(teamId).orElseThrow(
			() -> new IllegalArgumentException("팀이 존재하지 않습니다.")
		);
		Channel channel = new Channel(channelName, team);
		return channelRepository.save(channel);
	}

	//채널 삭제
	public void delete(long channelSeq) {
		channelRepository.deleteById(channelSeq);
	}

	//채널 수정
	public Channel modify(long channelSeq, String newName) {
		Channel modifiedChannel = channelRepository.findById(channelSeq).orElseThrow(
			() -> new IllegalArgumentException("팀이 존재하지 않습니다.")
		);
		modifiedChannel.setChannelName(newName);
		return modifiedChannel;
	}
}