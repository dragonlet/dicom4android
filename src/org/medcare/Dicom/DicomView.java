package org.medcare.Dicom;

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

import java.util.ArrayList;

import org.medcare.Dicom.DicomActivity.ImageController;
import org.medcare.controller.MultiTouchController;
import org.medcare.controller.MultiTouchController.MultiTouchImageController;
import org.medcare.controller.MultiTouchController.PointInfo;
import org.medcare.controller.MultiTouchController.PositionAndScale;

import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DicomView extends SurfaceView implements SurfaceHolder.Callback, MultiTouchImageController<ImageController> {

	private static final String TAG = "DicomView";
	
	ArrayList<ImageController> imageControllers = new ArrayList<ImageController>();
	
	private MultiTouchController<ImageController> multiTouchController = new MultiTouchController<ImageController>(this);

	private PointInfo currTouchPoint = new PointInfo();

	private boolean mShowDebugInfo = false;

	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	// --

	private Paint mLinePaintTouchPointCircle = new Paint();

	public DicomThread dicomThread;

	private DicomActivity dicomActivity;

	// ---------------------------------------------------------------------------------------------------

	public DicomView(DicomActivity dicomActivity) {
		this(dicomActivity, null);
	}

	public DicomView(DicomActivity dicomActivity, AttributeSet attrs) {
		this(dicomActivity, attrs, 0);
	}

	public DicomView(DicomActivity dicomActivity, AttributeSet attrs, int defStyle) {
		super(dicomActivity, attrs, defStyle);
		this.dicomActivity = dicomActivity;
		init(dicomActivity);
	}

	private void init(DicomActivity dicomActivity) {
		getHolder().addCallback(this);
		Log.e(TAG, "ONCREATE PANEL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		dicomThread = new DicomThread(getHolder(), this);
		setFocusable(true);
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundColor(Color.BLACK);
	}

	/** Called by activity's onResume() method to load the images */
	public void loadImages(DicomActivity dicomActivity) {
		for (ImageController myImageController : imageControllers) {
			myImageController.load(myImageController.getImageDicom());
		}
	}

	/** Called by activity's onPause() method to free memory used for loading the images */
	public void unloadImages() {
		for (ImageController myImageController : imageControllers) {
			myImageController.unload();
		}
	}

	// ---------------------------------------------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = imageControllers.size();
		for (int i = 0; i < n; i++) {
			Log.i(TAG, "ONDRAW imageControllerIndex " + i + " current " + getCurrentImageControllerIndex());
			imageControllers.get(i).draw(canvas);
		}
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}

	// ---------------------------------------------------------------------------------------------------

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		if (currTouchPoint.isDown()) {
			float[] xs = currTouchPoint.getXs();
			float[] ys = currTouchPoint.getYs();
			float[] pressures = currTouchPoint.getPressures();
			int numPoints = Math.min(currTouchPoint.getNumTouchPoints(), 2);
			for (int i = 0; i < numPoints; i++)
				canvas.drawCircle(xs[i], ys[i], 50 + pressures[i] * 80, mLinePaintTouchPointCircle);
			if (numPoints == 2)
				canvas.drawLine(xs[0], ys[0], xs[1], ys[1], mLinePaintTouchPointCircle);
		}
	}


	// ---------------------------------------------------------------------------------------------------

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return multiTouchController.onTouchEvent(event);
	}

	/** Get the image that is under the single-touch point, or return null (canceling the drag op) if none */
	public int getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = imageControllers.size();
		for (int i = n - 1; i >= 0; i--) {
			ImageController im = imageControllers.get(i);
			if (im.containsPoint(x, y))
				return i;
		}
		return -1;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be under the point (non-null is returned by getDraggableObjectAtPoint())
	 * and a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(int index, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (index != -1) {
			ImageController img = imageControllers.get(index);
			// Move image to the top of the stack when selected
			imageControllers.remove(img );
			imageControllers.add(img);
			multiTouchController.setSelectedObject(imageControllers.size() - 1);
		} else {
			// Called with img == null when drag stops.
		}
		invalidate();
	}

	/** Get the current position and scale of the selected image. Called whenever a drag starts or is reset. */
	public void getPositionAndScale(int index, PositionAndScale posAndScaleOut) {
		// FIXME affine-izem (and fix the fact that the anisotropic_scale part requires averaging the two scale factors)
		ImageController img = imageControllers.get(index);
		posAndScaleOut.set(img .getCenterX(), img.getCenterY(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2, (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(), img.getScaleY(),
				(mUIMode & UI_MODE_ROTATE) != 0, img.getAngle());
	}

	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(int img, PositionAndScale newPosAndScale, PointInfo touchPoint) {
		Log.i(TAG, "setPositionAndScale img " + img);
		currTouchPoint.set(touchPoint);
		boolean ok = imageControllers.get(img).setPos(newPosAndScale);
		Log.i(TAG, "setPositionAndScale ok " + ok);
		if (ok)
			invalidate();
		return ok;
	}
	
	/** Set the position and scale of the dragged/stretched image. */
	public boolean setPositionAndScale(int img, PositionAndScale newPosAndScale) {
		Log.i(TAG, "setPositionAndScale img " + img);
		boolean ok = imageControllers.get(img).setPos(newPosAndScale);
		Log.i(TAG, "setPositionAndScale ok " + ok);
		if (ok)
			invalidate();
		return ok;
	}

	// ----------------------------------------------------------------------------------------------

	public void clear(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		Log.e(TAG, "TESTDICOM CLEAR");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG,
				"ONCREATE SURFACECREATED ALIVE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "
						+ dicomThread.isAlive());
		Log.e(TAG,
				"ONCREATE SURFACECREATED PAUSED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "
						+ dicomThread.mPaused);
		Log.e(TAG,
				"ONCREATE SURFACECREATED STOPED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "
						+ dicomThread.mStopped);
		if (dicomThread.mPaused) {
			dicomThread.resumeThread();
		} else {
			Log
					.e(TAG,
							"ONCREATE SURFACECREATED START !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
			dicomThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		dicomThread.pauseThread();
		Log.e(TAG,
				"ONCREATE SURFACEDESTROYED PAUSED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "
						+ dicomThread.mPaused);
	}

	public class DicomThread extends Thread {
		private SurfaceHolder surfaceHolder;
		private DicomView dicomView;
		boolean mPaused;
		boolean mStopped;

		public DicomThread(SurfaceHolder surfaceHolder, DicomView dicomView) {
			this.surfaceHolder = surfaceHolder;
			this.dicomView = dicomView;
		}

		public SurfaceHolder getSurfaceHolder() {
			return surfaceHolder;
		}

		/*
		 * ======================================================================
		 * 
		 * Gestion des evenements, assignation des traitements aux taches
		 * 
		 * 
		 * 
		 * ======================================================================
		 */

		public boolean action(String arg) {
			sendRemotes(arg);
			return (localAction(arg));
		}

		public boolean localAction(String arg) {
			if (arg == null || arg.equals(""))
				return false;
			Log.i("XMPPClient", "localAction arg [" + arg + "]");
			if (imageControllers.size() > 0) {
				Log.i("XMPPClient", "localAction "
						+ getImageController(0).getImageName() + " arg [" + arg
						+ "]");
				if (arg.equals("Quitter")) {
					System.exit(0);
				} else if (arg.startsWith("multiseuillage")) {
					dicomActivity.multiseuil();
				} else if (arg.startsWith("binarisation")) {
					dicomActivity.binarise();
				} else if (arg.startsWith("median")) {
					dicomActivity.median();
				} else if (arg.startsWith("inversion")) {
					dicomActivity.invert();;
				} else if (arg.startsWith("zoomIn")) {
					dicomActivity.zoomIn();
				} else if (arg.startsWith("zoomOut")) {
					dicomActivity.zoomOut();
				} else if (arg.startsWith("reInitialise")) {
					dicomActivity.reInitialise();
				} else if (arg.startsWith("gradient")) {
					dicomActivity.gradient();
				} else if (arg.startsWith("segmentation")) {
					dicomActivity.gradient();
				} else if (arg.startsWith("Del")) {
					dicomActivity.delDicom(surfaceHolder);
				} else if (arg.startsWith("SELECT_DICOM")) {
					dicomActivity.selectDicom(arg);
				} else if (arg.startsWith("ACTION_MOVE")) {
					dicomActivity.actionMove(arg);
				} else if (arg.startsWith("TAGS_INFO")) {
					dicomActivity.drawInfoWindow();
				} else if (arg.startsWith("TAGS_OFF")) {
					dicomActivity.dismissPopup();
				} else {
					new DicomLoadTask(dicomActivity).execute(arg);
				}
				return true;
			}
			new DicomLoadTask(dicomActivity).execute(arg);
			return true;
		}

		@Override
		public void run() {
			Canvas c;
			mStopped = false;
			mPaused = false;
			while (!mStopped) {
				if (!waitForResume())
					break;
				c = null;
				ImageController curentImageController = getImageController(getCurrentImageControllerIndex());
				if (curentImageController != null
						&& curentImageController.getChange()) {
					try {
						c = surfaceHolder.lockCanvas(null);
						synchronized (surfaceHolder) {
							dicomView.onDraw(c);
						}
						curentImageController.setChange(false);
					} finally {
						// do this in a finally so that if an exception is
						// thrown
						// during the above, we don't leave the Surface in an
						// inconsistent state
						if (c != null) {
							surfaceHolder.unlockCanvasAndPost(c);
						}
					}
					dicomView.postInvalidate();
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		public void stopThread() {
			synchronized (this) {
				mPaused = false;
				mStopped = true;
				this.notify();
			}
		}

		public void pauseThread() {
			synchronized (this) {
				mPaused = true;
				this.notify();
			}
		}

		public void resumeThread() {
			synchronized (this) {
				mPaused = false;
				this.notify();
			}
		}

		private boolean waitForResume() {
			synchronized (this) {
				if (mStopped)
					return false;

				while (mPaused) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (mStopped)
					return false;
			}
			return true;
		}
	}
	
	private class DicomLoadTask extends AsyncTask<String, Void, Void> {

		private DicomActivity dicomActivity;
		private final ProgressDialog dialog;
		
		public DicomLoadTask(DicomActivity dicomActivity) {
			this.dicomActivity = dicomActivity;
			this.dialog = new ProgressDialog(dicomActivity);
		}
		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Loading Image...");
			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected Void doInBackground(final String... imageNames) {
			dicomActivity.ouvrirImage(imageNames[0]);
			return null;
		}

		// can use UI thread here
		protected void onPostExecute(final Void unused) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
		}
	}
	
	public ImageController getImageController(int index) {
		//Log.e(TAG, "DicomView getImageController !!!!!!!!!!! index " + index);
		if (index > -1)
			return imageControllers.get(index);
		else
			return null;
	}
	
	public ImageController getCurrentImageController() {
		//Log.e(TAG, "DicomView getImageController !!!!!!!!!!! index " + index);
		return getImageController(getCurrentImageControllerIndex());
	}
	
	public int getCurrentImageControllerIndex() {
		//Log.e(TAG, "DicomView getCurrentImageControllerIndex !!!!!!!!!!! index " + index);
		return (imageControllers.size() - 1);
	}
	
	public void setCurrentImageControllerIndex(int imageControllerIndex) {
		multiTouchController.setSelectedObject(imageControllerIndex);
	}

	@Override
	public boolean sendRemotes(String arg) {
		return dicomActivity.sendRemotes(arg);
	}
}
