package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.channel.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
