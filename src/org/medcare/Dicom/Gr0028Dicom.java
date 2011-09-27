/** Gr0028Dicom.java - contexte du stockage de l'image DICOM
 *
 * @author
 * @version $Revision: 0.2 $ $Date: 2010/02/20 22:22:35 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01b,18Fev2010,andleg   prise en compte des grandeurs reelles
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

class Gr0028Dicom {

	private static final String TAG = "Gr0028Dicom";

	// Photometric Interpretation
	String PhotometricInterpretation;

	// Nb lignes de l'image
	int Rows;

	// Nb colonnes de l'image
	int Columns;

	// Bits Allocated by pixel
	int BitsAllocated;

	// Nb significated Bits
	int BitsStored;

	// Position of the significated Bits
	int HighBit;

	// Smallest value
	int SmallestValue;

	// Largest value
	int LargestValue;

	// Number Frame
	int NumberFrame;

	// Pixel Spacing: first distance in Rows between two pixels, then distance
	// in Columns
	boolean pixelSizeValid;
	float[] PixelSpacing = new float[2];

	public Gr0028Dicom() {
		Rows = 0;
		Columns = 0;
		BitsAllocated = 0;
		BitsStored = 0;
		HighBit = 0;
		NumberFrame = 0;
		PixelSpacing[0] = (float) 0.0;
		PixelSpacing[1] = (float) 0.0;
		pixelSizeValid = false;
	}

	public boolean setValue(TagDicom tag, Vector v) {
		int typeVr, VM, i;
		boolean ret = true;

		typeVr = ((Integer) v.elementAt(0)).intValue();
		VM = ((Integer) v.elementAt(1)).intValue();

		typeVr &= 0xFF00;

		if (2 + VM != v.size())
			Log.i(TAG, "Anomalie taille vecteur");

		for (i = 2; i < v.size(); i++) {
			switch (typeVr) {
			case 0x1000:
				ret &= setValueString(tag, (String) v.elementAt(i), i - 2);
				break;
			case 0x2000:
				ret &= setValueInteger(tag, ((Integer) v.elementAt(i))
						.intValue(), i - 2);
				break;
			case 0x3000:
				ret &= setValueFloat(tag,
						((Float) v.elementAt(i)).floatValue(), i - 2);
				break;
			case 0x5000:
				ret &= setValueLong(tag, ((Long) v.elementAt(i)).longValue(),
						i - 2);
				break;
			}
		}

		return ret;
	}

	private boolean setValueLong(TagDicom tag, long value, int indice) {

		boolean ret;

		if (tag.getGroup() != 0x0028)
			return false;

		ret = true;

		switch (tag.getElement()) {
		default:
			ret = false;
		}

		return ret;
	}

	private boolean setValueInteger(TagDicom tag, int value, int indice) {

		boolean ret;

		if (tag.getGroup() != 0x0028)
			return false;

		ret = true;

		switch (tag.getElement()) {
		case 0x0008:
			NumberFrame = value;
			break;
		case 0x0010:
			Rows = value;
			break;
		case 0x0011:
			Columns = value;
			break;
		case 0x0100:
			BitsAllocated = value;
			break;
		case 0x0101:
			BitsStored = value;
			break;
		case 0x0102:
			HighBit = value;
			break;
		case 0x0106:
			SmallestValue = value;
			break;
		case 0x0107:
			LargestValue = value;
			break;
		default:
			ret = false;
		}

		return ret;
	}

	private boolean setValueString(TagDicom tag, String value, int indice) {

		boolean ret;

		if (tag.getGroup() != 0x0028)
			return false;

		ret = true;

		switch (tag.getElement()) {
		case 0x0004:
			PhotometricInterpretation = new String(value.trim());
			break;
		default:
			ret = false;
		}

		return ret;
	}

	private boolean setValueFloat(TagDicom tag, float value, int indice) {

		boolean ret;

		if (tag.getGroup() != 0x0028)
			return false;

		ret = true;

		switch (tag.getElement()) {
		case 0x0030:
			PixelSpacing[indice] = value;
			pixelSizeValid = true;
			break;
		default:
			ret = false;
		}

		return ret;
	}
}
