package com.worldbank.tests.pages;

import org.hamcrest.Matchers;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.worldbank.core.common.BasePage;
import com.worldbank.utility.AssertUtils;
import com.worldbank.utility.ConstantUtils;
import com.worldbank.utility.WaitUtils;
import com.worldbank.utility.WorldBankUtils;

public class HomePage extends BasePage {

	@FindBy(locator = "view.header.menu")
	private WebElement viewMenu;
	
	private WebElement getLnkTab(String tabName) {
		return WorldBankUtils.getElementWithFormat("lnk.header.menu.option", tabName, tabName, tabName.toLowerCase());
	}

	/*
	 * This will launch the web site that is specified in
	 * "resources/setting.properties" file by key 'navigation.url'.
	 */
	public void launchWebsite() {
			getDriver().get(getContext().getString("navigation.url"));
	}

	/*
	 * Verify that the home page is displayed by verifying the title of the home
	 * page.
	 */
	public void verifyHomePage() {
		WaitUtils.waitForPageToLoad();
		AssertUtils.assertPageTitle(Matchers.equalToIgnoringCase(ConstantUtils.TXT_HOMEPAGE_TITLE));
	}

	/*
	 * Click on the tabName from the menu bar.
	 */
	public void clickTab(String tabName) {
		WaitUtils.waitForVisible(viewMenu);
		WorldBankUtils.clickUsingJavaScript(getLnkTab(tabName));
		
	}

}
