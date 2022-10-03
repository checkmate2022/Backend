package com.checkmate.backend.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.checkmate.backend.entity.schedule.Schedule;
import com.checkmate.backend.entity.team.Team;
import com.checkmate.backend.entity.user.User;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	Schedule findByScheduleSeq(long scheduleId);

	List<Schedule> findAllByTeam(Optional<Team> team);

	@Query(
		"select s from Schedule s where s.user=:user and function('YEAR',s.scheduleStartdate)<=function('YEAR',:time)"
			+ "and function('YEAR',s.scheduleEnddate)>=function('YEAR',:time) and function('MONTH',s.scheduleStartdate)<=function('MONTH',:time)"
			+ "and function('MONTH',s.scheduleEnddate)>=function('MONTH',:time) and function('DAY',s.scheduleStartdate)<=function('DAY',:time)"
			+ "and function('DAY',s.scheduleEnddate)>=function('DAY',:time)")
	List<Schedule> findSchedulesByScheduleStartdateBetweenAndScheduleEnddate(LocalDateTime time, User user);

	@Query(
		"select s from Schedule s where s.user=:user and s.scheduleStartdate>CURRENT_DATE and s.notificationTime=0")
	List<Schedule> findSchedulesByNotificationTimeAndAndScheduleStartdateAndUser(User user);

	// @Query("select s from Schedule s where s.user=:user and s.scheduleStartdate<=:time or s.scheduleEnddate>=:time")
	// List<Schedule> findSchedulesByScheduleStartdateBetweenAndScheduleEnddate(LocalDateTime time, User user);
}
