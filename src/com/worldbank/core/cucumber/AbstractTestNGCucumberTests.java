package com.worldbank.core.cucumber;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.worldbank.core.report.JSONReportMerger;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

public abstract class AbstractTestNGCucumberTests {
	private TestNGCucumberRunner testNGCucumberRunner;

	@BeforeClass(alwaysRun = true)
	public void setUpClass(ITestContext context) throws Exception {
		testNGCucumberRunner = new TestNGCucumberRunner(context, this.getClass());
	}

	@Test(groups = "cucumber", description = "Runs Cucumber Feature", dataProvider = "features")
	public void feature(CucumberFeatureWrapper cucumberFeature) {
		testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
	}

	@DataProvider
	public Object[][] features() {
		return testNGCucumberRunner.provideFeatures();
	}

	@AfterClass
	public void tearDownClass() throws Exception {
		testNGCucumberRunner.finish();
	}

	/*
	 * This will return the test data for the scenario mentioned in the
	 * TestData.xml as per the test case id.
	 */
	@AfterSuite
	public void buildReport() {
		try {
			JSONReportMerger.main(new String[] { TestNGCucumberRunner.outputDirectory });
		} catch (Throwable e) {
			e.printStackTrace();
		}
		File reportOutputDirectory = new File(TestNGCucumberRunner.outputDirectory);
		List<String> jsonFiles = new ArrayList<>();
		jsonFiles = listf(TestNGCucumberRunner.outputDirectory, jsonFiles, "json");

		String projectName = "WorldBankData";
		boolean skippedFails = true;
		boolean pendingFails = false;
		boolean undefinedFails = true;
		boolean parallelTesting = true;

		Configuration configuration = new Configuration(reportOutputDirectory, projectName);
		// optionally only if you need
		configuration.setStatusFlags(skippedFails, pendingFails, undefinedFails);
		configuration.setParallelTesting(parallelTesting);

		ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
		reportBuilder.generateReports();

		// For Demo Purpose only, copying Test log and CSV file in the project
		// directory.
		List<String> logFiles = new ArrayList<>();
		logFiles = listf(TestNGCucumberRunner.outputDirectory + "\\Logs", logFiles, "txt");
		try {
			FileUtils.copyFile(new File(logFiles.get(0)), new File("TestLog.txt"));
			FileUtils.copyFile(new File(reportOutputDirectory + "\\CountriesData.csv"), new File("CountriesData.csv"));
		} catch (IOException e) {
		}
	}

	private static List<String> listf(String directoryName, List<String> files, String fileFilter) {
		File directory = new File(directoryName);
		File[] fList = null;
		// get all the files from the directory
		if (fileFilter.equalsIgnoreCase("json")) {
			fList = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					boolean isDir = new File(dir, name).isDirectory();
					return isDir || name.endsWith(".json");
				}
			});
		} else if (fileFilter.equalsIgnoreCase("txt")) {
			fList = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					boolean isDir = new File(dir, name).isDirectory();
					return isDir || name.endsWith(".txt");
				}
			});
		}

		for (File file : fList) {
			if (file.isFile()) {
				files.add(file.getAbsolutePath());
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files, fileFilter);
			}
		}
		return files;
	}
}
