package com.checkmate.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.team.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

	Team findByTeamSeq(Long teamId);

}
