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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.lion328.knot.util.FileUtil;

public class DownloadList {
	
	private List<URL> files = new ArrayList<URL>();
	private List<String> deleteFiles = new ArrayList<String>(), deleteFolders = new ArrayList<String>();
	
	public DownloadList(URL listURL, URL downloadURL) throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(listURL.openStream()));
		String temp;
		while((temp = bf.readLine()) != null) {
			String[] data = temp.split("|");
			if(data[0].equalsIgnoreCase("add")) files.add(new URL(downloadURL, data[2]));
			else if(data[0].equalsIgnoreCase("delete")){
				if(data[1].equalsIgnoreCase("file")) deleteFiles.add(data[2]);
				else if(data[1].equalsIgnoreCase("folder")) deleteFolders.add(data[2]);
			}
		}
		bf.close();
	}
	
	public URL[] getFileURLsToDownload() {
		return files.toArray(new URL[files.size()]);
	}
	
	public String[] getDeleteFiles() {
		return deleteFiles.toArray(new String[deleteFiles.size()]);
	}
	
	public String[] getDeleteFolders() {
		return deleteFolders.toArray(new String[deleteFolders.size()]);
	}
	
	public void delete(File baseDir) {
		File f;
		for(String s : deleteFiles) {
			f = new File(baseDir, s);
			if(f.exists()) f.delete();
		}
		for(String s : deleteFolders) {
			f = new File(baseDir, s);
			if(f.exists())
				for(File file : FileUtil.listAllFiles(f)) if(file.exists()) file.delete();
		}
	}
}
