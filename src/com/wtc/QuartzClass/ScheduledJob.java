package com.wtc.QuartzClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;

import com.wtc.Database.DatabaseConnection;

public class ScheduledJob implements Job {
	static Logger log = Logger.getLogger(ScheduledJob.class.getName());
	private DatabaseConnection obj = new DatabaseConnection();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {

			SchedulerContext schedulerContext = null;
			schedulerContext = context.getScheduler().getContext();

			String scriptValue = (String) schedulerContext.get(((JobDetail) context.getJobDetail()).getKey().getGroup());

			DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			Date lstExecutionP = null;

			String currentDT = dtFormat.format(date);
			String currentDate = currentDT.substring(0, 10);
			Date currentDateP = dFormat.parse(currentDate);
			Date currentDTP = dtFormat.parse(currentDT);
			/*
			 *  Based scriptValue, currentdate get the record from DB
			 */
			HashMap<String, String> map = obj.getLastExecutionDate(scriptValue, currentDT);
			String lstExecDate = null;
			String startDate = null;
			String endDate = null;
			String scriptName = null;
			Date startDTP = null;
			Date endDTP = null;
			int scriptId = 0;
			int executionCount = 0;
			int lastExecutionCount = 0;
			String filePath = null;
			if (!map.isEmpty()) {
				startDate = map.get("startDate");
				endDate = map.get("endDate");
				lstExecDate = map.get("lastExecutionDate");
				scriptId = Integer.parseInt(map.get("id"));
				scriptName = map.get("scriptName");
				executionCount = Integer.parseInt(map.get("executionCount"));
				lastExecutionCount = Integer.parseInt(map.get("lastExecutionCount"));
				filePath = map.get("filePath");
				if (lstExecDate != null) {
					lstExecutionP = dFormat.parse(lstExecDate);
				}
				startDTP = dtFormat.parse(startDate);
				endDTP = dtFormat.parse(endDate);
			}

			if (filePath == null) {
				filePath = "";

			}

			System.out.println(scriptId + " " + scriptValue);
			if (lstExecutionP != null) {

				if (currentDateP.after(lstExecutionP)) {
					if ((currentDTP.after(startDTP)) && (currentDTP.before(endDTP))) {
						Boolean firstRecord = Boolean.TRUE;
						while (executionCount > 0) {

							if (firstRecord) {
								Date executedDate = new Date();
								obj.updateLastExecutionDate(scriptValue, dFormat.format(executedDate).toString(), executionCount, scriptId);
								firstRecord = Boolean.FALSE;
							}

							if (filePath == null) {
								filePath = "";

							}
							String urlString = "http://localhost:8080/DCU/ScriptRunProcess?comboValue="
											+ URLEncoder.encode(scriptValue, "UTF-8") + "&filePath=" + URLEncoder.encode(filePath, "UTF-8")
											+ "&scriptName=" + URLEncoder.encode(scriptName, "UTF-8");

							URL serv = new URL(urlString);
							URLConnection sr = serv.openConnection();
							sr.connect();
							InputStream is = sr.getInputStream();
							InputStreamReader isr = new InputStreamReader(is);
							BufferedReader in = new BufferedReader(isr);

							String inputLine;
							while ((inputLine = in.readLine()) != null)
								System.out.println("Input line: " + inputLine);
							in.close();
							executionCount = executionCount - 1;
						}
					}
				} else if (currentDateP.getTime() == lstExecutionP.getTime()) {

					if (lastExecutionCount > 0 && (executionCount > lastExecutionCount)) {
						int remainigExeCount = executionCount - lastExecutionCount;
						Boolean firstRecord = Boolean.TRUE;

						while (remainigExeCount > 0) {

							if (firstRecord) {
								Date executedDate = new Date();
								obj.updateLastExecutionDate(scriptValue, dFormat.format(executedDate).toString(),
												(executionCount + lastExecutionCount), scriptId);
								firstRecord = Boolean.FALSE;
							}

							String urlString = "http://localhost:8080/DCU/ScriptRunProcess?comboValue="
											+ URLEncoder.encode(scriptValue, "UTF-8") + "&filePath=" + URLEncoder.encode(filePath, "UTF-8")
											+ "&scriptName=" + URLEncoder.encode(scriptName, "UTF-8");

							URL serv = new URL(urlString);
							URLConnection sr = serv.openConnection();
							sr.connect();
							InputStream is = sr.getInputStream();
							InputStreamReader isr = new InputStreamReader(is);
							BufferedReader in = new BufferedReader(isr);

							String inputLine;
							while ((inputLine = in.readLine()) != null)
								System.out.println("Input line: " + inputLine);
							in.close();
							remainigExeCount = remainigExeCount - 1;
						}

					}

				}

			} else {
				if (startDTP != null && endDTP != null) {
					if ((currentDTP.after(startDTP)) && (currentDTP.before(endDTP))) {
						Boolean firstRecord = Boolean.TRUE;
						while (executionCount > 0) {
							if (firstRecord) {
								Date executedDate = new Date();
								obj.updateLastExecutionDate(scriptValue, dFormat.format(executedDate).toString(), executionCount, scriptId);
								firstRecord = Boolean.FALSE;
							}

							String urlString = "http://localhost:8080/DCU/ScriptRunProcess?comboValue="
											+ URLEncoder.encode(scriptValue, "UTF-8") + "&filePath=" + URLEncoder.encode(filePath, "UTF-8")
											+ "&scriptName=" + URLEncoder.encode(scriptName, "UTF-8");

							URL serv = new URL(urlString);
							URLConnection sr = serv.openConnection();
							sr.connect();
							InputStream is = sr.getInputStream();
							InputStreamReader isr = new InputStreamReader(is);
							BufferedReader in = new BufferedReader(isr);

							String inputLine;
							while ((inputLine = in.readLine()) != null)
								System.out.println("Input line: " + inputLine);
							in.close();
							executionCount = executionCount - 1;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
