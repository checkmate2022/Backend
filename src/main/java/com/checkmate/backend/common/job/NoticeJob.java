package com.checkmate.backend.common.job;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.checkmate.backend.entity.schedule.Notification;
import com.checkmate.backend.repo.NotificationRepository;
import com.checkmate.backend.service.FCMService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoticeJob implements Job {

	private final NotificationRepository notificationRepository;
	private final FCMService fcmService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LocalDateTime now=LocalDateTime.now();
		List<Notification> nowNotice = new ArrayList<Notification>();


		System.out.println(now.getHour() + " : " + now.getMinute() + "execute 함수를 실행합니다.");

		//현재날짜와 시간을 생성한다.(초단위 제외)

		//DB에서  알림 여부가 N인 사용자 이메일, 스케줄 타이틀, 알림시간을 가져온다.
		List<Notification> doseNoticeList = notificationRepository.findNotificationsByNotificationDate();


		if(doseNoticeList.size() == 0) return;

		for (Notification notice : doseNoticeList) {
			LocalDateTime noticeTime = notice.getNotificationDate();
			System.out.println("db에서 가져온 알림 : " + notice.getTitle());
			System.out.println("현재 시 : " + now.getHour());
			System.out.println("현재 분 : " + now.getMinute());
			if(noticeTime.getHour() == now.getHour() && noticeTime.getMinute() == now.getHour()) {
				System.out.println("지금 보낼 알림 : " + notice.getTitle());
				System.out.println("알림 시간입니다!!!");
				nowNotice.add(notice);
				notificationRepository.delete(notice);
			}
		}


		//알림시간이 현재시간과 동일하면 해당 이메일로 메일을 보낸다.
		//해당 알림을 Y로 바꾼다.
		for (Notification notice : nowNotice) {
			System.out.println("전송할 알림 : " + notice.getTitle());
			try {
				fcmService.sendMessageTo(notice.getUserId(),notice.getTitle(),notice.getBody());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}