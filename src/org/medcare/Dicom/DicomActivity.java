/** DicomActivity.java - Test de lecture d'une image DICOM
 * et des traitements
 *
 * @author
 * @version $Revision: 0.2 $ $Date: 2010/03/19 23:26:50 $ $Author: andleg $
 *
 * Modification History
 * ---------------------
 * 01c,22Nov2010,andleg   corrections suite filtrage SFR
 * 01b,19Mar2010,andleg   corrections suite evolution DICOM
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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.medcare.R;
import org.medcare.controller.MultiTouchController.PositionAndScale;
import org.medcare.xmpp.Account;
import org.medcare.xmpp.CollabBoard;
import org.medcare.xmpp.SettingsDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.PopupWindow;

public class DicomActivity extends Activity {

	private static final int FILE = 0;
	private static final int EDIT = 1;
	private static final int CT = 2;
	private static final int MR = 3;
	private static final int OT = 4;
	private static final int US = 5;
	private static final int DIV = 6;
	private static final int COLLAB = 7;

	private static final int ACCOUNT_CREATE = 0;
	private static final int ACCOUNT_SETTER = 1;
	private static final int PASSWD_SETTER = 2;
	private static final int SETTING_DIALOG = 3;

	// private static final int Ouvrir_MENU_ITEM = Menu.FIRST;
	private static final int Quitter_MENU_ITEM = Menu.FIRST + 1;

	private static final int CT_MONO2_12_lomb_an2 = Quitter_MENU_ITEM + 1;
	private static final int CT_MONO2_16_brain = CT_MONO2_12_lomb_an2 + 1;
	private static final int CT_MONO2_16_chest = CT_MONO2_16_brain + 1;
	private static final int CT_MONO2_16_ort = CT_MONO2_16_chest + 1;
	private static final int CT_MONO2_8_abdo = CT_MONO2_16_ort + 1;
	private static final int CT_MONO2_16_ankle = CT_MONO2_8_abdo + 1;

	private static final int MR_MONO2_12_an2 = CT_MONO2_16_ankle + 1;
	private static final int MR_MONO2_12_angio_an1 = MR_MONO2_12_an2 + 1;
	private static final int MR_MONO2_12_shoulder = MR_MONO2_12_angio_an1 + 1;
	private static final int MR_MONO2_16_head = MR_MONO2_12_shoulder + 1;
	private static final int MR_MONO2_16_knee = MR_MONO2_16_head + 1;
	private static final int MR_MONO2_8_16x_heart = MR_MONO2_16_knee + 1;

	private static final int OT_MONO2_8_a7 = MR_MONO2_8_16x_heart + 1;
	private static final int OT_MONO2_8_colon = OT_MONO2_8_a7 + 1;
	private static final int OT_MONO2_8_hip = OT_MONO2_8_colon + 1;
	private static final int OT_PAL_8_face = OT_MONO2_8_hip + 1;

	private static final int US_MONO2_8_8x_execho = OT_PAL_8_face + 1;
	private static final int US_PAL_8_10x_echo = US_MONO2_8_8x_execho + 1;
	private static final int US_RGB_8_epicard = US_PAL_8_10x_echo + 1;

	private static final int CR_MONO1_10_chest = US_RGB_8_epicard + 1;
	private static final int R_MONO1_10_chest = CR_MONO1_10_chest + 1;
	private static final int NM_MONO2_16_13x_heart = R_MONO1_10_chest + 1;
	private static final int XA_MONO2_8_12x_catheter = NM_MONO2_16_13x_heart + 1;

	private static final int SET_CONNECT = XA_MONO2_8_12x_catheter + 1;
	private static final int QUIT = SET_CONNECT + 1;

	private static final int initialise_MENU_ITEM = QUIT + 1;
	private static final int zoomIn_MENU_ITEM = initialise_MENU_ITEM + 1;
	private static final int zoomOut_MENU_ITEM = zoomIn_MENU_ITEM + 1;
	private static final int multiseuillage_MENU_ITEM = zoomOut_MENU_ITEM + 1;
	private static final int binarisation_MENU_ITEM = multiseuillage_MENU_ITEM + 1;
	private static final int median_MENU_ITEM = binarisation_MENU_ITEM + 1;
	private static final int inversion_MENU_ITEM = median_MENU_ITEM + 1;
	private static final int gradient_MENU_ITEM = inversion_MENU_ITEM + 1;
	private static final int segmentation_MENU_ITEM = gradient_MENU_ITEM + 1;

	private static final String TAG = "TESTDICOM";
	private static int INFO_WINDOW_WIDTH = 150;
	private static int INFO_WINDOW_HEIGHT = 100;
	public static final int REGISTRATION_TIMEOUT = 30 * 1000; // ms
	
	/** The max number of touch points that can be present on the screen at once */
	public static final int MAX_TOUCH_POINTS = 20;
	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;

	private int mUIMode = UI_MODE_ROTATE;

	// Image img,imgtt,imtamp;
	boolean move;
	Bitmap imgtt;
	ImageController curentImageController;
	private View mPopupView;
	private WebView mPopupWebView;
	private PopupWindow mPopup;
	private Handler mHandler = new Handler();
	XMPPConnection connection;
	private Account account;
	public DicomView dicomView;
	private Paint borderPaint;
	private Paint textPaint;
	private Paint innerPaint;
	private CollabBoard collabBoard;
	HttpClient httpClient = null;
	private String xmppUsername = null;
	String xmppRemoteUser = null;
	   // We can be in one of these 3 states
	   static final int NONE = 0;
	   static final int DRAG = 1;
	   static final int ZOOM = 2;
	private static final long XMPP_GAP = 1000;
	   int mode = NONE;

	   // Remember some things for zooming
	   PointF start = new PointF();
	   PointF mid = new PointF();
	   float oldDist = 1f;
	private long sendRemotesTime;
	private long lastSendRemotesTime = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "ONCREATE TESTDICOM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dicomView = new DicomView(this);
		setContentView(dicomView);
		mPopupView = getLayoutInflater().inflate(R.layout.web_view, null);
		mPopupWebView = (WebView) mPopupView.findViewById(R.id.web);
		collabBoard = new CollabBoard(this);
	}

	public void onConfigurationChange(Configuration config) {
		//Pour eviter les plantages en cas de rotation
		Log.e(TAG, "ONCONFIGURATIONCHANGE TESTDICOM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	/**
	 * Create some menu options.
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// SubMenu fileMenu = menu.addSubMenu("Fichier");
		SubMenu editMenu = menu.addSubMenu("Processing");
		// fileMenu.add(FILE, Quitter_MENU_ITEM, 1, "Quitter");
		SubMenu ctMenu = menu.addSubMenu("CT");
		SubMenu mrMenu = menu.addSubMenu("MR");
		SubMenu otMenu = menu.addSubMenu("OT");
		SubMenu usMenu = menu.addSubMenu("US");
		SubMenu divMenu = menu.addSubMenu("AUTRES");
		SubMenu collabMenu = menu.addSubMenu("COLLAB");

		ctMenu.add(CT, CT_MONO2_12_lomb_an2, 0, "CT-MONO2-12-lomb-an2");
		ctMenu.add(CT, CT_MONO2_16_brain, 1, "CT-MONO2-16-brain");
		ctMenu.add(CT, CT_MONO2_16_chest, 2, "CT-MONO2-16-chest");
		ctMenu.add(CT, CT_MONO2_16_ort, 3, "CT-MONO2-16-ort");
		ctMenu.add(CT, CT_MONO2_8_abdo, 4, "CT-MONO2-8-abdo");
		ctMenu.add(CT, CT_MONO2_16_ankle, 5, "CT-MONO2-16-ankle");

		mrMenu.add(MR, MR_MONO2_12_an2, 0, "MR-MONO2-12-an2");
		mrMenu.add(MR, MR_MONO2_12_angio_an1, 1, "MR-MONO2-12-angio-an1");
		mrMenu.add(MR, MR_MONO2_12_shoulder, 2, "MR-MONO2-12-shoulder");
		mrMenu.add(MR, MR_MONO2_16_head, 3, "MR-MONO2-16-head");
		mrMenu.add(MR, MR_MONO2_16_knee, 4, "MR-MONO2-16-knee");
		mrMenu.add(MR, MR_MONO2_8_16x_heart, 5, "MR-MONO2-8-16x-heart");

		otMenu.add(OT, OT_MONO2_8_a7, 0, "OT-MONO2-8-a7");
		otMenu.add(OT, OT_MONO2_8_colon, 1, "OT-MONO2-8-colon");
		otMenu.add(OT, OT_MONO2_8_hip, 2, "OT-MONO2-8-hip");
		otMenu.add(OT, OT_PAL_8_face, 3, "OT-PAL-8-face");

		usMenu.add(US, US_MONO2_8_8x_execho, 0, "US-MONO2-8-8x-execho");
		usMenu.add(US, US_PAL_8_10x_echo, 1, "US-PAL-8-10x-echo");
		usMenu.add(US, US_RGB_8_epicard, 2, "US-RGB-8-epicard");

		divMenu.add(DIV, CR_MONO1_10_chest, 0, "CR_MONO1_10_chest");
		divMenu.add(DIV, R_MONO1_10_chest, 1, "R-MONO1-10-chest");
		divMenu.add(DIV, NM_MONO2_16_13x_heart, 2, "NM-MONO2-16-13x-heart");
		divMenu.add(DIV, XA_MONO2_8_12x_catheter, 3, "XA-MONO2-8-12x-catheter");

		collabMenu.add(COLLAB, SET_CONNECT, 3, getString(R.string.main_menu_set_connect));
		collabMenu.add(COLLAB, QUIT, 4, getString(R.string.main_menu_quit));

		editMenu.add(EDIT, initialise_MENU_ITEM, 0, "reInit");
		editMenu.add(EDIT, zoomIn_MENU_ITEM, 1, "zoomIn");
		editMenu.add(EDIT, zoomOut_MENU_ITEM, 2, "zoomOut");
		editMenu.add(EDIT, multiseuillage_MENU_ITEM, 3, "thresholding");
		editMenu.add(EDIT, binarisation_MENU_ITEM, 4, "binarization");
		editMenu.add(EDIT, median_MENU_ITEM, 5, "median");
		editMenu.add(EDIT, inversion_MENU_ITEM, 6, "inversion");
		editMenu.add(EDIT, gradient_MENU_ITEM, 7, "gradient");
		editMenu.add(EDIT, segmentation_MENU_ITEM, 8, "segmentation");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "MENU ITEM " + item.getItemId());
		switch (item.getItemId()) {
		case Quitter_MENU_ITEM:
			this.finish();
			break;
		case CT_MONO2_12_lomb_an2:
			alertImage("CT-MONO2-12-lomb-an2");
			break;
		case CT_MONO2_16_brain:
			dicomView.dicomThread.action("CT-MONO2-16-brain");
			break;
		case CT_MONO2_16_chest:
			alertImage("CT-MONO2-16-chest");
			break;
		case CT_MONO2_16_ort:
			dicomView.dicomThread.action("CT-MONO2-16-ort");
			break;
		case CT_MONO2_8_abdo:
			dicomView.dicomThread.action("CT-MONO2-8-abdo");
			break;
		case CT_MONO2_16_ankle:
			dicomView.dicomThread.action("CT-MONO2-16-ankle");
			break;
		case MR_MONO2_12_an2:
			dicomView.dicomThread.action("MR-MONO2-12-an2");
			break;
		case MR_MONO2_12_angio_an1:
			alertImage("MR-MONO2-12-angio-an1");
			break;
		case MR_MONO2_12_shoulder:
			alertImage("MR-MONO2-12-shoulder");
			break;
		case MR_MONO2_16_head:
			dicomView.dicomThread.action("MR-MONO2-16-head");
			break;
		case MR_MONO2_16_knee:
			dicomView.dicomThread.action("MR-MONO2-16-knee");
			break;
		case MR_MONO2_8_16x_heart:
			dicomView.dicomThread.action("MR-MONO2-8-16x-heart");
			break;
		case OT_MONO2_8_a7:
			dicomView.dicomThread.action("OT-MONO2-8-a7");
			break;
		case OT_MONO2_8_colon:
			dicomView.dicomThread.action("OT-MONO2-8-colon");
			break;
		case OT_MONO2_8_hip:
			dicomView.dicomThread.action("OT-MONO2-8-hip");
			break;
		case OT_PAL_8_face:
			alertImage("OT-PAL-8-face");
			break;
		case US_MONO2_8_8x_execho:
			dicomView.dicomThread.action("US-MONO2-8-8x-execho");
			break;
		case US_PAL_8_10x_echo:
			alertImage("US-PAL-8-10x-echo");
			break;
		case US_RGB_8_epicard:
			alertImage("US-RGB-8-epicard");
			break;
		case CR_MONO1_10_chest:
			alertImage("CR-MONO1-10-chest");
			break;
		case NM_MONO2_16_13x_heart:
			dicomView.dicomThread.action("NM-MONO2-16-13x-heart");
			break;
		case XA_MONO2_8_12x_catheter:
			alertImage("XA-MONO2-8-12x-catheter");
			break;
		case SET_CONNECT:
			startActivityForResult(new Intent(this, SettingsDialog.class),
					SETTING_DIALOG);
			return true;
		case QUIT:
			this.finish();
			return true;
		case initialise_MENU_ITEM:
			dicomView.dicomThread.action("reInitialise");
			break;
		case zoomIn_MENU_ITEM:
			dicomView.dicomThread.action("zoomIn");
			break;
		case zoomOut_MENU_ITEM:
			dicomView.dicomThread.action("zoomOut");
			break;
		case multiseuillage_MENU_ITEM:
			/*
			 * Intent intent2 = new Intent(FAmeMap.this,
			 * org.android.fAme.SearchActivity.class);
			 * startActivityForResult(intent2, GET_SEARCH_TEXT);
			 */
			dicomView.dicomThread.action("multiseuillage");
			break;
		case binarisation_MENU_ITEM:
			dicomView.dicomThread.action("binarisation");
			break;
		case median_MENU_ITEM:
			dicomView.dicomThread.action("median");
			break;
		case inversion_MENU_ITEM:
			dicomView.dicomThread.action("inversion");
			break;
		case gradient_MENU_ITEM:
			dicomView.dicomThread.action("gradient");
			break;
		case segmentation_MENU_ITEM:
			dicomView.dicomThread.action("segmentation");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle locBundle = null;
		if (intent != null)
			locBundle = intent.getExtras();
		if (locBundle != null) {
			switch (requestCode) {
			case SETTING_DIALOG:
				connectionSettings(locBundle);
				break;
			default:
				break;
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e(TAG, "onKeyDown!!!!!!!!!!!!!!! " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_PLUS) {
			Log.e(TAG, "zoomIn!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("zoomIn");
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MINUS) {
			Log.e(TAG, "zoomOut!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("zoomOut");
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			Log.e(TAG, "zoomIn!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("zoomIn");
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			Log.e(TAG, "zoomOut!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("zoomOut");
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			Log.e(TAG, "Del!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("Del");
		} else if (keyCode == KeyEvent.KEYCODE_DEL) {
			Log.e(TAG, "del!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("Del");
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			Log.e(TAG, "TAGS_INFO!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("TAGS_INFO");
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			Log.e(TAG, "TAGS_OFF!!!!!!!!!!!!!!!");
			this.dicomView.dicomThread.action("TAGS_OFF");
		}
		return false;
	}

	public Paint getInnerPaint() {
		if (innerPaint == null) {
			innerPaint = new Paint();
			innerPaint.setARGB(225, 75, 75, 75); // gray
			innerPaint.setAntiAlias(true);
		}
		return innerPaint;
	}

	public Paint getBorderPaint() {
		if (borderPaint == null) {
			borderPaint = new Paint();
			borderPaint.setARGB(255, 255, 255, 255);
			borderPaint.setAntiAlias(true);
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setStrokeWidth(2);
		}
		return borderPaint;
	}

	public Paint getTextPaint() {
		if (textPaint == null) {
			textPaint = new Paint();
			textPaint.setARGB(255, 255, 255, 255);
			textPaint.setAntiAlias(true);
		}
		return textPaint;
	}

	public ImageController getRemoteImage(final URL aURL) {

		HttpGet httpget = new HttpGet(aURL.toExternalForm());
		try {

/*Modif pour SFR
			Log.e(TAG, "OPENCONNECTION!!!!!!!!!!!!!!!");
			final URLConnection conn = aURL.openConnection();
			// conn.setRequestProperty("User-Agent",
			// "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Linux; U; Windows NT 6.1; en-us; dream");
			Log.e(TAG, "CONNECT!!!!!!!!!!!!!!!");

			conn.connect();
			Log.e(TAG, "INPUTSTREAM!!!!!!!!!!!!!!!");

			final BufferedInputStream bin = new BufferedInputStream(conn
					.getInputStream());
//Log.e(TAG, "Response code is " + conn.getResponseCode());
*/
			if (httpClient == null) {
				this.httpClient = new DefaultHttpClient();
			}
			Log.e(TAG, "AFTER OPENCONNECTION!!!!!!!!!!!!!!!");
			final HttpParams params = httpClient.getParams();
			//HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
			//httpget.setHeader("User-Agent", "Mozilla/5.0 (Linux; U; Windows NT 6.1; en-us; dream");
			//httpget.setHeader("User-Agent", USER_AGENT);
			//httpget.addHeader("Authorization", "Basic " + getCredentials());
			//LIGNE pour SFR
			HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), false);
			HttpResponse httpResponse = httpClient.execute(httpget);
			final BufferedInputStream bin = new BufferedInputStream(httpResponse.getEntity().getContent());
			Log.e(TAG, "IMAGEDICOM!!!!!!!!!!!!!!!");
			ImageDicom imageDicom = new ImageDicom();
			try {
				Log.e(TAG, "IMAGEDICOM READ!!!!!!!!!!!!!!!");
				imageDicom.read(bin);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "IMAGEDICOM READ EXCEPTION !!!!!!!!!!!!!!!");
				e.printStackTrace();
			}
			// final Bitmap bm = BitmapFactory.decodeStream(bis);
			bin.close();
			return initImageController(imageDicom);
		} catch (Exception e) {
			Log.e(TAG, "OPENCONNECTION ERROR!!!!!!!!!..." + e.getMessage());
			try {
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
				}
			} catch (Exception e1) {
				Log.e(TAG, "OPENCONNECTION EXCEPTION ERROR!!!!!!!!!..." + e1.getMessage());
			}
			httpClient = null;
		}
		return null;
	}

	private void alertImage(String imageName) {
		new AlertDialog.Builder(DicomActivity.this).setTitle("Image " + imageName + " indisponible")
			.setMessage("Cliquez et choisissez une autre image")
			.setIcon(R.drawable.question).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.e(TAG, "alertImage..RESULT_OK ");
					}
				}).show();
		return;
	}

	public void alertMessage(String text) {
		new AlertDialog.Builder(DicomActivity.this).setTitle("Alert")
			.setMessage(text)
			.setIcon(R.drawable.question).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.e(TAG, "alertImage..RESULT_OK ");
					}
				}).show();
		return;
	}
	
	// ouvre une image (ancien nom : init() )
	public void ouvrirImage(String imageName) {
		try {
			Log.i(TAG, "OUVRIRIMAGE");
			ImageController curentImageController = getRemoteImage(new URL(
					"http://compiere-mfgscm.sourceforge.net/" + imageName));
			if (curentImageController != null) {
				curentImageController.setImageName(imageName);
				curentImageController.setChange(true);
				Log.i(TAG, "GRAPHICS ADD " + imageName);
				dicomView.imageControllers.add(curentImageController);
				Log.e(TAG, "OUVRIRIMAGE index " + (dicomView.imageControllers.size() - 1));
				dicomView.setCurrentImageControllerIndex(dicomView.imageControllers.size() - 1);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public static void main(String[] args) {
		DicomActivity tstd = new DicomActivity();
		tstd.setTitle("testtraitement Application");
	}

	public int[] setARGB(int[][] tab, int Rows, int Columns) {

		int tabPixel[] = new int[Rows * Columns];
		int i, j, k = 0, pix, SmallestValue = 0, LargestValue = 0;

		for (i = 0; i < Rows; i++)
			for (j = 0; j < Columns; j++) {
				pix = tab[i][j];
				if (pix > LargestValue)
					LargestValue = pix;
				if (pix < SmallestValue)
					SmallestValue = pix;
			}
		Log
				.i(TAG, "setARGB - min :" + SmallestValue + " max : "
						+ LargestValue);

		for (i = 0; i < Rows; i++)
			for (j = 0; j < Columns; j++, k++) {
				pix = tab[i][j];
				// change dynamic of the image
				pix = (int) ((float) 255.0 * (pix - SmallestValue) / (LargestValue - SmallestValue));
				if (pix < 0)
					pix = 0;
				if (pix > 255)
					pix = 255;

				tabPixel[k] = (255 << 24) | (pix << 16) | (pix << 8) | pix;

			}

		return tabPixel;
	}

	/* ======================================================================== */
	/* traitement d'initialisation: */
	/* - calcul le nbre de colonnes de l'image : columns */
	/* - calcul le nbre de lignes de l'image : rows */
	/* - charge l'image ds le tableau monoTab[rows]][columns] */
	/* qui est un objet int, defini en global */
	/* ======================================================================== */

	public ImageController initImageController(ImageDicom imageDicom) {
		Log.i(TAG, "debut initialisation");
		if (imageDicom != null && imageDicom.isImageLoad()) {
			Log.i(TAG, "Image BITMAP CREATED");
			return (new ImageController(imageDicom));
		}
		return null;
	}
	
	public void reInitialise() {
		Log.i(TAG, "Image BITMAP reInitialise");
		curentImageController = dicomView.getCurrentImageController();
		if (curentImageController == null)
			return;
		/*int monoTab[][] = curentImageController.getMonoTab();
		int c = curentImageController.getColumns();
		int r = curentImageController.getRows();
		int[] tabTT = setARGB(monoTab, r, c);
		//tabTT = setARGB(_monoTab, rows, columns);
		//Bitmap.createBitmap(tabTT, offset, columns, columns, rows, Bitmap.Config.RGB_565);
		int offset = 0; // The index of the first color to read from pixels[]
		curentImageController.setBitmap(Bitmap.createBitmap(tabTT, offset, c, c, r, Bitmap.Config.RGB_565));
		Log.i(TAG, "Image BITMAP reInitialise SETBITMAP");*/
		curentImageController.setBitmap(curentImageController.getOriBitMap());
	}
		
	/* ======================================================================== */
	/* traitement executant un multiseuillage sur l'im monoTab */
	/* ======================================================================== */

	public void multiseuil() {
		Log.i(TAG, "debut traitement multiseuillage");
		curentImageController = dicomView.getCurrentImageController();
		if (curentImageController == null)
			return;
		int monoTab[][] = curentImageController.getMonoTab();
		int c = curentImageController.getColumns();
		int r = curentImageController.getRows();

		int i, j, pix = 0, LargestValue = 0, SmallestValue = 0;
		for (i = 0; i < r; i++)
			for (j = 0; j < c; j++) {
				pix = monoTab[i][j];
				if (pix > LargestValue)
					LargestValue = pix;
				if (pix < SmallestValue)
					SmallestValue = pix;
			}
		for (i = 0; i < r; i++)
			for (j = 0; j < c; j++) {
				pix = monoTab[i][j];
				if (pix < LargestValue / 4)
					monoTab[i][j] = 0;
				if (pix > (LargestValue - LargestValue / 4))
					monoTab[i][j] = LargestValue - LargestValue / 4;
			}
		Log.i(TAG, "fin calcul");
		int[] tabTT = setARGB(monoTab, r, c);
		int offset = 0; // The index of the first color to read from pixels[]
		curentImageController.setBitmap(Bitmap.createBitmap(tabTT, offset, c, c, r, Bitmap.Config.ARGB_8888));
	}

	/* ======================================================================== */
	/* traitement executant une binarisation sur l'im monoTab */
	/* - seuils fixes: */
	/* ======================================================================== */

	public void binarise() {
		curentImageController = dicomView.getCurrentImageController();
		if (curentImageController == null)
			return;
		Log.i(TAG, "debut traitement binarisation");
		int monoTab[][] = curentImageController.getMonoTab();
		int c = curentImageController.getColumns();
		int r = curentImageController.getRows();

		int i, j, pix = 0, LargestValue = 0, SmallestValue = 0;
		for (i = 0; i < r; i++)
			for (j = 0; j < c; j++) {
				pix = monoTab[i][j];
				if (pix > LargestValue)
					LargestValue = pix;
				if (pix < SmallestValue)
					SmallestValue = pix;
			}

		int MediumValue = 0;
		MediumValue = ((LargestValue + SmallestValue) / 2);
		for (i = 0; i < r; i++)
			for (j = 0; j < c; j++) {
				pix = monoTab[i][j];
				if (pix < MediumValue)
					monoTab[i][j] = 0;
				else
					monoTab[i][j] = 255;
			}
		Log.i(TAG, "fin calcul");

		int[] tabTT = setARGB(monoTab, r, c);
		int offset = 0; // The index of the first color to read from pixels[]
		curentImageController.setBitmap(Bitmap.createBitmap(tabTT, offset, c, c, r, Bitmap.Config.ARGB_8888));
	}

	/* ======================================================================== */
	/* traitement executant un filtrage median de l'im monoTab */
	/* - prise en compte de 4 voisins en croix */
	/* ======================================================================== */

	public void median() {
		Log.i(TAG, "debut traitement MEDIAN");
		curentImageController = dicomView.getCurrentImageController();
		if (curentImageController == null)
			return;
		int monoTab[][] = curentImageController.getMonoTab();
		int c = curentImageController.getColumns();
		int r = curentImageController.getRows();

		/*
		 * choix des variables : "pix" est le pixel a traiter; "vali" sont les 4
		 * voisins du pixel a traiter"
		 */
		int i, j, pix = 0, LargestValue = 0, SmallestValue = 0;
		int[][] resTab = new int[r][c]; /* tableau resultant du traitement */

		int[] val = new int[5];

		/* traitement des bords */

		/* cas angle haut gauche */
		val[0] = monoTab[0][0];
		val[1] = monoTab[1][0];
		val[2] = monoTab[0][1];
		val[3] = monoTab[1][0];
		val[4] = monoTab[0][1];
		resTab[0][0] = tri(val);
		/* cas angle haut droite */
		val[0] = monoTab[0][c - 1];
		val[1] = monoTab[1][c - 1];
		val[2] = monoTab[0][c - 2];
		val[3] = monoTab[1][c - 1];
		val[4] = monoTab[0][c - 2];
		resTab[0][c - 1] = tri(val);
		/* cas angle bas gauche */
		val[0] = monoTab[r - 1][0];
		val[1] = monoTab[r - 2][0];
		val[2] = monoTab[r - 1][1];
		val[3] = monoTab[r - 2][0];
		val[4] = monoTab[r - 1][1];
		resTab[r - 1][0] = tri(val);
		/* cas angle bas droite */
		val[0] = monoTab[r - 1][c - 1];
		val[1] = monoTab[r - 2][c - 1];
		val[2] = monoTab[r - 1][c - 2];
		val[3] = monoTab[r - 2][c - 1];
		val[4] = monoTab[r - 1][c - 2];
		resTab[r - 1][c - 1] = tri(val);

		/* traitement ligne horizontale du haut de gauche a droite */
		for (j = 1; j < c - 2; j++) {
			val[0] = monoTab[0][j];
			val[1] = monoTab[1][j];
			val[2] = monoTab[0][j + 1];
			val[3] = monoTab[1][j];
			val[4] = monoTab[0][j - 1];
			resTab[0][j] = tri(val);
		}
		/* traitement ligne horizontale du bas de gauche a droite */
		for (j = 1; j < c - 2; j++) {
			val[0] = monoTab[r - 1][j];
			val[1] = monoTab[r - 2][j];
			val[2] = monoTab[r - 1][j + 1];
			val[3] = monoTab[r - 2][j];
			val[4] = monoTab[r - 1][j - 1];
			resTab[r - 1][j] = tri(val);
		}
		/* traitement ligne verticale gauche de haut en bas */
		for (i = 1; i < r - 2; i++) {
			val[0] = monoTab[i][0];
			val[1] = monoTab[i - 1][0];
			val[2] = monoTab[i][1];
			val[3] = monoTab[i + 1][0];
			val[4] = monoTab[i][1];
			resTab[i][0] = tri(val);
		}

		/* traitement ligne verticale droite, de haut en bas */
		for (i = 1; i < r - 2; i++) {
			val[0] = monoTab[i][c - 1];
			val[1] = monoTab[i - 1][c - 1];
			val[2] = monoTab[i][c - 2];
			val[3] = monoTab[i - 1][c - 1];
			val[4] = monoTab[i][c - 2];
			resTab[i][c - 1] = tri(val);
		}

		/*
		 * reste du traitement entre les bornes pour i [1;columns-2] pour j [1;rows-2]
		 */

		for (i = 1; i < r - 2; i++)
			for (j = 1; j < c - 2; j++) {
				val[0] = monoTab[i][j];
				val[1] = monoTab[i - 1][j];
				val[2] = monoTab[i][j + 1];
				val[3] = monoTab[i + 1][j];
				val[4] = monoTab[i][j - 1];
				resTab[i][j] = tri(val);
			}

		Log.i(TAG, "fin calcul");

		int[] tabTT = setARGB(resTab, r, c);
		int offset = 0; // The index of the first color to read from pixels[]
		curentImageController.setBitmap(Bitmap.createBitmap(tabTT, offset, c, c, r, Bitmap.Config.ARGB_8888));
	}

	/*--------------------------------------------*/
	/* fonction de tri d'un tableau de 5 valeurs */
	/* renvoi la val mediane du tableau */
	/*--------------------------------------------*/

	public int tri(int[] tab) {
		int i = 0, j = 0;
		int max;
		max = tab[0];
		while (i < 3) {
			max = 0;
			for (j = 0; j < 5; j++) {
				if (tab[j] > max) {
					max = tab[j];
					tab[j] = -1;
				}
			}
			i++;
		}
		return (max);
	}

	/* ======================================================================== */
	/* traitement executant une inversion sur l'im monoTab */
	/* - nvlle val= largestValue-ancienne Val */
	/* ======================================================================== */

	public void invert() {
		Log.i(TAG, "debut traitement d'inversion");
		curentImageController = dicomView.getCurrentImageController();
		if (curentImageController == null)
			return;
		int monoTab[][] = curentImageController.getMonoTab();
		int c = curentImageController.getColumns();
		int r = curentImageController.getRows();

		int i, j, pix = 0, LargestValue = 0, SmallestValue = 0;
		for (i = 0; i < r; i++)
			for (j = 0; j < c; j++) {
				pix = monoTab[i][j];
				if (pix > LargestValue)
					LargestValue = pix;
				if (pix < SmallestValue)
					SmallestValue = pix;
			}

		for (i = 0; i < r; i++)
			for (j = 0; j < c; j++) {
				monoTab[i][j] = LargestValue - monoTab[i][j];
			}
		
		Log.i(TAG, "fin calcul");

		int[] tabTT = setARGB(monoTab, r, c);
		int offset = 0; // The index of the first color to read from pixels[]
		curentImageController.setBitmap(Bitmap.createBitmap(tabTT, offset, c, c, r, Bitmap.Config.ARGB_8888));
	}

	/* ======================================================================== */
	/* traitement effacant l'image Dicom couranta */
	/* - si columns'est la derniere, efface le tableau */
	/* ======================================================================== */

	public void delDicom(SurfaceHolder surfaceHolder) {
		Log.i(TAG, "debut traitement DEL");
		curentImageController = dicomView.getCurrentImageController();
		if (curentImageController == null)
			return;
		dicomView.imageControllers.remove(curentImageController);
		int index = dicomView.imageControllers.size() - 1;
		Canvas c = null;
		try {
			if (index >= 0) {
				curentImageController = dicomView.imageControllers.get(dicomView.imageControllers.size() - 1);
				curentImageController.setChange(true);
			} else {
				curentImageController = null;
				c = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					dicomView.clear(c);
				}
			}
		} finally {
			// do this in a finally so that if an exception is
			// thrown
			// during the above, we don't leave the Surface in an
			// inconsistent state
			if (c != null) {
				surfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}

	/* ======================================================================== */
	/* traitement executant un filtre gradient de l'im monoTab */
	/* - prise en compte de 4 voisins en croix */
	/* ======================================================================== */

	public void gradient() {
		Log.i(TAG, "debut traitement GRADIENT");
		int monoTab[][] = curentImageController.getMonoTab();
		int c = curentImageController.getColumns();
		int r = curentImageController.getRows();
		/*
		 * choix des variables : "pix" est le pixel a traiter; "vali" sont les 4
		 * voisins du pixel a traiter"
		 */
		int i, j, pix = 0;
		int[][] resTab = new int[r][c]; /* tableau resultant du traitement */

		int[] val = new int[5];

		/* traitement des bords */

		/* cas angle haut gauche */
		val[0] = monoTab[0][0];
		val[1] = monoTab[1][0];
		val[2] = monoTab[0][1];
		val[3] = monoTab[1][0];
		val[4] = monoTab[0][1];
		resTab[0][0] = val[2] - val[4] + val[3] - val[1];
		/* cas angle haut droite */
		val[0] = monoTab[0][c - 1];
		val[1] = monoTab[1][c - 1];
		val[2] = monoTab[0][c - 2];
		val[3] = monoTab[1][c - 1];
		val[4] = monoTab[0][c - 2];
		resTab[0][c - 1] = val[2] - val[4] + val[3] - val[1];
		/* cas angle bas gauche */
		val[0] = monoTab[r - 1][0];
		val[1] = monoTab[r - 2][0];
		val[2] = monoTab[r - 1][1];
		val[3] = monoTab[r - 2][0];
		val[4] = monoTab[r - 1][1];
		resTab[r - 1][0] = val[2] - val[4] + val[3] - val[1];
		/* cas angle bas droite */
		val[0] = monoTab[r - 1][c - 1];
		val[1] = monoTab[r - 2][c - 1];
		val[2] = monoTab[r - 1][c - 2];
		val[3] = monoTab[r - 2][c - 1];
		val[4] = monoTab[r - 1][c - 2];
		resTab[r - 1][c - 1] = val[2] - val[4] + val[3] - val[1];

		/* traitement ligne horizontale du haut de gauche a droite */
		for (j = 1; j < c - 2; j++) {
			val[0] = monoTab[0][j];
			val[1] = monoTab[1][j];
			val[2] = monoTab[0][j + 1];
			val[3] = monoTab[1][j];
			val[4] = monoTab[0][j - 1];
			resTab[0][j] = val[2] - val[4] + val[3] - val[1];
		}
		/* traitement ligne horizontale du bas de gauche a droite */
		for (j = 1; j < c - 2; j++) {
			val[0] = monoTab[r - 1][j];
			val[1] = monoTab[r - 2][j];
			val[2] = monoTab[r - 1][j + 1];
			val[3] = monoTab[r - 2][j];
			val[4] = monoTab[r - 1][j - 1];
			resTab[r - 1][j] = val[2] - val[4] + val[3] - val[1];
		}
		/* traitement ligne verticale gauche de haut en bas */
		for (i = 1; i < r - 2; i++) {
			val[0] = monoTab[i][0];
			val[1] = monoTab[i - 1][0];
			val[2] = monoTab[i][1];
			val[3] = monoTab[i + 1][0];
			val[4] = monoTab[i][1];
			resTab[i][0] = val[2] - val[4] + val[3] - val[1];
		}

		/* traitement ligne verticale droite, de haut en bas */
		for (i = 1; i < r - 2; i++) {
			val[0] = monoTab[i][c - 1];
			val[1] = monoTab[i - 1][c - 1];
			val[2] = monoTab[i][c - 2];
			val[3] = monoTab[i - 1][c - 1];
			val[4] = monoTab[i][c - 2];
			resTab[i][c - 1] = val[2] - val[4] + val[3] - val[1];
		}

		/*
		 * reste du traitement entre les bornes pour i [1;columns-2] pour j [1;rows-2]
		 */

		for (i = 1; i < r - 2; i++)
			for (j = 1; j < c - 2; j++) {
				val[0] = monoTab[i][j];
				val[1] = monoTab[i - 1][j];
				val[2] = monoTab[i][j + 1];
				val[3] = monoTab[i + 1][j];
				val[4] = monoTab[i][j - 1];
				resTab[i][j] = val[2] - val[4] + val[3] - val[1];
			}
		Log.i(TAG, "fin calcul");

		int[] tabTT = setARGB(resTab, r, c);
		int offset = 0; // The index of the first color to read from pixels[]
		curentImageController.setBitmap(Bitmap.createBitmap(tabTT, offset, c, c, r, Bitmap.Config.ARGB_8888));
	}

	public void actionMove(String arg) {
		// TODO Auto-generated method stub
		String[] params = cutString(arg, ",");
		PositionAndScale newPosAndScale = new PositionAndScale();
		if (params.length >= 2) {
			int index = Integer.parseInt(params[1]);
			newPosAndScale.fromString(arg.substring(arg.indexOf(params[1]) + 2));
			Log.e("NOMBRE", "actionMove setPositionAndScale !!!!!!!!!!! index " + index + "args " + arg.substring(arg.indexOf(params[1]) + 2));
			//PositionAndScale PointInfo
			dicomView.setPositionAndScale(index, newPosAndScale);
		}
	}

	/* ======================================================================== */
	/* traitement executant un zoom sur l'im img, suivant parametres */
	/* fixes par l'utilisateur */
	/* ======================================================================== */

	public void zoomIn() {
		if (mPopup != null && mPopup.isShowing()) {
			zoomPopup(true);
		}
	}

	public void zoomOut() {
		if (mPopup != null && mPopup.isShowing()) {
			zoomPopup(false);
		}
	}
	
	public void zoomPopup(boolean sens) {
		int width = DicomActivity.this.mPopup.getWidth();
		int height = DicomActivity.this.mPopup.getHeight();
		if (sens) {
			width = (int) (width * 1.3);
			height = (int) (height * 1.3);
		} else {
			width = (int) (width / 1.3);
			height = (int) (height / 1.3);
		}
		DicomActivity.this.mPopup.update(width, height);
	}

	public int char2int(char c) {
		if (c == '0')
			return 0;
		if (c == '1')
			return 1;
		if (c == '2')
			return 2;
		if (c == '3')
			return 3;
		if (c == '4')
			return 4;
		if (c == '5')
			return 5;
		if (c == '6')
			return 6;
		if (c == '7')
			return 7;
		if (c == '8')
			return 8;
		if (c == '9')
			return 9;
		return -1;
	}

	public int id2IP(String id) {
		int num = 0;
		int comptMul = 1;
		int index = id.length() - 1;
		int chiff = -1;
		char cour;

		do {
			cour = id.charAt(index);
			chiff = char2int(cour);
			num += chiff * comptMul;
			comptMul *= 10;
			index--;
		} while ((index >= 0));
		return num;
	}

	public int selectDicom(String arg) {
		Log.e(TAG, "TESTDICOM selectDicom !!!!!!!!! arg " + arg);
		String[] params = cutString(arg, ",");
		int index = -1;
		if (params.length >= 2) {
			index = Integer.parseInt(params[1]);
		} else {
			return -1;
		}
		Log.e(TAG, "TESTDICOM selectDicom !!!!!!!!!!! index " + index);
		//curentImageController = dicomView._graphics.get(index);
		Collections.swap(dicomView.imageControllers, dicomView.imageControllers.size() - 1, index);
		dicomView.setCurrentImageControllerIndex(index);
		curentImageController.setChange(true);
		return index;
	}

	/**
	 * See StringTokenizer for delim parameter format
	 */
	public static String[] cutString(String str, String delim) {
		ArrayList<String> strings = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(str, delim);
		while (tokenizer.hasMoreTokens()) {
			strings.add(tokenizer.nextToken());
		}

		return strings.toArray(new String[0]);
	}

	// ----------------------------------------------------------------------------------------------

	class ImageController {

		private BitmapDrawable bitmapDrawable;

		private boolean firstLoad;

		private int width, height, displayWidth, displayHeight;

		private float centerX, centerY, scaleX, scaleY, angle;

		private float minX, maxX, minY, maxY;

		private ImageDicom _imageDicom;
		private int _monoTab[][];
		private int rows, columns, offset;
		private int[] tabTT;

		private String _imageName;

		private boolean change;

		private static final float SCREEN_MARGIN = 100;
		
		public ImageController(ImageDicom imageDicom) {
			this.firstLoad = true;
			load(imageDicom);
			Log.i(TAG, "Image BITMAP CREATED");
		}
		
		public void setBitmap(Bitmap bitmap) {
			this.bitmapDrawable = new BitmapDrawable(bitmap);
			getMetrics(this.bitmapDrawable);
			this.change = true;
		}

		public boolean getChange() {
			return this.change;
		}

		public void setChange(boolean change) {
			this.change = change;
		}

		public int getRows() {
			return this.rows;
		}
		public int getColumns() {
			return this.columns;
		}
		public int[][] getMonoTab() {
			return this._monoTab;
		}

		public Bitmap getOriBitMap() {
			return Bitmap.createBitmap(tabTT, offset, columns, columns, rows, Bitmap.Config.RGB_565);
		}
		public String getImageName() {
			return _imageName;
		}
		public void setImageName(String imageName) {
			this._imageName = imageName;
		}
		private void getMetrics(BitmapDrawable drawable) {
			Bitmap bitmap = drawable.getBitmap();
			// The DisplayMetrics don't seem to always be updated on screen rotate, so we hard code a portrait
			// screen orientation for the non-rotated screen here...
			this.displayWidth = bitmap.getWidth();
			this.displayHeight = bitmap.getHeight();
		}

		/** Called by activity's onResume() method to load the images */
		public void load(ImageDicom imageDicom) {
			this._imageDicom = imageDicom;
			this._imageName = "unknown";

			Log.i(TAG, "Image DICOM LOADED");
			this.rows = imageDicom.getRows();
			this.columns = imageDicom.getColumns();
			Log.i(TAG, "imageDicom rows :" + rows + " columns :" + columns);

			this._monoTab = imageDicom.getRectangularROIofImageMonochrome(new Rectangle(0, 0, columns, rows));

			Log.i(TAG, "fin extraction tab monchrome");
			if (_monoTab == null) {
				Log.i(TAG, "impossible d'extraire le tableau d'entier");
				// return null;
			}
			tabTT = setARGB(_monoTab, rows, columns);
			// imgtt = createImage( new MemoryImageSource(columns,rows,tabTT,0,columns));
			offset = 0; // The index of the first color to read from
			Log.i(TAG, "LOAD columns " + columns + " rows " + rows + " firstLoad " + firstLoad);
			this.bitmapDrawable = new BitmapDrawable(Bitmap.createBitmap(tabTT, offset, columns, columns, rows, Bitmap.Config.RGB_565));
			getMetrics(this.bitmapDrawable);
			this.width = bitmapDrawable.getIntrinsicWidth();
			this.height = bitmapDrawable.getIntrinsicHeight();
			float cx, cy, sx, sy;
			if (firstLoad) {
				cx = displayWidth/2;
				cy = displayHeight/2;
				float sc = 1.0f;
				sx = sy = sc;
				firstLoad = false;
			} else {
				// Reuse position and scale information if it is available
				cx = this.centerX;
				cy = this.centerY;
				sx = this.scaleX;
				sy = this.scaleY;
			}

			Log.i(TAG, "imageDicom setPos cx :" + cx + " cy " + cy + " sx " + sx + " sy " + sy);
			setPos(cx, cy, sx, sy, 0.0f);
		}

		/** Called by activity's onPause() method to free memory used for loading the images */
		public void unload() {
			this.bitmapDrawable = null;
		}

		/** Set the position and scale of an image in screen coordinates */
		public boolean setPos(PositionAndScale newImgPosAndScale) {
			return setPos(newImgPosAndScale.getXOff(), newImgPosAndScale.getYOff(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale
					.getScaleX() : newImgPosAndScale.getScale(), (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale.getScaleY()
					: newImgPosAndScale.getScale(), newImgPosAndScale.getAngle());
		}

		/** Set the position and scale of an image in screen coordinates */
		private boolean setPos(float centerX, float centerY, float scaleX, float scaleY, float angle) {
			float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
			float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX + ws, newMaxY = centerY + hs;
			this.centerX = centerX;
			this.centerY = centerY;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.angle = angle;
			this.minX = newMinX;
			this.minY = newMinY;
			this.maxX = newMaxX;
			this.maxY = newMaxY;
			return true;
		}

		/** Return whether or not the given screen coords are inside this image */
		public boolean containsPoint(float scrnX, float scrnY) {
			// FIXME: need to correctly account for image rotation
			return (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);
		}

		public void draw(Canvas canvas) {
			canvas.save();
			float dx = (maxX + minX) / 2;
			float dy = (maxY + minY) / 2;
			Log.i("XMPPClient", "DRAW " + dy + " dy " + dy + " minX " + minX + " minY " + minY + " maxX " + maxX + " maxY " + maxY + " angle " + angle);
			this.bitmapDrawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			canvas.translate(dx, dy);
			canvas.rotate(angle * 180.0f / (float) Math.PI);
			canvas.translate(-dx, -dy);
			this.bitmapDrawable.draw(canvas);
			canvas.restore();
		}

		public BitmapDrawable getDrawable() {
			return this.bitmapDrawable;
		}
		
		public void setDrawable(BitmapDrawable drawable) {
			this.bitmapDrawable = drawable;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public float getCenterX() {
			return centerX;
		}

		public float getCenterY() {
			return centerY;
		}

		public float getScaleX() {
			return scaleX;
		}

		public float getScaleY() {
			return scaleY;
		}

		public float getAngle() {
			return angle;
		}

		// FIXME: these need to be updated for rotation
		public float getMinX() {
			return minX;
		}

		public float getMaxX() {
			return maxX;
		}

		public float getMinY() {
			return minY;
		}

		public float getMaxY() {
			return maxY;
		}

		public ImageDicom getImageDicom() {
			return this._imageDicom;
		}

		public void setImageDicom(ImageDicom imageDicom) {
			this._imageDicom = imageDicom;
		}
	}
	
	public boolean sendRemotes(String arg) {
		if (connection == null) {
			Log.d(TAG, "sendRemotes Please Login First");
			return false;
		}

		Log.d(TAG, "sendRemotes " + arg);
		String to = getXmppRemoteUser();
		Message msg = new Message(to, Message.Type.chat);
		msg.setBody(arg);
		sendRemotesTime = Calendar.getInstance().getTimeInMillis();
		//To avoid xmpp account to be blocked
		if ((lastSendRemotesTime > 0) && ((sendRemotesTime - lastSendRemotesTime) < XMPP_GAP)) {
			try {
				Thread.sleep(1000 - (sendRemotesTime - lastSendRemotesTime));
			} catch (InterruptedException e) {
				Log.e(TAG, e.toString());
			}
		}
		Log.i("NOMBRE", "Sending text [" + arg + "] to [" + to + "]");
		connection.sendPacket(msg);
		lastSendRemotesTime = sendRemotesTime;
		return true;
	}
	
	public XMPPConnection getConnection() {
		return connection;
	}
	
	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		collabBoard.setConnection(connection);
	}

	public void connectionSettings(Bundle locBundle) {
		Log.i("XMPPClient", "connectionSettings");
		Bundle accountBundle = locBundle.getBundle("settingsResults");
		String host = accountBundle.getString("host");
		String port = accountBundle.getString("port");
		String service = accountBundle.getString("service");
		String username = accountBundle.getString("username");
		String password = accountBundle.getString("password");
		this.xmppRemoteUser = accountBundle.getString("remoteuser");
		this.xmppUsername = username;

		// Create a connection
		ConnectionConfiguration connConfig = new ConnectionConfiguration(host,
				Integer.parseInt(port), service);
		StringBuffer buffer = new StringBuffer("system");
		buffer.append(File.separator).append("etc");
		buffer.append(File.separator).append("security");
		buffer.append(File.separator).append("cacerts.bks");
		connConfig.setTruststorePath(buffer.toString());
		connConfig.setTruststoreType("bks");
		connConfig.setTruststorePassword("changeit");
		Log.i("XMPPClient",
				"[SettingsDialog] ConnectionConfiguration created for " + host
						+ " port " + port);
		XMPPConnection connection = new XMPPConnection(connConfig);
		Log.i("XMPPClient", "[SettingsDialog] Connection created for "
				+ connection.getHost());
		try {
			connection.connect();
			Log.i("XMPPClient", "[SettingsDialog] Connected to "
					+ connection.getHost());
		} catch (XMPPException ex) {
			Log.e("XMPPClient", "[SettingsDialog] Failed to connect to "
					+ connection.getHost());
			Log.e("XMPPClient", ex.toString());
			alertMessage("Failed to connect to " + host);
			setConnection(null);
			return;
		}
		try {
			Log.i("XMPPClient", "[SettingsDialog] trying login as " + username
					+ " password " + password);
			connection.login(username, password);
			Log.i("XMPPClient", "Logged in as " + connection.getUser());

			// Set the status to available
			Presence presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			setConnection(connection);
		} catch (XMPPException ex) {
			Log.e("XMPPClient", ex.toString());
			Log.e(TAG, "[SettingsDialog] Failed to log in as " + username);
			alertMessage("Failed to connect to " + username + " with pass " + password);
			setConnection(null);
			if (ex.getXMPPError() != null) {
				int err = ex.getXMPPError().getCode();
				// Responding to the various errors appropriately and allowing
				// signing in on the fly
				switch (err) {
				case 401:
				case 407:
					Log.e(TAG, "Failed to Login you must Create Account"
							+ ex.getXMPPError().getMessage());
				default:
					Log.e(TAG, "Failed to Login" + ex.getXMPPError().getMessage());
				}
			}
			return;
		}
	}

	void drawInfoWindow() {
		ImageController curentImageController = dicomView.getCurrentImageController();
		if (curentImageController == null)
			return;

		if (curentImageController != null) {
			// GeoPoint geoPoint = DicomActivity.this.selectedSearchPoint
			// .getPoint();
			ImageDicom id = curentImageController._imageDicom;

			// initialisation de la liste a partir de la Hashtable
			Hashtable h = id.getListGroupDicom();
			Enumeration en = h.elements();
			en = h.keys();
			Log.e("DicomActivity", "getStringInfoDicom "
					+ curentImageController._imageName);
			String page = "<html><body><font size=-2><p>"
					+ curentImageController._imageName + "</p>";
			while (en.hasMoreElements()) {
				int groupId = ((Integer) en.nextElement()).intValue();
				page = page + "<p>" + groupId + "</p>" + "<p>"
						+ id.getStringInfoDicom(groupId) + "</p>";
			}
			page = page + "</font></body></html>";
			mPopupWebView.loadData(page, "text/html", "utf-8");
			Bitmap img = curentImageController.getDrawable().getBitmap();
			int bitmapWidth = img.getWidth();
			int bitmapHeight = img.getHeight();
			Log.e("DicomActivity", "BitMap width = " + bitmapWidth + " height "
					+ bitmapHeight);
			showPopup(DicomActivity.this.dicomView, Gravity.NO_GRAVITY, (int)(curentImageController.getCenterX() - bitmapWidth/2), (int)(curentImageController.getCenterY() - bitmapHeight/2), bitmapWidth, bitmapHeight);
		}
	}

	void dismissPopup() {
		if (DicomActivity.this.mPopup != null) {
			DicomActivity.this.mPopup.dismiss();
		}
	}

	private void showPopup(View parent, int gravity, int x, int y, int width,
			int height) {
		Log.e("DicomActivity", "showPopup width = " + width + " height " + height);
		if (DicomActivity.this.mPopup == null) {
			PopupWindow p = new PopupWindow(this);
			p.setFocusable(false);
			p.setContentView(mPopupView);
			p.setWidth(width);
			p.setHeight(height);
			p.setBackgroundDrawable(null);

			p.setAnimationStyle(R.style.PopupAnimation);
			DicomActivity.this.mPopup = p;
		} else {
			DicomActivity.this.mPopup.setWidth(width);
			DicomActivity.this.mPopup.setHeight(height);
		}
		// coords = myImageController.getCoordinates();
		// hitTestRecr.offset(coords.getX(), coords.getY());
		DicomActivity.this.mPopup.showAtLocation(parent, gravity, x, y);
	}
	   /** Determine the space between the first two fingers */
	   private float spacing(MotionEvent event) {
	      float x = event.getX(0) - event.getX(1);
	      float y = event.getY(0) - event.getY(1);
	      return FloatMath.sqrt(x * x + y * y);
	   }

	   /** Calculate the mid point of the first two fingers */
	   private void midPoint(PointF point, MotionEvent event) {
	      float x = event.getX(0) + event.getX(1);
	      float y = event.getY(0) + event.getY(1);
	      point.set(x / 2, y / 2);
	   }

	public String getXmppRemoteUser() {
		return this.xmppRemoteUser;
	}
}
