/** ValueTagDicom.java - Stockage d'une marque Dicom et de sa valeur associee
 *
 * @author Andre Charles Legendre
 *
 * @version $Revision: 0.3 $ $Date: 2010/03/25 18:51:12 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01b,19Mar2010,andleg   correction suite evolution DICOM
 * 01a,24Dec2009,andleg   written
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

public class ValueTagDicom extends TagDicom {

	private String v;

	/**
	 * Construct a ValueTagDicom by default
	 * 
	 */
	public ValueTagDicom() {
		super();
		v = new String("");
	}

	/**
	 * Construct a ValueTagDicom
	 * 
	 * @param group
	 *            -the group of the tag
	 * @param element
	 *            -the element of the tag
	 * @param value
	 *            -the value associated to the tag
	 * 
	 */
	public ValueTagDicom(int group, int element, String value) {
		super.setTag(group, element);
		v = new String(value);
	}

	/**
	 * Modify the tag of ValueTagDicom
	 * 
	 * @param t
	 *            -the new tag
	 * 
	 */
	public void setTagDicom(TagDicom t) {
		super.setTag(t.getGroup(), t.getElement());
	}

	/**
	 * Get the tag of ValueTagDicom
	 * 
	 * @return the tag
	 * 
	 */
	public TagDicom getTagDicom() {
		return this;
	}

	/**
	 * Modify the value of ValueTagDicom
	 * 
	 * @param v
	 *            -the new value
	 * 
	 */
	public void setValue(String v) {
		this.v = new String(v);
	}

	/**
	 * Get the value of ValueTagDicom
	 * 
	 * @return the value
	 * 
	 */
	public String getValue() {
		return v;
	}

}
