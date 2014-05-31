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

public class HTTPStatus {

	public static final HTTPStatus OK = new HTTPStatus(200, "OK");
	public static final HTTPStatus BAD_REQUEST = new HTTPStatus(400, "Bad Request");
	public static final HTTPStatus FORBIDDEN = new HTTPStatus(403, "Forbidden");
	public static final HTTPStatus NOT_FOUND = new HTTPStatus(404, "Not Found");
	public static final HTTPStatus INTERNAL_SERVER_ERROR = new HTTPStatus(500, "Internal Server Error");
	public static final HTTPStatus BAD_GATEWAY = new HTTPStatus(502, "Bad Gateway");
	
	private int status_id;
	private String name;
	
	public HTTPStatus(int id, String name) {
		status_id = id;
		this.name = name;
	}
	
	public int getID() {
		return status_id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof HTTPStatus) {
			HTTPStatus s = (HTTPStatus)o;
			return status_id == s.status_id;
		}
		return false;
	}

}
