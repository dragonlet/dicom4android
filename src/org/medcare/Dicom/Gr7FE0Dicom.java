/** Gr7FE0Dicom.java - lecture et stockage des pixels de l'image DICOM
 *
 * @author
 * @version $Revision: 0.6 $ $Date: 2010/04/21 22:51:56 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01f,21Avr2010,andleg   ajout methode getBaseDicomMonochrome
 * 01e,31Mar2010,andleg   correction dans getIndicePlan
 * 01d,30Mar2010,andleg   regler pb memoire lors de la creation des images par setARGB
 * 01c,20Mar2010,andleg   corrections suite sauvegarde
 * 01b,18Dec2009,andleg   optimisation pour gestion memoire
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

public class Gr7FE0Dicom {

	// for debug
	private boolean debug = false;

	// Base 3D en niveaux de gris
	// [nbre de frames][nbre de lignes][nbre de colonnes]
	private int BaseDicomMonochrome[][][];
	private int Rows;
	private int Columns;
	private int SmallestValue;
	private int LargestValue;
	private int NumberFrame;

	private boolean imageDicomLoad = false;

	public Gr7FE0Dicom() {
		imageDicomLoad = false;
		Rows = 0;
		Columns = 0;
		NumberFrame = 0;
		BaseDicomMonochrome = null;
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
	 * Return boolean specifying if image monochrome is load
	 * 
	 * @param boolean the value of debug
	 * 
	 */
	public boolean isImageMonochromeLoad() {
		return imageDicomLoad;
	}

	/**
	 * Return the 3B Base if image monochrome is load
	 * 
	 */
	public int[][][] getBaseDicomMonochrome() {
		if (isImageMonochromeLoad())
			return BaseDicomMonochrome;
		else
			return null;
	}

	/**
	 * free the memory occuped by the object
	 * 
	 */
	public void freeMemory() {
		Rows = 0;
		Columns = 0;
		NumberFrame = 0;
		SmallestValue = 0;
		LargestValue = 0;
		BaseDicomMonochrome = null;
		// active garbage collector
		System.gc();
	}

	/**
	 * Return the number of rows of the image (i.e. the height)
	 * 
	 * @return int - the number of rows
	 * 
	 */
	public int getRows() {
		return Rows;
	}

	/**
	 * Return the number of columns of the image (i.e. the width)
	 * 
	 * @return int - the number of columns
	 * 
	 */
	public int getColumns() {
		return Columns;
	}

	/**
	 * Return the number of frames in the image for a 2D DICOM Image, return 1
	 * 
	 * @return int - the number of frames
	 * 
	 */
	public int getNumberFrames() {
		return NumberFrame;
	}

	/**
	 * Return the value of the pixel in grey level for a 2D DICOM image if the
	 * image DICOM is multiframe, return the grey value in the first frame(0)
	 * 
	 * @param (x,y) the coordinate of pixel in 2D dimension
	 * @return int - the value of pixel in grey level
	 * 
	 */
	public int getPixel(int x, int y) {
		return getPixel(0, x, y);
	}

	/**
	 * Return the value of the pixel in grey level for a 3D DICOM image
	 * 
	 * @param n
	 *            the indice of the frame
	 * @param (x,y) the coordinate of pixel in 2D dimension
	 * @return int - the value of pixel in grey level
	 * 
	 */
	public int getPixel(int n, int x, int y) {
		// x designate column
		// y designate row
		return BaseDicomMonochrome[n][y][x];
	}

	/**
	 * Return the values of the rectangular region in grey level for a 2D DICOM
	 * image
	 * 
	 * @param ROI
	 *            the region of interest
	 * @return a 2D array storing the values of pixel for this ROI in the format
	 *         [Rows][Columns]
	 * 
	 */
	public int[][] getRectangularROIofImageMonochrome(Rectangle ROI) {
		return getRectangularROIofImageMonochrome(0, ROI);
	}

	/**
	 * Return the values of the rectangular region in grey level for a 3D DICOM
	 * image
	 * 
	 * @param frame
	 *            - indice of the frame in the 3D DICOM image
	 * @param ROI
	 *            the region of interest
	 * @return a 2D array storing the values of pixel for this ROI in the format
	 *         [Rows][Columns]
	 * 
	 */
	public int[][] getRectangularROIofImageMonochrome(int frame, Rectangle ROI) {

		if (!isImageMonochromeLoad())
			return null;

		if ((frame < 0) || (frame >= NumberFrame)) {
			System.err.println("Gr7FE0: frame mal choisie");
			return null;
		}

		// x designate column
		// y designate row

		if ((ROI.x < 0) || (ROI.y < 0) || (ROI.width < 0) || (ROI.height < 0)
				|| (ROI.x + ROI.width > getColumns())
				|| (ROI.y + ROI.height > getRows())) {
			System.err.println("Gr7FE0: region mal choisie");
			return null;
		}

		try {

			int RoiMonochrome[][];
			RoiMonochrome = new int[ROI.height][ROI.width];
			int i, j, nf;

			for (i = 0; i < ROI.height; i++)
				for (j = 0; j < ROI.width; j++) {
					RoiMonochrome[i][j] = BaseDicomMonochrome[frame][ROI.y + i][ROI.x
							+ j];
				}

			return RoiMonochrome;

		} catch (OutOfMemoryError e) {
			System.err.println("imageARGB: pb d'allocation memoire");
			return null;
		}
	}

	/**
	 * Return the image in the ARGB format for a 2D DICOM image
	 * 
	 * @return an 2D image
	 * @see java.awt.Image
	 * 
	 */
	public int[] imageARGB() {
		return imageARGB(SmallestValue, LargestValue, 0, new Rectangle(Columns,
				Rows));
	}

	/**
	 * Return the image in the ARGB format for a 3D DICOM image adapt the
	 * contrast in the image in respect of min-max values 0 for the values
	 * <=min, 255 for the values >= max
	 * 
	 * @param min
	 *            -max interval of grey level
	 * @param frame
	 *            - indice of the frame
	 * @return an 2D image
	 * @see java.awt.Image
	 * 
	 */
	public int[] imageARGB(int min, int max, int frame) {
		return imageARGB(min, max, frame, new Rectangle(Columns, Rows));
	}

	/**
	 * Return the image in the ARGB format corresponding to a ROI for a 3D DICOM
	 * image adapt the contrast in the image in respect of min-max values 0 for
	 * the values <=min, 255 for the values >= max
	 * 
	 * @param min
	 *            -max interval of grey level
	 * @param frame
	 *            - indice of the frame
	 * @param ROI
	 *            - region of interest
	 * @return an 2D image
	 * @see java.awt.Image
	 * 
	 */
	public int[] imageARGB(int min, int max, int frame, Rectangle ROI) {
		if (!isImageMonochromeLoad())
			return null;

		try {
			int imageTemp[][] = getRectangularROIofImageMonochrome(frame, ROI);

			int tabPixel[] = new int[ROI.height * ROI.width];
			int i, j, k = 0, pix;

			for (i = 0; i < ROI.height; i++)
				for (j = 0; j < ROI.width; j++, k++) {
					pix = imageTemp[i][j];

					// change dynamic of the image
					pix = (int) ((float) 255.0 * (pix - min) / (max - min));
					if (pix < 0)
						pix = 0;
					if (pix > 255)
						pix = 255;

					tabPixel[k] = (255 << 24) | (pix << 16) | (pix << 8) | pix;
				}

			return tabPixel;

		} catch (OutOfMemoryError e) {
			System.err.println("imageARGB: pb d'allocation memoire");
			return null;
		}

	}

	/**
	 * initialize the object with a grey image
	 * 
	 * @param ROI
	 *            - specifying the dimension of the image
	 * @param imageMonochrome
	 *            - the 2D image
	 * 
	 */
	public void setRectangularROIofImageMonochrome(Rectangle ROI,
			int[][] imageMonochrome) {

		// initialisation par defaut
		imageDicomLoad = false;
		NumberFrame = 0;
		Rows = 0;
		Columns = 0;

		// x designate column
		// y designate row

		if ((ROI.x < 0) || (ROI.y < 0) || (ROI.width < 0) || (ROI.height < 0)) {
			return;
		}

		try {
			BaseDicomMonochrome = new int[1][ROI.height][ROI.width];
		} catch (OutOfMemoryError ex) {
			System.err
					.println("setRectangularROIofImageMonochrome: pb d'allocation memoire");
			return;
		}

		NumberFrame = 1;
		Rows = ROI.height;
		Columns = ROI.width;

		int i, j, min = 0, max = 0, pix;

		for (i = 0; i < Rows; i++)
			for (j = 0; j < Columns; j++) {
				pix = BaseDicomMonochrome[0][i][j] = imageMonochrome[i][j];
				if (i + j == 0) {
					min = max = pix;
				} else {
					if (pix > max)
						max = pix;
					else if (pix < min)
						min = pix;
				}
			}

		LargestValue = max;
		SmallestValue = min;

		imageDicomLoad = true;
	}

	/**
	 * initialize the object with a 3D grey image
	 * 
	 * @param NumberFrame
	 *            - the number of frame in the 3D image
	 * @param Rows
	 *            - the number of rows in the 3D image (the height of the image)
	 * @param Columns
	 *            - the number of columns in the 3D image (the width of the
	 *            image)
	 * @param baseMonochrome
	 *            - the 3D image
	 * 
	 */
	public void setBaseDicomMonochrome(int NumberFrame, int Rows, int Columns,
			int[][][] baseMonochrome) {

		// initialisation par defaut
		imageDicomLoad = false;
		this.NumberFrame = 0;
		this.Rows = 0;
		this.Columns = 0;

		if ((NumberFrame <= 0) || (Rows <= 0) || (Columns <= 0))
			return;

		this.Rows = Rows;
		this.Columns = Columns;
		this.NumberFrame = NumberFrame;
		int i, j, k, min = 0, max = 0, pix;

		// Il s'agit d'affectation, donc passage du pointeur (mais pas
		// d'allocation)
		BaseDicomMonochrome = baseMonochrome;

		for (k = 0; k < NumberFrame; k++)
			for (i = 0; i < Rows; i++)
				for (j = 0; j < Columns; j++) {
					pix = BaseDicomMonochrome[k][i][j];
					if (i + j + k == 0) {
						min = max = pix;
					} else {
						if (pix > max)
							max = pix;
						else if (pix < min)
							min = pix;
					}
				}

		LargestValue = max;
		SmallestValue = min;

		// Log.i("Memoire totale apres affectation="+Runtime.getRuntime().totalMemory());

		imageDicomLoad = true;
	}

	/**
	 * get the largest value in the DICOM image
	 * 
	 * @return the maximum value
	 */
	public int getLargestValue() {
		if (isImageMonochromeLoad())
			return LargestValue;
		else
			return 0;
	}

	/**
	 * get the smallest value in the DICOM image
	 * 
	 * @return the minimum value
	 */
	public int getSmallestValue() {
		if (isImageMonochromeLoad())
			return SmallestValue;
		else
			return 0;
	}

	/**
	 * get the number of significant bits in the DICOM image example : if
	 * largest value=1023 the return 10 (car 1023<=2^10)
	 * 
	 * @return the number of significant bit
	 */
	public int getIndicePlanMax() {

		if (!isImageMonochromeLoad())
			return -1;

		if (SmallestValue < 0)
			return 16;

		if (LargestValue == 0)
			return 0;

		int ind = 0, maxInd = LargestValue;

		do {
			ind++;
			maxInd >>= 1;
		} while (maxInd != 0);

		return ind;
	}

	/**
	 * get the smallest value in a frame of the DICOM image
	 * 
	 * @param f
	 *            - indice of the frame
	 * @return the minimum value in the frame
	 */
	public int getSmallestValueFrame(int f) {

		if (!isImageMonochromeLoad())
			return 0;

		int i, j, min = 0, pix;

		for (i = 0; i < Rows; i++)
			for (j = 0; j < Columns; j++) {
				pix = BaseDicomMonochrome[f][i][j];
				if (i + j == 0) {
					min = pix;
				} else {
					if (pix < min)
						min = pix;
				}
			}

		return min;
	}

	/**
	 * get the largest value in a frame of the DICOM image
	 * 
	 * @param f
	 *            - indice of the frame
	 * @return the maximum value in the frame
	 */
	public int getLargestValueFrame(int f) {

		if (!isImageMonochromeLoad())
			return 0;

		int i, j, max = 0, pix;

		for (i = 0; i < Rows; i++)
			for (j = 0; j < Columns; j++) {
				pix = BaseDicomMonochrome[f][i][j];
				if (i + j == 0) {
					max = pix;
				} else {
					if (pix > max)
						max = pix;
				}
			}

		return max;
	}

}
