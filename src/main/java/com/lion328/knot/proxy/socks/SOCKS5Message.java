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
import com.lion328.knot.util.IFunction;

public class SOCKS5Message implements INetworkCommunicable {
	
	public static final byte VERSION = 0x5, AUTH_NONE = 0x0, COMMAND_STREAM = 0x1, COMMAND_BINDING = 0x2, COMMAND_UDP = 0x3
			, ADDRESS_IPV4 = 0x1, ADDRESS_DOMAIN = 0x3, ADDRESS_IPV6 = 0x4;
	public static final int METHOD_NO_ACCEPTABLE = 0xFF;
	
	private int command, port, addr_type;
	private byte[] addr;
	private String addr_string;
	
	private IFunction bindEvent;
	
	public SOCKS5Message(IFunction bindEvent) {
		this.bindEvent = bindEvent;
	}

	@Override
	public void communicate(DataInputStream in, DataOutputStream out) throws IOException {
		if(in.readUnsignedByte() != VERSION) throw new IOException("Invaild SOCKS5 request.");
		byte[] authMethods = new byte[in.readUnsignedByte()];
		in.readFully(authMethods);
		boolean flag = false;
		for(byte b : authMethods) {
			if(b == AUTH_NONE) {
				flag = true;
				break;
			}
		}
		
		out.writeByte(VERSION);
		if(!flag) out.writeByte(METHOD_NO_ACCEPTABLE);
		
		in.readUnsignedByte();
		command = in.readUnsignedByte();
		in.read();
		addr_type = in.readUnsignedByte();
		switch(addr_type) {
		case ADDRESS_IPV4:
			addr = new byte[4];
			in.readFully(addr);
			break;
		case ADDRESS_DOMAIN:
			byte[] buff = new byte[in.readUnsignedByte()];
			in.readFully(buff);
			addr_string = new String(buff);
			addr = InetAddress.getByName(addr_string).getAddress();
			break;
		case ADDRESS_IPV6:
			addr = new byte[16];
			in.readFully(addr);
			break;
		}
		port = in.readUnsignedShort();
		
		out.writeByte(0x5);
		out.writeByte(0x0);
		out.writeByte(0x0);
		out.writeByte(addr_type);
		switch(addr_type) {
		case ADDRESS_IPV4:
		case ADDRESS_IPV6:
			out.write(addr);
			break;
		case ADDRESS_DOMAIN:
			out.writeByte(addr_string.length());
			out.write(addr_string.getBytes());
			break;
		}
		out.writeShort(port);
	}

	public int getCommand() {
		return command;
	}

	public int getPort() {
		return port;
	}

	public int getAddresstype() {
		return addr_type;
	}

	public byte[] getAddress() {
		return addr;
	}

	public String getAddressString() {
		return addr_string;
	}

}
