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

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
		if(params.length == 0)
			return new SimpleType(clazz);
		
		final TypeDescriptor [] paramTypes = new TypeDescriptor[params.length];
		for(int index = 0; index < paramTypes.length; ++index)
			paramTypes[index] = fromIterator(iterator);
		
		return new GenericType(clazz, paramTypes);
	}
}
