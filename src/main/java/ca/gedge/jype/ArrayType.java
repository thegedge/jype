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

import java.lang.reflect.Array;

/**
 * A descriptor for an array type.
 */
public class ArrayType implements TypeDescriptor {
	/** The array's type */
	private Class<?> clazz;

	/** The number of dimensions */
	private int numDims;

	/**
	 * Constructs a single dimension array type from a given {@link Class}.
	 * 
	 * @param clazz  the array's type
	 * 
	 * @throws NullPointerException  if the class is <code>null</code>
	 */
	public ArrayType(Class<?> clazz) {
		this(clazz, 1);
	}
	
	/**
	 * Constructs an array type from a given {@link Class} with a specified
	 * number of dimensions.
	 * 
	 * @param clazz  the array's type
	 * @param numDims  the number of dimensions
	 * 
	 * @throws NullPointerException  if the class is <code>null</code>
	 * @throws IllegalArgumentException  if numDims is non-positive
	 */
	public ArrayType(Class<?> clazz, int numDims) {
		if(clazz == null)
			throw new NullPointerException("Class cannot be null");

		if(numDims < 1)
			throw new IllegalArgumentException("Number of array dimensions must be at least 1");

		this.clazz = clazz;
		this.numDims = numDims;
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() + numDims;
	}
	
	@Override
	public String toString() {
		if(numDims == 1)
			return clazz.getName() + "[]";

		final StringBuilder sb = new StringBuilder(clazz.getName());
		for(int index = 0; index < numDims; ++index)
			sb.append("[]");
		return sb.toString();
	}

	@Override
	public boolean isAssignableFrom(TypeDescriptor type) {
		return (type != null
		        && type instanceof ArrayType
		        && clazz.isAssignableFrom( ((ArrayType)type).clazz )
		        && (numDims == ((ArrayType)type).numDims) );
	}
}
