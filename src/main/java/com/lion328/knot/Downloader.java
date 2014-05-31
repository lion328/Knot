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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Downloader implements Runnable {

	private final Downloader self = this;
	
	private URL url;
	private File saveto;
	
	private int downloadedBytes = 0;
	private int totalBytes = 0;
	private boolean downloading = false;
	
	private FileOutputStream fout;
	
	private List<IDownloaderEvent> events = new ArrayList<IDownloaderEvent>();
	
	public Downloader(URL url, File saveto) throws FileNotFoundException{
		this.url = url;
		this.saveto = saveto;
		fout = new FileOutputStream(saveto);
	}
	
	public void run() {
		if(downloading) return;
		downloading = true;
        BufferedInputStream in = null;
        try
        {
           	HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
           	urlc.setRequestMethod("GET");
           	totalBytes = urlc.getContentLength();
           	in = new BufferedInputStream(urlc.getInputStream());
            int data;
            while ((data = in.read()) != -1)
            {
                fout.write(data);
                downloadedBytes++;
                if(downloadedBytes % 1024 == 0) new Thread() {
                	public void run() {
                		for(IDownloaderEvent e : events) e.onUpdate(self);
                	}
                }.start();
            }
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        finally
        {
        	try {
        		if (in != null)
        			in.close();
        		if (fout != null)
        			fout.close();
        	} catch(IOException e) {
        		e.printStackTrace();
        	}
        }
        downloading = false;
	}
	
	public int getDownloadedBytes() {
		return downloadedBytes;
	}
	
	public int getContentLength() {
		return totalBytes;
	}
	
	public int getPercent() {
		return (int)(Math.floor(downloadedBytes / totalBytes) * 100);
	}
	
	public File getFile() {
		return saveto;
	}
	
	public boolean isDownloading() {
		return downloading;
	}
	
	public void addDownloadedChangeEvent(IDownloaderEvent de) {
		events.add(de);
	}
}
