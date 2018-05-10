package com.worldbank.tests.stepDefs;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.worldbank.core.common.BaseTestProvider;
import com.worldbank.core.common.BaseTest;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class CucumberHooks {

	@Before
	public void beforeHook(Scenario scenario) {
		BaseTest baseTest = BaseTestProvider.getBaseTest();

		// Set path for log file
		baseTest.getContext().setProperty("logpath", baseTest.getString("outputdir") + "\\Logs\\"
				+ baseTest.getString("testname") + "\\" + scenario.getName() + ".txt");

		baseTest.getContext().setProperty("current.scenario", scenario);
		baseTest.getDriver().manage().deleteAllCookies();
		baseTest.getDriver().manage().window().maximize();

		// This will load the test data for each scenario
		String testcaseid = scenario.getSourceTagNames().toString();
		testcaseid = testcaseid.substring(testcaseid.indexOf("@Testid_")).replaceFirst("@Testid_", "").split(",")[0]
				.replace("[", "").replace("]", "");
		baseTest.loadTestdata(testcaseid);
	}

	@After
	public static void afterHook(Scenario scenario) {
		WebDriver driver = BaseTestProvider.getBaseTest().getDriver();
		boolean alwaysCaptureScreenshot = BaseTestProvider.getBaseTest().getContext()
				.getBoolean("success.screenshots", false);
		if (alwaysCaptureScreenshot || scenario.isFailed()) {
			try {
				byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
				scenario.embed(screenshot, "image/png");
			} catch (WebDriverException e) {
				System.err.println(e.getMessage());
			}
		}
		BaseTestProvider.getBaseTest().tearDown();
	}

}
