package com.checkmate.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkmate.backend.entity.meeting.Meeting;
import com.checkmate.backend.entity.team.Team;

public interface MeetingRepository extends JpaRepository<Meeting, String> {

	@Query("select c from Meeting c where c.id=:id")
	Optional<Meeting> findById(@Param("id") String id);

	List<Meeting> findMeetingsByTeam(Team team);

}
