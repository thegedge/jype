/*
 * Copyright (C) 2013 Jason Gedge <http://www.gedge.ca>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * 
 */
package ca.gedge.jype;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Test TypeFactory.
 */
public class TestTypeFactory {
	@Test
	public void testFromList() {
		try {
			assertEquals(
			    "java.lang.String",
			    TypeFactory.fromList(String.class).toString()
			);
		} catch(IllegalArgumentException exc) {
			fail("TypeFactory.fromList(String.class).toString()");
		}

		try {
			assertEquals(
			    "java.util.List<java.lang.String>",
			    TypeFactory.fromList(List.class, String.class).toString()
			);
		} catch(IllegalArgumentException exc) {
			fail("TypeFactory.fromList(List.class, String.class).toString()");
		}

		try {
			assertEquals(
			    "java.lang.String[]",
			    TypeFactory.fromList(Array.newInstance(String.class, 0).getClass()).toString()
			);
		} catch(IllegalArgumentException exc) {
			fail("TypeFactory.fromList(Array.class, String.class).toString()");
		}

		try {
			assertEquals(
			    "java.util.Map<java.lang.String,java.util.Map<java.lang.Integer,java.util.List<java.lang.String>>>",
			    TypeFactory.fromList(Map.class, String.class, Map.class, Integer.class, List.class, String.class).toString()
			);
		} catch(IllegalArgumentException exc) {
			fail("TypeFactory.fromList(Map.class, String.class, Map.class, Integer.class, List.class, String.class).toString()");
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromListTooFew() {
		TypeFactory.fromList(Map.class, String.class);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromListTooMany() {
		TypeFactory.fromList(Map.class, String.class, Number.class, Integer.class);
	}
	
	@Test
	public void testParse() {
		final String [] tests = new String[] {
			"int",
			"char[]",
			"void",
			"java.lang.String",
			"java.util.List<java.lang.String>",
			"java.util.Hashtable<java.lang.String,java.util.List<java.lang.Object>>"
		};
		
		for(String test : tests) {
			try {
				assertEquals(test, TypeFactory.parse(test).toString());
			} catch(Exception exc) {
				fail("failed to parse \"" + test + "\"");
			}
		}
	}
}
