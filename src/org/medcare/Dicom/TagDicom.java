/** TagDicom.java - Marque DICOM
 *
 * @author
 * @version $Revision: 0.5 $ $Date: 2010/03/31 19:57:54 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01d,31Mar2010,andleg   ajout constructeur groupe+element + methode equals
 * 01c,23Mar2010,andleg   ajout methode toString (interet pour debug)
 * 01b,19Mar2010,andleg   modif. suite sauvegarde DICOM
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

public class TagDicom {

	// group et element sont en fait des non signes sur 16 bits
	private int group;
	private int element;

	/**
	 * construct a tag by default
	 * 
	 */
	public TagDicom() {
		group = 0;
		element = 0;
	}

	/**
	 * construct a tag for initialize the group & the element
	 * 
	 */
	public TagDicom(int g, int e) {
		group = g;
		element = e;
	}

	/**
	 * get the element of the tag
	 * 
	 */
	public int getElement() {
		return element;
	}

	/**
	 * get the group of the tag
	 * 
	 */
	public int getGroup() {
		return group;
	}

	/**
	 * set the element of the tag
	 * 
	 * @param element
	 *            the element
	 */
	public void setElement(int element) {
		this.element = element;
	}

	/**
	 * set the group of the tag
	 * 
	 * @param group
	 *            the group
	 */
	public void setGroup(int group) {
		this.group = group;
	}

	/**
	 * set the group and the element of a tag
	 * 
	 * @param g
	 *            the group
	 * @param e
	 *            the element
	 */
	public void setTag(int g, int e) {
		if (g > 0)
			group = g;
		else
			group = 0;
		if (e > 0)
			element = e;
		else
			element = 0;
	}

	/**
	 * set the group and the element of a tag with a array of byte
	 * 
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @param tab
	 *            the array of byte
	 */
	public void setTag(int type, byte tab[]) throws FormatException {
		int t0, t1, t2, t3;
		if (tab.length < 4) {
			group = -1;
			element = -1;
			throw new FormatException(
					"length of array of byte designated a tag false: l="
							+ tab.length);
		} else {
			t0 = tab[0];
			if (t0 < 0)
				t0 += 256;
			t1 = tab[1];
			if (t1 < 0)
				t1 += 256;
			t2 = tab[2];
			if (t2 < 0)
				t2 += 256;
			t3 = tab[3];
			if (t3 < 0)
				t3 += 256;

			if (type == VrDicom.BIG_ENDIAN) {
				group = ((int) t0) << 8 + (int) t1;
				element = ((int) t2) << 8 + (int) t3;
			} else {
				group = (int) t0 + (((int) t1) << 8);
				element = (int) t2 + (((int) t3) << 8);
			}
		}
	}

	/**
	 * return the group and the element in the format of an array of byte
	 * 
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return the array of byte
	 * @exception FormatException
	 * 
	 */
	public byte[] getTag(int type) throws FormatException {
		byte[] Tag = new byte[4];

		Tag[0] = 0;
		Tag[1] = 0;
		Tag[2] = 0;
		Tag[3] = 0;

		if ((getGroup() > 65535) || (getGroup() < 0) || (getElement() > 65535)
				|| (getElement() < 0)) {
			throw new FormatException("value of tag incompatible: g="
					+ getGroup() + " e=" + getElement());
		} else {
			int t0 = getGroup() & 0xff;
			if (t0 >= 128)
				t0 -= 256;
			int t1 = (getGroup() & 0xff00) >> 8;
			if (t1 >= 128)
				t1 -= 256;

			int t2 = getElement() & 0xff;
			if (t2 >= 128)
				t2 -= 256;
			int t3 = (getElement() & 0xff00) >> 8;
			if (t3 >= 128)
				t3 -= 256;

			if (type == VrDicom.BIG_ENDIAN) {
				Tag[0] = (byte) t1;
				Tag[1] = (byte) t0;
				Tag[2] = (byte) t3;
				Tag[3] = (byte) t2;
			} else {
				Tag[0] = (byte) t0;
				Tag[1] = (byte) t1;
				Tag[2] = (byte) t2;
				Tag[3] = (byte) t3;
			}
		}

		return Tag;
	}

	/**
	 * Determine if two tags are equals
	 * 
	 * @return boolean
	 * 
	 */
	public boolean equals(TagDicom to) {
		return (to.getGroup() == getGroup())
				&& (to.getElement() == getElement());
	}

	/**
	 * Determine if it's a private Group
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isPrivateGroup() {
		return ((group % 2) == 1)
				&& (((group & 0xff00) != 0x7f00) || (group > 0x7fe0))
				&& (group != 0xffff);
	}

	/**
	 * Determine if it's a private Owner
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isPrivateOwner() {
		return (element >= 0x0010) && (element <= 0x00ff);
	}

	/**
	 * Determine if it's a private Tag
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isPrivateTag() {
		return (element >= 0x1000) && (element <= 0xffff);
	}

	/**
	 * Determine if it's a Metaheader Group
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isMetaheaderGroup() {
		return (group == 0x0002);
	}

	/**
	 * Determine if it's a length Tag
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isLengthElement() {
		return (element == 0x0000);
	}

	/**
	 * Determine if it's a length Tag
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isLengthElementOrLengthToEnd() {
		return (element == 0x0000)
				|| ((group == 0x0008) && (element == 0x0001));
	}

	/**
	 * Determine if it's a pixel Tag
	 * 
	 * @return boolean
	 * 
	 */
	public boolean isPixelDataElement() {
		return ((group == 0x7fe0) && (element == 0x0010))
				|| ((group == 0x7fe1) && (element > 0x00ff) && ((element & 0x00ff) == 0x0010));
	}

	/**
	 * Returns a string representation of the object
	 * 
	 * @return String representation
	 * 
	 */
	public String toString() {
		return new String("group=0x" + Integer.toHexString(group)
				+ ";element=0x" + Integer.toHexString(element));
	}

}
