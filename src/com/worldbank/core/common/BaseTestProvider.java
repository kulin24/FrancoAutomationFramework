package com.worldbank.core.common;

import java.util.Iterator;
import java.util.Vector;

public class BaseTestProvider extends ThreadLocal<BaseTest> {
	private final Vector<BaseTest> lst = new Vector<BaseTest>();
	private static final BaseTestProvider INSTANCE = new BaseTestProvider();

	private BaseTestProvider() {
	}

	@Override
	protected BaseTest initialValue() {
		BaseTest stb = new BaseTest();
		lst.add(stb);
		return stb;
	}

	@Override
	public void remove() {
		get().tearDown();
		super.remove();
	}

	@Override
	public void set(BaseTest value) {
		if (null == value) {
			remove();
		} else {
			super.set(value);
		}
	}

	public static BaseTest getBaseTest() {
		return INSTANCE.get();
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Iterator<BaseTest> iter = INSTANCE.lst.iterator();
				while (iter.hasNext()) {
					iter.next().tearDown();
					iter.remove();
				}
			}

		});
	}
}
