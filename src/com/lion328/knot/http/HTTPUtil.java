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

package com.lion328.knot.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HTTPUtil {
	
	public static HTTPHeader[] parseHeader(String[] lines) throws IOException {
		List<HTTPHeader> out = new ArrayList<HTTPHeader>();
		for(String line : lines) {
			line = line.trim();
			if(!line.contains(":")) throw new IOException("Invaild HTTP request. (Invaild HTTP header)");
			
			String key = line.substring(0, line.indexOf(':'));
			String value = line.substring(line.indexOf(':') + 2);
			out.add(new HTTPHeader(key, value));
		}
		return out.toArray(new HTTPHeader[out.size()]);
	}
	
	public static byte[] send(URL target, boolean isPost, byte[] data, String... param) {
		String path = target.getPath();
		if(path.length() == 0) path = "/";
		HTTPRequest request = new HTTPRequest(HTTPVersion.V1_1, isPost ? HTTPMethod.POST : HTTPMethod.GET, 
							  target, data, param);
		try {
			Socket sock = new Socket(target.getHost(), target.getDefaultPort());
			InputStream in = sock.getInputStream();
			OutputStream out = sock.getOutputStream();
			out.write(request.toByteArray());
			HTTPResponse response = new HTTPResponse(in);
			in.close();
			out.close();
			sock.close();
			return response.getData();
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String sendString(URL target, boolean isPost, byte[] data, String... param) {
		byte[] out = send(target, isPost, data, param);
		try {
			return new String(out, "UTF-8");
		} catch(Exception e) {
			return new String(out);
		}
	}
}
