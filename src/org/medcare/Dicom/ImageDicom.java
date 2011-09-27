/** ImageDicom.java - Image DICOM
 *
 * @author
 * @version $Revision: 0.9 $ $Date: 2010/05/22 15:16:48 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01h,22May2010,andleg	setVectorTagDicom reinitialize the saved list of DICOM tags 
 * 01g,01Avr2010,andleg   correction bug (changement tag lors de la sauvegarde)
 * 01f,21Avr2010,andleg   ajout methode getBaseDicomMonochrome
 * 01e,31Mar2010,andleg   ajout creation automatique des tags definissant les caracteristiques d'1 image
 *                      ajout methode permettant d'inclure des tags dans une image DICOM
 * 01d,19Mar2010,andleg   modif. suite sauvegarde DICOM
 * 01c,09Mar2010,andleg   correction bug lecture donnees (cas lecture 0 octets)
 * 01b,18Fev2010,andleg   prise en compte des grandeurs reelles (ajout points d'entree)
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.util.Vector;
import java.util.Hashtable;

import android.util.Log;

public class ImageDicom {

	protected Gr7FE0Dicom gr7FE0 = new Gr7FE0Dicom();
	protected Gr0028Dicom gr0028 = new Gr0028Dicom();
	protected SaveTagDicom std = new SaveTagDicom();
	protected boolean errorReadDicom = false;
	protected boolean errorWriteDicom = false;
	private static final String TAG = "ImageDicom";

	// protected Frame frame=null;

	protected boolean debug = true;
	protected long length = -1;
	// protected PanelDicom pdic=null;

	// by default, the type of storage is LITTLE_ENDIAN
	protected int typeStorage = VrDicom.LITTLE_ENDIAN;
	protected int typeStorageVR = VrDicom.EXPLICIT;

	/**
	 * construct an ImageDicom by default in this case, there isn't yet an image
	 * affected to this object
	 * 
	 */
	public ImageDicom() {
	}

	/**
	 * construct an ImageDicom as a monoframe in gray level (i.e. one image in
	 * the object)
	 * 
	 * @param stag
	 *            represent the list of element of the image
	 * @param Rows
	 *            number of Rows in the 2D array (i.e. the height of the image)
	 * @param Columns
	 *            number of Columns in the 2D array (i.E. the width of the
	 *            image)
	 * @param image
	 *            represent the image in gray level
	 * 
	 */
	public ImageDicom(String stag, int Rows, int Columns, int[][] image) {
		try {
			std.setVectorDicom(stag, gr0028);
		} catch (Exception e) {
			Log.e(TAG, "ImageDicom: e=" + e.getMessage());
			e.printStackTrace();
		}
		setRectangularROIofImageMonochrome(new Rectangle(Rows, Columns), image);
	}

	/**
	 * construct an ImageDicom as a multiframe in gray level (i.e. eventualy one
	 * image or more in the object)
	 * 
	 * @param stag
	 *            represent the list of element of the image
	 * @param NbFrames
	 *            number of Frame in the 3D array (i.e. the number of images)
	 * @param Rows
	 *            number of Rows in the 3D array (i.e. the height of the image)
	 * @param Columns
	 *            number of Columns in the 3D array (i.E. the width of the
	 *            image)
	 * @param images
	 *            represent the images in gray level
	 * 
	 */
	public ImageDicom(String stag, int nbFrames, int Rows, int Columns,
			int[][][] images) {
		try {
			std.setVectorDicom(stag, gr0028);
		} catch (Exception e) {
			Log.e(TAG, "ImageDicom: e=" + e.getMessage());
			e.printStackTrace();
		}
		setBaseDicomMonochrome(nbFrames, Rows, Columns, images);
	}

	/**
	 * Set the frame of the application
	 * 
	 * @param fr
	 *            the frame
	 * 
	 */
	/*
	 * public void setFrame(Frame fr) { frame=fr; }
	 */

	/**
	 * Set the debug
	 * 
	 * @param boolean the value of debug
	 * 
	 */
	public void setDebug(boolean d) {
		debug = d;
		std.setDebug(d);
		gr7FE0.setDebug(d);
	}

	/**
	 * Set the length of the image DICOM
	 * 
	 * @param l
	 *            the length
	 * 
	 */
	public void setLength(long l) {
		length = l;
		if (length > 0) {
			// pdic=new PanelDicom(length);
		}
	}

	/**
	 * Return the panel DICOM
	 * 
	 * @return the panel
	 * 
	 */
	/*
	 * public PanelDicom getPanelDicom() { return pdic; }
	 */

	/**
	 * Realize the reading of a DICOM stream
	 * 
	 * @param bin
	 *            stream d'entree correspondant a un fichier, une URL, ...
	 * @return boolean true if success
	 * @exception Exception
	 *                if there is a problem during the reading of data
	 * 
	 */
	public boolean read(BufferedInputStream bin) throws Exception {
		ImageDicomRead idr = new ImageDicomRead(this, bin);
		return isImageLoad();
	}

	/**
	 * Realize the writing of a DICOM image on a stream
	 * 
	 * @param bou
	 *            stream d'ecriture correspondant a un fichier, une URL, ...
	 * @return boolean true if success
	 * @exception Exception
	 *                if there is a problem during the reading of data
	 * 
	 */
	public boolean write(BufferedOutputStream bou) throws Exception {
		ImageDicomWrite idr = new ImageDicomWrite(this, bou);
		return idr.isImageWrite();
	}

	/**
	 * free the memory occuped by the object
	 * 
	 */
	public void freeMemory() {
		if (isImageLoad())
			gr7FE0.freeMemory();
	}

	/**
	 * indicate if an image is load in this object
	 * 
	 * @return true if image loading
	 */
	public boolean isImageLoad() {
		boolean b = false;
		if (!errorReadDicom)
			b = gr7FE0.isImageMonochromeLoad();
		return b;
	}

	/**
	 * Return the 3B Base if image monochrome is load
	 * 
	 */
	public int[][][] getBaseDicomMonochrome() {
		if (isImageLoad())
			return gr7FE0.getBaseDicomMonochrome();
		else
			return null;
	}

	/**
	 * get the number of rows in the image (height of the image)
	 * 
	 * @return int - the number of rows
	 */
	public int getRows() {
		int rows = 0;
		if (isImageLoad())
			rows = gr7FE0.getRows();
		return rows;
	}

	/**
	 * get the number of columns in the image (width of the image)
	 * 
	 * @return int - the number of columns
	 */
	public int getColumns() {
		int columns = 0;
		if (isImageLoad())
			columns = gr7FE0.getColumns();
		return columns;
	}

	/**
	 * get the number of frames in the image (case 3D DICOM Image)
	 * 
	 * @return int - the number of frames (1 if 2D DICOM Image)
	 */
	public int getFrames() {
		int nbframes = 0;
		if (isImageLoad())
			nbframes = gr7FE0.getNumberFrames();
		return nbframes;
	}

	public int getLargestValue() {
		return gr7FE0.getLargestValue();
	}

	public int getSmallestValue() {
		return gr7FE0.getSmallestValue();
	}

	public int getLargestValueFrame(int f) {
		if (getFrames() == 1)
			return gr7FE0.getLargestValue();
		else
			return gr7FE0.getLargestValueFrame(f);

	}

	public int getSmallestValueFrame(int f) {
		if (getFrames() == 1)
			return gr7FE0.getSmallestValue();
		else
			return gr7FE0.getSmallestValueFrame(f);
	}

	public int getIndicePlanMax() {
		return gr7FE0.getIndicePlanMax();
	}

	public int[] getImageARGB() {
		int[] tab = null;
		if (!errorReadDicom)
			tab = gr7FE0.imageARGB();
		return tab;
	}

	public int[] imageARGB(int min, int max, int frame) {
		int[] tab = null;
		if (!errorReadDicom)
			tab = gr7FE0.imageARGB(min, max, frame);
		return tab;
	}

	public int[] imageARGB(int min, int max, int frame, Rectangle ROI) {
		int[] tab = null;
		if (!errorReadDicom)
			tab = gr7FE0.imageARGB(min, max, frame, ROI);
		return tab;
	}

	public int[][] getRectangularROIofImageMonochrome(Rectangle ROI) {
		int[][] tab = null;
		if (!errorReadDicom)
			tab = gr7FE0.getRectangularROIofImageMonochrome(ROI);
		return tab;
	}

	public int[][] getRectangularROIofImageMonochrome(int frame, Rectangle ROI) {
		int[][] tab = null;
		if (!errorReadDicom)
			tab = gr7FE0.getRectangularROIofImageMonochrome(frame, ROI);
		return tab;
	}

	public void setRectangularROIofImageMonochrome(Rectangle ROI,
			int[][] imageMonochrome) {
		gr7FE0.setRectangularROIofImageMonochrome(ROI, imageMonochrome);
		gr0028.Rows = gr7FE0.getRows();
		gr0028.Columns = gr7FE0.getColumns();
		gr0028.NumberFrame = gr7FE0.getNumberFrames();
		gr0028.SmallestValue = gr7FE0.getSmallestValue();
		gr0028.LargestValue = gr7FE0.getLargestValue();
		int bs = gr7FE0.getIndicePlanMax();
		if (bs > 8)
			gr0028.BitsAllocated = 16;
		else
			gr0028.BitsAllocated = 8;
		gr0028.BitsStored = bs;
		gr0028.HighBit = bs - 1;
		gr0028.PhotometricInterpretation = new String("MONOCHROME1");
		std.updateContextGr0028(gr0028);
	}

	public void setBaseDicomMonochrome(int NumberFrame, int Rows, int Columns,
			int[][][] baseMonochrome) {
		gr7FE0.setBaseDicomMonochrome(NumberFrame, Rows, Columns,
				baseMonochrome);
		gr0028.Rows = gr7FE0.getRows();
		gr0028.Columns = gr7FE0.getColumns();
		gr0028.NumberFrame = gr7FE0.getNumberFrames();
		gr0028.SmallestValue = gr7FE0.getSmallestValue();
		gr0028.LargestValue = gr7FE0.getLargestValue();
		int bs = gr7FE0.getIndicePlanMax();
		if (bs > 8)
			gr0028.BitsAllocated = 16;
		else
			gr0028.BitsAllocated = 8;
		gr0028.BitsStored = bs;
		gr0028.HighBit = bs - 1;
		gr0028.PhotometricInterpretation = new String("MONOCHROME1");
		std.updateContextGr0028(gr0028);
	}

	public Vector getVectorTagDicom() {
		Vector v = null;
		if (!errorReadDicom)
			v = std.getVectorDicom();
		return v;
	}

	public String getStringVectorTagDicom() {
		String s = null;
		if (isImageLoad()) {
			s = std.getStringVectorDicom();
		}
		return s;
	}

	/**
	 * set a list of DICOM tags. Be careful, reset the old list
	 * 
	 * @param s
	 *            stores the DICOM tags
	 * 
	 */
	public void setVectorTagDicom(String s) {
		std = new SaveTagDicom();
		std.setVectorDicom(s);
	}

	public void addTagDicom(int g, int e, String value) {
		TagDicom t = new TagDicom(g, e);
		DataElement dt = new DataElement();

		// verifier que l'element a ete troue
		int i = dt.findTag(t);
		if (i >= 0) {
			try {
				VrDicom vr = new VrDicom();
				Vector v = vr.getValueVR(dt.getTypeVr(), dt.getVM(), value);
				std.addTagDicom(t, v);
			} catch (Exception ex) {
			}
		}
	}

	public String getStringInfoDicom(int group) {
		String st = new String("");
		DataElement dt = new DataElement();
		TagDicom tag;
		int el;
		ValueTagDicom vtag;

		vtag = std.getFirstTag();

		if (vtag == SaveTagDicom.vtdNull) {
			st = "No Informations";
		} else {
			do {
				tag = vtag.getTagDicom();
				el = dt.findTag(tag);
				if (el >= 0) {
					if ((group == 0) || (group == tag.getGroup()))
						st = new String(st + dt.getDenomination() + ":"
								+ (vtag.getValue()).trim() + "\n");
				}
				vtag = std.getNextTag();
			} while (vtag != SaveTagDicom.vtdNull);
		}

		return st;
	}

	public boolean isPixelSizeValid() {
		return gr0028.pixelSizeValid;
	}

	public void setPixelSize(float distanceInRows, float distanceInColumns) {
		// add the tag and its value in the vector
		TagDicom tag = new TagDicom();
		tag.setTag((int) 0x0028, (int) 0x0030);
		Vector v = new Vector(4);
		v.addElement(new Integer(DataElement.vrDS));
		v.addElement(new Integer(2));
		v.addElement(new Float(distanceInRows));
		v.addElement(new Float(distanceInColumns));
		std.addTagDicom(tag, v);
		gr0028.setValue(tag, v);
	}

	public float getDistanceInRows() {
		if (gr0028.pixelSizeValid)
			return gr0028.PixelSpacing[0];
		else
			return (float) 0.0;
	}

	public float getDistanceInColumns() {
		if (gr0028.pixelSizeValid)
			return gr0028.PixelSpacing[0];
		else
			return (float) 0.0;
	}

	public Hashtable getListGroupDicom() {
		DataElement dt = new DataElement();
		TagDicom tag;
		int el;
		ValueTagDicom vtag;

		Hashtable h = new Hashtable();
		vtag = std.getFirstTag();

		// Construction de la Hashtable
		if (vtag != SaveTagDicom.vtdNull) {
			do {
				tag = vtag.getTagDicom();
				el = dt.findTag(tag);
				if (el >= 0) {
					Object s = h.get(new Integer(tag.getGroup()));
					if (s == null)
						h.put(new Integer(tag.getGroup()), dt
								.getDenominationGroup());
				}
				vtag = std.getNextTag();
			} while (vtag != SaveTagDicom.vtdNull);
		}

		return h;
	}

}

/**
 * TimeOutDicom.java - gestion of timeout for reading/writing a stream DICOM
 * 
 */

/*
 * class TimeOutDicom extends Thread {
 * 
 * static boolean bTimeOut = false; private int timeOut = 0; private Thread to =
 * null;
 * 
 * public TimeOutDicom(int timeOut) { this.timeOut = timeOut; bTimeOut = false;
 * }
 * 
 * public void runTimeOut() { to = new Thread(this); to.start(); }
 * 
 * public void stopTimeOut() { to.stop(); }
 * 
 * public void run() { bTimeOut = false; try { sleep(timeOut); bTimeOut = true;
 * } catch (InterruptedException ie) { } }
 * 
 * public static boolean isTimeOut() { return bTimeOut; } }
 */

/**
 * ImageDicomRead.java - Lecture d'un stream Dicom
 * 
 */

class ImageDicomRead extends Thread {

	private static final String TAG = "ImageDicomRead";
	private ImageDicom id;
	// private TimeOutDicom tod = null;
	private BufferedInputStream bin = null;

	public ImageDicomRead(ImageDicom id, BufferedInputStream bin) {

		this.bin = bin;

		// mettre timeOut de 10 secondes sur lecture de bloc
		// tod = new TimeOutDicom(10000);

		this.id = id;
		Thread t = new Thread(this);
		t.start();

		boolean to = false;

		while ((t.isAlive()) && (!to)) {

			// endormir le processus pendant quelque secondes
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				t.stop();
				return;
			}
			// to = TimeOutDicom.isTimeOut();
		}
		if (to)
			t.stop();
	}

	private int readDataDicom(byte[] tab, int nbByte) {
		int ret = 0, i = 0;

		while (i < nbByte) {

			try {
				// tod.runTimeOut();
				ret = bin.read(tab, i, nbByte - i);
				// tod.stopTimeOut();
			} catch (IOException e) {
				Log.e(TAG, "IO Error: " + e.getMessage());
				ret = -1;
				// tod.stopTimeOut();
				break;
			}

			if (ret <= 0)
				break;

			i += ret;
		}

		if (ret <= 0)
			return -1;

		/*
		 * if (id.getPanelDicom()!=null) {
		 * id.getPanelDicom().updateLength(nbByte); }
		 */

		return nbByte;
	}

	private int getValueLength(int length, byte tab[]) throws Exception {

		int valueLength, t0 = 0, t1 = 0, t2 = 0, t3 = 0;

		if ((length != 1) && (length != 2) && (length != 4)) {
			throw new Exception("length of Value Length false");
		} else {
			t0 = tab[0];
			if (t0 < 0)
				t0 += 256;
			if (length > 1) {
				t1 = tab[1];
				if (t1 < 0)
					t1 += 256;
				if (length > 2) {
					t2 = tab[2];
					if (t2 < 0)
						t2 += 256;
					t3 = tab[3];
					if (t3 < 0)
						t3 += 256;
				}
			}
			if (id.typeStorage == VrDicom.BIG_ENDIAN) {
				if (length == 1)
					valueLength = t0;
				else if (length == 2)
					valueLength = (((int) t0) << 8) + ((int) t1);
				else
					valueLength = (((int) t0) << 24) + (((int) t1) << 16)
							+ (((int) t2) << 8) + ((int) t3);
			} else {
				if (length == 1)
					valueLength = t0;
				else if (length == 2)
					valueLength = (((int) t1) << 8) + ((int) t0);
				else
					valueLength = (((int) t3) << 24) + (((int) t2) << 16)
							+ (((int) t1) << 8) + ((int) t0);
			}
		}
		return valueLength;
	}

	public int readImage() {

		int BaseDicomMonochrome[][][];
		int Rows;
		int Columns;
		int NumberFrame;

		byte tabForData[];
		int totalByte = 0;
		int nbByteToRead;
		int mask, maskEl;
		int i, j, nbByte, data, t0, t1;

		Log.d(TAG, "readImage Image=" + id.gr0028.PhotometricInterpretation);

		if ((!id.gr0028.PhotometricInterpretation.equals("MONOCHROME1"))
				&& (!id.gr0028.PhotometricInterpretation.equals("MONOCHROME2")))
			return 0;

		if (id.gr0028.NumberFrame <= 0) {
			id.gr0028.NumberFrame = 1;
		}

		switch (id.gr0028.BitsAllocated) {
		case 16:
			nbByteToRead = 2;
			break;
		case 8:
			nbByteToRead = 1;
			break;
		default:
			nbByteToRead = -1;
		}

		Log.d(TAG, "Largest value=" + id.gr0028.LargestValue);
		Log.d(TAG, "Smallest value=" + id.gr0028.SmallestValue);

		// creation of the mask
		mask = 0;
		for (i = 0; i < id.gr0028.BitsStored; i++) {
			maskEl = 1 << i;
			mask += maskEl;
		}
		if (id.gr0028.HighBit > id.gr0028.BitsStored)
			mask <<= (id.gr0028.HighBit - id.gr0028.BitsStored);

		// Reading of the image
		if (nbByteToRead != -1) {

			Rows = id.gr0028.Rows;
			Columns = id.gr0028.Columns;
			NumberFrame = id.gr0028.NumberFrame;

			BaseDicomMonochrome = new int[NumberFrame][Rows][Columns];

			tabForData = new byte[nbByteToRead * Columns];
			byte[] tabForData2 = new byte[nbByteToRead];

			try {
				for (int k = 0; k < NumberFrame; k++)
					for (i = 0; i < Rows; i++) {
						nbByte = readDataDicom(tabForData, nbByteToRead
								* Columns);
						totalByte += nbByte;
						if (nbByte != nbByteToRead * Columns) {
							Rows = 0;
							Columns = 0;
							NumberFrame = 0;
							break;
						}
						for (j = 0; j < Columns; j++) {
							tabForData2[0] = tabForData[j * nbByteToRead];
							if (nbByteToRead > 1)
								tabForData2[1] = tabForData[j * nbByteToRead
										+ 1];
							data = getValueLength(nbByteToRead, tabForData2);
							data &= mask;
							if (id.gr0028.HighBit > id.gr0028.BitsStored)
								data >>= (id.gr0028.HighBit - id.gr0028.BitsStored);
							BaseDicomMonochrome[k][i][j] = data;
						}
					}

				id.gr7FE0.setBaseDicomMonochrome(NumberFrame, Rows, Columns,
						BaseDicomMonochrome);

			} catch (Exception e) {
				Log.e(TAG, "Error: " + e.getMessage());
			}
		}

		return totalByte;
	}

	// processus de lecture d'une image DICOM
	public void run() {
		VrDicom vr = new VrDicom();
		TagDicom tag = new TagDicom();
		byte tabForTag[] = new byte[4];
		byte tabForVR[] = new byte[2];
		int indiceElement;
		int typeVR;
		int indiceVR;
		int valueLength;
		Vector vectorVR;
		long ret = 1;

		id.errorReadDicom = false;
		vr.setDebug(id.debug);

		try {

			while (ret > 0) {

				// read the tag of DICOM file
				ret = readDataDicom(tabForTag, 4);
				if (ret < 0) {
					Log.d(TAG, "Fin de fichier");
					break;
				}

				tag.setTag(id.typeStorage, tabForTag);

				Log.d(TAG, "Group=" + tag.getGroup());
				Log.d(TAG, "Element=" + tag.getElement());

				if ((tabForTag[0] == (byte) 'D')
						&& (tabForTag[1] == (byte) 'I')
						&& (tabForTag[2] == (byte) 'C')
						&& (tabForTag[3] == (byte) 'M')) {
					// Some manufacturers put this tag in the file
					Log.d(TAG, "Tag : file Dicom");
					continue;
				}

				indiceElement = vr.findTag(tag);
				if (indiceElement == -1) {
					Log.d(TAG, "------Element not found");

					// skip this tag

					// read 2 bytes (before)
					// if VR not implicit then field VR exist
					ret = readDataDicom(tabForVR, 2);
					if (ret != 2)
						break;

					String svVR = new String(tabForVR);
					indiceVR = vr.getIndiceVR(svVR);

					if (indiceVR >= 0) {
						// we are in the scheme Explicit VR
						if ((indiceVR == VrDicom.vrOB)
								|| (indiceVR == VrDicom.vrOW)
								|| (indiceVR == VrDicom.vrSQ)) {
							// skip 2 bytes
							ret = readDataDicom(tabForVR, 2);
							if (ret != 2)
								break;
							ret = readDataDicom(tabForTag, 4);
							if (ret != 4)
								break;
							valueLength = getValueLength(4, tabForTag);
						} else {
							ret = readDataDicom(tabForTag, 2);
							if (ret != 2)
								break;
							valueLength = getValueLength(2, tabForTag);
						}

					} else {
						// we consider that the two bytes (read before) are a
						// part of value Length
						// and we are in the scheme Implicit VR

						ret = readDataDicom(tabForTag, 2);
						if (ret != 2)
							break;
						tabForTag[2] = tabForTag[0];
						tabForTag[3] = tabForTag[1];
						tabForTag[0] = tabForVR[0];
						tabForTag[1] = tabForVR[1];

						valueLength = getValueLength(4, tabForTag);
					}

					if (valueLength > 0)
						bin.skip(valueLength);

					continue;

				}

				// tag found in database
				typeVR = vr.getTypeVr();
				if (typeVR != VrDicom.retired_element) {
					Log.d(TAG, "typeVr=" + typeVR);

					// I read the two next bytes and then compare with the Value
					// Representation

					// if VR not implicit then field VR exist
					ret = readDataDicom(tabForVR, 2);
					if (ret != 2)
						break;

					String svVR = new String(tabForVR);
					indiceVR = vr.getIndiceVR(svVR);

					Log.d(TAG, "indiceVr=" + indiceVR);

					if ((indiceVR == typeVR)
							|| ((typeVR == VrDicom.vrUSorSS) && ((indiceVR == VrDicom.vrUS) || (indiceVR == VrDicom.vrSS)))
							|| ((typeVR == VrDicom.vrOWorOB) && ((indiceVR == VrDicom.vrOW) || (indiceVR == VrDicom.vrOB)))) {

						// then we have a tag with explicit value
						if ((indiceVR == VrDicom.vrOB)
								|| (indiceVR == VrDicom.vrOW)
								|| (indiceVR == VrDicom.vrSQ)) {
							// skip 2 bytes
							ret = readDataDicom(tabForVR, 2);
							if (ret != 2)
								break;
							ret = readDataDicom(tabForTag, 4);
							if (ret != 4)
								break;
							valueLength = getValueLength(4, tabForTag);
						} else {
							ret = readDataDicom(tabForTag, 2);
							if (ret != 2)
								break;
							valueLength = getValueLength(2, tabForTag);
						}

					} else {

						Log.d(TAG, "Scheme implicit VR");

						// we consider that the two bytes (read before) are a
						// part of value Length
						// and we are in the scheme Implicit VR

						ret = readDataDicom(tabForTag, 2);
						if (ret != 2)
							break;
						tabForTag[2] = tabForTag[0];
						tabForTag[3] = tabForTag[1];
						tabForTag[0] = tabForVR[0];
						tabForTag[1] = tabForVR[1];

						valueLength = getValueLength(4, tabForTag);

					}

				} else {

					// the tag is retired then there is no treatment
					// read this element and skip

					Log.d(TAG, "Retired element\n");

					ret = readDataDicom(tabForTag, 4);
					if (ret != 4)
						break;
					valueLength = getValueLength(4, tabForTag);

					byte tabParameter[] = new byte[valueLength];
					ret = readDataDicom(tabParameter, valueLength);

					if (ret < valueLength)
						break;

					continue;
				}

				Log.d(TAG, "valueLength=" + valueLength);

				// Analyze the parameter
				if (valueLength > 0) {

					if ((tag.getGroup() == 0x7FE0)
							&& (tag.getElement() == 0x0010)) {
						Log.d(TAG, "lecture image");
						ret = readImage();
						if ((ret != valueLength))
							Log.d(TAG, "octets lus=" + ret);
					} else {

						byte tabParameter[] = new byte[valueLength];

						ret = readDataDicom(tabParameter, valueLength);

						if (ret < valueLength) {
							Log.d(TAG, "pb lecture des donnees : lus=" + ret);
							break;
						}

						vectorVR = vr.getValueVR(VrDicom.LITTLE_ENDIAN, typeVR,
								vr.getVM(), tabParameter);
						if (tag.getGroup() == 0x0028)
							if (id.gr0028.setValue(tag, vectorVR))
								Log.d(TAG, "Pris en compte");

						// add the tag and its value in the vector
						if (vectorVR.size() > 2)
							id.std.addTagDicom(tag, vectorVR);

					}

				}

			}

			if (id.isImageLoad())
				Log.d(TAG, "Image chargee");

		} catch (IOException e) {
			Log.e(TAG, "IO Error: " + e.getMessage());
			id.errorReadDicom = true;
		} catch (Exception e) {
			Log.e(TAG, "Error: " + e.getMessage());
			id.errorReadDicom = true;
		}

	}

}

/**
 * ImageDicomWrite.java - Ecriture d'une image DICOM sur un stream
 * 
 */

class ImageDicomWrite extends Thread {

	private static final String TAG = "ImageDicomWrite";
	private ImageDicom id;
	// private TimeOutDicom tod = null;
	private BufferedOutputStream bou = null;
	private VrDicom vr = null;

	private boolean imageWrite = false;

	public ImageDicomWrite(ImageDicom id, BufferedOutputStream bou) {

		this.bou = bou;

		// mettre timeOut de 10 secondes sur ecriture de bloc
		// tod = new TimeOutDicom(10000);

		this.id = id;
		Thread t = new Thread(this);
		t.start();

		boolean to = false;

		while ((t.isAlive()) && (!to)) {

			// endormir le processus pendant quelque secondes
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				t.stop();
				return;
			}
			// to = TimeOutDicom.isTimeOut();
		}
		if (to)
			t.stop();
	}

	public boolean isImageWrite() {
		return (!id.errorWriteDicom) & (imageWrite);
	}

	private int writeDataDicom(byte[] tab, int nbByte) {
		int ret = 0;

		try {
			// tod.runTimeOut();
			bou.write(tab, 0, nbByte);
			bou.flush();
			// tod.stopTimeOut();
			ret = nbByte;
		} catch (IOException e) {
			Log.e(TAG, "IO Error: " + e.getMessage());
			ret = -1;
			id.errorWriteDicom = true;
			// tod.stopTimeOut();
		}

		return ret;
	}

	private void formatExplicitImplicitVR(int typeVR, String VR_String,
			long valueLength) {

		int ret;

		byte[] tabForVR = new byte[2];

		if (id.typeStorageVR == VrDicom.EXPLICIT) {

			tabForVR[0] = (byte) VR_String.charAt(0);
			tabForVR[1] = (byte) VR_String.charAt(1);

			ret = writeDataDicom(tabForVR, 2);
			if (ret != 2)
				return;

			if ((typeVR == VrDicom.vrOB) || (typeVR == VrDicom.vrOW)
					|| (typeVR == VrDicom.vrSQ) || (typeVR == VrDicom.vrOWorOB)) {

				tabForVR[0] = (byte) 0;
				tabForVR[1] = (byte) 0;

				ret = writeDataDicom(tabForVR, 2);
				if (ret != 2)
					return;

				// ecrire valueLength
				byte[] b2 = vr.formatLong(id.typeStorage, valueLength);
				ret = writeDataDicom(b2, 4);
				if (ret != 4)
					return;

			} else {

				// ecrire valueLength
				byte[] b2 = vr.formatInt(id.typeStorage, (int) valueLength);
				ret = writeDataDicom(b2, 2);
				if (ret != 2)
					return;
			}

		} else {

			// ecrire valueLength
			byte[] b2 = vr.formatLong(id.typeStorage, valueLength);
			ret = writeDataDicom(b2, 4);
			if (ret != 4)
				return;
		}

		Log.d(TAG, "formatExplicitImplicitVR OK");

		return;
	}

	private String vrGr7FE0() {
		String s = null;
		switch (id.gr0028.BitsAllocated) {
		case 16:
			s = new String("OW");
			break;
		case 8:
			s = new String("OB");
			break;
		default:
			s = new String("");
		}
		return s;
	}

	private long lengthGr7FE0() {
		long nbByteToWrite;
		switch (id.gr0028.BitsAllocated) {
		case 16:
			nbByteToWrite = 2;
			break;
		case 8:
			nbByteToWrite = 1;
			break;
		default:
			return (long) -1;
		}
		return nbByteToWrite * id.gr0028.Rows * id.gr0028.Columns
				* id.gr0028.NumberFrame;
	}

	public int writeImage() {

		int Rows;
		int Columns;
		int NumberFrame;

		byte tabForData[];
		int totalByte = 0;
		int nbByteToWrite;
		int nbByte;

		Log.d(TAG, "writeImage Image=" + id.gr0028.PhotometricInterpretation);

		if ((!id.gr0028.PhotometricInterpretation.equals("MONOCHROME1"))
				&& (!id.gr0028.PhotometricInterpretation.equals("MONOCHROME2")))
			return 0;

		if (id.gr0028.NumberFrame <= 0) {
			id.gr0028.NumberFrame = 1;
		}

		switch (id.gr0028.BitsAllocated) {
		case 16:
			nbByteToWrite = 2;
			break;
		case 8:
			nbByteToWrite = 1;
			break;
		default:
			nbByteToWrite = -1;
		}

		// Writing the image
		if (nbByteToWrite != -1) {

			Rows = id.gr0028.Rows;
			Columns = id.gr0028.Columns;
			NumberFrame = id.gr0028.NumberFrame;

			Log.d(TAG, "Rows=" + Rows);
			Log.d(TAG, "Columns=" + Columns);
			Log.d(TAG, "NumberFrame=" + NumberFrame);

			tabForData = new byte[nbByteToWrite * Columns];

			try {
				for (int k = 0; k < NumberFrame; k++)
					for (int i = 0; i < Rows; i++) {
						for (int j = 0; j < Columns; j++) {

							int pix = id.gr7FE0.getPixel(k, j, i);
							if (id.gr0028.HighBit > id.gr0028.BitsStored)
								pix <<= (id.gr0028.HighBit - id.gr0028.BitsStored);

							if (nbByteToWrite == 2) {
								byte[] b2 = vr.formatInt(id.typeStorage, pix);
								tabForData[j * nbByteToWrite] = b2[0];
								tabForData[j * nbByteToWrite + 1] = b2[1];
							} else {
								byte[] b2 = new byte[1];
								int p = pix & 0xff;
								if (p > 127)
									p -= 256;
								b2[0] = (byte) p;
								tabForData[j * nbByteToWrite] = b2[0];
							}

						}

						nbByte = writeDataDicom(tabForData, nbByteToWrite
								* Columns);
						totalByte += nbByte;
						if (nbByte != nbByteToWrite * Columns) {
							break;
						}

					}

			} catch (Exception e) {
				Log.e(TAG, "Error: " + e.getMessage());
			}
		}

		return totalByte;
	}

	// processus d'ecriture d'une image DICOM
	public void run() {

		TagDicom tag = new TagDicom();
		ValueTagDicom vtag = new ValueTagDicom();

		DataElement dt = new DataElement();
		byte tabForTag[] = new byte[4];
		byte tabForVR[] = new byte[2];
		int VM;
		int indiceElement;
		int typeVR;
		Vector vectorVR;
		long ret = 1;

		vr = new VrDicom();
		id.errorReadDicom = false;
		vr.setDebug(id.debug);

		// verifier que l'image est chargee avant de l'ecrire sur le stream
		if (!id.isImageLoad())
			return;

		try {

			// ecrire l'ensemble des tags avant d'ecrire l'image
			vtag = id.std.getFirstTag();
			while (vtag != SaveTagDicom.vtdNull) {
				tag = vtag.getTagDicom();

				indiceElement = dt.findTag(tag);
				if (indiceElement < 0) {
					id.errorWriteDicom = true;
					break;
				}

				typeVR = dt.getTypeVr();
				VM = dt.getVM();

				ret = writeDataDicom(tag.getTag(id.typeStorage), 4);
				if (ret != 4)
					break;

				// recup de VR String
				vr.getIndiceVR(typeVR);
				String VR_String = vr.getStringVR();

				// calcul de value Length et du tableau d'octets
				Vector v = vr.getValueVR(typeVR, VM, vtag.getValue());
				byte[] b1 = vr.setValueVR(id.typeStorage, v);

				formatExplicitImplicitVR(typeVR, VR_String, b1.length);
				if (id.errorWriteDicom)
					break;

				ret = writeDataDicom(b1, b1.length);
				if (ret != b1.length)
					break;

				// passer a la prochaine marque
				vtag = id.std.getNextTag();
			}

			// ecrire le tag designant les octets de l'image DICOM
			tag = new TagDicom();
			tag.setGroup(0x7FE0);
			tag.setElement(0x0010);

			ret = writeDataDicom(tag.getTag(id.typeStorage), 4);
			if (ret != 4)
				return;

			// formatter la longueur et le type de VR pour gr7FE0
			formatExplicitImplicitVR(VrDicom.vrOWorOB, vrGr7FE0(),
					lengthGr7FE0());

			// ecrire les pixels
			ret = writeImage();
			if (ret != lengthGr7FE0())
				return;

			imageWrite = true;

		} catch (Exception e) {
			Log.e(TAG, "Error: " + e.getMessage());
			id.errorWriteDicom = true;
		}

	}

}
