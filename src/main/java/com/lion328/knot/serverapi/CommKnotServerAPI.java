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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import com.lion328.knot.GlobalVariables;
import com.lion328.knot.MinecraftServerStatus;
import com.lion328.knot.http.HTTPRequestPoster;
import com.lion328.knot.util.MD5Util;

public class CommKnotServerAPI {
	
	public static boolean checkMD5Checksum(URI basePath, URI file) throws NoSuchAlgorithmException, RuntimeException, IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("file=");
		sb.append(basePath.relativize(file).getPath());
		sb.append("&md5=");
		sb.append(MD5Util.getFileHashString(new File(file)));
		return Boolean.parseBoolean(HTTPRequestPoster.sendGetRequest(GlobalVariables.getAuthenticationURL().getPath(), sb.toString()));
	}
	
	public static String getNewsContent() {
		return HTTPRequestPoster.sendGetRequest(GlobalVariables.getNewsURL().getPath(), "");
	}
	
	public static MinecraftServerStatus getServerStatus() throws IOException {
		return new MinecraftServerStatus(GlobalVariables.getServerHost(), GlobalVariables.getServerPort());
	}
}
