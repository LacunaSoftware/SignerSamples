package com.lacunasoftware.signer.client;


import java.util.Arrays;


class Util {
	static String repeatChar(char c, int count) {
		char[] array = new char[count];
		Arrays.fill(array, c);
		return new String(array);
	}
}
