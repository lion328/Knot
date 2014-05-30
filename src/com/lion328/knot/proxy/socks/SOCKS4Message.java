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

package com.lion328.knot.proxy.socks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import com.lion328.knot.proxy.INetworkCommunicable;
import com.lion328.knot.util.ByteUtil;
import com.lion328.knot.util.IFunction;
import com.lion328.knot.util.IOUtil;

public class SOCKS4Message implements INetworkCommunicable {
	
	public static final byte VERSION = 0x4, COMMAND_CONNECT = 0x1, COMMAND_BINDING = 0x2
							, REQUEST_GRANTED = 0x5A, REQUEST_REJECTED = 0x5B;
	
	private byte command;
	private int port;
	private byte[] ip;
	private String userid;
	
	private IFunction bindEvent;
	
	public SOCKS4Message(IFunction bindevent) {
		bindEvent = bindevent;
	}
	
	@Override
	public void communicate(DataInputStream in, DataOutputStream out) throws IOException {
		if(in.readByte() != VERSION) throw new IOException("Invaild SOCKS4 request.");
		command = in.readByte();
		port = in.readUnsignedShort();
		in.readFully(ip);
		
		boolean is4a = ((ip[0] | ip[1] | ip[2]) == 0x0) && (ip[3] != 0x0); //Check for SOCKS4a
		
		userid = IOUtil.readUntil(in, '\0');
		userid = userid.substring(0, userid.length() - 2);
		
		if(is4a) {
			String domain = IOUtil.readUntil(in, '\0');
			domain = domain.substring(0, domain.length() - 2);
			ip = InetAddress.getByName(domain).getAddress();
		}
		
		boolean binding_rej = false;
		if(command == COMMAND_BINDING)
			binding_rej = (boolean)bindEvent.function(new Object[]{ip, port})[0];
		
		out.writeByte(0x0);
		if(binding_rej) out.writeByte(REQUEST_REJECTED);
		else out.writeByte(REQUEST_GRANTED);
		out.write(new byte[]{0x1,(byte)0xC3,(byte)0xAA,0xA,0x1,0x0});
	}

	public byte getCommand() {
		return command;
	}

	public int getPort() {
		return port;
	}

	public byte[] getIPAddress() {
		return ip;
	}

	public String getUserID() {
		return userid;
	}
}
