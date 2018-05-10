package com.worldbank.tests.pages;

import java.util.List;

import org.hamcrest.Matchers;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.worldbank.core.common.BasePage;
import com.worldbank.utility.AssertUtils;
import com.worldbank.utility.ConstantUtils;
import com.worldbank.utility.LocatorUtils;
import com.worldbank.utility.WaitUtils;
import com.worldbank.utility.WorldBankUtils;

public class CountriesPage extends BasePage {

	@FindBy(locator = "lbl.countries.page.title")
	private WebElement lblPageTitle;

	@FindBy(locator = "lst.lnk.countries.page.country.name")
	private List<WebElement> lstlnkCountryName;

	public List<WebElement> getLstlnkCountryName() {
		return lstlnkCountryName;
	}

	/**
	 * * This will return Web Element of link to the country name given as a
	 * parameter.
	 *
	 * @param countryName
	 * @return
	 */
	 
	private WebElement getLnkCountry(String countryName) {
		return WorldBankUtils.getElementWithFormat("lnk.countries.page.country.name.format", countryName);
	}

	/**
	 *  * Verify Countries page is displayed by verifying the title of the
	 * Countries title & page title.
	 */
	public void verifyCountriesPage() {
		WaitUtils.waitForVisible(lblPageTitle);

		AssertUtils.assertVisible(lblPageTitle);
		// Verify that title 'Countries' is displayed.
		AssertUtils.assertTextMatches(lblPageTitle, Matchers.equalToIgnoringCase(ConstantUtils.TXT_COUNTRIES));
		// Verify the page title.
		AssertUtils.assertPageTitle(Matchers.equalToIgnoringCase(ConstantUtils.TXT_COUNTRIESPAGE_TITLE));
	}

	/**
	 * Click on the country name link given as parameter.
	 * @param countryName
	 */
	public void selectCountry(String countryName) {
		WaitUtils.waitForVisible(LocatorUtils
				.getBy(String.format(getContext().getString("lnk.countries.page.country.name.format"), countryName)));
		WorldBankUtils.clickUsingJavaScript(getLnkCountry(countryName));
	}

}
