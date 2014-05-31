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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteUtil {
	
	public static byte[] replace(byte[] src, byte[] from, byte[] to) {
		int[] offsets = findAllIndexOf(src,from);
		List<Byte> out = new ArrayList<Byte>();
		MAIN: for(int i = 0; i < src.length; i++) { 
			for(Integer offset : offsets) {
				if(i == offset) {
					if(i > offset) continue;
					out.addAll(Arrays.asList(Util.byteToByte(to)));
					i += from.length - 1;
					continue MAIN;
				}
			}
			out.add(src[i]);
		}
		return Util.byteToByte(out.toArray(new Byte[out.size()]));
	}
	
	public static int[] findAllIndexOf(byte[] src, byte[] find) {
		List<Integer> arr = new ArrayList<Integer>();
		for(int i = 0; i < src.length - find.length + 1; i++) {
			byte[] temp = Arrays.copyOfRange(src, i, i + find.length);
			if(Arrays.equals(temp, find)) arr.add(i);
		}
		return Util.IntegerToInt(arr.toArray(new Integer[arr.size()]));
	}
	
	public static byte[] combineByteArray(byte[] one, byte[] two) {
		byte[] out = new byte[one.length + two.length];
		System.arraycopy(one, 0, out, 0, one.length);
		System.arraycopy(two, 0, out, one.length, two.length);
		return out;
	}
}
