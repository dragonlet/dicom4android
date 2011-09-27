/** SaveTagDicom.java - Sauvegarde des marques Dicom presentes dans une image
 *
 * @author
 * @version $Revision: 0.6 $ $Date: 2010/03/31 15:16:07 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01e,31Mar2010,andleg   ajout possibilite d'ajouter/remplacer des Tags+updateContextGr0028
 * 01d,23Mar2010,andleg   ajout debug
 * 01c,19Mar2010,andleg   correction pour cas ValueTagDicom sans info (getValue==null)
 * 01b,18Fev2010,andleg   correction pour cas 1 marque DICOM
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
import java.util.StringTokenizer;

import android.util.Log;

public class SaveTagDicom {

	private static final String TAG = "SaveTagDicom";
	private Vector vectorTag = null;
	private int ptr = -1;
	static ValueTagDicom vtdNull = new ValueTagDicom(-1, -1, "");

	private boolean debug = false;

	/**
	 * construct a vector for saving the tags by default
	 * 
	 */
	public SaveTagDicom() {
		vectorTag = new Vector();
	}

	/**
	 * Set the debug
	 * 
	 * @param boolean the value of debug
	 * 
	 */
	public void setDebug(boolean d) {
		debug = d;
	}

	/**
	 * to verify if a this Tag already exist in the vector
	 * 
	 * @param t
	 *            the tag
	 * @return -1 if not then the indice in the vector
	 * 
	 */
	private int isTagExist(TagDicom t) {
		int indice = -1;

		for (int i = 0; i < vectorTag.size(); i++) {
			ValueTagDicom v = (ValueTagDicom) vectorTag.elementAt(i);
			if (v.getTagDicom().equals(t)) {
				indice = i;
				break;
			}
		}

		return indice;
	}

	/**
	 * to add a Tag and its value in the vector
	 * 
	 * @param t
	 *            the tag and its value
	 * 
	 */
	public void addTagDicom(ValueTagDicom t) {
		// filtrage de marque si String egale null
		if ((t.getValue() == null) || (t.getValue().equals("")))
			return;

		// filtrage de la longueur d'un groupe
		if (t.getTagDicom().getElement() > 0) {
			// verifier que la marque n'existe pas deja, sinon la remplacer
			int ind = isTagExist(t.getTagDicom());
			if (ind < 0)
				vectorTag.addElement(t);
			else
				vectorTag.setElementAt(t, ind);
			Log.d(TAG, "SaveTagDicom: marque " + t);
		}
	}

	/**
	 * to add a Tag and its value in the vector
	 * 
	 * @param t
	 *            the tag
	 * @param value
	 *            the value of the tag
	 * 
	 */
	public void addTagDicom(TagDicom t, String value) {
		ValueTagDicom vtd = new ValueTagDicom();
		vtd.setTagDicom(t);
		vtd.setValue(value);
		addTagDicom(vtd);
	}

	/**
	 * to add a Tag and its value in the vector
	 * 
	 * @param t
	 *            the tag
	 * @param value
	 *            the value of the tag store in a vector in the format: first
	 *            element: the type of VR (entier) second element: the number of
	 *            value (case multiple values) the other element(s): the
	 *            value(s)
	 * 
	 */
	public void addTagDicom(TagDicom t, Vector value) {

		int typeVr, VM, i;
		String s = new String();

		ValueTagDicom vtd = new ValueTagDicom();
		vtd.setTagDicom(t);

		// if (t.getGroup()>=0x7FE0)
		Log.i(TAG, "ajout du tag : group=" + t.getGroup() + " element="
				+ t.getElement());

		// construct the string
		typeVr = ((Integer) value.elementAt(0)).intValue();
		VM = ((Integer) value.elementAt(1)).intValue();

		typeVr &= 0xFF00;

		for (i = 2; i < value.size(); i++) {
			switch (typeVr) {
			case 0x1000:
				s = s.concat((String) value.elementAt(i));
				break;
			case 0x2000:
				s = s.concat(((Integer) value.elementAt(i)).toString());
				break;
			case 0x3000:
				s = s.concat(((Float) value.elementAt(i)).toString());
				break;
			case 0x5000:
				s = s.concat(((Long) value.elementAt(i)).toString());
				break;
			}
			if (i < value.size() - 1)
				s = s.concat("\\");
		}

		vtd.setValue(s);
		addTagDicom(vtd);
	}

	public boolean isEndOfList() {
		boolean ret = false;
		if (vectorTag.isEmpty())
			ret = true;
		else {
			if (ptr >= vectorTag.lastIndexOf(vectorTag.lastElement()))
				ret = true;
		}
		return ret;
	}

	public ValueTagDicom getFirstTag() {
		ValueTagDicom t;
		if (!vectorTag.isEmpty()) {
			ptr = 0;
			t = (ValueTagDicom) vectorTag.elementAt(ptr);
		} else
			t = vtdNull;

		return t;
	}

	public ValueTagDicom getNextTag() {
		ValueTagDicom t;
		if (!isEndOfList()) {
			if (ptr < 0)
				ptr = 0;
			else
				ptr++;
			t = (ValueTagDicom) vectorTag.elementAt(ptr);
		} else
			t = vtdNull;
		return t;
	}

	public Vector getVectorDicom() {
		return vectorTag;
	}

	/**
	 * to get the contents of the vector in the form of a string
	 * 
	 * @return the contents
	 * 
	 */
	public String getStringVectorDicom() {
		// allocation d'un buffer de 4094 caracteres
		StringBuffer s = new StringBuffer(4096);

		int el;
		DataElement dt = new DataElement();
		ValueTagDicom vtag;
		TagDicom tag;

		vtag = getFirstTag();

		if (vtag != vtdNull) {
			do {
				tag = vtag.getTagDicom();
				el = dt.findTag(tag);

				Log.d(TAG, "tag: " + tag);
				Log.d(TAG, "el=" + el);

				if (el >= 0) {
					Log.d(TAG, "value=" + vtag.getValue());
					if ((vtag.getValue() != null)
							&& (!((vtag.getValue()).trim().equals("")))) {
						s.append(tag.getGroup());
						s.append("\n");
						s.append(tag.getElement());
						s.append("\n");
						s.append((vtag.getValue()).trim() + "\n");
					}
				}
				vtag = getNextTag();
			} while (vtag != vtdNull);
		}

		Log.d(TAG, "getStringVectorDicom s=" + s.toString());

		return s.toString();
	}

	public void setVectorDicom(String s) {
		int el;
		Integer i;
		DataElement dt = new DataElement();
		ValueTagDicom vtag;
		TagDicom tag;

		if (s != "") {
			StringTokenizer st = new StringTokenizer(s, "\n");

			while (st.hasMoreTokens()) {
				String e = st.nextToken();
				if (e != "") {
					// Initialisation of the tag
					tag = new TagDicom();
					tag.setGroup((Integer.valueOf(e)).intValue());
					e = st.nextToken();
					tag.setElement((Integer.valueOf(e)).intValue());
					e = st.nextToken();

					el = dt.findTag(tag);
					if (el < 0) {
						Log.i(TAG, "Anomalie=" + e);
					}

					// add this tag and its value in the vector
					addTagDicom(tag, e);
				}
			}
		}

	}

	public void setVectorDicom(String s, Gr0028Dicom gr0028) {
		int el;
		Integer i;
		VrDicom vr = new VrDicom();
		ValueTagDicom vtag;
		TagDicom tag;

		if (s != "") {
			StringTokenizer st = new StringTokenizer(s, "\n");

			while (st.hasMoreTokens()) {
				String e = st.nextToken();
				if (e != "") {
					boolean ignore = false;

					// Initialisation of the tag
					tag = new TagDicom();
					try {
						tag.setGroup((Integer.valueOf(e)).intValue());
					} catch (NumberFormatException ex) {
						Log.e(TAG, "setVectorDicom:" + e + " "
								+ ex.getMessage());
						ignore = true;
					}
					e = st.nextToken();
					try {
						tag.setElement((Integer.valueOf(e)).intValue());
					} catch (NumberFormatException ex) {
						Log.i(TAG, "setVectorDicom:" + e + " "
								+ ex.getMessage());
						ignore = true;
					}
					e = st.nextToken();

					if (ignore)
						continue;

					el = vr.findTag(tag);
					if (el < 0) {
						Log.i(TAG, "Anomalie " + e);
					} else if (tag.getGroup() == 0x0028) {
						try {
							Vector vectorVR = vr.getValueVR(vr.getTypeVr(), vr
									.getVM(), e);
							gr0028.setValue(tag, vectorVR);
						} catch (Exception ex) {
							Log.e(TAG, "setVectorDicom: Exception="
									+ ex.getMessage());
						}
					}

					// add this tag and its value in the vector
					addTagDicom(tag, e);
				}
			}
		}

	}

	/**
	 * store the context of GR0028 in the vector
	 * 
	 * @param gr0028
	 *            - the context of DICOM Image
	 * 
	 */
	public void updateContextGr0028(Gr0028Dicom gr0028) {
		TagDicom t;
		Vector v = new Vector();
		DataElement dt = new DataElement();

		// Renseigner Photometric Interpretation
		t = new TagDicom(0x0028, 0x0004);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(gr0028.PhotometricInterpretation);
		addTagDicom(t, v);

		// Renseigner Number of Frames
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0008);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.NumberFrame));
		addTagDicom(t, v);

		// Renseigner Number of Rows
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0010);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.Rows));
		addTagDicom(t, v);

		// Renseigner Number of Columns
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0011);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.Columns));
		addTagDicom(t, v);

		// Renseigner Bits Allocated
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0100);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.BitsAllocated));
		addTagDicom(t, v);

		// Renseigner Bits Stored
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0101);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.BitsStored));
		addTagDicom(t, v);

		// Renseigner High Bit
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0102);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.HighBit));
		addTagDicom(t, v);

		// Renseigner Smallest Value
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0106);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.SmallestValue));
		addTagDicom(t, v);

		// Renseigner Largest Value
		v.removeAllElements();
		t = new TagDicom(0x0028, 0x0107);
		dt.findTag(t);
		v.addElement(new Integer(dt.getTypeVr()));
		v.addElement(new Integer(dt.getVM()));
		v.addElement(new Integer(gr0028.LargestValue));
		addTagDicom(t, v);

		// Renseigner PixelSize si necessaire
		// a prevoir
	}

}
