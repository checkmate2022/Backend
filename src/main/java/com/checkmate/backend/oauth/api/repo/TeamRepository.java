package com.checkmate.backend.oauth.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.oauth.api.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

	Team findByTeamSeq(Long teamId);

}
