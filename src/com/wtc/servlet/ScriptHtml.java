package com.wtc.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContext;

import com.wtc.Database.DatabaseConnection;

public class ScriptHtml {
	private static final String SAVE_DIR = "Htmls";
	DatabaseConnection obj = new DatabaseConnection();
	public Properties properties;

	public void saveErrorLogHtml(String scriptId, String scriptName, int errorCount, ServletContext servletContext) {

		FileInputStream instream = null;
		FileOutputStream outstream = null;
		try {
			obj.readPropertiesFile();
			File input = new File(obj.properties.getProperty("local.testoutput.path"));

			String fileName = input.getName();
			System.out.println(input.getAbsolutePath());
			System.out.println(this.getClass().getClassLoader().getResource(".").toString());

			String path = obj.properties.getProperty("local.project.path");

			Long curMilliSecs = System.currentTimeMillis();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//get current date time with Date()
			Date date = new Date();
			String dateString = dateFormat.format(date);
			System.out.println(dateFormat.format(date));
			String savePath = File.separator + scriptId + File.separator + curMilliSecs + File.separator
							+ dateString.replaceAll("[^\\w\\s]", "").replaceAll(" ", "").trim();

			savePath = path + File.separator + SAVE_DIR + savePath;
			File firstDir = new File(savePath);
			if (!firstDir.exists()) {
				firstDir.mkdirs();
			}

			File file = new File(firstDir.toString() + File.separator + fileName);
			instream = new FileInputStream(input);
			outstream = new FileOutputStream(file);

			byte[] buffer = new byte[1024];

			int length;
			/*copying the contents from input stream to
			 * output stream using read and write methods
			 */
			while ((length = instream.read(buffer)) > 0) {
				outstream.write(buffer, 0, length);
			}

			//Closing the input/output file streams
			instream.close();
			outstream.close();
			File finalPath = new File(file.toString().replace(".\\", ""));
			obj.insertErrorLog(scriptId, dateString, errorCount, finalPath.toString(), scriptName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
