/** VrDicom.java - Formattage des marques Dicom
 *
 * @author
 * @version $Revision: 0.4 $ $Date: 2010/03/02 15:13:57 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01c,31Mar2010,andleg   correction bug pour cas vrUSorSS
 * 01b,16Mar2010,andleg   modif. pour sauvegarde
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

import java.util.Vector;

import android.util.Log;

public class VrDicom extends DataElement {

	static final int LITTLE_ENDIAN = 0, BIG_ENDIAN = 1, NO_ENDIAN = -1;

	static final int EXPLICIT = 0x10;
	static final int IMPLICIT = 0x11;

	private static final String TAG = "VrDicom";

	private String[] stValueRepresentation = { "AE", "AS", "AT", "CS", "DA",
			"DS", "DT", "FL", "FD", "IS", "LO", "LT", "OB", "OW", "PN", "SH",
			"SL", "SQ", "SS", "ST", "TM", "UI", "UL", "US" };
	private int[] codeValueRepresentation = { vrAE, vrAS, vrAT, vrCS, vrDA,
			vrDS, vrDT, vrFL, vrFD, vrIS, vrLO, vrLT, vrOB, vrOW, vrPN, vrSH,
			vrSL, vrSQ, vrSS, vrST, vrTM, vrUI, vrUL, vrUS };
	private int[] lengthValueRepresentation = { 0, 4, 4, 0, 8, 0, 0, 4, 8, 0,
			0, 0, 0, 0, 0, 0, 4, 0, 2, 0, 0, 0, 4, 2 };
	private int[] maxLengthValueRepresentation = { 16, 4, 4, 16, 8, 16, 26, 4,
			8, 12, 64, 10240, 0, 0, 64, 16, 4, 0, 2, 1024, 16, 64, 4, 2 };
	private int indice;

	private boolean ignoreLengthError = true;
	private boolean debug = false;

	/**
	 * construct a VrDicom by default
	 * 
	 */
	public VrDicom() {
	};

	/**
	 * Set the debug
	 * 
	 * @param boolean the value of debug
	 * 
	 */
	public void setDebug(boolean d) {
		debug = d;
	}

	public int getIndiceVR(String VR) {
		boolean find = false;
		int ind = 0;

		while (ind < stValueRepresentation.length) {
			if (VR.equals(stValueRepresentation[ind])) {
				find = true;
				break;
			}
			ind++;
		}

		if (!find)
			return -1;

		indice = ind;
		return codeValueRepresentation[indice];
	}

	public int getIndiceVR(int VR) {
		boolean find = false;
		int ind = 0;

		while (ind < codeValueRepresentation.length) {
			if (VR == codeValueRepresentation[ind]) {
				find = true;
				break;
			}
			ind++;
		}

		if (!find)
			return -1;

		indice = ind;
		return ind;
	}

	public String getStringVR() {
		if ((indice >= 0) && (indice < stValueRepresentation.length))
			return stValueRepresentation[indice];
		else
			return "";
	}

	public int getLength() {
		if ((indice >= 0) && (indice < lengthValueRepresentation.length))
			return lengthValueRepresentation[indice];
		else
			return -1;
	}

	public int getMaxLength() {
		if ((indice >= 0) && (indice < maxLengthValueRepresentation.length))
			return maxLengthValueRepresentation[indice];
		else
			return -1;
	}

	/**
	 * for concat two array of byte
	 * 
	 * @param b1
	 *            the first array
	 * @param b2
	 *            the second array
	 * @return the result of the concatenation b1+b2
	 */
	private byte[] concatByte(byte[] b1, byte[] b2) {
		byte[] b = new byte[b1.length + b2.length];
		for (int i = 0; i < b1.length; i++)
			b[i] = b1[i];
		for (int i = 0; i < b2.length; i++)
			b[b1.length + i] = b2[i];
		return b;
	}

	/**
	 * for formatting a String in DICOM with a space
	 * 
	 * @param s
	 *            the string
	 * @return string the result of the format
	 */
	private String formatString(String s) {
		return formatString(s, " ");
	}

	/**
	 * for the format of a String in DICOM with a character
	 * 
	 * @param s
	 *            the string
	 * @param columns
	 *            the char used for format
	 * @return string the result of the format
	 */
	private String formatString(String s, String c) {
		int l = s.length();
		int n = l % 2;
		if (n > 0)
			s.concat(c);
		return s;
	}

	/**
	 * Return the integer corresponding to an array of bytes in the respect of
	 * Integer String in DICOM
	 * 
	 * @param tab
	 *            array of bytes
	 * @return int the value of the array
	 * @exception NumberFormatException
	 *                if length of value > 12 or array is not a integer
	 */
	private int getVR_IS(byte tab[]) throws NumberFormatException {
		String svVal = new String(tab);
		return getVR_IS(svVal);
	}

	/**
	 * Return the integer corresponding to a String in the respect of Integer
	 * String in DICOM
	 * 
	 * @param tab
	 *            String
	 * @return int the value of the String
	 * @exception NumberFormatException
	 *                if length of value > 12 or if String is not a integer
	 */
	private int getVR_IS(String tab) throws NumberFormatException {
		int IS;
		float ISf;
		if (tab.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Integer Value IS false: " + tab);
		}
		tab = tab.trim();
		if (tab.length() > 0)
			try {
				ISf = Float.valueOf(tab).intValue();
				IS = (int) ISf;
			} catch (NumberFormatException e) {
				throw new NumberFormatException(
						"format of Integer Value IS incorrect: " + tab);
			}
		else
			IS = 0;

		return IS;
	}

	/**
	 * Create a array of byte corresponding to the value of a integer in the
	 * respect of Integer String in DICOM
	 * 
	 * @param i
	 *            integer
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if length of value > 12
	 */
	private byte[] setVR_IS(int i) throws NumberFormatException {
		byte[] IS = null;

		String s = String.valueOf(i);
		s = formatString(s);
		if (s.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Integer Value IS false: " + s);
		}
		IS = s.getBytes();

		return IS;
	}

	/**
	 * Return the array of integer corresponding to an array of bytes in the
	 * respect of Integer String in DICOM
	 * 
	 * @param tab
	 *            array of bytes
	 * @param nbValues
	 *            number of integer expected
	 * @return int[] the array of integer
	 * @exception NumberFormatException
	 *                if length of one value > 12 or if a piece of array is not
	 *                a integer
	 */
	private int[] getVR_IS(byte tab[], int nbValues)
			throws NumberFormatException {
		String svVal = new String(tab);
		return getVR_IS(svVal, nbValues);
	}

	/**
	 * Return the array of integer corresponding to a String in the respect of
	 * Integer String in DICOM
	 * 
	 * @param svVal
	 *            String
	 * @param nbValues
	 *            number of integer expected
	 * @return int[] the array of integer
	 * @exception NumberFormatException
	 *                if length of value > 12 or if pice of String is not a
	 *                integer
	 */
	private int[] getVR_IS(String svVal, int nbValues)
			throws NumberFormatException {
		int IS[] = new int[nbValues];
		int i = 0, index = 0, pos = 0;

		svVal = svVal.trim();
		while (i < nbValues) {
			// search the separator
			if (i < nbValues - 1) {
				pos = svVal.indexOf((int) '\\', index);
				if (pos == -1) {
					pos = svVal.length();
					break;
				}
			} else
				pos = svVal.length();
			IS[i] = getVR_IS(svVal.substring(index, pos));
			index = pos + 1;
			i++;
		}

		if (i < nbValues) {
			IS[i] = getVR_IS(svVal.substring(index, pos));
			i++;
			for (; i < nbValues; i++)
				IS[i] = IS[i - 1];
		}

		return IS;
	}

	/**
	 * Create a array of byte corresponding to the value of a array of integer
	 * in the respect of Integer String in DICOM
	 * 
	 * @param bIS
	 *            array of integer
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if length of value > 12
	 */
	private byte[] setVR_IS(int[] bIS) throws NumberFormatException {
		StringBuffer b = new StringBuffer("");
		String s = new String("\\");
		for (int i = 0; i < bIS.length; i++) {
			b.append(new String(setVR_IS(bIS[i])));
			// ajout du separateur si necessaire
			if (i < bIS.length - 1)
				b.append(s);
		}
		return b.toString().getBytes();
	}

	/**
	 * Return the float corresponding to an array of bytes in the respect of
	 * Decimal String in DICOM
	 * 
	 * @param tab
	 *            array of bytes
	 * @return float the value of the array
	 * @exception NumberFormatException
	 *                if length of value > 16 or if piece of array is not a
	 *                decimal
	 */
	private float getVR_DS(byte tab[]) throws NumberFormatException {
		String svVal = new String(tab);
		return getVR_DS(svVal);
	}

	/**
	 * Return the float corresponding to a String in the respect of Decimal
	 * String in DICOM
	 * 
	 * @param svVal
	 *            String
	 * @return float the value of the String
	 * @exception NumberFormatException
	 *                if length of value > 16 or if piece of String is not a
	 *                decimal
	 */
	private float getVR_DS(String svVal) throws NumberFormatException {
		float DS;
		if (svVal.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Decimal Value DS false: " + svVal);
		}
		svVal = svVal.trim();
		if (svVal.length() > 0)
			try {
				DS = Float.valueOf(svVal).floatValue();
			} catch (NumberFormatException e) {
				throw new NumberFormatException(
						"format of Decimal Value DS incorrect: " + svVal);
			}
		else
			DS = (float) 0.0;

		return DS;
	}

	/**
	 * Create a array of byte corresponding to the value of a float in the
	 * respect of Decimal String in DICOM
	 * 
	 * @param f
	 *            float
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if length of value > 16
	 */
	private byte[] setVR_DS(float f) throws NumberFormatException {
		byte[] DS = null;

		String s = String.valueOf(f);
		s = formatString(s);
		if (s.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Decimal Value DS false: " + s);
		}
		DS = s.getBytes();

		return DS;
	}

	/**
	 * Return an array of float corresponding to a array of byte in the respect
	 * of Decimal String in DICOM
	 * 
	 * @param tab
	 *            array of byte
	 * @param nbValues
	 *            number of value expected
	 * @return float[] the array of float
	 * @exception NumberFormatException
	 *                if length of value > 16 or if piece of array is not a
	 *                decimal
	 */
	private float[] getVR_DS(byte tab[], int nbValues)
			throws NumberFormatException {
		String svVal = new String(tab);
		return getVR_DS(svVal, nbValues);
	}

	/**
	 * Return an array of float corresponding to a String in the respect of
	 * Decimal String in DICOM
	 * 
	 * @param svVal
	 *            String
	 * @param nbValues
	 *            number of value expected
	 * @return float[] the array of float
	 * @exception NumberFormatException
	 *                if length of value > 16 or if piece of String is not a
	 *                decimal
	 */
	private float[] getVR_DS(String svVal, int nbValues)
			throws NumberFormatException {
		float DS[] = new float[nbValues];
		int i = 0, index = 0, pos = 0;

		svVal = svVal.trim();
		while (i < nbValues) {
			// search the separator
			if (i < nbValues - 1) {
				pos = svVal.indexOf((int) '\\', index);
				if (pos == -1) {
					pos = svVal.length();
					break;
				}
			} else
				pos = svVal.length();
			DS[i] = getVR_DS(svVal.substring(index, pos));
			index = pos + 1;
			i++;
		}

		if (i < nbValues) {
			DS[i] = getVR_DS(svVal.substring(index, pos));
			i++;
			for (; i < nbValues; i++)
				DS[i] = DS[i - 1];
		}

		return DS;
	}

	/**
	 * Create a array of byte corresponding to the value of a array of float in
	 * the respect of Decimal String in DICOM
	 * 
	 * @param bDS
	 *            array of float
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if length of value > 16
	 */
	private byte[] setVR_DS(float[] bDS) throws NumberFormatException {
		StringBuffer b = new StringBuffer("");
		String s = new String("\\");
		for (int i = 0; i < bDS.length; i++) {
			b.append(new String(setVR_DS(bDS[i])));
			// ajout du separateur si necessaire
			if (i < bDS.length - 1)
				b.append(s);
		}
		return b.toString().getBytes();
	}

	/**
	 * Return the String corresponding to an array of bytes in the respect of
	 * Long String in DICOM
	 * 
	 * @param tab
	 *            array of bytes
	 * @return long the value of the array
	 * @exception NumberFormatException
	 *                if length of value > 64 or if piece of array is not a
	 *                decimal
	 */
	private String getVR_LO(byte tab[]) throws NumberFormatException {
		return getVR_LO(new String(tab));
	}

	/**
	 * Return the String corresponding to a String in the respect of Long String
	 * in DICOM
	 * 
	 * @param svVal
	 *            String
	 * @return long the value of the array
	 * @exception NumberFormatException
	 *                if length of value > 64 or if piece of String is not a
	 *                decimal
	 */
	private String getVR_LO(String svVal) throws FormatException {
		if (svVal.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new FormatException("length of Long Value LO false: "
						+ svVal);
		}
		svVal = svVal.trim();

		return svVal;
	}

	/**
	 * Create a array of byte corresponding to the value of a long in the
	 * respect of Long String in DICOM
	 * 
	 * @param l
	 *            long
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if length of value > 64
	 */
	private byte[] setVR_LO(String s) throws FormatException {
		byte[] LO = null;

		s = formatString(s);
		if (s.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new FormatException("length of Long Value LO false: " + s);
		}
		LO = s.getBytes();

		return LO;
	}

	/**
	 * Return the int corresponding to an array of byte in the respect of
	 * Unsigned Short in DICOM
	 * 
	 * @param tab
	 *            array of byte
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return int the value of the array
	 * @exception NumberFormatException
	 *                if length of value !=2
	 */
	private int getVR_US(int type, byte tab[]) throws NumberFormatException {
		int US, t0, t1;
		if (tab.length != getMaxLength()) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Unsigned Short Value US false");
		}
		if (tab.length > 0)
			t0 = tab[0];
		else
			t0 = 0;
		if (t0 < 0)
			t0 += 256;
		if (tab.length > 1)
			t1 = tab[1];
		else
			t1 = 0;
		if (t1 < 0)
			t1 += 256;
		if (type == BIG_ENDIAN)
			US = (((int) t0) << 8) + ((int) t1);
		else
			US = (((int) t1) << 8) + ((int) t0);

		return US;
	}

	/**
	 * Return the int corresponding to a String in the respect of Unsigned Short
	 * in DICOM
	 * 
	 * @param tab
	 *            String
	 * @return int the value
	 * @exception NumberFormatException
	 */
	private int getVR_US(String tab) throws NumberFormatException {
		float USf = (float) 0.0;

		tab = tab.trim();
		if (tab.length() > 0)
			try {
				USf = Float.valueOf(tab).intValue();
			} catch (NumberFormatException e) {
				if (!ignoreLengthError)
					throw new NumberFormatException(
							"format of Unsigned Short Value US incorrect: "
									+ tab);
			}

		return (int) USf;
	}

	/**
	 * Create a array of byte corresponding to the value of a int in the respect
	 * of Unsigned Short in DICOM
	 * 
	 * @param i
	 *            int
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if i >= (2^16) or i<0
	 */
	private byte[] setVR_US(int type, int i) throws NumberFormatException {
		byte[] US = new byte[2];

		US[0] = 0;
		US[1] = 0;
		if ((i > 65535) || (i < 0)) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"value of int incompatible with Unsigned Short Value US : "
								+ i);
		} else {
			int t0 = i & 0xff;
			if (t0 >= 128)
				t0 -= 256;
			int t1 = (i & 0xff00) >> 8;
			if (t1 >= 128)
				t1 -= 256;
			if (type == BIG_ENDIAN) {
				US[0] = (byte) t1;
				US[1] = (byte) t0;
			} else {
				US[0] = (byte) t0;
				US[1] = (byte) t1;
			}
		}

		return US;
	}

	/**
	 * Return the array of int corresponding to an array of byte in the respect
	 * of Unsigned Short in DICOM
	 * 
	 * @param tab
	 *            array of byte
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @param nbValues
	 *            size of the array of integer
	 * @return int[] the array of values
	 * @exception NumberFormatException
	 *                if length of value !=2*nbValues
	 */
	private int[] getVR_US(int type, byte tab[], int nbValues)
			throws NumberFormatException {
		byte b[] = new byte[2];
		int tabUS[] = new int[nbValues];
		int i;

		if (tab.length != getMaxLength() * nbValues) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Unsigned Short Value US false");
		}

		for (i = 0; i < nbValues; i++) {
			if (tab.length > i * 2)
				b[0] = tab[i * 2];
			else
				b[0] = 0;
			if (tab.length > i * 2 + 1)
				b[1] = tab[i * 2 + 1];
			else
				b[1] = 0;
			tabUS[i] = getVR_US(type, b);
		}

		return tabUS;
	}

	/**
	 * Return the array of int corresponding to a String in the respect of
	 * Unsigned Short in DICOM
	 * 
	 * @param tab
	 *            String
	 * @param nbValues
	 *            size of the array of integer
	 * @return int the value
	 * @exception NumberFormatException
	 */
	private int[] getVR_US(String svVal, int nbValues)
			throws NumberFormatException {
		int tabUS[] = new int[nbValues];
		int i = 0, index = 0, pos = 0;
		int US;
		float USf;

		svVal = svVal.trim();
		while (i < nbValues) {
			// search the separator
			if (i < nbValues - 1) {
				pos = svVal.indexOf((int) '\\', index);
				if (pos == -1) {
					pos = svVal.length();
					break;
				}
			} else
				pos = svVal.length();
			tabUS[i] = getVR_US(svVal.substring(index, pos));
			index = pos + 1;
			i++;
		}

		if (i < nbValues) {
			tabUS[i] = getVR_US(svVal.substring(index, pos));
			i++;
			for (; i < nbValues; i++)
				tabUS[i] = tabUS[i - 1];
		}

		return tabUS;
	}

	/**
	 * Create a array of byte corresponding to the array of int in the respect
	 * of Unsigned Short in DICOM
	 * 
	 * @param i
	 *            int
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if a int is >= (2^16) or i<0
	 */
	private byte[] setVR_US(int type, int[] i) throws NumberFormatException {
		byte[] US = new byte[getMaxLength() * i.length];
		byte[] b;

		for (int j = 0; j < i.length; j++) {
			b = setVR_US(type, i[j]);
			US[j * 2] = b[0];
			US[j * 2 + 1] = b[1];
		}

		return US;
	}

	/**
	 * Return the long corresponding to an array of byte in the respect of
	 * Unsigned Long in DICOM
	 * 
	 * @param tab
	 *            array of byte
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return int the value of the array
	 * @exception NumberFormatException
	 *                if length of value !=4
	 */
	private long getVR_UL(int type, byte tab[]) throws NumberFormatException {
		long UL, t0, t1, t2, t3;
		if (tab.length != getMaxLength()) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Unsigned Long Value UL false: l="
								+ tab.length + " maxLength=" + getMaxLength());
			if (debug)
				System.err.println("length of Unsigned Long Value UL false: l="
						+ tab.length + " maxLength=" + getMaxLength());
		}
		if (tab.length > 0)
			t0 = tab[0];
		else
			t0 = 0;
		if (t0 < 0)
			t0 += 256;
		if (tab.length > 1)
			t1 = tab[1];
		else
			t1 = 0;
		if (t1 < 0)
			t1 += 256;
		if (tab.length > 2)
			t2 = tab[2];
		else
			t2 = 0;
		if (t2 < 0)
			t2 += 256;
		if (tab.length > 3)
			t3 = tab[3];
		else
			t3 = 0;
		if (t3 < 0)
			t3 += 256;
		if (type == BIG_ENDIAN)
			UL = (((long) t0) << 32) + (((long) t1) << 16) + (((long) t2) << 8)
					+ ((long) t3);
		else
			UL = (((long) t3) << 32) + (((long) t2) << 16) + (((long) t1) << 8)
					+ ((long) t0);

		return UL;
	}

	/**
	 * Return the long corresponding to a String in the respect of Unsigned Long
	 * in DICOM
	 * 
	 * @param tab
	 *            String
	 * @return int the value
	 * @exception NumberFormatException
	 */
	private long getVR_UL(String tab) throws NumberFormatException {
		float ULf = (float) 0.0;

		tab = tab.trim();
		if (tab.length() > 0)
			try {
				ULf = Float.valueOf(tab).intValue();
			} catch (NumberFormatException e) {
				if (!ignoreLengthError)
					throw new NumberFormatException(
							"format of Unsigned Long Value UL incorrect: "
									+ tab);
			}

		return (long) ULf;
	}

	/**
	 * Create a array of byte corresponding to the value of a long in the
	 * respect of Unsigned Long in DICOM
	 * 
	 * @param l
	 *            long
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if l >= 4294967296 (2^32) or l<0
	 */
	private byte[] setVR_UL(int type, long l) throws NumberFormatException {
		byte[] UL = new byte[getMaxLength()];

		UL[0] = 0;
		UL[1] = 0;
		UL[2] = 0;
		UL[3] = 0;
		if ((l > 0xffffffffL) || (l < 0)) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"value of int incompatible with Unsigned Long Value UL : "
								+ l);
		} else {
			int t0 = (int) (l & 0xff);
			if (t0 >= 128)
				t0 -= 256;
			int t1 = (int) ((l & 0xff00) >> 8);
			if (t1 >= 128)
				t1 -= 256;
			int t2 = (int) ((l & 0xff0000) >> 16);
			if (t2 >= 128)
				t2 -= 256;
			int t3 = (int) ((l & 0xff000000) >> 24);
			if (t3 >= 128)
				t3 -= 256;
			if (type == BIG_ENDIAN) {
				UL[0] = (byte) t3;
				UL[1] = (byte) t2;
				UL[2] = (byte) t1;
				UL[3] = (byte) t0;
			} else {
				UL[0] = (byte) t0;
				UL[1] = (byte) t1;
				UL[2] = (byte) t2;
				UL[3] = (byte) t3;
			}
		}

		return UL;
	}

	/**
	 * Create a array of byte corresponding to the array of int in the respect
	 * of Unsigned Short in DICOM
	 * 
	 * @param l
	 *            array of long
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if a long is >= 4294967296 (2^32) or <0
	 */
	private byte[] setVR_UL(int type, long[] l) throws NumberFormatException {
		byte[] UL = new byte[getMaxLength() * l.length];
		byte[] b;

		for (int j = 0; j < l.length; j++) {
			b = setVR_UL(type, l[j]);
			UL[j * 4] = b[0];
			UL[j * 4 + 1] = b[1];
			UL[j * 4 + 2] = b[3];
			UL[j * 4 + 3] = b[3];
		}

		return UL;
	}

	/**
	 * Return the String corresponding to an array of byte in the respect of
	 * Code String in DICOM
	 * 
	 * @param tab
	 *            array of byte
	 * @return String the value of the array
	 * @exception FormatException
	 *                if length of value > 16
	 */
	private String getVR_CS(byte tab[]) throws FormatException {
		return getVR_CS(new String(tab));
	}

	/**
	 * Return the String corresponding to an initial String in the respect of
	 * Code String in DICOM
	 * 
	 * @param String
	 *            the initialString
	 * @return String the final value
	 * @exception FormatException
	 *                if length of value > 16
	 */
	private String getVR_CS(String tab) throws FormatException {
		String CS;

		if (tab.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new FormatException(
						"length of Code String Value CS false: " + tab);
		}
		CS = tab.trim();

		return CS;
	}

	/**
	 * Create a array of byte corresponding to the value of a String in the
	 * respect of Code String in DICOM
	 * 
	 * @param s
	 *            String
	 * @return the array of byte
	 * @exception FormatException
	 *                if length of value > 16
	 */
	private byte[] setVR_CS(String s) throws FormatException {
		byte[] CS = null;

		s = formatString(s);
		if (s.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new FormatException(
						"length of Code String Value CS false: " + s);
		}
		CS = s.getBytes();

		return CS;
	}

	/**
	 * Return the String corresponding to an array of byte in the respect of
	 * Unique Identifier String in DICOM
	 * 
	 * @param tab
	 *            array of byte
	 * @return String the value of the identifier
	 * @exception FormatException
	 *                if length of value > 64
	 */
	private String getVR_UI(byte tab[]) throws FormatException {
		return getVR_UI(new String(tab));
	}

	/**
	 * Return the String corresponding to a String in the respect of Unique
	 * Identifier String in DICOM
	 * 
	 * @param tab
	 *            array of byte
	 * @return String the value of the identifier
	 * @exception FormatException
	 *                if length of value > 64
	 */
	private String getVR_UI(String tab) throws FormatException {
		String UI;

		if (tab.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new FormatException(
						"length of Unique Identifier Value UI false: " + tab);
		}
		UI = tab;
		UI = UI.trim();

		return UI;
	}

	/**
	 * Create a array of byte corresponding to the value of a String in the
	 * respect of Unique Identifier String in DICOM
	 * 
	 * @param s
	 *            String
	 * @return the array of byte
	 * @exception FormatException
	 *                if length of value > 64
	 */
	private byte[] setVR_UI(String s) throws FormatException {
		byte[] UI = null;

		s = formatString(s, "\0");
		if (s.length() > getMaxLength()) {
			if (!ignoreLengthError)
				throw new FormatException(
						"length of Unique Identifier Value UI false: " + s);
		}
		UI = s.getBytes();

		return UI;
	}

	/**
	 * Return the int corresponding to an array of byte in the respect of Other
	 * Byte String in DICOM function limited for the moment
	 * 
	 * @param tab
	 *            String
	 * @return int the value
	 */
	private int getVR_OB(byte tab[]) throws FormatException {
		int i = tab.length - 1;
		int j = 1, OB = 0;

		while (i >= 0) {
			OB += ((int) (tab[i])) * j;
			i--;
			j *= 10;
		}

		return OB;
	}

	/**
	 * Return the int corresponding to an array of byte in the respect of Other
	 * Byte String in DICOM function limited for the moment
	 * 
	 * @param tab
	 *            String
	 * @return int the value
	 * @exception NumberFormatException
	 */
	private int getVR_OB(String tab) throws NumberFormatException {
		float OBf = (float) 0.0;

		tab = tab.trim();

		if (tab.length() > 0)
			try {
				OBf = Float.valueOf(tab).intValue();
			} catch (NumberFormatException e) {
				if (!ignoreLengthError)
					throw new NumberFormatException(
							"format of Other Byte Value OB incorrect: " + tab);
			}

		return (int) OBf;
	}

	/**
	 * Create a array of byte corresponding to the value of a int in the respect
	 * of Other Byte String in DICOM function limited for the moment
	 * 
	 * @param i
	 *            int
	 * @return the array of byte
	 */
	private byte[] setVR_OB(int i) throws NumberFormatException {
		byte[] OB = null;

		if (i < 0) {
			if (!ignoreLengthError)
				throw new FormatException(
						"format of Other Byte Value OB incorrect: " + i);
		}
		String OBs = String.valueOf(i);
		OB = new byte[OBs.length()];
		for (int j = 0; j < OBs.length(); j++)
			OB[j] = (byte) (OBs.charAt(j) - '0');

		return OB;
	}

	/**
	 * Return the long corresponding to an array of byte in the respect of
	 * Attribute Tag in DICOM this long integer designate a tag : for example
	 * value 001800FF represents Data Element Tag (0018,00FF)
	 * 
	 * @param tab
	 *            array of byte
	 * @return the long value
	 * @exception NumberFormatException
	 */
	private long getVR_AT(byte tab[]) throws NumberFormatException {
		long AT = 0;
		long b;

		if (tab.length > getMaxLength()) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"length of Attribute Tag AT false: " + tab.length);
		}

		if (tab.length > 0)
			b = tab[0];
		else
			b = 0;
		if (b < 0)
			b += 256;
		AT += b << 16;
		if (tab.length > 1)
			b = tab[1];
		else
			b = 0;
		if (b < 0)
			b += 256;
		AT += b << 24;
		if (tab.length > 0)
			b = tab[2];
		else
			b = 0;
		if (b < 0)
			b += 256;
		AT += b;
		if (tab.length > 0)
			b = tab[3];
		else
			b = 0;
		if (b < 0)
			b += 256;
		AT += b << 8;

		return AT;
	}

	/**
	 * Return the int corresponding to a String in the respect of Attribute Tag
	 * in DICOM this integer designate a tag : for example value 001800FF
	 * represents Data Element Tag (0018,00FF)
	 * 
	 * @param tab
	 *            array of byte
	 * @return int the value
	 * @exception NumberFormatException
	 */
	private int getVR_AT(String tab) throws NumberFormatException {
		float ATf = (float) 0.0;

		tab = tab.trim();

		if (tab.length() > 0)
			try {
				ATf = Float.valueOf(tab).intValue();
			} catch (NumberFormatException e) {
				if (!ignoreLengthError)
					throw new NumberFormatException(
							"format of Attribute Tag Value AT incorrect: "
									+ tab);
			}

		return (int) ATf;
	}

	/**
	 * Create a array of byte corresponding to the value of long corresponding
	 * at an Attribute Tag in the respect of DICOM norm
	 * 
	 * @param l
	 *            long
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if l >= 4294967296 (2^32) or l<0
	 */
	private byte[] setVR_AT(long l) throws NumberFormatException {
		byte[] AT = new byte[getMaxLength()];

		AT[0] = 0;
		AT[1] = 0;
		AT[2] = 0;
		AT[3] = 0;
		if ((l > 0xffffffffL) || (l < 0)) {
			if (!ignoreLengthError)
				throw new NumberFormatException(
						"value of int incompatible with Attribute Tag Value AT : "
								+ l);
		} else {
			int t0 = (int) (l & 0xff);
			if (t0 >= 128)
				t0 -= 256;
			int t1 = (int) ((l & 0xff00) >> 8);
			if (t1 >= 128)
				t1 -= 256;
			int t2 = (int) ((l & 0xff0000) >> 16);
			if (t2 >= 128)
				t2 -= 256;
			int t3 = (int) ((l & 0xff000000) >> 24);
			if (t3 >= 128)
				t3 -= 256;

			AT[0] = (byte) t2;
			AT[1] = (byte) t3;
			AT[2] = (byte) t0;
			AT[3] = (byte) t1;
		}

		return AT;
	}

	/**
	 * Return the vector corresponding to an analyze of an array of byte in the
	 * respect of Tag in DICOM
	 * 
	 * the first element of the vector store the type of tag the second element
	 * store the number of value expected (case multi-value in the array) the
	 * other element are stored in the specific format dependent of the typeVr
	 * 
	 * @see DataElement
	 * 
	 * @param optionEndian
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @param typeVr
	 *            type of tag
	 * @param typeVr
	 *            number of value expected
	 * @param tabParameter
	 *            array of byte
	 * @return the Vector
	 * @exception NumberFormatException
	 *                or FormatException
	 */
	public Vector getValueVR(int optionEndian, int typeVR, int VM,
			byte[] tabParameter) throws NumberFormatException, FormatException {
		return getValueVR(optionEndian, typeVR, VM, tabParameter, null);
	}

	/**
	 * Return the vector corresponding to an analyze of a String in the respect
	 * of Tag in DICOM
	 * 
	 * the first element of the vector store the type of tag the second element
	 * store the number of value expected (case multi-value in the array) the
	 * other element are stored in the specific format dependent of the typeVr
	 * 
	 * @see DataElement
	 * 
	 * @param optionEndian
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @param typeVr
	 *            type of tag
	 * @param typeVr
	 *            number of value expected
	 * @param tabParameter
	 *            array of byte
	 * @return the Vector
	 * @exception NumberFormatException
	 *                or FormatException
	 */
	public Vector getValueVR(int typeVR, int VM, String tabParameter)
			throws NumberFormatException, FormatException {
		return getValueVR(NO_ENDIAN, typeVR, VM, null, tabParameter);
	}

	/**
	 * Return the vector corresponding to an analyze of an array of byte in the
	 * respect of Tag in DICOM
	 * 
	 * the first element of the vector store the type of tag the second element
	 * store the number of value expected (case multi-value in the array) the
	 * other element are stored in the specific format dependent of the typeVr
	 * 
	 * @see DataElement
	 * 
	 * @param optionEndian
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @param typeVr
	 *            type of tag
	 * @param typeVr
	 *            number of value expected
	 * @param tabParameter
	 *            array of byte
	 * @return the Vector
	 * @exception NumberFormatException
	 *                or FormatException
	 */
	private Vector getValueVR(int optionEndian, int typeVR, int VM,
			byte[] tabParameter, String sParameter) {
		Vector v = new Vector(2 + VM);

		v.addElement(new Integer(typeVR));
		v.addElement(new Integer(VM));

		// positionnement de l'indice afin de pouvoir recuperer la longueur
		// maximale
		// aujourd'hui, on suppose que les image sont en niveau de gris positif
		if (typeVR == vrUSorSS)
			typeVR = vrUS;
		indice = getIndiceVR(typeVR);

		Log.d(TAG, "typeVr=" + typeVR);
		Log.d(TAG, "VM=" + VM);
		Log.d(TAG, "indice=" + indice);

		try {
			switch (typeVR) {
			case vrIS:
				int tabInt[];
				if (optionEndian != NO_ENDIAN)
					tabInt = getVR_IS(tabParameter, VM);
				else
					tabInt = getVR_IS(sParameter, VM);
				for (int i = 0; i < VM; i++) {
					Log.d(TAG, "IS=" + tabInt[i]);
					v.addElement(new Integer(tabInt[i]));
				}
				break;
			case vrDS:
				float tabFloat[];
				if (optionEndian != NO_ENDIAN)
					tabFloat = getVR_DS(tabParameter, VM);
				else
					tabFloat = getVR_DS(sParameter, VM);
				for (int i = 0; i < VM; i++) {
					Log.d(TAG, "DS=" + tabFloat[i]);
					v.addElement(new Float(tabFloat[i]));
				}
				break;
			case vrLO:
				if (optionEndian != NO_ENDIAN)
					v.addElement(getVR_LO(tabParameter));
				else
					v.addElement(getVR_LO(sParameter));
				Log.d(TAG, "LO=" + (String) v.elementAt(v.size() - 1));
				break;
			case vrUS:
				int tabUS[];
				if (optionEndian != NO_ENDIAN)
					tabUS = getVR_US(optionEndian, tabParameter, VM);
				else
					tabUS = getVR_US(sParameter, VM);
				for (int i = 0; i < VM; i++) {
					Log.d(TAG, "US=" + tabUS[i]);
					v.addElement(new Integer(tabUS[i]));
				}
				break;
			case vrUL:
				if (optionEndian != NO_ENDIAN)
					v
							.addElement(new Long(getVR_UL(optionEndian,
									tabParameter)));
				else
					v.addElement(new Long(getVR_UL(sParameter)));
				Log.d(TAG, "UL=" + (Long) v.elementAt(v.size() - 1));
				break;
			case vrCS:
				if (optionEndian != NO_ENDIAN)
					v.addElement(getVR_CS(tabParameter));
				else
					v.addElement(getVR_CS(sParameter));
				Log.d(TAG, "CS=" + (String) v.elementAt(v.size() - 1));
				break;
			case vrUI:
				if (optionEndian != NO_ENDIAN)
					v.addElement(getVR_UI(tabParameter));
				else
					v.addElement(getVR_UI(sParameter));
				Log.d(TAG, "UI=" + (String) v.elementAt(v.size() - 1));
				break;
			case vrUSorSS:
				int tabUSSS[];
				if (optionEndian != NO_ENDIAN)
					tabUSSS = getVR_US(optionEndian, tabParameter, VM);
				else
					tabUSSS = getVR_US(sParameter, VM);
				for (int i = 0; i < VM; i++) {
					Log.d(TAG, "US=" + tabUSSS[i]);
					v.addElement(new Integer(tabUSSS[i]));
				}
				break;
			case vrOB:
				if (optionEndian != NO_ENDIAN)
					v.addElement(new Integer(getVR_OB(tabParameter)));
				else
					v.addElement(new Integer(getVR_OB(sParameter)));
				Log.d(TAG, "OB=" + (Integer) v.elementAt(v.size() - 1));
				break;
			case vrAT:
				if (optionEndian != NO_ENDIAN)
					v.addElement(new Long(getVR_AT(tabParameter)));
				else
					v.addElement(new Long(getVR_AT(sParameter)));
				Log.d(TAG, "AT=" + (Long) v.elementAt(v.size() - 1));
				break;
			default:
				if (debug) {
					System.err.println("type non traite " + typeVR);
				}
				if ((typeVR & 0xFF00) == 0x1000) {
					if (optionEndian != NO_ENDIAN)
						v.addElement(new String(tabParameter));
					else
						v.addElement(new String(sParameter));
					System.err.println("cependant element vector");
				}
			}
		} catch (NumberFormatException nfe) {
			v = new Vector(2);

			v.addElement(new Integer(typeVR));
			v.addElement(new Integer(VM));
		} catch (FormatException fe) {
			v = new Vector(2);

			v.addElement(new Integer(typeVR));
			v.addElement(new Integer(VM));
		}

		return v;
	}

	/**
	 * Create a array of byte corresponding to the vector in the respect of
	 * DICOM norm
	 * 
	 * the first element of the vector store the type of tag the second element
	 * store the number of value expected (case multi-value in the array) the
	 * other element are stored in the specific format dependent of the typeVr
	 * 
	 * @see DataElement
	 * 
	 * @param optionEndian
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @param v
	 *            vector
	 * @return the array of byte
	 */
	public byte[] setValueVR(int optionEndian, Vector v) {
		byte[] b = new byte[0];

		try {
			int typeVR, VM;

			typeVR = ((Integer) v.elementAt(0)).intValue();
			VM = ((Integer) v.elementAt(1)).intValue();

			// positionnement de l'indice afin de pouvoir recuperer la longueur
			// maximale
			// aujourd'hui, on suppose que les image sont en niveau de gris
			// positif
			if (typeVR == vrUSorSS)
				typeVR = vrUS;
			indice = getIndiceVR(typeVR);

			Log.d(TAG, "setValueVR:");
			Log.d(TAG, "size=" + v.size());
			Log.d(TAG, "typeVr=" + typeVR);
			Log.d(TAG, "VM=" + VM);
			Log.d(TAG, "indice=" + indice);

			switch (typeVR) {
			case vrIS:
				int[] tab_vrIS = new int[v.size() - 2];
				for (int i = 2; i < v.size(); i++) {
					tab_vrIS[i - 2] = ((Integer) v.elementAt(i)).intValue();
					Log.d(TAG, "vrIS=" + tab_vrIS[i - 2]);
				}
				b = setVR_IS(tab_vrIS);
				break;
			case vrDS:
				float[] tab_vrDS = new float[v.size() - 2];
				for (int i = 2; i < v.size(); i++) {
					tab_vrDS[i - 2] = ((Float) v.elementAt(i)).floatValue();
					Log.d(TAG, "vrDS=" + tab_vrDS[i - 2]);
				}
				b = setVR_DS(tab_vrDS);
				break;
			case vrLO:
				if (v.size() > 2) {
					Log.d(TAG, "vrLO=" + ((String) v.elementAt(2)));
					b = setVR_LO((String) v.elementAt(2));
				}
				break;
			case vrUS:
				int[] tab_vrUS = new int[v.size() - 2];
				for (int i = 2; i < v.size(); i++) {
					tab_vrUS[i - 2] = ((Integer) v.elementAt(i)).intValue();
					Log.d(TAG, "vrUS=" + tab_vrUS[i - 2]);
				}
				b = setVR_US(optionEndian, tab_vrUS);
				break;
			case vrUL:
				long[] tab_vrUL = new long[v.size() - 2];
				for (int i = 2; i < v.size(); i++) {
					tab_vrUL[i - 2] = ((Long) v.elementAt(i)).longValue();
					Log.d(TAG, "vrUL=" + tab_vrUL[i - 2]);
				}
				b = setVR_UL(optionEndian, tab_vrUL);
				break;
			case vrCS:
				if (v.size() > 2) {
					Log.d(TAG, "vrCS=" + ((String) v.elementAt(2)));
					b = setVR_CS((String) v.elementAt(2));
				}
				break;
			case vrUI:
				if (v.size() > 2) {
					Log.d(TAG, "vrUI=" + ((String) v.elementAt(2)));
					b = setVR_UI((String) v.elementAt(2));
				}
				break;
			case vrUSorSS:
				int[] tab_vrUSorSS = new int[v.size() - 2];
				for (int i = 2; i < v.size(); i++) {
					tab_vrUSorSS[i - 2] = ((Integer) v.elementAt(i)).intValue();
					Log.d(TAG, "vrUSorSS=" + tab_vrUSorSS[i - 2]);
				}
				b = setVR_US(optionEndian, tab_vrUSorSS);
				break;
			case vrOB:
				if (v.size() > 2) {
					Log.d(TAG, "vrOB=" + ((Integer) v.elementAt(2)).intValue());
					b = setVR_OB(((Integer) v.elementAt(2)).intValue());
				}
				break;
			case vrAT:
				if (v.size() > 2) {
					Log.d(TAG, "vrAT=" + ((Long) v.elementAt(2)).longValue());
					b = setVR_AT(((Long) v.elementAt(2)).longValue());
				}
				break;
			default:
				Log.d(TAG, "type non traite " + typeVR);
				if ((typeVR & 0xFF00) == 0x1000) {
					if (v.size() > 2) {
						Log.d(TAG, "vrCS=" + ((String) v.elementAt(2)));
						b = setVR_CS((String) v.elementAt(2));
					}
				}
			}

		} catch (NumberFormatException nfe) {
		} catch (FormatException fe) {
		}

		return b;
	}

	/**
	 * Create a array of byte corresponding to the value of a int in the respect
	 * of Unsigned Long in DICOM
	 * 
	 * @param l
	 *            long
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if i >= (2^16) or i<0
	 */
	public byte[] formatLong(int optionEndian, long l)
			throws NumberFormatException {
		// positionnement du type obligatoire
		getIndiceVR(vrUL);
		return setVR_UL(optionEndian, l);
	}

	/**
	 * Create a array of byte corresponding to the value of a int in the respect
	 * of Unsigned Short in DICOM
	 * 
	 * @param i
	 *            int
	 * @param type
	 *            BIG_ENDIAN or LITTLE_ENDIAN
	 * @return the array of byte
	 * @exception NumberFormatException
	 *                if i >= (2^16) or i<0
	 */
	public byte[] formatInt(int optionEndian, int i)
			throws NumberFormatException {
		// positionnement du type obligatoire
		getIndiceVR(vrUS);
		return setVR_US(optionEndian, i);
	}
}
