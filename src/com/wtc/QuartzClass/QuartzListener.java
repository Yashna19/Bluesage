package com.wtc.QuartzClass;

import static org.quartz.JobBuilder.newJob;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.wtc.Database.DatabaseConnection;

public class QuartzListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContext) {
		System.out.println("Context Initialized");
		try {

			DatabaseConnection dbcon = new DatabaseConnection();
			List<String> list = dbcon.getDetails();
			for (String listValue : list) {
				String scriptValue = listValue.trim();
				Scheduler scheduler = null;

				JobDetail job = newJob(ScheduledJob.class).withIdentity(scriptValue, scriptValue + "Group").build();
				Trigger trigger = TriggerBuilder.newTrigger().withIdentity(scriptValue + "Trigger", scriptValue + "Group")
								.withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * * * ?")).build();
				scheduler = new StdSchedulerFactory().getScheduler();
				scheduler.getContext().put(scriptValue + "Group", scriptValue);
				scheduler.start();
				scheduler.scheduleJob(job, trigger);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContext) {
		System.out.println("Context Destroyed");
	}
}