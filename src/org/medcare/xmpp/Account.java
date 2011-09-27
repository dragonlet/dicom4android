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

/**XMPP servers may require a number of attributes to be set when creating a new account.
 The standard account attributes are as follows:

 <ul>
 <li>name -- the user's name.
 <li>first -- the user's first name.
 <li>last -- the user's last name.
 <li>email -- the user's email address.
 <li>city -- the user's city.
 <li>state -- the user's state.
 <li>zip -- the user's ZIP code.
 <li>phone -- the user's phone number.
 <li>url -- the user's website.
 <li>date -- the date the registration took place.
 <li>misc -- other miscellaneous information to associate with the account.
 <li>text -- textual information to associate with the account.
 <li>remove -- empty flag to remove account.
 </ul>
 */

import java.util.*;

/**
 * a class that holds the extra information vCard
 */
public class Account {

	public Account(String fName, String lName, String email, String city,
			String state, String zip, String phone, String url, String text) {

		this.fName = fName;
		this.lName = lName;
		this.email = email;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.url = url;
		this.text = text;
		this.date = new Date().toString();

		map = new HashMap<String, String>();

		map.put("first", fName);
		map.put("last", lName);
		map.put("email", email);
		map.put("city", city);
		map.put("state", state);
		map.put("zip", zip);
		map.put("phone", phone);
		map.put("url", url);
		map.put("date", date);
		map.put("text", text);

	}

	/**
	 * helps in updating the account as the packet takes a map
	 */
	public Map<String, String> getMap() {
		return map;
	}

	String fName, lName, email, city, state, zip, phone, url, text, date;
	Map<String, String> map;
}
