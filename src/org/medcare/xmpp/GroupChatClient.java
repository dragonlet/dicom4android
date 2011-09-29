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

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.util.Log;

/**
 * A class that handles the chatroom chat. It is running on its own thread and
 * each chat room gets to be added to the tabbed pan
 * getWindow().addContentView(textView, params);
 */
public class GroupChatClient implements PacketListener, Runnable, RosterListener {

	private static final String TAG = null;

	public GroupChatClient(CollabBoard collabBoard, XMPPConnection connection,
			String room) {
		this.connection = connection;
		this.collabBoard = collabBoard;
		this.room = room;
	}

	public void run() {

		try {
			groupChat = new MultiUserChat(connection, room);
			groupChat.join(collabBoard.login.userName);
		} catch (XMPPException ex) {
			Log.e(TAG, "Failed to Gett Group Chat: "
					+ ex.getXMPPError().getMessage());
		}
		groupChat.addParticipantListener(this);
		groupChat.addMessageListener(this);
		setRoster(); // this usualy wakes up only after some roster activity :(
		buildUI();
	}

	private void buildUI() {
	}

	/**
	 * process the filterd messages we get
	 */
	public void processPacket(Packet packet) {

		if (Message.class.isInstance(packet)
				&& ((Message) packet).getType() == Message.Type.groupchat) {
			String string, from;
			Message msg = (Message) packet;
			from = StringUtils.parseResource(msg.getFrom());
			if (from != null && from != "")
				string = "[" + from + "]  " + msg.getBody();
			else
				string = msg.getBody();
Log.i(TAG,"GROUPCHAT message" + string);
Log.i("NOMBRE", "GROUPCHAT message" + string);
		} else if (Presence.class.isInstance(packet)) {
Log.i(TAG,"GROUPCHAT presence");
			setRoster();
		}
	}

	/**
	 * process roster changes
	 */
	public void rosterModified() {
		setRoster();
	}

	/*
	 * public void keyTyped(KeyEvent evt) { }
	 * 
	 * public void keyReleased(KeyEvent evt) { }
	 * 
	 * public void keyPressed(KeyEvent evt) { // we are only listening to entery
	 * int keyCode = evt.getKeyCode();
	 * 
	 * // this alows multiline entery if (keyCode == KeyEvent.KEYCODE_ENTER &&
	 * evt.isShiftPressed()) { try {
	 * entery.getDocument().insertString(entery.getCaretPosition(), "\n", null);
	 * 
	 * } catch (Exception e) { } } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
	 * String jab = entery.getText(); try { groupChat.sendMessage(jab);
	 * entery.setText(""); } catch (XMPPException e) { Log.e(TAG,
	 * e.getXMPPError().toString()); } catch (Exception e) { } } }
	 */

	public void sendMessage(String jab) {
		try {
			groupChat.sendMessage(jab);
		} catch (XMPPException e) {
			//Helper.alert(e.getXMPPError().toString());
		} catch (Exception e) {
		}
	}

	public void sendMessage(Message jab) {
		try {
			groupChat.sendMessage(jab);
		} catch (XMPPException e) {
			//Helper.alert(e.getXMPPError().toString());
		} catch (Exception e) {
		}
	}

	/**
	 * show roster set up the Roster and load the buddies into the users map so
	 * we can call the JID by the name (The Roster usualy takes a bit to return)
	 */
	private void setRoster() {

		try {
			Iterator<String> jabbersIter = (Iterator<String>) groupChat.getParticipants();
			String jid;
			Vector<String> jabbers = new Vector<String>();

			while (jabbersIter.hasNext()) {
				jid = (String) jabbersIter.next();
				jabbers.add(jid);
			}
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * check for a similar room
	 */
	public boolean equals(GroupChatClient groupChat) {

		return room.equals(groupChat.room);
	}

	/**
	 * Overriding hashCode as we are going to use the record for storage in a
	 * hash
	 */
	public int hashCode() {
		return room.hashCode();
	}

	// JPanel panel1, panel2, panel3, panelFill, panelEmpty;
	// JEditorPane jabbing, entery ;
	// JPanel jabbingPanel, enteryPanel;
	// JButton exitButton, endButton;
	// JScrollPane listScrollPane ;
	XMPPConnection connection;
	CollabBoard collabBoard;
	String room, rosterSelectionString;
	MultiUserChat groupChat = null;

	@Override
	public void entriesAdded(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void entriesDeleted(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void entriesUpdated(Collection<String> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void presenceChanged(Presence arg0) {
		// TODO Auto-generated method stub

	}
}
