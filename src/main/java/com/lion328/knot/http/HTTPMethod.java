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

public enum HTTPMethod {
	GET("GET"), POST("POST"), HEAD("HEAD"), DELETE("DELETE"), PUT("PUT"), TRACE("TRACE"), OPTIONS("OPTIONS"), CONNECT("CONNECT")
	, PATCH("PATCH");
	
	private String name;
	
	HTTPMethod(String name) { // Obfuscated enum.name() fixed
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
