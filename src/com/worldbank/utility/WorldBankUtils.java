package com.worldbank.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.worldbank.core.common.BaseTestProvider;
import com.worldbank.core.common.BaseTest;
import com.worldbank.tests.beans.CountryDataBean;

public class WorldBankUtils {

	private static WebDriver getDriver() {
		return BaseTestProvider.getBaseTest().getDriver();
	}

	private static BaseTest getBaseTest() {
		return BaseTestProvider.getBaseTest();
	}

	/**
	 * Returns web element for locators in which string format is used.
	 */
	public static WebElement getElementWithFormat(String locKey, Object... arguments) {
		return getDriver().findElement(LocatorUtils.getBy(String.format(getBaseTest().getString(locKey), arguments)));
	}

	/**
	 * Returns list of web elements for locators in which string format is used.
	 */
	public static List<WebElement> getElementsWithFormat(String locKey, Object... arguments) {
		return getDriver().findElements(LocatorUtils.getBy(String.format(getBaseTest().getString(locKey), arguments)));
	}

	/**
	 * Sorts the list based on given parameters.
	 * 
	 * @param countryDataList
	 *            - list of beans that is to be sorted sortProperty - Sort
	 *            criteria i.e. - Population or GDP or GDP Growth sortOrder -
	 *            Sort order - asc or desc
	 * @return the sorted list
	 * 
	 */
	public static List<CountryDataBean> sortList(List<CountryDataBean> countryDataList, final String sortProperty,
			final String sortOrder) {
		Collections.sort(countryDataList, new Comparator<CountryDataBean>() {

			@Override
			public int compare(CountryDataBean o1, CountryDataBean o2) {
				int result = 0;
				if (sortProperty.equalsIgnoreCase(ConstantUtils.TXT_POPULATION)) {
					result = getBigDecimal(o1.getPopulation()).subtract(getBigDecimal(o2.getPopulation())).signum();
				} else if (sortProperty.equalsIgnoreCase(ConstantUtils.TXT_GDP)) {
					result = getBigDecimal(o1.getGdp()).subtract(getBigDecimal(o2.getGdp())).signum();
				} else if (sortProperty.equalsIgnoreCase(ConstantUtils.TXT_GDP_GROWTH)) {
					result = getBigDecimal(o1.getGdpGrowth()).subtract(getBigDecimal(o2.getGdpGrowth())).signum();
				}
				if (sortOrder.equalsIgnoreCase("desc")) {
					return result * (-1);
				} else {
					return result;
				}
			}

			public BigDecimal getBigDecimal(String data) {
				data = data.replace("$", "").replace("%", "").replace(" ", "").replace(",", "");
				BigDecimal number = null;
				BigDecimal million = new BigDecimal("1000000");
				BigDecimal billion = new BigDecimal("1000000000");
				BigDecimal trillion = new BigDecimal("1000000000000");

				if (data.contains("million")) {
					number = new BigDecimal(data.replace("million", "").trim()).multiply(million);
				} else if (data.contains("billion")) {
					number = new BigDecimal(data.replace("billion", "").trim()).multiply(billion);
				} else if (data.contains("trillion")) {
					number = new BigDecimal(data.replace("trillion", "").trim()).multiply(trillion);
				} else {
					number = new BigDecimal(data.trim());
				}
				return number;
			}

		});
		return countryDataList;
	}

	/**
	 * Writes the bean list data to CSV file.
	 * 
	 * @param beanList
	 *            - list of beans that contains data.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static void writeToCSVFile(List<CountryDataBean> beanList) {
		CSVWriter csvWriter = null;
		try {
			csvWriter = new CSVWriter(new FileWriter(getBaseTest().getContext().getString("outputdir") + "\\"
					+ getBaseTest().getContext().getString("testname") + "\\CountriesData.csv"));
			BeanToCsv<CountryDataBean> bc = new BeanToCsv<CountryDataBean>();

			// mapping of columns with their positions
			ColumnPositionMappingStrategy<CountryDataBean> mappingStrategy = new ColumnPositionMappingStrategy<CountryDataBean>();

			mappingStrategy.setType(CountryDataBean.class);

			// Fields in Employee Bean
			String[] columns = new String[] { "Country", "Population", "GDP", "GDPGrowth" };
			mappingStrategy.setColumnMapping(columns);
			bc.write(mappingStrategy, csvWriter, beanList);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				csvWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// This file is kept only for demo purpose
		try {
			FileUtils.copyFile(
					new File(getBaseTest().getContext().getString("outputdir") + "\\"
							+ getBaseTest().getContext().getString("testname") + "\\CountriesData.csv"),
					new File(getBaseTest().getContext().getString("outputdir") + "\\CountriesData.csv"));
		} catch (IOException e) {
		}
	}

	public static void clickUsingJavaScript(WebElement element) {
		JavascriptExecutor executor = (JavascriptExecutor) getDriver();
		executor.executeScript("arguments[0].click();", element);
	}

}
