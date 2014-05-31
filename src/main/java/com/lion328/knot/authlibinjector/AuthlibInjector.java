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

package com.lion328.knot.authlibinjector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.lion328.knot.util.ByteUtil;
import com.lion328.knot.util.FileUtil;
import com.lion328.knot.util.Util;
import com.lion328.knot.util.ZipUtil;

public class AuthlibInjector {

	public static File getInjectedLibrary(File originalLib) throws IOException {
		File tempPath = new File(Util.getAppdata(), "Knot-temp"), extract = new File(tempPath, "extract");
		File injected = new File(tempPath, "authlib-" + System.nanoTime() + ".jar");
		
		//Cleanup old lib
		File[] fs = FileUtil.listAllFiles(tempPath);
		if(fs != null) for(File f : fs)
			if(!f.isDirectory() && f.getName().startsWith("authlib-"))
				f.delete();
		
		//No https
		ZipUtil.extract(originalLib, extract);
		File target = new File(extract, "com/mojang/authlib/yggdrasil/YggdrasilUserAuthentication.class".replace("/", File.separator));
		Files.write(target.toPath(), replaceHTTPS(target));
		target = new File(extract, "com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService.class".replace("/", File.separator));
		Files.write(target.toPath(), replaceHTTPS(target));
		target = new File(extract, "com/mojang/authlib/legacy/LegacyUserAuthentication.class".replace("/", File.separator));
		Files.write(target.toPath(), replaceHTTPS(target));
		ZipUtil.createJarFile(injected, extract, FileUtil.listAllFiles(extract));
		FileUtil.delete(extract);
		return injected;
	}
	
	private static byte[] replaceHTTPS(File target) throws IOException {
		byte[] data = FileUtil.readBytes(target);
		data = ByteUtil.replace(data, "https://".getBytes(), "http://".getBytes());
		int[] offsets = ByteUtil.findAllIndexOf(data, "http://".getBytes());
		for(Integer offset : offsets)
			data[offset - 1] = (byte)(data[offset - 1] - 1);
		return data;
	}
	
}
