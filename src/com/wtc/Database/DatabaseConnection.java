package com.wtc.Database;

import static org.quartz.JobBuilder.newJob;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.wtc.QuartzClass.ScheduledJob;
import com.wtc.globalAccelerators.Utils;

public class DatabaseConnection extends Utils{
		private Connection conImpportal;
//	public Properties properties;
	public boolean status = false;
	public ClassLoader loader = Thread.currentThread().getContextClassLoader();

	/*
	 * method to get connection object
	 */
	public Connection getConnection() {

		Connection conImpportal = null;
		try {
			Utils.readPropertiesFile();
			String sqlUrl = properties.getProperty("local.db.read.url");
			String sqlUsername = properties.getProperty("local.db.read.username");
			String sqlPassword = properties.getProperty("local.db.read.password");

			System.out.println(sqlUrl);

			String driver = properties.getProperty("local.db.read.driver");
			Class.forName(driver);
			conImpportal = DriverManager.getConnection(sqlUrl, sqlUsername, sqlPassword);
			if (conImpportal != null) {
				System.out.println("connection established");
			} else {
				System.out.println("No Connection");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return conImpportal;
	}

	/*
	 * method to read properties file properties
	 */
	/*public void readPropertiesFile() throws IOException {
		URL resourceUrl = loader.getResource("resources/OR.properties");
		properties = new Properties();
		try(InputStream input = resourceUrl.openStream()) {
			properties.load(input);
		}
	}*/
	/*
	 * method to retrieve all scriptsvalues from db those are scheduled
	 */
	public List<String> getDetails() {
		List<String> list = new ArrayList<String>();
		Connection conImpportal = getConnection();
		String sql = "select scriptvalue from bluesage.schedule_details";
		try (PreparedStatement ps = conImpportal.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(conImpportal);

		}
		return list;
	}

	/*
	 * method to retrieve all the records from DB
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getAllScripts() {
		JSONObject json = null;
		JSONArray arr = new JSONArray();
		Connection conImpportal = getConnection();
		String sql = "select * from bluesage.schedule_details";
		try (PreparedStatement ps = conImpportal.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {

			while (rs.next()) {
				json = new JSONObject();
				json.put("StartDate", rs.getString(1));
				json.put("EndDate", rs.getString(2));
				json.put("ScriptName", rs.getString(3));
				json.put("ScriptValue", rs.getString(4));
				json.put("ScriptId", rs.getInt(5));
				json.put("executionCount", rs.getInt(6));
				arr.add(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(conImpportal);
		}
		return arr;
	}

	/*
	 * method to retrieve records from db using scriptvalue and currentdate
	 */
	public HashMap<String, String> getLastExecutionDate(String scriptValue, String currentDTP) {
		HashMap<String, String> map = new HashMap<String, String>();
		Connection conImpportal = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "select * from bluesage.schedule_details where scriptValue=?";

		try {
			ps = conImpportal.prepareStatement(sql);
			ps.setString(1, scriptValue);
			rs = ps.executeQuery();
			while (rs.next()) {
				sql = "select * from bluesage.schedule_details where scriptValue=? and startdatetime<=? and enddatetime>=?";
				ps = conImpportal.prepareStatement(sql);
				ps.setString(1, scriptValue);
				ps.setString(2, currentDTP);
				ps.setString(3, currentDTP);
				rs1 = ps.executeQuery();
				while (rs1.next()) {
					map.put("startDate", rs1.getString(1));
					map.put("endDate", rs1.getString(2));
					map.put("scriptName", rs1.getString(3));
					map.put("scriptValue", rs1.getString(4));
					map.put("id", String.valueOf(rs1.getInt(5)));
					map.put("executionCount", String.valueOf(rs1.getInt(6)));
					map.put("lastExecutionDate", rs1.getString(7));
					map.put("lastExecutionCount", String.valueOf(rs1.getInt(8)));
					map.put("filePath", rs1.getString(9));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
				if (ps != null) {
					ps.close();
				}
				closeConnection(conImpportal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/*
	 * method to retrieve id's from db using scriptvalue, startDate and endDate
	 */
	public int getIdByScriptValue(String scriptValue, String sD, String eD) {
		int id = 0;
		Connection conImpportal = getConnection();
		ResultSet rs = null;
		String sql = "select id from bluesage.schedule_details where scriptValue=? and startDateTime=? and endDateTime=?";
		try (PreparedStatement ps = conImpportal.prepareStatement(sql);) {
			ps.setString(1, scriptValue);
			ps.setString(2, sD);
			ps.setString(3, eD);
			rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				closeConnection(conImpportal);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	public int checkScheduledTime(String scriptValue, String scheduleStartDate, String scheduleEndDate) {

		Connection conImpportal = getConnection();
		int recordCount = 0;
		String sql = "select COUNT(1) from bluesage.schedule_details where scriptValue = ? and " + " startDateTime BETWEEN ? AND ? OR "
				+ "	endDateTime  BETWEEN ? AND ? OR " + " ? BETWEEN startDateTime AND endDateTime ";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = conImpportal.prepareStatement(sql);
			ps.setString(1, scriptValue);
			ps.setString(2, scheduleStartDate);
			ps.setString(3, scheduleEndDate);
			ps.setString(4, scheduleStartDate);
			ps.setString(5, scheduleEndDate);
			ps.setString(6, scheduleStartDate);

			rs = ps.executeQuery();
			rs.next();
			recordCount = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				closeConnection(conImpportal);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return recordCount;
	}

	/*
	 * method to update scheduled scripts
	 */
	@SuppressWarnings({ "unchecked" })
	public JSONObject scheduleScript(String scriptName, String scriptValue, String scheduleStartDate, String scheduleEndDate, int count,
			String filePath) {
		JSONObject json = new JSONObject();
		Connection conImpportal = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			int recordCount = checkScheduledTime(scriptValue, scheduleStartDate, scheduleEndDate);
			if (recordCount == 0) {
				String sql = "insert into bluesage.schedule_details (startDateTime, endDateTime, scriptName, scriptValue, executeCount,filePath)"
						+ " values (?, ?, ?, ?, ?,?)";
				ps = conImpportal.prepareStatement(sql);
				ps.setString(1, scheduleStartDate);
				ps.setString(2, scheduleEndDate);
				ps.setString(3, scriptName);
				ps.setString(4, scriptValue);
				ps.setInt(5, count);
				ps.setString(6, filePath);
				int rowAffected = ps.executeUpdate();
				//				int id = getIdByScriptValue(scriptValue, scheduleStartDate, scheduleEndDate);
				scheduleNewScripts(rowAffected, scriptValue, scheduleStartDate, scheduleEndDate);
				json.put("success", true);
				json.put("Data", "Succesfully Inserted in DB");
			} else {
				json.put("success", false);
				json.put("message", "Already Script Scheduled at these Timings");
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "Some thing went wrong");
			e.printStackTrace();
		} finally {

			try {

				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}

				closeConnection(conImpportal);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return json;
	}

	/*
	 * method to schedule new scripts
	 */
	public void scheduleNewScripts(int rowAffected, String scriptValue, String sd, String ed) {
		if (rowAffected == 1) {
			Scheduler scheduler = null;
			try {
				scheduler = new StdSchedulerFactory().getScheduler();

				List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();

				Boolean currentJobScheduled = Boolean.FALSE;

				for (JobExecutionContext jobCtx : currentJobs) {
					String thisGroupName = jobCtx.getJobDetail().getKey().getGroup();
					if ((thisGroupName.equalsIgnoreCase(scriptValue + "Group"))) {
						currentJobScheduled = Boolean.TRUE;
					}

				}

				if (!currentJobScheduled) {
					JobDetail job = newJob(ScheduledJob.class).withIdentity(scriptValue, scriptValue + "Group").build();
					Trigger trigger = TriggerBuilder.newTrigger().withIdentity(scriptValue + "Trigger", scriptValue + "Group")
							.withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")).build();

					scheduler.getContext().put(scriptValue + "Group", scriptValue);
					scheduler.start();
					scheduler.scheduleJob(job, trigger);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateLastExecutionDate(String scriptValue, String date, int executionCount, int scriptId) {
		Connection conImpportal = getConnection();
		String sql = "update bluesage.schedule_details set lastExecutionDate=?, lastExecutionCount = ? WHERE scriptValue = ? and id=? ";
		try (PreparedStatement ps = conImpportal.prepareStatement(sql);) {
			ps.setString(1, date);
			ps.setInt(2, executionCount);
			ps.setString(3, scriptValue);
			ps.setInt(4, scriptId);
			int i = ps.executeUpdate();
			if (i > 0) {
				System.out.println("LastExecutionDate has beeb update for the script: " + scriptValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(conImpportal);
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject deleteScript(int scriptId) {
		JSONObject json = new JSONObject();
		Connection conImpportal = getConnection();
		String sql = "delete from bluesage.schedule_details where id=?";
		try (PreparedStatement ps = conImpportal.prepareStatement(sql);) {
			ps.setInt(1, scriptId);
			int i = ps.executeUpdate();
			if (i > 0) {
				json.put("Status", "Success");
				json.put("Data", "Data deleted Successfully");
			} else {
				json.put("Status", "No Records");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection(conImpportal);
		}
		return json;
	}

	public void closeConnection(Connection conImpportal) {
		try {

			if (!conImpportal.isClosed()) {
				conImpportal.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public JSONObject editScript(String scriptName,String scriptValue, int scriptId, String startDT, String endDT, int exeCount, String filePath) {
		JSONObject json = new JSONObject();
		Connection conImpportal = getConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			int recordCount = checkScheduledTime(scriptValue, startDT, endDT);
			if (recordCount == 0) {
				
				String sql = "update bluesage.schedule_details set startDateTime=?, endDateTime = ?, executecount=?,filePath=?,scriptValue=?,scriptName=? where  id=?";
				ps = conImpportal.prepareStatement(sql);
				ps.setString(1, startDT);
				ps.setString(2, endDT);
				ps.setInt(3, exeCount);
				ps.setString(4, filePath);
				ps.setString(5, scriptValue);
				ps.setString(6, scriptName);
				ps.setInt(7, scriptId);
				int rowAffected = ps.executeUpdate();
				if (rowAffected == 1) {
					json.put("success", true);
					json.put("Data", "Edited the values");
					scheduleNewScripts(rowAffected, scriptValue, startDT, endDT);
				} else {
					json.put("success", false);
					json.put("message", "No records found to edit");
				}

			}else{
				String lstExe="SELECT count(*) FROM bluesage.schedule_details where id=? and scriptValue=? and lastExecutionDate IS NOT NULL ";
				ps = conImpportal.prepareStatement(lstExe);
				ps.setInt(1, scriptId);
				ps.setString(2, scriptValue);
				rs=ps.executeQuery();
				int res=0;
				if(rs.next()){
					res=rs.getInt(1);
				}
				if(res==0){
					String sql = "update bluesage.schedule_details set startDateTime=?, endDateTime = ?, executecount=?,filePath=?,scriptValue=?,scriptName=? where  id=?";
					ps = conImpportal.prepareStatement(sql);
					ps.setString(1, startDT);
					ps.setString(2, endDT);
					ps.setInt(3, exeCount);
					ps.setString(4, filePath);
					ps.setString(5, scriptValue);
					ps.setString(6, scriptName);
					ps.setInt(7, scriptId);
					int rowAffected = ps.executeUpdate();
					if (rowAffected == 1) {
						json.put("success", true);
						json.put("Data", "Edited the values");
						scheduleNewScripts(rowAffected, scriptValue, startDT, endDT);
					} else {
						json.put("success", false);
						json.put("message", "No records found to edit");
					}
				}else{
					json.put("success", false);
					json.put("message", "Script already scheduled and executed");
				}
				
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("message", "Something went wrong");
			e.printStackTrace();
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}

				closeConnection(conImpportal);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public void insertErrorLog(String scriptId, String date, int errorCount, String path, String scriptName) {
		Connection conImpportal = getConnection();
		PreparedStatement ps = null;
		try {
			String sql = "insert into bluesage.error_logs (scriptId, scriptname,error_count, date, path)" + " values (?, ?, ?, ?,?)";
			ps = conImpportal.prepareStatement(sql);
			ps.setString(1, scriptId);
			ps.setString(2, scriptName);
			ps.setInt(3, errorCount);
			ps.setString(4, date);
			ps.setString(5, path);
			int rowAffected = ps.executeUpdate();
			if (rowAffected > 0) {
				System.out.println("Successfully inserted in db");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {

				if (ps != null) {
					ps.close();
				}

				closeConnection(conImpportal);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public JSONObject getErrorLogData() {
		JSONObject json = null;
		JSONObject data = new JSONObject();
		JSONArray arr = new JSONArray();
		Connection conImpportal = getConnection();
		String sql = "select * from bluesage.error_logs";
		try (PreparedStatement ps = conImpportal.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {

			while (rs.next()) {
				json = new JSONObject();
				json.put("scriptId", rs.getString(1));
				json.put("id", rs.getInt(2));
				json.put("error_count", rs.getInt(3));
				DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				DateFormat df2=new SimpleDateFormat("MM/dd/yyyy hh:mm a");
				Date d1=df1.parse(rs.getString(4));
				String date=df2.format(d1);
				json.put("date", date);
				json.put("path", "/DCU/htmlerrorreport?id=" + rs.getInt(2));
				json.put("scriptName", rs.getString(6));
				arr.add(json);
			}
			data.put("success", true);
			data.put("Data", arr);

		} catch (Exception e) {
			data.put("success", false);
			data.put("message", "Somewhere went wrong");
			e.printStackTrace();
		} finally {

			closeConnection(conImpportal);
		}
		return data;
	}

	public String getErrorReport(String id) {

		Connection conImpportal = getConnection();
		String sql = "select path from bluesage.error_logs where id = ? ";
		String filePath = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conImpportal.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				filePath = rs.getString(1);

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}

			} catch (Exception e) {

			}
			closeConnection(conImpportal);
		}
		return filePath;
	}
}
