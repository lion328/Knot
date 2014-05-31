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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lion328.knot.serverapi.Authentication;

@Deprecated
public class OldLaunchGame implements ILaunchGame{
	
	private File baseDir, bin;
	private String username, maxRam, sessionid;

	public OldLaunchGame(File baseDir, Authentication auth, String maxRam) {
		this(baseDir,auth.getUsername(),auth.getSessionID(),maxRam);
	}
	
	public OldLaunchGame(File baseDir, String username, String sessionid, String maxRam){
		this.baseDir = baseDir;
		this.bin = new File(baseDir, "bin");
		this.username = username;
		this.maxRam = maxRam;
		this.sessionid = sessionid;
	}

	public void startGame() throws IOException{
		List<String> params = new ArrayList<String>();
		params.add("java");
		params.add("-cp");
		params.add("minecraft.jar;lwjgl.jar;lwjgl_util.jar;jinput.jar");
		params.add("-Xmx" + maxRam);
		params.add("-Djava.library.path=natives");
		params.add("net.minecraft.client.Minecraft");
		params.add(username);
		params.add(sessionid);
		ProcessBuilder pb = new ProcessBuilder(params);
		pb.directory(bin);
		pb.start();
	}

	@Override
	public File getBaseDirectory() {
		return baseDir;
	}

	@Override
	public void setAuth(Authentication auth) {
		username = auth.getUsername();
		sessionid = auth.getSessionID();
	}
	
}