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

import java.util.Arrays;

/**
 * A descriptor for a generic type.
 */
public class GenericType implements TypeDescriptor {
	/**
	 * Convert a {@link Class} array to a {@link TypeDescriptor} array.
	 * 
	 * @param classes  the classes
	 * 
	 * @return an array of type descriptors
	 * @throws NullPointerException  if any class is <code>null</code>
	 */
	private static TypeDescriptor[] classToDescriptorArray(Class<?>... classes) {
		final TypeDescriptor[] types = new TypeDescriptor[classes.length];
		for(int index = 0; index < classes.length; ++index)
			types[index] = new SimpleType(classes[index]);
		return types;
	}

	/** The class describing this type */
	private Class<?> clazz;

	/** The class' generic parameters */
	private TypeDescriptor[] params;

	/**
	 * Constructs a generic type from a given {@link Class} and {@link Class}es
	 * for the generic parameters.
	 * 
	 * @param clazz  the class this type will describe
	 * @param paramClasses  the generic parameters for the given class
	 * 
	 * @throws IllegalArgumentException  if the number of generic parameters does
	 *               not match the length of {@link Class#getTypeParameters()}.
	 *               
	 * @throws NullPointerException  if any parameter is <code>null</code>
	 */
	public GenericType(Class<?> clazz, Class<?>... paramClasses) {
		this(clazz, classToDescriptorArray(paramClasses));
	}

	/**
	 * Constructs a generic type from a given {@link Class} and descriptors
	 * for the generic parameters.
	 * 
	 * @param clazz  the class this type will describe
	 * @param paramTypes  the generic parameters for the given class
	 * 
	 * @throws IllegalArgumentException  if the number of generic parameters does
	 *               not match the length of {@link Class#getTypeParameters()}.
	 *
	 * @throws NullPointerException  if any parameter is <code>null</code>
	 */
	public GenericType(Class<?> clazz, TypeDescriptor... paramTypes) {
		if(clazz == null)
			throw new NullPointerException("Class cannot be null");

		if(paramTypes.length != clazz.getTypeParameters().length)
			throw new IllegalArgumentException("Number of generic parameters must match given class");

		for(TypeDescriptor param : paramTypes) {
			if(param == null)
				throw new NullPointerException("Generic parameters cannot be null");
		}

		this.clazz = clazz;
		this.params = paramTypes;
	}

	@Override
	public int hashCode() {
		return 13*clazz.hashCode() + 47*Arrays.hashCode(params);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(clazz.getName());
		sb.append('<');
		sb.append(params[0].toString());
		for(int index = 1; index < params.length; ++index) {
			sb.append(',');
			sb.append(params[index].toString());
		}
		sb.append('>');
		return sb.toString();
	}

	@Override
	public boolean isAssignableFrom(TypeDescriptor type) {
		boolean ret = (type != null
		               && type instanceof GenericType
		               && clazz.isAssignableFrom( ((GenericType)type).clazz )
		               && params.length == ((GenericType)type).params.length);
		
		if(ret) {
			// Check all the generic parameters
			final TypeDescriptor[] otherParams = ((GenericType)type).params;
			for(int index = 0; index < params.length; ++index) {
				ret = (params[index].isAssignableFrom(otherParams[index]));
				
				// Break out whenever a non-assignable generic parameter is found
				if(!ret)
					break;
			}
		}
		
		return ret;
	}
}
