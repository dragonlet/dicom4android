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
public class AccountSetter extends Activity implements View.OnClickListener {
	static final String ACTIVITY_RESULT = new String(
			"android.intent.action.SETUP_RESULTS");
	EditText fName;
	EditText lName, email, city, state, zip, phone, url, text;
	Account account;
	private Bundle resultBundle;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		resultBundle = new Bundle();
		setContentView(R.layout.account_setter);
		getWindow().setFlags(4, 4);
		setTitle("XMPP Settings");
		Button ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(this);
		fName = (EditText) findViewById(R.id.fName);
		lName = (EditText) findViewById(R.id.lName);
		city = (EditText) findViewById(R.id.city);
		state = (EditText) findViewById(R.id.state);
		zip = (EditText) findViewById(R.id.zip);
		phone = (EditText) findViewById(R.id.phone);
		email = (EditText) findViewById(R.id.email);
		url = (EditText) findViewById(R.id.url);
		text = (EditText) findViewById(R.id.text);
	}

	public void onClick(View v) {
		resultBundle.putString("fName", fName.getText().toString());
		resultBundle.putString("lName", lName.getText().toString());
		resultBundle.putString("city", city.getText().toString());
		resultBundle.putString("state", state.getText().toString());
		resultBundle.putString("zip", zip.getText().toString());
		resultBundle.putString("phone", phone.getText().toString());
		resultBundle.putString("email", email.getText().toString());
		resultBundle.putString("url", url.getText().toString());
		resultBundle.putString("text", text.getText().toString());
		Intent intent = new Intent(ACTIVITY_RESULT);
		intent.putExtra("setupResults", this.resultBundle);
		setResult(RESULT_OK, intent);
		finish();
	}

	public Account getAccount() {
		return account;
	}
}
