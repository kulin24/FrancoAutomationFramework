package com.worldbank.core.cucumber;

import cucumber.runtime.CucumberException;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;

public class FeatureResultListener implements Reporter {
	public static final String PENDING_STATUS = "pending";
	public static final String UNDEFINED_MESSAGE = "There are undefined steps";
	public static final String PENDING_MESSAGE = "There are pending steps";
	private Reporter reporter;
	private boolean strict;
	private Throwable error = null;

	public FeatureResultListener(Reporter reporter, boolean strict) {
		this.reporter = reporter;
		this.strict = strict;
	}

	public void after(Match match, Result result) {
		collectError(result);
		reporter.after(match, result);
	}

	public void before(Match match, Result result) {
		collectError(result);
		reporter.before(match, result);
	}

	public void embedding(String mimeType, byte[] data) {
		reporter.embedding(mimeType, data);
	}

	public void match(Match match) {
		reporter.match(match);
	}

	public void result(Result result) {
		collectError(result);
		reporter.result(result);
	}

	private void collectError(Result result) {
		if (result.getStatus().equals(Result.FAILED)) {
			if (error == null || isUndefinedError(error)
					|| isPendingError(error)) {
				error = result.getError();
			}
		} else if (result.getStatus().equals(PENDING_STATUS) && strict) {
			if (error == null || isUndefinedError(error)) {
				error = new CucumberException(PENDING_MESSAGE);
			}
		} else if (result.getStatus().equals(Result.UNDEFINED.getStatus())
				&& strict) {
			if (error == null) {
				error = new CucumberException(UNDEFINED_MESSAGE);
			}
		}
	}

	private boolean isPendingError(Throwable error) {
		return (error instanceof CucumberException)
				&& error.getMessage().equals(PENDING_MESSAGE);
	}

	private boolean isUndefinedError(Throwable error) {
		return (error instanceof CucumberException)
				&& error.getMessage().equals(UNDEFINED_MESSAGE);
	}

	public void write(String text) {
		reporter.write(text);
	}

	public boolean isPassed() {
		return error == null;
	}

	public Throwable getFirstError() {
		return error;
	}

	public void startFeature() {
		error = null;
	}
}
