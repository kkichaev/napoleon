/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * NapoleonInstrumentationTestRunner
 *
 * kki   14/09/2010   creating
 */

package com.grsoft.tests;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

import com.grsoft.tests.network.FormatTestCase;

public class NapoleonInstrumentationTestRunner extends InstrumentationTestRunner
{
	@Override
	public TestSuite getAllTests()
	{
		InstrumentationTestSuite testSuite = new InstrumentationTestSuite(this);
		
		testSuite.addTestSuite(FormatTestCase.class);
		
		return testSuite;
	}
	
	@Override
	public ClassLoader getLoader()
	{
		return NapoleonInstrumentationTestRunner.class.getClassLoader();
	}
}
