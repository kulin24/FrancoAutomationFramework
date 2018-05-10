package com.worldbank.core.cucumber;

import cucumber.runtime.model.CucumberFeature;

public class CucumberFeatureWrapper {
	private final CucumberFeature cucumberFeature;

	public CucumberFeatureWrapper(CucumberFeature cucumberFeature) {
		this.cucumberFeature = cucumberFeature;
	}

	public CucumberFeature getCucumberFeature() {
		return cucumberFeature;
	}

	@Override
	public String toString() {
		return cucumberFeature.getGherkinFeature().getName();
	}
}
