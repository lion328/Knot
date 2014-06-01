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

package com.lion328.knot.util;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

public class Util {
	
	public static File getAppdata() {
		switch(OSUtil.getOS()) {
			default:
			case LINUX: return new File(System.getProperty("user.home"));
			case OSX: return new File(System.getProperty("user.home"), "Library/Application Support");
			case WINDOWS: return new File(System.getenv("AppData"));
		}
	}
	
	public static Byte[] byteToByte(byte[] in) {
		Byte[] out = new Byte[in.length];
		for(int i = 0; i < in.length; i++) 
			out[i] = in[i];
		return out;
	}
	
	public static byte[] byteToByte(Byte[] in) {
		byte[] nope = new byte[in.length];
		for(int i = 0; i < in.length; i++)
			nope[i] = in[i];
		return nope;
	}
	
	public static int[] IntegerToInt(Integer[] in) {
		int[] nope = new int[in.length];
		for(int i = 0; i < in.length; i++)
			nope[i] = in[i];
		return nope;
	}
	
	public static ServerSocket getRandomPortServerSocket() throws IOException {
		for(int port = 900; port < Short.MAX_VALUE; port++) {
			try {
				return new ServerSocket(port);
			} catch(IOException e) { continue; }
		}
		throw new IOException("No free port found.");
	}
	
	public static String getDomainName(URL url) {
		String out = url.getProtocol() + "://" + url.getAuthority();
		return out;
	}
	
	public static String getHost(URL url) {
	    return url.getHost() + ":" + (url.getProtocol().equals("http") ? 80 : url.getPort());
	}
	
	public static URL safeURL(String url) {
		try {
			return new URL(url);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
