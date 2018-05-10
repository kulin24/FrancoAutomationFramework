package com.worldbank.tests.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.worldbank.core.common.BasePage;
import com.worldbank.tests.beans.CountryDataBean;
import com.worldbank.utility.WaitUtils;

public class CountryHomePage extends BasePage {

	@FindBy(locator = "lbl.countries.page.title")
	private WebElement lblPageTitle;

	@FindBy(locator = "lbl.countryhome.page.population.data.amount")
	private WebElement lblPopulationAmount;

	@FindBy(locator = "lbl.countryhome.page.gdp.data.amount")
	private WebElement lblGDPAmount;

	@FindBy(locator = "lbl.countryhome.page.gdpgrowth.data.amount")
	private WebElement lblGDPGrowthAmount;

	/**
	 * Fill the Country Data bean from screen for the given country.
	 * 
	 * @param countryName
	 * @return
	 */

	public CountryDataBean fillCountryDataBean(String countryName) {
		WaitUtils.waitForVisible(lblPopulationAmount);
		String population = lblPopulationAmount.getText();
		String gdp = lblGDPAmount.getText();
		String gdpGrowth = lblGDPGrowthAmount.getText();

		return new CountryDataBean(countryName, population, gdp, gdpGrowth);
	}
}
