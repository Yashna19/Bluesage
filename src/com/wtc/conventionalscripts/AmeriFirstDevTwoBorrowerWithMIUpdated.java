package com.wtc.conventionalscripts;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.wtc.globalAccelerators.BaseClass;

public class AmeriFirstDevTwoBorrowerWithMIUpdated  extends BaseClass{

	public static int getErrorCount() {  
		return errorCount;
	}

	public static void setErrorCount(int errorCount) {
		AmeriFirstDevTwoBorrowerWithMIUpdated.errorCount = errorCount;
	}

	public static String getScript_id() {
		return script_id;
	}

	public static void setScript_id(String script_id) {
		AmeriFirstDevTwoBorrowerWithMIUpdated.script_id = script_id;
	}

	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		AmeriFirstDevTwoBorrowerWithMIUpdated.filePath = filePath;
	}

	public int currentNo = 1;
	public int currentTestNo = 1;

	@BeforeSuite
	public void getTestCountFromExcel(ITestContext context) throws InterruptedException, IOException, InvalidFormatException {
		String className = this.getClass().getSimpleName();
		String time = getCurrentTimeStamp();
		extentHtmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") +"\\Reports\\"+className+""+time+".html");
		report = new ExtentReports();
		report.attachReporter(extentHtmlReporter);
		metaDataFile = "AmeriFirstDev2bWithMIUpdatedMetaData";
		dataFile = "AmeriFirstDev2bWithMIUpdated";
		CredentialsFile = "AmeriFirstCredentials";
//		String reportsPath = String.valueOf(currentNo);
		getTestCount(context, metaDataFile, dataFile,CredentialsFile);
	}

	@BeforeMethod
	public void openBrowser() throws InvalidFormatException, IOException {
		String s[][] = getExcelValues(CredentialsFile);
		browser = s[0][4];
		launchBrowser();
	}

	@Test
	public void testCRMAndLOS() throws FileNotFoundException, InvalidFormatException, InterruptedException, IOException {
		extentTest  = report.createTest("Test :" + currentTestNo);
		startPoint = "";
		endPoint = "";
		String s[][] = getExcelValues(CredentialsFile);
		String CrmURL = s[0][1];
		String CrmUsername = s[0][2];
		String CrmPassword= s[0][3];
		String LpURL = s[1][1];
		String LPUsername = s[1][2];
		String LPPassword= s[1][3];
		CRMApp = true;
		LPApp = true;
		String crmLoanNumber = "";
		String lpNumber = "";  
		enterDataIntoApp(CrmURL, LpURL,crmLoanNumber,lpNumber,CrmUsername, CrmPassword,LPUsername, LPPassword,CRMApp,LPApp);
		currentTestNo ++ ;
		Assert.assertEquals(results, true, currentRowNumber + " Testcase failed");
	}

	@AfterMethod
	public void getReports() {
		report.flush();
		driver.quit();
	}
	@AfterSuite
	public void checkResult() {
		driver.quit();

	}




}
