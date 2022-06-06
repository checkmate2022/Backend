package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.team.TeamParticipant;
import com.checkmate.backend.entity.user.User;

public interface TeamParticipantRepository extends JpaRepository<TeamParticipant, Long> {

	@Transactional
	@Modifying
	@Query("delete from TeamParticipant p where p.team=:team")
	void deleteAllByTeam(@Param("team") Team team);

	List<TeamParticipant> findAllByUser(User user);

}
