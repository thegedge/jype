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
package ca.gedge.jype;

import java.util.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * Tests all type descriptors.
 */
public class TestTypeDescriptor {
	@Test
	public void testSimpleType() {
		final SimpleType stringType = new SimpleType(String.class);
		final SimpleType numberType = new SimpleType(Number.class);
		final SimpleType integerType = new SimpleType(Integer.class);
		
		assertTrue(stringType.isAssignableFrom(stringType));
		assertTrue(numberType.isAssignableFrom(integerType));
		assertFalse(integerType.isAssignableFrom(numberType));
		assertFalse(stringType.isAssignableFrom(numberType));
	}
	
	@Test
	public void testArrayType() {
		final ArrayType numberArray = new ArrayType(Number.class);
		final ArrayType integerArray = new ArrayType(Integer.class);
		final ArrayType doubleArray = new ArrayType(Double.class);
		
		assertTrue(numberArray.isAssignableFrom(numberArray));
		assertTrue(numberArray.isAssignableFrom(integerArray));
		assertTrue(numberArray.isAssignableFrom(doubleArray));
		assertFalse(integerArray.isAssignableFrom(numberArray));
		assertFalse(integerArray.isAssignableFrom(doubleArray));
	}
	
	@Test
	public void testGenericTypeSingleParam() {
		final GenericType l_n = new GenericType(List.class, Number.class);
		final GenericType a_i = new GenericType(ArrayList.class, Integer.class);
		
		assertTrue(l_n.isAssignableFrom(a_i));
		assertFalse(a_i.isAssignableFrom(l_n));
		
		final GenericType l_ls = new GenericType(List.class, l_n);
		final GenericType l_as = new GenericType(List.class, a_i);
		final GenericType a_ls = new GenericType(ArrayList.class, l_n);
		final GenericType a_as = new GenericType(ArrayList.class, a_i);
		
		assertTrue (l_ls.isAssignableFrom(l_ls));
		assertTrue (l_ls.isAssignableFrom(l_as));
		assertTrue (l_ls.isAssignableFrom(a_ls));
		assertTrue (l_ls.isAssignableFrom(a_as));
		
		assertFalse(l_as.isAssignableFrom(l_ls));
		assertTrue (l_as.isAssignableFrom(l_as));
		assertFalse(l_as.isAssignableFrom(a_ls));
		assertTrue (l_as.isAssignableFrom(a_as));
		
		assertFalse(a_ls.isAssignableFrom(l_ls));
		assertFalse(a_ls.isAssignableFrom(l_as));
		assertTrue (a_ls.isAssignableFrom(a_ls));
		assertTrue (a_ls.isAssignableFrom(a_as));
		
		assertFalse(a_as.isAssignableFrom(l_ls));
		assertFalse(a_as.isAssignableFrom(l_as));
		assertFalse(a_as.isAssignableFrom(a_ls));
		assertTrue (a_as.isAssignableFrom(a_as));
	}
	
	@Test
	public void testGenericTypeMultipleParams() {
		final GenericType l_n = new GenericType(List.class, Number.class);
		final GenericType a_i = new GenericType(ArrayList.class, Integer.class);
		
		final GenericType m_n_s = new GenericType(Map.class, Number.class, String.class);
		final GenericType m_i_s = new GenericType(Map.class, Integer.class, String.class);
		
		assertTrue(m_n_s.isAssignableFrom(m_i_s));
		assertFalse(m_i_s.isAssignableFrom(m_n_s));
		
		final GenericType m_n_ln = new GenericType(Map.class, new SimpleType(Number.class), l_n);
		final GenericType m_n_ai = new GenericType(Map.class, new SimpleType(Number.class), a_i);
		final GenericType tm_i_ai = new GenericType(TreeMap.class, new SimpleType(Integer.class), a_i);
		
		assertTrue(m_n_ln.isAssignableFrom(m_n_ai));
		assertTrue(m_n_ln.isAssignableFrom(tm_i_ai));
		assertTrue(m_n_ai.isAssignableFrom(tm_i_ai));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWrongNumberGenericParams() {
		new GenericType(Map.class, String.class, List.class, Number.class);
	}
}
