package com.checkmate.backend.oauth.api.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.oauth.api.entity.Participant;
import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.Team;
import com.checkmate.backend.oauth.api.entity.TeamParticipant;
import com.checkmate.backend.oauth.api.entity.User;

public interface TeamParticipantRepository extends JpaRepository<TeamParticipant, Long> {

	@Transactional
	@Modifying
	@Query("delete from TeamParticipant p where p.team=:team")
	void deleteAllByTeam(@Param("team") Team team);

	List<TeamParticipant> findAllByUser(User user);

}
