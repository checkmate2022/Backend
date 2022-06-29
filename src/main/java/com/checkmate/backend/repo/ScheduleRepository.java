package com.checkmate.backend.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.team.Team;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	Schedule findByScheduleSeq(long scheduleId);

	List<Schedule> findAllByTeam(Optional<Team> team);
}
