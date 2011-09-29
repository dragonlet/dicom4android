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

import java.io.LineNumberReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.medcare.Dicom.DicomActivity;

import android.os.Handler;
import android.util.Log;

/**
 * CollabBoard is the central class of the collaboration part of the application, it establish connection, create chat, crate rooms, process packets etc... 
 */

public class CollabBoard implements PacketListener,
		RosterListener {

	int SUB_PNLS = 3;
	boolean debug = true;
	private static final String TAG = "COLLABBOARD";
	private Handler mHandler = new Handler();
	private DicomActivity dicomActivity;

	/**
	 * The constructor it takes a name for the frame
	 */
	public CollabBoard(DicomActivity dicomActivity) {
		this.dicomActivity = dicomActivity;
		usersMap = new HashMap<String, String>();
		rooms = new HashMap<String, Runnable>();
		jabbers = new HashMap<String, Runnable>();

		// creates the type of filter we need for the incoming packets
		myFilter = new OrFilter(new PacketTypeFilter(Message.class),
				new OrFilter(new PacketTypeFilter(Presence.class),
						new PacketTypeFilter(IQ.class)));
	}

	/**
	 * Implementing the PacketListner processPacket according to the packet type
	 */
	public void processPacket(Packet packet) {
		Log.e(TAG, "GOT PACKET!!!!!!!! ");
Log.e("NOMBRE", "CollabBoard GOT PACKET!!!!!!!! ");
		if (Message.class.isInstance(packet)) {
		} else if (Presence.class.isInstance(packet)) {
		} else if (IQ.class.isInstance(packet)) {
		}
		if (packet instanceof Message) {
			Log.e(TAG, "GOT MESSAGE!!!!!!!! ");
			final Message msg = (Message) packet;
			Message.Type type = msg.getType();
			//if (msg.getBody() != null && type != Message.Type.groupchat && type != Message.Type.chat)
			//ATTENTION ANDRE Pourquoi eliminer les groupChats ???
			if (msg.getBody() != null && type != Message.Type.groupchat) {
Log.e("NOMBRE", "CollabBoard [" + msg.getFrom() + "]  [Type:] " + msg.getType() + "\n" + msg.getBody());
				Log.e(TAG, "[" + msg.getFrom() + "]  [Type:] " + msg.getType() + "\n" + msg.getBody());
				String fromName = StringUtils.parseBareAddress(msg
								.getFrom());
				Log.e(TAG, "GOT TEXT!!!!!!!! [" + msg.getBody() + "] from [" + fromName + "]");
				// Add the incoming message to the list view ANDRE GERER LES AUTRES TYPES PLUS FINEMENET
				if (type != Message.Type.error)
					mHandler.post(new Runnable() {
						public void run() {
							dicomActivity.dicomView.dicomThread.localAction(msg.getBody());
						}
					});
			}
		} else if (packet instanceof Presence) {
			Log.e(TAG, "GOT PRESENCE!!!!!!!! ");
			Presence presence = (Presence) packet;
			// If we got request to subscribe will approve it here automatically
Presence.Type type = presence.getType();
String from = presence.getFrom();
Log.e("NOMBRE", "CollabBoard GOT PRESENCE!!!!!!!! " + from + " type " + type);
			if (type == Presence.Type.subscribe) {
				
				presence.setFrom(presence.getTo());
				presence.setTo(from);
				presence.setType(Presence.Type.subscribe);
				connection.sendPacket(presence);

				// Now will add to our roster
				presence.setType(Presence.Type.subscribe);
				connection.sendPacket(presence);
			}
			setRoster();
		} else if (packet instanceof IQ) {
			Log.e(TAG, "GOT IQ !!!!!!!! ");
			IQ iq = (IQ) packet;
			if (iq.getType() == IQ.Type.RESULT) {
				setRoster();
			}
		} else {
			Log.e(TAG, "GOT ELSE UNKNOWN !!!!!!!! ");
		}
	}

	/**
	 * Creating the initial connection
	 */
	/**
	 * Called by Settings dialog when a connection is establised with the XMPP
	 * server
	 * 
	 * @param connection
	 */
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		logged = true;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			connection.addPacketListener(this, myFilter);
			roster = connection.getRoster();
			roster.reload(); // Boy this took a while to get!
			setRoster();
		}
	}

	/**
	 * creates the actual private chat
	 */
	protected void chat(String jid, String nickName) {

		if (connection == null || !logged) {
			dicomActivity.alertMessage("Please Connect first");
			Log.e(TAG, "Please Login");
			return;
		}
		client = new ChatClient(this, connection, jid, nickName);
		jabbers.put(jid, client); // not used yet...
		Thread t = new Thread(client);
		t.setDaemon(false);
		t.start();
	}

	/**
	 * Joins a chat room, creating a room object and spinning a thread
	 * 
	 * @param evt
	 */
	public void join(String room) {

		if (connection == null) {
			dicomActivity.alertMessage("Please Connect first");
			Log.e(TAG, "Please Connect");
			return;
		}

		// if we already in the room get to it
		if (rooms.containsKey(room)) {
			try {
				// Afficher
				return;
			} catch (Exception e) {/* go on bro */
			}
		}

		groupChat = new GroupChatClient(this, connection, room);
		rooms.put(room, groupChat);

		Thread t = new Thread(groupChat);
		t.setDaemon(false);
		t.start();
	}

	/**
	 * Quit a chat room
	 * 
	 * @param evt
	 */
	public void quitRoom(String room) {

		if (connection == null) {
			dicomActivity.alertMessage("Please Connect first");
			Log.e(TAG, "Please Connect");
			return;
		}

		// if we are not in the room return
		if (!rooms.containsKey(room)) {
			try {
				return;
			} catch (Exception e) {/* go on bro */
			}
		}

		groupChat = (GroupChatClient) rooms.get(room);
		rooms.remove(room);
		connection.removePacketListener(groupChat);
		groupChat.groupChat.leave();
		groupChat = null;
	}

	/**
	 * adds a jaberon to your roster
	 * 
	 * @param evt
	 */
	public void addJabber(String jid, String name) {

		// validate jid
		String dName = StringUtils.parseName(jid).trim();
		String dServer = StringUtils.parseServer(jid).trim();
		if (debug)
			Log.i(TAG, "addJabber dName " + dName + " dServer " + dServer);
		if (dName == null || dName == "" || dServer == null || dServer == ""
				|| dServer.length() < 2) {
			Log.e(TAG,
					"Please enter a valid Jabber ID i.e you@JabberServer.com");
			return;
		}
		Presence presence = new Presence(Presence.Type.subscribe);
		presence.setFrom(login.userName);
		presence.setTo(jid);
		presence.setProperty("name", name);
		connection.sendPacket(presence);
	}

	/**
	 * show roster set up the Roster and load the buddies into the users map.
	 * This allows us to call the JID using just the user (nick)name
	 */
	private void setRoster() {

		if (connection == null) {
			dicomActivity.alertMessage("Please Connect first");
			Log.e(TAG, "Please Connect");
			return;
		}

		if (roster == null) {
			return;
		}

		try {

			Collection<RosterGroup> groups = (Collection<RosterGroup>) roster.getGroups();
			Iterator<RosterGroup> groupsIterator = groups.iterator();
			//Iterator groupItems = null;
			Collection<RosterEntry> unfiledEntries = (Collection<RosterEntry>) roster.getUnfiledEntries();
			Iterator<RosterEntry> unfiledItems = unfiledEntries.iterator();
			RosterGroup group;
			RosterEntry entry = null;

			while (groupsIterator.hasNext()) {
				group = (RosterGroup) groupsIterator.next();
				Log.i(TAG, "ROSTER GROUP NAME : " + group.getName());
				Iterator<RosterEntry> groupItems = (Iterator<RosterEntry>) group.getEntries();

				while (groupItems.hasNext()) {
					entry = (RosterEntry) groupItems.next();
					String name = entry.getName();
					if (name == null || name == "")
						name = StringUtils.parseName(entry.getUser());
					Log.i(TAG, "ROSTER ITEM NAME : " + name);
					usersMap.put(name, entry.getUser());
					Presence p = roster.getPresence(entry.getUser());
					if (p != null) {
						if (p.getType() == Presence.Type.available) {
							// Presence type highlights Will wait for now
						}
					}
				}
			}

			if (unfiledItems.hasNext()) {

				while (unfiledItems.hasNext()) {
					entry = (RosterEntry) unfiledItems.next();
					String name = entry.getName();
					if (name == null || name == "")
						name = StringUtils.parseName(entry.getUser());
					// as above for the unfiled
					usersMap.put(name, entry.getUser());
					Presence p = roster.getPresence(entry.getUser());
					if (p != null) {
						if (p.getType() == Presence.Type.available) { // ditto
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("setRoster exception " + e);
		}
	}

	/**
	 * Shows the project Documntations in the internal Browser
	 * 
	 * @param evt
	 */
	public void showDocs() {
		//Helper.setNetActive(true);
		//Helper.showDoc("file:docs/index.html");
	}

	XMPPConnection connection;
	Roster roster;
	RosterEntry rosterSelection;
	ChatClient client;
	String rosterSelectionString;
	Map<String, String> usersMap;
	Login login;
	LineNumberReader lineReader;
	GroupChatClient groupChat;
	PacketFilter myFilter;
	// AddDialog addDialog;
	boolean logged = false;
	HashMap<String, Runnable> rooms, jabbers;

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
