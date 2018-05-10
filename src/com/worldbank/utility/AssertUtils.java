package com.worldbank.utility;

import org.apache.log4j.Logger;
import org.hamcrest.Matcher;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.worldbank.core.common.BaseTestProvider;

/**
 * This is utility class for assertion. It provides different assertion methods
 * on element. While using this assertion methods you don't need to put
 * additional waits.
 *
 */
public class AssertUtils {
	public static final long TIMEOUT_SEC = Long
			.valueOf(BaseTestProvider.getBaseTest().getString("default.timeout.sec", "30"));

	private static WebDriver getDriver() {
		return BaseTestProvider.getBaseTest().getDriver();
	}
	
	private static Logger getLogger() {
		return BaseTestProvider.getBaseTest().getLogger();
	}

	public static void assertPageTitle(Matcher<String> val) {
		String actualVal = getDriver().getTitle();
		if (!val.matches(actualVal)) {
			String msg = String.format("Expected page title text should be [%s] : but found [%s] ", val, actualVal);
			getLogger().error(msg);
			throw new AssertionError(msg);
		}
	}

	public static void assertVisible(WebElement ele) {
		try {
			ele.isDisplayed();
		} catch (Exception e) {
			getLogger().error("Expected element " + ele + " should be visible : but found not visible");
			throw new AssertionError("Expected element " + ele + " should be visible : but found not visible");
		}
	}

	public static void assertNotDisplayed(WebElement ele) {
		try {
			ele.isDisplayed();
			getLogger().error("Expected element " + ele + " should not be visible : but found visible");
			throw new AssertionError("Expected element " + ele + " should not be visible : but found visible");
		} catch (Exception e) {
		}
	}

	public static void assertEnabled(WebElement ele) {
		try {
			ele.isEnabled();
		} catch (Exception e) {
			getLogger().error("Expected element " + ele + " should be Enabled : but found Disabled");
			throw new AssertionError("Expected element " + ele + " should be Enabled : but found Disabled");
		}
	}

	public static void assertDisabled(WebElement ele) {
		try {
			if (ele.isEnabled()) {
				getLogger().error("Expected element " + ele + " should be Disabled : but found Enabled");
				throw new AssertionError("Expected element " + ele + " should be Disabled : but found Enabled");
			}
		} catch (Exception e) {
		}
	}

	public static void assertSelected(WebElement ele) {
		try {
			ele.isSelected();
		} catch (Exception e) {
			getLogger().error("Expected element " + ele + " should not be Selected : but found Selected");
			throw new AssertionError("Expected element " + ele + " should not be Selected : but found Selected");
		}
	}

	public static void assertNotSelected(WebElement ele) {
		try {
			if (ele.isSelected()) {
				getLogger().error("Expected element " + ele + " should be Selected : but found not Selected");
				throw new AssertionError("Expected element " + ele + " should be Selected : but found not Selected");
			}
		} catch (Exception e) {
		}
	}

	public static void assertAttributeMatches(WebElement ele, String attrName, Matcher<String> val) {
		String actualVal = ele.getAttribute(attrName);
		if (!val.matches(actualVal)) {
			String msg = String.format("Expected %s attribute %s value should be [%s] : but found [%s] ", ele,
					attrName, val, actualVal);
			getLogger().error(msg);
			throw new AssertionError(msg);
		}
	}

	public static void assertAttributeNotMatches(WebElement ele, String attrName, Matcher<String> val) {
		String actualVal = ele.getAttribute(attrName);
		if (val.matches(actualVal)) {
			String msg = String.format("Expected %s attribute %s value should not be [%s] : but found [%s] ", ele,
					attrName, val, actualVal);
			getLogger().error(msg);
			throw new AssertionError(msg);
		}
	}

	public static void assertTextMatches(WebElement ele, Matcher<String> val) {
		String actualVal = ele.getText();
		if (!val.matches(actualVal)) {
			String msg = String.format("Expected %s text should be [%s] : but found [%s] ", ele, val, actualVal);
			getLogger().error(msg);
			throw new AssertionError(msg);
		}
	}

	public static void assertTextNotMatches(WebElement ele, Matcher<String> val) {
		String actualVal = ele.getText();
		if (val.matches(actualVal)) {
			String msg = String.format("Expected %s text should not be [%s] : but found [%s] ", ele, val,
					actualVal);
			getLogger().error(msg);
			throw new AssertionError(msg);
		}
	}
}
