package com.checkmate.backend.oauth.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkmate.backend.oauth.api.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	Schedule findByScheduleSeq(long scheduleId);

}
