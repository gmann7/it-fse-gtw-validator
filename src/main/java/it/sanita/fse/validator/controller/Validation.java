package it.sanita.fse.validator.controller;

import javax.validation.ValidationException;

import it.sanita.fse.validator.utility.StringUtility;

/**
 * 
 * @author CPIERASC
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
		Boolean notValid = false;
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
