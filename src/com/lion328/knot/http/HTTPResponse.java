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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lion328.knot.util.ByteUtil;
import com.lion328.knot.util.IOUtil;

public class HTTPResponse {

	private HTTPVersion version = null;
	private HTTPStatus status = null;
	private List<HTTPHeader> headers = new ArrayList<HTTPHeader>();
	private byte[] data;
	
	public HTTPResponse(HTTPVersion version, HTTPStatus status, byte[] data) {
		this.version = version;
		this.status = status;
		this.data = data;
	}
	
	public HTTPResponse(InputStream in) throws IOException {
		String[] responseLine = IOUtil.readUntil(in, '\n').trim().split(" ");
		
		StringBuilder statusName = new StringBuilder();
		for(int i = 2; i < responseLine.length; i++) {
			statusName.append(responseLine[i]);
		}
		
		if(responseLine.length < 3) throw new IOException("Invaild HTTP response. (Invaild response line)");
		
		for(HTTPVersion version : HTTPVersion.values()) {
			if(("HTTP/" + version.getVersion()).equalsIgnoreCase(responseLine[0])) {
				this.version = version;
				break;
			}
		}
		if(version == null) throw new IOException("Invaild HTTP response. (Invaild HTTP version)");
		
		status = new HTTPStatus(Integer.parseInt(responseLine[1]), statusName.toString());
		
		boolean isDataChunked = false;
		int length = 0;
		String temp;
		StringBuilder tempBuilder = new StringBuilder();
		while(!(temp = IOUtil.readUntil(in, '\n')).equals("\r\n"))
			tempBuilder.append(temp);
		
		for(HTTPHeader header : HTTPUtil.parseHeader(tempBuilder.toString().split("\r\n"))) {
			if(header.getKey().equalsIgnoreCase("Transfer-Encoding") && header.getValue().equalsIgnoreCase("chunked")) isDataChunked = true;
			if(header.getKey().equalsIgnoreCase("Content-Length")) length = Integer.parseInt(header.getValue());
			headers.add(header);
		}
		
		if(isDataChunked) {
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			while(true) {
				length = Integer.parseInt(IOUtil.readUntil(in, '\n').trim().split(";")[0], 16);
				if(length == 0) {
					tempBuilder = new StringBuilder();
					while(!(temp = IOUtil.readUntil(in, '\n')).equals("\r\n"))
						tempBuilder.append(temp);
					
					headers.addAll(Arrays.asList(HTTPUtil.parseHeader(tempBuilder.toString().split("\r\n"))));
					
					data = byteout.toByteArray();
					break;
				}
				
				byte[] byteTemp = new byte[length];
				in.read(byteTemp);
				byteout.write(byteTemp);
			}
		} else {
			data = new byte[length];
			in.read(data);
		}
	}

	public HTTPVersion getVersion() {
		return version;
	}

	public HTTPStatus getStatus() {
		return status;
	}

	public byte[] getData() {
		return data;
	}
	
	public String getHeader(String key) {
		for(HTTPHeader header : headers)
			if(header.getKey().equalsIgnoreCase(key))
				return header.getValue();
		return null;
	}
	
	public void setHeader(String key, String value) {
		for(HTTPHeader header : headers)
			if(header.getKey().equalsIgnoreCase(key)) {
				headers.remove(header);
				headers.add(new HTTPHeader(key, value));
				return;
			}
		headers.add(new HTTPHeader(key, value));
	}
	
	public void removeHeader(String key) {
		for(HTTPHeader header : headers)
			if(header.getKey().equalsIgnoreCase(key)) {
				headers.remove(header);
				return;
			}
	}
	
	public byte[] toByteArray() {
		StringBuilder builder = new StringBuilder();
		byte[] temp;

		builder.append("HTTP/"); builder.append(version.getVersion()); builder.append(' ');
		builder.append(status.getID()); builder.append(' '); builder.append(status.getName());
		
		builder.append("\r\n");
		
		for(HTTPHeader header : headers) {
			if((header.getKey().equalsIgnoreCase("Transfer-Encoding") && header.getValue().equalsIgnoreCase("chunked")) || header.getKey().equalsIgnoreCase("Content-Length")) continue;

			builder.append(header.getKey()); builder.append(": ");
			builder.append(header.getValue());
			
			builder.append("\r\n");
		}
		
		if(data != null) if(data.length > 0) builder.append("Content-Length: "); builder.append(data.length); builder.append("\r\n\r\n");
		
		try {
			temp = builder.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			temp = builder.toString().getBytes();
		}
		
		return ByteUtil.combineByteArray(temp, data);
	}
}
