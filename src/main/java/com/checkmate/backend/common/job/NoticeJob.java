package com.checkmate.backend.common.job;

import java.io.IOException;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.checkmate.backend.service.FCMService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoticeJob extends QuartzJobBean {
	private final FCMService fcmService;
	private static final Logger logger = LoggerFactory.getLogger(NoticeJob.class);

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
		String title = jobDataMap.getString("title");
		String body = jobDataMap.getString("body");
		String userId = jobDataMap.getString("userId");

		try {
			fcmService.sendMessageTo(userId,title, body);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}