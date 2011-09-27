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

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import android.util.Log;

/**
 * A class that handles the private chat with individual users. It runs on its
 * own thread so each chat gets its own thread.
 */
public class ChatClient implements MessageListener, Runnable {

	private static final String TAG = null;
	private ChatManager chatmanager;
	XMPPConnection connection;
	String buddy;
	CollabBoard client;
	String nickName;
	Chat chat;

	public ChatClient(CollabBoard client, XMPPConnection connection,
			String buddy, String nickName) {
		// super(client, "ChatAway "+client.login.userName);
		this.connection = connection;
		this.buddy = buddy;
		this.client = client;
		this.nickName = nickName;
		this.chatmanager = connection.getChatManager();
		// FIXME setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		buildUI();

	}

	public void run() {
		chat = chatmanager.createChat(buddy, this);
		// chat = new Chat(connection, buddy);
		// chat.addMessageListener(this);
		// show();
	}

	// gtalk seems to refuse non-chat messages
	// messages without bodies seem to be caused by things like typing
	public void processMessage(Chat chat, Message message) {
		if (message.getType().equals(Message.Type.chat)
				&& message.getBody() != null) {
			Log.i(TAG, "Received: " + message.getBody());
		} else {
			Log.e(TAG, "I got a message I didn''t understand");
		}
	}

	private void buildUI() {
	}

	public void sendMessage(String jab) {
		try {
			chat.sendMessage(jab);
		} catch (XMPPException e) {
			//Helper.alert(e.getXMPPError().toString());
		} catch (Exception e) {
		}
	}

	public void sendMessage(Message jab) {
		try {
			chat.sendMessage(jab);
		} catch (XMPPException e) {
			//Helper.alert(e.getXMPPError().toString());
		} catch (Exception e) {
		}
	}

	/**
	 * check for a similar chat as buddy holds the full jid (with resource as
	 * oppose to just name as romeo@montague.net/desctop is not similar to
	 * romeo@montague.net/wireless
	 */
	public boolean equals(ChatClient client) {

		return buddy.equals(client.buddy);
	}

	/**
	 * Overriding hashCode as we are going to use the client for storage in a
	 * hash
	 */
	public int hashCode() {
		if (buddy != null)
			return buddy.hashCode();
		else
			return super.hashCode();
	}
}
