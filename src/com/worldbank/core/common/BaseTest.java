package com.worldbank.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/*
* This is a base class that holds driver and other information that required
* during test execution. In order to get thread-safe test base object use it
* through BaseProvider class.
*/
public class BaseTest {
	public static final String DESIRED_CAPABILITIES = "desired.capabilities";
	private static final String WEBDRIVER_NAME = "webdriver.name";
	private static final String TESTDATA_KEY = "testdata.repo";
	private static final String OBJECT_REPO_KEY = "object.repo";
	private static final String CHROME_PATH = "webdriver.chrome.driver";
	private static final String IE_PATH = "webdriver.ie.driver";
	private WebDriver driver;
	private PropertiesConfiguration context = new PropertiesConfiguration();
	private XMLConfiguration testData = new XMLConfiguration();;
	private Configuration specificTestData;

	BaseTest() {
		initialSetup();
	}

	public WebDriver getDriver() {
		if (null == driver)
			initDriver();
		return driver;
	}

	public void tearDown() {
		if (null != driver) {
			driver.quit();
			driver = null;
		}
	}

	/*
	 * Returns all the properties - settings properties, locator properties,
	 * other properties stored at runtime.
	 */
	public Configuration getContext() {
		return context;
	}

	/*
	 * This will return the test data for the scenario mentioned in the
	 * TestData.xml as per the test case id.
	 */
	public Configuration getTestData() {
		return specificTestData;
	}

	public String getString(String key) {
		return context.getString(key);
	}

	public String getString(String key, String defVal) {
		return context.getString(key, defVal);
	}

	private void initDriver() {
		try {
			String chromePath = context.getString(CHROME_PATH, "servers/chromedriver.exe");
			System.setProperty(CHROME_PATH, chromePath);
			String iePath = context.getString(IE_PATH, "servers/chromedriver.exe");
			System.setProperty(IE_PATH, iePath);

			WEBDRIVER webdriver = WEBDRIVER.valueOf(getString(WEBDRIVER_NAME, "Firefox"));
			Capabilities desiredCapabilities = getDesiredCapabilities(webdriver.getCapabilites());

			driver = webdriver.getDriver(desiredCapabilities);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to create local driver", e);
		}
	}

	@SuppressWarnings("unchecked")
	private DesiredCapabilities getDesiredCapabilities(DesiredCapabilities capabilities) {
		if (!context.containsKey(DESIRED_CAPABILITIES))
			return capabilities;
		Map<String, Object> desiredCapabilities = new HashMap<String, Object>(capabilities.asMap());
		Gson gson = new GsonBuilder().create();
		Map<String, Object> extraCapabilities = gson.fromJson(context.getString(DESIRED_CAPABILITIES, "{}"), Map.class);

		desiredCapabilities.putAll(extraCapabilities);
		return new DesiredCapabilities(desiredCapabilities);
	}

	private enum WEBDRIVER {
		Firefox(FirefoxDriver.class, DesiredCapabilities.firefox()), IE(InternetExplorerDriver.class,
				DesiredCapabilities.internetExplorer()), Chrome(ChromeDriver.class, DesiredCapabilities.chrome());

		private Class<? extends WebDriver> driverClass;
		private DesiredCapabilities capabilites;

		private WEBDRIVER(Class<? extends WebDriver> driverClass, DesiredCapabilities cap) {
			this.driverClass = driverClass;
			this.capabilites = cap;
		}

		public DesiredCapabilities getCapabilites() {
			return capabilites;
		}

		public WebDriver getDriver(Capabilities desiredCapabilities) {
			try {
				Constructor<? extends WebDriver> con = driverClass.getConstructor(Capabilities.class);
				con.setAccessible(true);
				return con.newInstance(desiredCapabilities);
			} catch (Exception e) {
				System.err.println("desired capabilities will be ignored.");
			}
			try {
				return driverClass.newInstance();
			} catch (Exception e) {
				throw new WebDriverException("Unable to create webdriver", e);
			}
		}
	}

	// This will return the list of files in the given path
	public static ArrayList<File> listf(String directoryName, ArrayList<File> files, String fileFilter) {
		File directory = new File(directoryName);
		File[] fList = null;
		// get all the files from the directory
		if (fileFilter.equalsIgnoreCase("xml")) {
			fList = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					boolean isDir = new File(dir, name).isDirectory();
					return isDir || name.endsWith(".xml");
				}
			});
		} else if (fileFilter.equalsIgnoreCase("properties")) {
			fList = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					boolean isDir = new File(dir, name).isDirectory();
					return isDir || name.endsWith(".properties");
				}
			});
		}

		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files, fileFilter);
			}
		}
		return files;
	}

	// This will load properties into context or test data nodes into testdata
	// object
	public void loadPropTestData(String filename, String fileFilter) {

		try {
			FileInputStream fs = null;
			ArrayList<File> dirfiles = new ArrayList<File>();
			dirfiles = listf(filename, dirfiles, fileFilter);
			for (File file : dirfiles) {
				fs = new FileInputStream(file.getAbsoluteFile());
				if (fileFilter.equalsIgnoreCase("properties")) {
					PropertiesConfiguration props = new PropertiesConfiguration();
					props.load(fs);
					context.copy(props);
					props.clear();
				} else if (fileFilter.equalsIgnoreCase("xml")) {
					testData.load(file);
				}
			}
		} catch (Exception e) {
			System.out.println("Can not create property object" + e);
			e.printStackTrace();
		}
	}

	// This will make the initial setup of loading all the test data files and
	// all the object repository and settings
	public void initialSetup() {
		AbstractConfiguration.setDefaultListDelimiter(';');
		try {
			File settingFile = new File("resources\\settings.properties");
			context.load(settingFile);

			String[] testDataDir = context.getString(TESTDATA_KEY).split(";");
			String[] objectRepoDir = context.getString(OBJECT_REPO_KEY).split(";");
			for (String tdDir : testDataDir) {
				loadPropTestData(tdDir, "xml");
			}
			for (String orDir : objectRepoDir) {
				loadPropTestData(orDir, "properties");
			}
		} catch (Exception e) {
			System.out.println("Unable to load initial setup");
		}
	}

	// This will load the test data for specific test case key that will be
	// specified in the parameter
	public void loadTestdata(String tcKey) {
		XMLConfiguration config = testData;
		config.setExpressionEngine(new XPathExpressionEngine());
		if (specificTestData != null) {
			specificTestData.clear();
		}

		specificTestData = config.subset("testcase[@id='" + tcKey + "']");

	}

	// This will add properties from the map parameter, it will override the
	// existing key value.
	public void addAll(Map<String, String> props) {
		Iterator<String> iterator = props.keySet().iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			String val = props.get(key);
			this.context.clearProperty(key);
			this.context.setProperty(key, val);
		}
	}

	public Logger getLogger() {
		Logger myLogger = Logger.getLogger(String.valueOf(Thread.currentThread().getId()));
		FileAppender myFileAppender;

		myFileAppender = new FileAppender();
		myFileAppender.setFile(getString("logpath"));
		myFileAppender.setLayout(new PatternLayout("%-5p %m %n"));
		myFileAppender.setThreshold(Level.INFO);
		myFileAppender.setAppend(true);
		myFileAppender.activateOptions();
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure(myFileAppender);
		return myLogger;
	}
}
