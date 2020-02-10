package com.lacunasoftware.signer.sample;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;


public class Util {
	private static Util instance;

	public static Util getInstance() {
		if (instance == null) {
			instance = new Util();
		}
		return instance;
	}

	private Util() {
	}

	public byte[] getResourceFile(String location) throws IOException {
		byte[] content;

		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(String.format("static/%s", location));
		if (resource == null) {
			throw new RuntimeException(String.format("The file \"%s\" was not found", location));
		}

		File file = new File(resource.getFile());
		return Files.readAllBytes(file.toPath());
	}
}
