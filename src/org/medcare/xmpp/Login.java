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

/**
 * An object that holds the basic login data
 */
public class Login {

	public Login(String server, int port, String userName, String password,
			String service) {

		this.server = server;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.service = service;
	}

	public String toString() {
		return "Server :" + server + " Port " + port + " userName " + userName
				+ " password " + password + " service " + service;
	}

	String server, userName, password, service;
	int port;
}
