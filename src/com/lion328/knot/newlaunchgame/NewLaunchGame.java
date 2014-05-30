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

package com.lion328.knot.newlaunchgame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lion328.knot.ILaunchGame;
import com.lion328.knot.authlibinjector.AuthlibInjector;
import com.lion328.knot.serverapi.Authentication;
import com.lion328.knot.util.FileUtil;
import com.lion328.knot.util.ZipUtil;

public class NewLaunchGame implements ILaunchGame{
	
	private File baseDir, json, librariesDir;
	private GameVersion gameVersion;
	private Map<String,String> gameArguments = new HashMap<String,String>();
	private String maxRam;
	
	public NewLaunchGame(String maxRam, File baseDir, String version) throws JsonSyntaxException, IOException {
		this.baseDir = baseDir;
		StringBuilder sb = new StringBuilder();
		sb.append("versions");
		sb.append(File.separator);
		sb.append(version);
		sb.append(File.separator);
		sb.append(version);
		sb.append(".json");
		this.json = new File(baseDir,sb.toString());
		this.librariesDir = new File(baseDir, "libraries");
		this.maxRam = maxRam;
		gameVersion = new Gson().fromJson(FileUtil.readFile(json), GameVersion.class);
		setGameArgument("version_name", version);
		setGameArgument("game_directory", baseDir.getAbsolutePath());
		setGameArgument("user_properties", "{}");
		setGameArgument("user_type", gameVersion.mainClass.equals("net.minecraft.launchwrapper.Launch") ? "legacy" : "mojang");
		setGameArgument("assets_root", baseDir.getAbsolutePath() + File.separator + "assets" + File.separator + (gameVersion.mainClass.equals("net.minecraft.launchwrapper.Launch") ? "virtual" + File.separator + "legacy" : ""));
		if(gameVersion.assets != null) setGameArgument("assets_index_name",gameVersion.assets);
	}

	public void setGameArgument(String key, String value) {
		gameArguments.put("${" + key + "}", value);
	}
	
	public void deleteGameArgument(String key) {
		if(gameArguments.containsKey("${" + key + "}")) gameArguments.remove("${" + key + "}");
	}
	
	public void startGame() throws IOException {
		File nativesDir = new File(json.getParentFile(), gameVersion.id + "-natives-knot");
		FileUtil.delete(nativesDir);
		List<String> params = new ArrayList<String>();
		params.add("java");
		params.add("-Xmx" + maxRam);
		params.add("-Djava.library.path=" + nativesDir.getAbsolutePath());
		params.add("-cp");
		
		StringBuilder cpTemp = new StringBuilder();
		cpTemp.append(new File(json.getParentFile(), gameVersion.id + ".jar").getAbsolutePath());
		
		for(GameLibrary gl : gameVersion.libraries){
			if(gl.isAllowedOS()){
				if(gl.isNative()){
					if(nativesDir.exists()) nativesDir.delete();
					ZipUtil.extract(gl.getFile(librariesDir), nativesDir);
					for(String exclude : gl.extract.exclude){
						FileUtil.delete(new File(nativesDir, exclude));
					}
				} else {
					File injectedAuthlib = null;
					if(gl.name.startsWith("com.mojang:authlib:"))
						injectedAuthlib = AuthlibInjector.getInjectedLibrary(gl.getFile(librariesDir));
					cpTemp.append(File.pathSeparator);
					cpTemp.append((injectedAuthlib == null ? gl.getFile(librariesDir) : injectedAuthlib).getAbsolutePath());
				}
			}
		}
		
		params.add(cpTemp.toString());
		params.add(gameVersion.mainClass);
		for(String gameArg : gameVersion.minecraftArguments.split(" ")){
			if(gameArg.startsWith("${"))
				if(gameArguments.containsKey(gameArg)) gameArg = gameArguments.get(gameArg);
			params.add(gameArg);
		}
		
		ProcessBuilder pb = new ProcessBuilder(params);
		pb.directory(baseDir);
		pb.start();
	}

	@Override
	public File getBaseDirectory() {
		return baseDir;
	}

	@Override
	public void setAuth(Authentication auth) {
		setUsername(auth.getUsername());
		setSessionID(auth.getSessionID());
		setUUID(auth.getUUID());
	}
	
	public void setUsername(String username) {
		setGameArgument("auth_player_name", username);
	}
	
	public void setUUID(String uuid) {
		setGameArgument("auth_uuid", uuid);
	}
	
	public void setSessionID(String sessionid) {
		setGameArgument("auth_session", sessionid);
		setGameArgument("auth_access_token", sessionid);
	}

}
