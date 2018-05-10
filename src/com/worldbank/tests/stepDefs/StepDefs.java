package com.worldbank.tests.stepDefs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import com.worldbank.core.common.BaseTestProvider;
import com.worldbank.core.common.BaseTest;
import com.worldbank.tests.beans.CountryDataBean;
import com.worldbank.tests.pages.CountriesPage;
import com.worldbank.tests.pages.CountryHomePage;
import com.worldbank.tests.pages.HomePage;
import com.worldbank.utility.ConstantUtils;
import com.worldbank.utility.WorldBankUtils;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepDefs {

	protected BaseTest getBaseTest() {
		return BaseTestProvider.getBaseTest();
	}

	protected Configuration getContext() {
		return BaseTestProvider.getBaseTest().getContext();
	}

	protected Logger getLogger() {
		return BaseTestProvider.getBaseTest().getLogger();
	}

	@Given("^open world bank web site$")
	public void openWebSite() {
		try {
			HomePage homePage = new HomePage();
			homePage.launchWebsite();
			getLogger().info("World Bank web site opened");
		} catch (Exception e) {
			getLogger().error("There was an error in launching website: " + getContext().getString("navigation.url"));
			throw e;
		}
	}

	@Then("^(.+) page of world bank web site is displayed$")
	public void verifyPageDisplayed(String pageName) {
		if (pageName.equalsIgnoreCase(ConstantUtils.TXT_HOME)) {
			try {
				HomePage homePage = new HomePage();
				homePage.verifyHomePage();
				getLogger().info("World Bank web site Home page was displayed.");
			} catch (Exception e) {
				getLogger().error("Home page of World Bank web site was not displayed.");
				throw e;
			}
		} else if (pageName.equalsIgnoreCase(ConstantUtils.TXT_COUNTRIES)) {
			try {
				CountriesPage countriesPage = new CountriesPage();
				countriesPage.verifyCountriesPage();
				getLogger().info("Countries page was displayed on World Bank web site.");
			} catch (Exception e) {
				getLogger().error("Countries page of World Bank web site was not displayed.");
				throw e;
			}
		}
	}

	@When("^click on (.+) tab$")
	public void clickTab(String tabName) {
		try {
			HomePage homePage = new HomePage();
			homePage.clickTab(tabName);
			getLogger().info("Clicked on " + tabName + " from menu bar.");
		} catch (Exception e) {
			getLogger().error("There was an error in clicking " + tabName + " tab.");
			throw e;
		}

	}

	@When("^read and note data for all countries on countries tab of world bank web site$")
	public void noteDataForAllCountries() {
		try {
			String[] countryNameList = BaseTestProvider.getBaseTest().getTestData().getString("countries").split(";");

			List<CountryDataBean> countryDataList = new ArrayList<CountryDataBean>();
			for (String countryName : countryNameList) {
				// Click country link
				CountriesPage countriesPage = new CountriesPage();
				countriesPage.selectCountry(countryName);

				// Read and note the data of country
				CountryHomePage countryHomePage = new CountryHomePage();
				countryDataList.add(countryHomePage.fillCountryDataBean(countryName));

				// Click on Countries Tab
				HomePage homePage = new HomePage();
				homePage.clickTab(ConstantUtils.TXT_COUNTRIES);
			}
			BaseTestProvider.getBaseTest().getContext().setProperty("countryDataList", countryDataList);
			getLogger().info("All the Countries information was read and noted successfully!");
		} catch (Exception e) {
			getLogger().error("There was an error while noting down all countries data.");
			throw e;
		}

	}

	@SuppressWarnings("unchecked")
	@Then("^log top (.+) countries as per (.+)$")
	public void topThreeCountries(String dataCount, String dataType) {
		getLogger().info("");
		getLogger().info("");
		try {
			List<CountryDataBean> listCountryDataBean = (ArrayList<CountryDataBean>) BaseTestProvider.getBaseTest()
					.getContext().getProperty("countryDataList");
			WorldBankUtils.sortList(listCountryDataBean, dataType, "desc");

			getLogger().info("Top " + dataCount + " countries as per " + dataType + " are listed below:");
			getLogger().info(String.format("%s %-25s %s", "No.", "Country", dataType));
			for (int i = 0; i < Integer.parseInt(dataCount); i++) {
				if (dataType.equalsIgnoreCase(ConstantUtils.TXT_POPULATION)) {
					getLogger().info(String.format("%s  %-25s %s", (i + 1) + ".",
							listCountryDataBean.get(i).getCountry(), listCountryDataBean.get(i).getPopulation()));
				} else if (dataType.equalsIgnoreCase(ConstantUtils.TXT_GDP)) {
					getLogger().info(String.format("%s  %-25s %s", (i + 1) + ".", listCountryDataBean.get(i).getCountry(),
							listCountryDataBean.get(i).getGdp()));
				} else if (dataType.equalsIgnoreCase(ConstantUtils.TXT_GDP_GROWTH)) {
					getLogger().info(String.format("%s  %-25s %s", (i + 1) + ".",
							listCountryDataBean.get(i).getCountry(), listCountryDataBean.get(i).getGdpGrowth()));
				}
			}
		} catch (Exception e) {
			getLogger().error("There was an error in finding top " + dataCount + " countries as per " + dataType);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Then("^export all the country data$")
	public void exportDataToCSV() {
		try {
			List<CountryDataBean> listCountryDataBean = (ArrayList<CountryDataBean>) BaseTestProvider.getBaseTest()
					.getContext().getProperty("countryDataList");
			WorldBankUtils.writeToCSVFile(listCountryDataBean);
		} catch (Exception e) {
			getLogger().error("There was an error in exporting all country data to CSV file.");
			throw e;
		}
	}
}