package com.keeptio.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

	public static File getNonExistantFile(String dirPath, String fileName, String ext) {
		File f = new File(dirPath + File.separator + fileName + ext);
		if (f.exists()) {
			int counter = 2;
			while (f.exists()) {
				String incFileName = String.format("%s_%d%s", fileName, counter, ext);
				f = new File(dirPath + File.separator + incFileName);
				counter++;
			}
		}
		return f;
	}
	
	public static byte[] readFromFile(String path) throws IOException {
		FileInputStream fis = new FileInputStream(new File(path));
		byte[] data = fis.readAllBytes();
		fis.close();
		return data;
	}

	// TODO: Add throws IOException and handle in callers
	public static void writeToFile(String path, byte[] data) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
