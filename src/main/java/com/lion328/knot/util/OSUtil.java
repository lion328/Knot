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

package com.lion328.knot.util;

public class OSUtil {
	
	public enum OS {LINUX,WINDOWS,OSX,UNKNOWN};
	
	public static OS getOS(){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("nix") || os.contains("nux") || os.contains("aix")) return OS.LINUX;
		if(os.contains("win")) return OS.WINDOWS;
		if(os.contains("mac")) return OS.OSX;
		return OS.UNKNOWN;
	}
}
