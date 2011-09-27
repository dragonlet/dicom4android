/** FormatException.java - Exception de Formattage des marques Dicom
 *
 * @author
 * @version $Revision: 0.1 $ $Date: 2010/03/19 22:54:06 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01a,18Mar2010,andleg   written
 *
 */

/*This file is part of dicom4android.

    dicom4android is free software: you can redistribute it and/or modify
    it under the terms of the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.medcare.Dicom;

public class FormatException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a FormatException with no detail message.
	 */
	public FormatException() {
		super();
	}

	/**
	 * Constructs a FormatException with the specified detail message
	 * 
	 * @param s
	 *            the detail message
	 */
	public FormatException(String s) {
		super(s);
	}

}
