/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.validator.controller;

import javax.validation.ValidationException;

import it.finanze.sanita.fse2.ms.gtw.validator.utility.StringUtility;

/**
 * 
 *
 *	Validation class.
 */
public final class Validation {
	
	/**
	 * Empty constructor.
	 */
	private Validation() {
	}

	/**
	 * Asserts that an object or a list of object is not {@code null}.
	 * 
	 * @param objs	List of objects to validate.
	 */
	public static void notNull(final Object... objs) {
		boolean notValid = false;
		for (final Object obj:objs) {
			if (obj == null) {
				notValid = true;
			} else if (obj instanceof String) {
				String checkString = (String)obj;
				checkString = checkString.trim();
				if(StringUtility.isNullOrEmpty(checkString)) {
					notValid = true;
				}
			}
			if (notValid) {
				throw new ValidationException("Violazione vincolo not null.");
			}
		}
	}

	public static void mustBeTrue(Boolean securityCheck, String msg) {
		if (securityCheck==null || !securityCheck) {
			throw new ValidationException(msg);
		}
	}

}
