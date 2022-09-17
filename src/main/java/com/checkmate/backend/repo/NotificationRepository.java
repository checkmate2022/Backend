package com.checkmate.backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.checkmate.backend.entity.schedule.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query(
		"select n from Notification n where function('YEAR',n.notificationDate)=function('YEAR',current_time) "
			+ "and function('MONTH',n.notificationDate)=function('MONTH',current_time)"
			+ "and function('DAY',n.notificationDate)=function('DAY',current_time) and n.isNotice=FALSE")
	List<Notification> findNotificationsByNotificationDate();

	@Query("select n from Notification n where n.userId=:userId and n.isNotice=TRUE")
	List<Notification> findNotificationsByUserIdAndIsNoticeTrue(String userId);

}
