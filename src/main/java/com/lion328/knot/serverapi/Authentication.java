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

package com.lion328.knot.serverapi;

import java.net.URL;

import com.lion328.knot.http.HTTPRequestPoster;

public class Authentication {
	
	private URL apiURL;
	private String username, password;
	private int version;
	private String[] result;
	
	public Authentication(String username, String password) throws Exception {
		this(new URL("http://login.minecraft.net/"), username, password, 13);
	}
	
	public Authentication(URL apiURL, String username, String password, int version) throws Exception{
		this.username = username;
		this.password = password;
		this.apiURL = apiURL;
		this.version = version;
		reLogin();
	}
	
	public void reLogin(String username, String password) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("user=");
		sb.append(username);
		sb.append("&password=");
		sb.append(password);
		sb.append("&version=");
		sb.append(Integer.toString(version));
		String result_ = HTTPRequestPoster.sendGetRequest(apiURL.toString(), sb.toString());
		if(!result_.contains("deprecated")) throw new Exception("Authentication error: " + result_);
		result = result_.split(":");
	}
	
	public void reLogin() throws Exception {
		reLogin(username, password);
	}
	
	public long getVersionPOSIX() {
		return Long.parseLong(result[0]);
	}
	
	public String getUsername() {
		return result[2];
	}
	
	public String getSessionID() {
		return result[3];
	}
	
	public String getUUID() {
		return result[4];
	}
}
