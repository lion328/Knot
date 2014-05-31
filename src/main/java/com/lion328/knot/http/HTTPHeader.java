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

package com.lion328.knot.http;

public class HTTPHeader implements Cloneable {

	private String key, value;
	
	public HTTPHeader(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public HTTPHeader clone() {
		return new HTTPHeader(key, value);
	}

	@Override
	public boolean equals(Object header) {
		if(header instanceof HTTPHeader) {
			HTTPHeader header_t = (HTTPHeader)header;
			return header_t.key.equalsIgnoreCase(key) && header_t.key.equals(value);
		}
		return false;
	}
	
}
