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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lion328.knot.http.HTTPMethod;
import com.lion328.knot.http.HTTPRequest;
import com.lion328.knot.http.HTTPResponse;
import com.lion328.knot.http.HTTPStatus;
import com.lion328.knot.http.HTTPVersion;
import com.lion328.knot.proxy.IHTTPDataHandler;
import com.lion328.knot.proxy.IProxyServer;
import com.lion328.knot.proxy.ThreadDoProxy;
import com.lion328.knot.util.IFunction;
import com.lion328.knot.util.IOUtil;
import com.lion328.knot.util.Util;

public class SOCKSProxyServer implements IProxyServer {

	private ServerSocket server;
	private boolean running = false;
	private List<IHTTPDataHandler> handlers = new ArrayList<IHTTPDataHandler>();
	
	public SOCKSProxyServer() throws IOException {
		this(Util.getRandomPortServerSocket());
	}
	
	public SOCKSProxyServer(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	public SOCKSProxyServer(ServerSocket server) throws IOException {
		this.server = server;
	}
	
	@Override
	public void run() {
		if(running) return;
		running = true;
		while(running) {
			try {
				new ThreadDoProxy(this, server.accept());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addHTTPDataHandler(IHTTPDataHandler handler) {
		handlers.add(handler);
	}

	@Override
	public int getPort() {
		return server.getLocalPort();
	}

	@Override
	public void doProxy(Socket client) throws IOException {
		DataInputStream in = new DataInputStream(client.getInputStream());
		DataOutputStream out = new DataOutputStream(client.getOutputStream());
		in.mark(65535);
		int version = in.readUnsignedByte();
		in.reset();
		if(version == 4) {
			IFunction bindEvent = new IFunction() {
				@Override
				public Object[] function(Object[] data) {
					boolean finish = false;
					try {
						new Socket().bind(new InetSocketAddress(InetAddress.getByAddress((byte[])data[0]), (Integer)data[1]));
						finish = true;
					} catch (Exception e) {
						finish = false;
					}
					return new Object[]{finish};
				}
			};
			SOCKS4Message message = new SOCKS4Message(bindEvent);
			message.communicate(in, out);
			if(message.getCommand() == SOCKS4Message.COMMAND_CONNECT) {
				in.mark(65535);
				String header = IOUtil.readUntil(in, '\n');
				String[] requestLine = header.split(" ");
				if(requestLine.length == 3) {
					if(requestLine[2].startsWith("HTTP/")) {
						in.reset();
						
						HTTPRequest clientRequest = new HTTPRequest(in);

						for(IHTTPDataHandler handler : handlers) {
							if(clientRequest.getPath().matches(handler.getURLRegex())) {
								out.write(handler.getPage(clientRequest).toByteArray());
								in.close();
								out.close();
								client.close();
								return;
							}
						}
						
						URL host = new URL(clientRequest.getPath());
						Socket pipeSocket = new Socket(host.getHost(), host.getDefaultPort());
						
						InputStream pipe_in = pipeSocket.getInputStream();
						OutputStream pipe_out = pipeSocket.getOutputStream();
						
						pipe_out.write(clientRequest.toByteArray());
						
						HTTPResponse response = new HTTPResponse(pipe_in);
						out.write(response.toByteArray());
						
						in.close();
						out.close();
						pipe_in.close();
						pipe_out.close();
						pipeSocket.close();
						client.close();
						return;
					}
				} else {
					InputStream pipe_in = null;
					OutputStream pipe_out = null;
					Socket target = null;
					try {
						target = new Socket();
						pipe_in = target.getInputStream();
						pipe_out = target.getOutputStream();
						IOUtil.pipe(pipe_in, out, null);
						IOUtil.pipe(in, pipe_out, null);
					} finally {
						pipe_in.close();
						pipe_out.close();
						target.close();
						in.close();
						out.close();
						client.close();
					}
				}
			}
		} else if(version == 5) {
			
		}
	}

	@Override
	public void stop() throws IOException {
		if(!running) return;
		server.close();
	}

}
