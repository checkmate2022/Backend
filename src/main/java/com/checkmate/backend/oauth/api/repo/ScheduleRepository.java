package com.checkmate.backend.oauth.api.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.oauth.api.entity.Participant;
import com.checkmate.backend.oauth.api.entity.Schedule;
import com.checkmate.backend.oauth.api.entity.Team;
import com.checkmate.backend.oauth.api.entity.User;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	Schedule findByScheduleSeq(long scheduleId);
	List<Schedule> findAllByTeam(Optional<Team> team);
}
