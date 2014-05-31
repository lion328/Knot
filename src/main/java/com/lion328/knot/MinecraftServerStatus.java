/*
   Copyright 2014 Waritnan Sookbuntherng

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.lion328.knot;

import java.io.*;
import java.net.*;

public class MinecraftServerStatus {

	private String host;
	private int port;
	private String[] data;
	private int version = 0;

	public MinecraftServerStatus(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
		refresh();
	}

	private void refresh() throws IOException {
		Socket socket = new Socket(host, port);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		out.writeByte(0xFE);
		out.writeByte(1);

		String rawdata = "";

		for (int i = 0; i < in.readShort(); i++) rawdata += in.readChar();

		if (rawdata.startsWith("\2471")) {
			data = rawdata.split((char) 0x00 + "");
			version = 1;
		}
		else data = rawdata.split("\247");

		in.close();
		out.close();
		socket.close();
	}
	
	public int getProtocolVersion(){
		if(version == 1) return Integer.parseInt(data[1]);
		return 0;
	}
	
	public String getServerVersion(){
		if(version == 1) return data[2];
		return "";
	}
	
	public String getMOTD(){
		if(version == 1) return data[3];
		return data[0];
	}
	
	public int getMaxPlayer(){
		if(version == 1) return Integer.parseInt(data[5]);
		return Integer.parseInt(data[2]);
	}
	
	public int getCurrentPlayer(){
		if(version == 1) return Integer.parseInt(data[4]);
		return Integer.parseInt(data[1]);
 	}
}