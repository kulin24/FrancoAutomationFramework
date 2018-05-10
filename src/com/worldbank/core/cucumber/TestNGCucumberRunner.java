package com.worldbank.core.cucumber;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.ITestContext;

import com.worldbank.core.common.BaseTestProvider;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;
import gherkin.formatter.Formatter;

public class TestNGCucumberRunner {

	private Runtime runtime;
	private RuntimeOptions runtimeOptions;
	private ResourceLoader resourceLoader;
	private FeatureResultListener resultListener;
	private ClassLoader classLoader;
	private static String buildID = "";
	public static String outputDirectory = "";

	/**
	 * Bootstrap the cucumber runtime
	 *
	 * @param clazz
	 *            Which has the cucumber.api.CucumberOptions and
	 *            org.testng.annotations.Test annotations
	 */

	public TestNGCucumberRunner(ITestContext context, Class<?> clazz) {
		BaseTestProvider.getBaseTest().addAll(context.getCurrentXmlTest().getAllParameters());
		Configuration testContext = BaseTestProvider.getBaseTest().getContext();

		String testName;
		String outputDir;
		testName = context.getCurrentXmlTest().getName();
		testContext.setProperty("testname", testName);
		try {
			
			if(new File("target\\pom.properties").exists()) {
				PropertiesConfiguration pomConfig;
				pomConfig = new PropertiesConfiguration(new File("target\\pom.properties"));
				buildID = pomConfig.getString("buildid");
			} else {
				Date now = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				buildID = dateFormat.format(now);
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		testContext.setProperty("outputdir", "results\\" + buildID);
		outputDirectory = "results\\" + buildID;
		outputDir = "results\\" + buildID + "/" + testName;
		File resultDir = new File(outputDir);
		resultDir.mkdirs();

		this.classLoader = clazz.getClassLoader();
		resourceLoader = new MultiLoader(classLoader);

		RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
		runtimeOptions = runtimeOptionsFactory.create();

		final PluginFactory pluginFactory = new PluginFactory();
		String plugins[] = { "json:" + outputDir + "/cucumber.json"};
		for (String plugin : plugins) {
			Object pluginObj = pluginFactory.create(plugin.trim());
			runtimeOptions.addPlugin(pluginObj);
		}

		TestNgReporter reporter = new TestNgReporter(System.out);
		runtimeOptions.addPlugin(reporter);

		if (testContext.containsKey("features")) {
			String[] featurPaths = (testContext.getStringArray("features"));
			runtimeOptions.getFeaturePaths().addAll(Arrays.asList(featurPaths));
		}

		if (testContext.containsKey("glue")) {
			String[] glues = (testContext.getStringArray("glue"));
			runtimeOptions.getGlue().addAll(Arrays.asList(glues));
		}

		if (testContext.containsKey("tags")) {
			String[] tags = (testContext.getStringArray("tags"));
			runtimeOptions.getFilters().addAll(Arrays.asList(tags));
		}

		ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
		resultListener = new FeatureResultListener(runtimeOptions.reporter(classLoader), runtimeOptions.isStrict());
		runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
	}

	/**
	 * Run the Cucumber features
	 */
	public void runCukes() {
		for (CucumberFeature cucumberFeature : getFeatures()) {
			cucumberFeature.run(runtimeOptions.formatter(classLoader), resultListener, runtime);
		}
		finish();
		if (!resultListener.isPassed()) {
			throw new CucumberException(resultListener.getFirstError());
		}
	}

	public void runCucumber(CucumberFeature cucumberFeature) {
		resultListener.startFeature();
		cucumberFeature.run(runtimeOptions.formatter(classLoader), resultListener, runtime);

		if (!resultListener.isPassed()) {
			throw new CucumberException(resultListener.getFirstError());
		}
	}

	public void finish() {
		Formatter formatter = runtimeOptions.formatter(classLoader);

		formatter.done();
		formatter.close();
		runtime.printSummary();
	}

	/**
	 * @return List of detected cucumber features
	 */
	public List<CucumberFeature> getFeatures() {
		return runtimeOptions.cucumberFeatures(resourceLoader);
	}

	/**
	 * @return returns the cucumber features as a two dimensional array of
	 *         {@link CucumberFeatureWrapper} objects.
	 */
	public Object[][] provideFeatures() {
		List<CucumberFeature> features = getFeatures();
		List<Object[]> featuresList = new ArrayList<Object[]>(features.size());
		for (CucumberFeature feature : features) {
			featuresList.add(new Object[] { new CucumberFeatureWrapper(feature) });
		}
		return featuresList.toArray(new Object[][] {});
	}
}
