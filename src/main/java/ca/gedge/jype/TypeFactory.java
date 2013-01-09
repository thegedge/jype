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
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory methods for Jype.
 */
public class TypeFactory {
	/**
	 * Convert a flat list of classes to an appropriate generic or simple type.
	 * 
	 * For example <code>fromList(List.class, String.class)</code> will return
	 * a type representing <code>List&lt;String&gt;</code> and
	 * <code>fromList(Map.class, String.class, List.class, Number.class)</code>
	 * will return a type representing <code>Map&lt;String, List&lt;Number&gt;&gt;</code>
	 * 
	 * @param classes  the flat list of classes
	 * 
	 * @return an applicable type descriptor
	 * 
	 * @throws IllegalArgumentException  if the given list of classes cannot be
	 *                                   converted to a valid type
	 */
	public static TypeDescriptor fromList(Class<?>... classes) {
		try {
			final Iterator<Class<?>> iter = Arrays.asList(classes).iterator();
			final TypeDescriptor descriptor = fromIterator(iter);
			if(iter.hasNext())
				throw new IllegalArgumentException("Class list is too large");
			
			return descriptor; // class list is juuuuuuust right
		} catch(NoSuchElementException exc) {
			throw new IllegalArgumentException("Class list is too small");
		}
	}
	
	/**
	 * Convert a flat list of classes (represented by an iterator) into a generic type.
	 * 
	 * @param iterator  an iterator over class instances
	 * 
	 * @return an applicable type descriptor
	 * 
	 * @throws NoSuchElementException  if the iterator runs out of elements before the given
	 *                                 list of classes can be converted to a valid type
	 */
	private static TypeDescriptor fromIterator(Iterator<Class<?>> iterator) {
		final Class<?> clazz = iterator.next();
		final TypeVariable<?> [] params = clazz.getTypeParameters();
		if(params.length == 0) {
			if(clazz.isArray()) {
				// Find the number of dimensions and non-array component type
				int numDims = 0;
				Class<?> arrayClass = clazz;
				while(arrayClass.isArray()) {
					++numDims;
					arrayClass = arrayClass.getComponentType();
				}
				
				return new ArrayType(arrayClass, numDims);
			} else {
				return new SimpleType(clazz);
			}
		}
		
		final TypeDescriptor [] paramTypes = new TypeDescriptor[params.length];
		for(int index = 0; index < paramTypes.length; ++index)
			paramTypes[index] = fromIterator(iterator);
		
		return new GenericType(clazz, paramTypes);
	}
	
	/** Non qualified types we will allow in {@link #parse(String)} */
	private final static Hashtable<String, Class<?>> nonQualifiedTypes;
	static {
		nonQualifiedTypes = new Hashtable<String, Class<?>>();
		nonQualifiedTypes.put("char", char.class);
		nonQualifiedTypes.put("byte", byte.class);
		nonQualifiedTypes.put("short", short.class);
		nonQualifiedTypes.put("int", int.class);
		nonQualifiedTypes.put("long", long.class);
		nonQualifiedTypes.put("float", float.class);
		nonQualifiedTypes.put("double", double.class);
		nonQualifiedTypes.put("boolean", boolean.class);
		nonQualifiedTypes.put("void", void.class);
		nonQualifiedTypes.put("String", String.class);
		nonQualifiedTypes.put("Object", Object.class);
	}
	
	/** Pattern to match stringified types */
	private static final Pattern p = Pattern.compile("([a-zA-Z][a-zA-Z0-9.]+)(?:(?:<(.+)>)|((?:\\[\\])+))?");
	
	/**
	 * Parse a {@link String} representation of a type. Similar to {@link Class#forName(String)},
	 * but also parses generic and array types. Some example strings:
	 * <ul>
	 *   <li>int</li>
	 *   <li>java.lang.String</li>
	 *   <li>java.lang.Number[]</li>
	 *   <li>java.util.List&lt;java.lang.String&gt;</li>
	 *   <li>java.util.List&lt;?&gt;</li>
	 *   <li>java.util.Map&lt;java.lang.Integer, java.util.TreeSet&lt;java.lang.Integer&gt;&gt;</li>
	 * </ul>
	 * 
	 * @param typeString  string specification of a type to parse
	 * 
	 * @returns an applicable type descriptor
	 * 
	 * @throws IllegalArgumentException  if the given string is formatted incorrectly
	 * @throws ClassNotFoundException  if any class in the given string is unknown
	 */
	public static TypeDescriptor parse(String typeString) throws ClassNotFoundException {
		Matcher m = p.matcher(typeString.replaceAll("\\s", ""));
		if(m.matches()) {
			// Get the base type
			Class<?> clazz = null;
			if(nonQualifiedTypes.containsKey(m.group(1))) {
				clazz = nonQualifiedTypes.get(m.group(1));
			} else {
				clazz = Class.forName(m.group(1));
			}

			// Parse based on whether or not this is an array, generic type, or neither 
			if(m.group(2) != null) {
				final String [] paramStrings = m.group(2).split(",");
				final TypeDescriptor [] params = new TypeDescriptor[paramStrings.length]; 
				for(int index = 0; index < params.length; ++index)
					params[index] = parse(paramStrings[index]);
				
				return new GenericType(clazz, params);
			} else if(m.group(3) != null) {
				return new ArrayType(clazz, m.group(3).length() / 2);
			} else {
				return new SimpleType(clazz);
			}
		}

		throw new IllegalArgumentException("Type string was in an illegal format: " + typeString);
	}
}
