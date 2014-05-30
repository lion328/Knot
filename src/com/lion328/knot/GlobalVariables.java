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

import java.net.MalformedURLException;
import java.net.URL;

import com.lion328.knot.http.HTTPRequestPoster;
import com.lion328.knot.util.Util;

public class GlobalVariables {
	
	private static URL AUTHENTICATION_URL, FILELIST_URL, DOWNLOAD_URL, MD5CHECKSUM_URL, NEWS_URL;
	private static String SERVER_HOST, LAUNCHER_TITLE, BASEPATH;
	private static int SERVER_PORT;

	private static void init() {
		try {
			String[] info = HTTPRequestPoster.sendGetRequest("http://127.0.0.1/knot/info.txt", "").split("|");
			AUTHENTICATION_URL = new URL(info[0]);
			FILELIST_URL = new URL(info[1]);
			DOWNLOAD_URL = new URL(info[2]);
			MD5CHECKSUM_URL = new URL(info[3]);
			NEWS_URL = new URL(info[4]);
			SERVER_HOST = info[5];
			SERVER_PORT = Integer.parseInt(info[6]);
			LAUNCHER_TITLE = info[7];
			BASEPATH = info[8].replace("{APPDATA}", Util.getAppdata().getAbsolutePath());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static URL getAuthenticationURL() {
		return AUTHENTICATION_URL;
	}
	
	public static URL getFileListURL() {
		return FILELIST_URL;
	}

	public static URL getDownloadURL() {
		return DOWNLOAD_URL;
	}

	public static URL getMD5ChecksumURL() {
		return MD5CHECKSUM_URL;
	}

	public static URL getNewsURL() {
		return NEWS_URL;
	}
	
	public static String getServerHost() {
		return SERVER_HOST;
	}

	public static int getServerPort() {
		return SERVER_PORT;
	}
	
	public static String getLauncherTitle() {
		return LAUNCHER_TITLE;
	}

	public static String getBasepath() {
		return BASEPATH;
	}

	static {
		init();
	}
}
