package ezvcard.parameters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/*
 Copyright (c) 2012, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * Represents a vCard parameter (aka "sub type").
 * @author Michael Angstadt
 */
public class VCardParameter {
	/**
	 * The name (e.g. "TYPE")
	 */
	protected final String name;

	/**
	 * The value (e.g. "home").
	 */
	protected final String value;

	/**
	 * @param name the name (e.g. "TYPE")
	 * @param value the value (e.g. "home")
	 */
	public VCardParameter(String name, String value) {
		this.name = name.toUpperCase();
		this.value = (value == null) ? null : value.toLowerCase();
	}

	/**
	 * Gets the parameter name.
	 * @return the parameter name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the value of the parameter.
	 * @return the value of the parameter (e.g. "home")
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VCardParameter other = (VCardParameter) obj;
		if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * Searches the static objects of a child class for one that has a certain
	 * value.
	 * @param typeValue the type value to look for
	 * @param clazz the child class
	 * @return the object or null if not found
	 */
	protected static <T extends VCardParameter> T findByValue(String typeValue, Class<T> clazz) {
		for (Field f : clazz.getFields()) {
			int mods = f.getModifiers();
			if (Modifier.isStatic(mods) && Modifier.isPublic(mods) && f.getDeclaringClass() == clazz) {
				try {
					Object obj = f.get(null);
					if (obj != null) {
						@SuppressWarnings("unchecked")
						T param = (T) obj;
						if (param.getValue().equalsIgnoreCase(typeValue)) {
							return param;
						}
					}
				} catch (Exception ex) {
					//reflection error
				}
			}
		}
		return null;
	}
}