package com.worldbank.core.common;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public abstract class BasePage {

	public BasePage() {
		// Initialize all the elements mentioned with FindBy annotation on
		// creating page object
		AjaxElementLocatorFactory factory = new AjaxElementLocatorFactory(getDriver(),
				Integer.parseInt(BaseTestProvider.getBaseTest().getString("default.timeout.sec")));
		PageFactory.initElements(factory, this);
	}

	protected WebDriver getDriver() {
		return BaseTestProvider.getBaseTest().getDriver();
	}

	protected BaseTest getBaseTest() {
		return BaseTestProvider.getBaseTest();
	}

	protected Configuration getContext() {
		return BaseTestProvider.getBaseTest().getContext();
	}

	protected Logger getLogger() {
		return BaseTestProvider.getBaseTest().getLogger();
	}
}
