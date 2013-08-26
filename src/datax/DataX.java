package datax;

import java.io.File;
import java.util.*;

public class DataX {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder = new File(".");
		ArrayList<String> fileList = ReadFiles.listFilesForFolder(folder);
		for (String fileLoc : fileList)
			System.out.println(fileLoc);
	}

}
