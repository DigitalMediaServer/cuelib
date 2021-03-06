/*
 * Cuelib library for manipulating cue sheets.
 * Copyright (C) 2007-2008 Jan-Willem van den Broek
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.digitalmediaserver.cuelib.tools.util.properties;

import java.io.Serializable;


/**
 * Interface for PropertyHandler factories.
 *
 * @author jwbroek
 */
public interface PropertyHandlerFactory extends Serializable {

	/**
	 * Get a PropertyHandler for the specified type.
	 *
	 * @param propertyType the property type class.
	 * @param <T> the type.
	 * @return A PropertyHandler for the specified type.
	 * @throws UnsupportedOperationException When the specified type is not
	 *             supported by this factory.
	 */
	public <T> PropertyHandler<T> getPropertyHandler(Class<T> propertyType) throws UnsupportedOperationException;
}
