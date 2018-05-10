package com.worldbank.utility;

import org.openqa.selenium.By;

import com.worldbank.core.common.BaseTestProvider;


public class LocatorUtils {

	/**
	 * This method is useful to convert string locator to {@link By}. String
	 * locator must be in format <locator strategy>=<locator value> For example:
	 * xpath=.//a or id=signBtn or css=#signBtn
	 * 
	 * it supports following locator strategies:
	 * xpath
	 * name
	 * link
	 * partiallink
	 * id
	 * css
	 * className
	 * tagname
	 * 
	 * @param locator
	 *            string locator or property key holding string locator
	 * @return By object
	 */
	public static By getBy(String locator) {
		locator = BaseTestProvider.getBaseTest().getString(locator, locator);
		String[] parts = locator.split("=", 2);
		if (parts.length < 2) {
			throw new RuntimeException(
					"invalid locator:" + locator + " Required '<locator stretgy>=<value>', for example: 'id=userName'");
		}
		String strategy = parts[0].trim().toLowerCase();
		String loc = parts[1].trim();
		By by = null;
		switch (LocatorType.valueOf(strategy)) {
		case xpath:
			by = By.xpath(loc);
			break;
		case name:
			by = By.name(loc);
			break;
		case link:
			by = By.linkText(loc);
			break;
		case partiallink:
			by = By.partialLinkText(loc);
			break;
		case id:
			by = By.id(loc);
			break;
		case css:
			by = By.cssSelector(loc);
			break;
		case className:
			by = By.className(loc);
			break;
		case tagname:
			by = By.tagName(loc);
			break;
		default:
			throw new RuntimeException("Invalid (or not supported) locator stretegy : " + strategy);
		}
		return by;
	}
	
	private enum LocatorType {
		xpath, name, link, partiallink, id, css, className, tagname 
	}

}
