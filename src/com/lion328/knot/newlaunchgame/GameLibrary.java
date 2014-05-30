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

public class GameLibrary {

	public String name;
	public GameLibraryRule[] rules;
	public GameNative natives;
	public GameExtractRule extract;
	
	private String[] cacheName;
	
	public boolean isNative(){
		return natives != null;
	}
	
	private void setCacheName(){
		if(cacheName == null) cacheName = name.split(":");
	}
	
	public File getFile(File librariesDir){
		setCacheName();
		return new File(librariesDir, cacheName[0].replace(".", File.separator) + "/" + cacheName[1] + "/" + cacheName[2] + "/" + cacheName[1] + "-" + cacheName[2] + (isNative() ? "-" + natives.getNative().replace("${arch}", System.getProperty("sun.arch.data.model")) : "") + ".jar");
	}
	
	public boolean isAllowedOS(){
		if(rules == null) return true;
		boolean allowedAll = false, disallowedAll = false;
		for(GameLibraryRule rule : rules){
			if(rule.os != null){
				if(rule.os.isMatch()){
					if(rule.action.equals(GameLibraryRule.ALLOW)) return true;
					else return false;
				}
			}
			if(rule.isMatchAllOS()){
				if(rule.action.equals(GameLibraryRule.ALLOW)){
					allowedAll = true;
					disallowedAll = false;
				}
				else{
					disallowedAll = true;
					allowedAll = false;
				}
			}
		}
		return allowedAll && !disallowedAll;
	}
}
