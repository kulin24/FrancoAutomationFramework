package com.worldbank.utility;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.worldbank.core.common.BaseTestProvider;


/**
 * This utility class provides wait methods for different conditions. The
 * default time out for wait is 30 seconds and can be set through
 * "default.timeout.sec" property.
 */
public class WaitUtils {
	public static final long TIMEOUT_SEC = Long
			.valueOf(BaseTestProvider.getBaseTest().getString("default.timeout.sec", "30"));

	private static WebDriver getDriver() {
		return BaseTestProvider.getBaseTest().getDriver();
	}

	public static void waitForPresent(By byLoc) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT_SEC);
			wait.until(ExpectedConditions.presenceOfElementLocated(byLoc));
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be present: " + byLoc.toString());
		}
	}

	public static void waitForNotVisible(By byLoc) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT_SEC);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(byLoc));
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be Not visible: " + byLoc.toString());
		}
	}

	public static void waitForVisible(By byLoc) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT_SEC);
			wait.until(ExpectedConditions.visibilityOfElementLocated(byLoc));
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be visible: " + byLoc.toString());
		}
	}
	
	public static void waitForNotVisible(WebElement ele) {
		int timeout = (int)TIMEOUT_SEC;
		while (ele.isDisplayed() && timeout > 0) {
				waitFor(1000);
			timeout--;
		}
		if(timeout == 0) {
			System.out.println("Timed out in waiting for element to be Not visible: " + ele);	
		}
	}

	public static void waitForVisible(WebElement ele) {
		int timeout = (int) TIMEOUT_SEC;
		boolean status = true;
		while (status && timeout > 0) {
			try {
				ele.isDisplayed();
				status = false;
			} catch (Exception e) {
				waitFor(1000);
			}
			timeout--;
		}

		if (timeout == 0) {
			System.out.println("Timed out in waiting for element to be visible: " + ele);
		}	
	}

	public static void waitForSelected(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT_SEC);
			wait.until(ExpectedConditions.elementToBeSelected(element));
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be selected: " + element.toString());
		}
	}

	public static void waitForNotSelected(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT_SEC);
			wait.until(ExpectedConditions.elementSelectionStateToBe(element, false));
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be not selected: " + element.toString());
		}
	}

	public static void waitForClickable(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT_SEC);
			wait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be clickable: " + element.toString());
		}
	}

	public static void waitForAttributeMatches(WebElement element, String attribute, String value) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(), TIMEOUT_SEC);
			wait.until(ExpectedConditions.attributeContains(element, attribute, value));
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element: " + element.toString() + " attribute: " + attribute
					+ " value to be: " + value);
		}
	}

	public static void waitForEnabled(final WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(),
					Integer.parseInt(BaseTestProvider.getBaseTest().getString("default.timeout.sec")));

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver wdriver) {
					return element.isEnabled();
				}
			});
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be Enabled: " + element.toString());
		}
	}

	public static void waitForDisabled(final WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(),
					Integer.parseInt(BaseTestProvider.getBaseTest().getString("default.timeout.sec")));

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver wdriver) {
					return !element.isEnabled();
				}
			});
		} catch (Exception e) {
			System.out.println("Timed out in waiting for element to be Disabled: " + element.toString());
		}
	}

	public static void waitForPageToLoad() {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(),
					Integer.parseInt(BaseTestProvider.getBaseTest().getString("default.timeout.sec")));

			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver wdriver) {
					return ((JavascriptExecutor) getDriver()).executeScript("return document.readyState")
							.equals("complete");
				}
			});
		} catch (Exception e) {
			System.out.println("Timed out in waiting for Page To load");
		}
	}

	public static void waitForAjaxToLoad() {
		try {
			WebDriverWait wait = new WebDriverWait(getDriver(),
					Integer.parseInt(BaseTestProvider.getBaseTest().getString("default.timeout.sec")));

			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {

					return ((Long) ((JavascriptExecutor) getDriver()).executeScript("return jQuery.active") == 0);

				}
			});
		} catch (Exception e) {
			System.out.println("Timed out in waiting for AJAX to load");
		}
	}

	public static void waitFor(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
