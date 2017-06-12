package image.explorer.model;

import java.io.InputStream;

public class FileRetreivalTester {
	public static void main(String[] args) {
		ConnectionDriver cd = new ConnectionDriver();
		InputStream stream = cd.downloadFile(9, ".JPG", true);
		System.out.println();
	}

}
