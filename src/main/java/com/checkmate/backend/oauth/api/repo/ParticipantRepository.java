package com.checkmate.backend.oauth.api.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.checkmate.backend.oauth.api.entity.Participant;
import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.User;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

	@Transactional
	@Modifying
	@Query("delete from Participant p where p.schedule=:schedule")
	void deleteAllBySchedule(@Param("schedule") Schedule schedule);

	List<Participant> findAllByUser(User user);

}
