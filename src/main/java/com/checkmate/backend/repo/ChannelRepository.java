package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.channel.Channel;
import com.checkmate.backend.entity.team.Team;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

	List<Channel> findAllByTeam(Team team);
}
