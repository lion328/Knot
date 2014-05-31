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
import java.net.URL;

import com.lion328.knot.serverapi.Authentication;
import com.lion328.knot.serverapi.CommKnotServerAPI;
import com.lion328.knot.serverapi.DownloadList;

public class MainController {
	
	private ILaunchGame launch;
	private ILauncherUI ui;
	private Authentication latestAuth = null;
	
	public MainController(ILaunchGame launch, ILauncherUI ui) throws IOException{
		this.launch = launch;
		this.ui = ui;
		ui.updateController(this);
		ui.updateNews(CommKnotServerAPI.getNewsContent());
		ui.updateServerStatus(CommKnotServerAPI.getServerStatus());
	}
	
	public void authentication(String username, String password){
		Authentication auth;
		String errmsg = null;
		try {
			auth = new Authentication(GlobalVariables.getAuthenticationURL(), username, password, 0x434F4D4D);
			latestAuth = auth;
		} catch (Exception e) {
			errmsg = e.getMessage();
			System.exit(1);
		}
		ui.updateAuthenticationStatus(errmsg == null, errmsg);
	}
	
	public void updateAndStartGame(){
		new Thread() {
			public void run() {
				try {
					DownloadList dllist = new DownloadList(GlobalVariables.getFileListURL(), GlobalVariables.getDownloadURL());
					dllist.delete(launch.getBaseDirectory());
					for(URL downloadLink : dllist.getFileURLsToDownload()) {
						File downloadTo = new File(downloadLink.getPath().substring(GlobalVariables.getDownloadURL().getPath().length()));
						Downloader dl = new Downloader(downloadLink, downloadTo);
						dl.addDownloadedChangeEvent(new IDownloaderEvent() {
							@Override
							public void onUpdate(Downloader dl) {
								ui.updateDownloadStatus(dl);
							}
						});
						dl.run();
					}
					
					launch.startGame();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void shutdown() {
		ui.close();
		System.exit(0);
	}
	
}
