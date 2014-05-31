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

package com.lion328.knot.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.lion328.knot.http.HTTPMethod;
import com.lion328.knot.http.HTTPRequest;
import com.lion328.knot.http.HTTPResponse;
import com.lion328.knot.http.HTTPStatus;
import com.lion328.knot.http.HTTPVersion;
import com.lion328.knot.util.IOUtil;
import com.lion328.knot.util.Util;

public class HTTPProxyServer implements IProxyServer {

	private ServerSocket server;
	private boolean running;
	private List<IHTTPDataHandler> handlers = new ArrayList<IHTTPDataHandler>();
	
	public HTTPProxyServer() throws IOException {
		this(Util.getRandomPortServerSocket());
	}
	
	public HTTPProxyServer(int port) throws IOException {
		this(new ServerSocket(port));
	}
	
	public HTTPProxyServer(ServerSocket server) throws IOException {
		this.server = server;
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
		InputStream in = client.getInputStream();
		OutputStream out = client.getOutputStream();
		
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
		
		if(clientRequest.getMethod() == HTTPMethod.CONNECT) {
			Socket pipeSocket = null;
			InputStream pipe_in = null;
			OutputStream pipe_out = null;
			try {
				HTTPStatus s = HTTPStatus.OK;
				
				String[] addr = clientRequest.getPath().split(":");
				if(addr.length == 2) {
					try {
						pipeSocket = new Socket(addr[0], Integer.parseInt(addr[1]));
						
						pipe_in = pipeSocket.getInputStream();
						pipe_out = pipeSocket.getOutputStream();
					} catch(Exception e) {
						s = HTTPStatus.NOT_FOUND;
					}
				} else s = HTTPStatus.NOT_FOUND;
				
				HTTPResponse r = new HTTPResponse(HTTPVersion.V1_1, s, new byte[]{});
				out.write(r.toByteArray());
				
				if(!s.equals(HTTPStatus.NOT_FOUND)) {
					IOUtil.pipe(in, pipe_out, null);
					IOUtil.pipe(pipe_in, out, null);
				}
			} finally {
				if(pipeSocket != null) {
					pipeSocket.close();
					pipe_in.close();
					pipe_out.close();
					return;
				}
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
	}

	@Override
	public void stop() throws IOException {
		if(running) server.close();
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
}
