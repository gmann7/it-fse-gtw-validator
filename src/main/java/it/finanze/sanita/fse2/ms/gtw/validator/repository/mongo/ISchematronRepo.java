/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.gtw.validator.repository.mongo;

import it.finanze.sanita.fse2.ms.gtw.validator.repository.entity.SchematronETY;

/**
 *	Schemamatron interface repository.
 */
public interface ISchematronRepo {
	
	/**
	 * Returns a Schematron identified by its {@code version}.
	 * 
	 * @param system of the Schematron to return.
	 * @return Schematron identified by its {@code version}.
	 */
	SchematronETY findByTemplateIdRoot(String templateIdRoot);

	SchematronETY findBySystemAndVersion(String system, String version);
}
