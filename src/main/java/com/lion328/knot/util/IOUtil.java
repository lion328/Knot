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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

	public static String readUntil(InputStream in, char until) throws IOException {
		StringBuilder sb = new StringBuilder();
		char temp;
		for(;;) {
			temp = (char)in.read();
			sb.append(temp);
			if(temp == until) break;
		}
		return sb.toString();
	}
	
	public static void pipe(final InputStream in, final OutputStream out, final IPipeFinishEvent finish) throws IOException {
		new Thread("CommKnot-Pipe") {
			@Override
			public void run() {
				try {
					int data;
					while((data = in.read()) != -1) out.write(data);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(finish != null)
						finish.onFinish(in,out);
				}
			}
		}.start();
	}
	
	public interface IPipeFinishEvent {
		public void onFinish(InputStream in, OutputStream out);
	}
}
