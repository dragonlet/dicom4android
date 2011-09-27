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

/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class AccountCreate extends Activity implements View.OnClickListener {
	static final String ACTIVITY_RESULT = new String(
			"android.intent.action.ACCOUNT_CREATE");
	private Bundle resultBundle;
	private EditText userid;
	private EditText password;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		resultBundle = new Bundle();
		setContentView(R.layout.account_create);
		getWindow().setFlags(4, 4);
		setTitle("Creation de compte");
		Button ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
		userid = (EditText) findViewById(R.id.userid);
		password = (EditText) findViewById(R.id.password);
	}

	public void onClick(View v) {
		resultBundle.putString("username", userid.getText().toString());
		resultBundle.putString("password", password.getText().toString());
		Intent intent = new Intent(ACTIVITY_RESULT);
		intent.putExtra("accountResults", this.resultBundle);
		setResult(RESULT_OK, intent);
		finish();
	}
}
