package com.checkmate.backend.common.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.checkmate.backend.common.job.InitSchedule;

public class ServletListener implements ServletContextListener {

	private InitSchedule init = new InitSchedule();

	public ServletListener() {
		// TODO Auto-generated constructor stub
	}

	public void contextDestroyed(ServletContextEvent arg0)  {
		init.end();
	}

	public void contextInitialized(ServletContextEvent arg0)  {
		init.start();
	}

}