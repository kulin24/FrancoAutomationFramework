package com.worldbank.tests.beans;

import java.io.Serializable;

public class CountryDataBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String country;
	private String population;
	private String gdp;
	private String gdpGrowth;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public String getGdp() {
		return gdp;
	}

	public void setGdp(String gdp) {
		this.gdp = gdp;
	}

	public String getGdpGrowth() {
		return gdpGrowth;
	}

	public void setGdpGrowth(String gdpGrowth) {
		this.gdpGrowth = gdpGrowth;
	}

	public CountryDataBean(String country, String population, String gdp, String gdpGrowth) {
		super();
		this.country = country;
		this.population = population;
		this.gdp = gdp;
		this.gdpGrowth = gdpGrowth;
	}

	@Override
	public String toString() {
		return "CountryData [Country=" + country + ", Population=" + population + ", GDP=" + gdp + ", GDP Growth="
				+ gdpGrowth + "]";
	}

}
