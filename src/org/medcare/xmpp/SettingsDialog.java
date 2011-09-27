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

package org.medcare.xmpp;

import org.medcare.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class SettingsDialog extends Activity implements
		android.view.View.OnClickListener {
	static final String ACTIVITY_RESULT = new String(
			"android.intent.action.SETTINGS_RESULTS");
	private EditText mHost;
	private EditText mPort;
	private EditText mService;
	private TextView mUsername;
	private TextView mPassword;
	private TextView mRemoteUser;
	private Bundle resultBundle;
	String DEFAULT_PORT = "5222";
	String DEFAULT_HOST = "talk.google.com";
	String SERVICE = "gmail.com";
	String ME = "account@gmail.com";
	String DEFAULT_REMOTE = "osfe.org@gmail.com";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		resultBundle = new Bundle();
		setContentView(R.layout.settings);
		getWindow().setFlags(4, 4);
		setTitle("XMPP Settings");
		Button ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
		mHost = (EditText) findViewById(R.id.host);
		mPort = (EditText) findViewById(R.id.port);
		mService = (EditText) findViewById(R.id.service);
		mUsername = (EditText) findViewById(R.id.userid);
		mPassword = (EditText) findViewById(R.id.password);
		mRemoteUser = (EditText) findViewById(R.id.remote_userid);
		mHost.setText(DEFAULT_HOST);
		mPort.setText(DEFAULT_PORT);
		mService.setText(SERVICE);
		mUsername.setText(ME);
		mRemoteUser.setText(DEFAULT_REMOTE);
	}

	public void onClick(View v) {
		resultBundle.putString("host", mHost.getText().toString());
		resultBundle.putString("port", mPort.getText().toString());
		resultBundle.putString("service", mService.getText().toString());
		resultBundle.putString("username", mUsername.getText().toString());
		resultBundle.putString("password", mPassword.getText().toString());
		resultBundle.putString("remoteuser", mRemoteUser.getText().toString());
		Intent intent = new Intent(ACTIVITY_RESULT);
		intent.putExtra("settingsResults", this.resultBundle);
		setResult(RESULT_OK, intent);
		finish();
	}
}
