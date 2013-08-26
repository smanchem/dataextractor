package datax;

import java.io.File;
import java.util.*;

public class ReadFiles {	
	static String temp = "";
	public static ArrayList<String> fileList = new ArrayList<String>();

	public static ArrayList<String> listFilesForFolder(File folder) {
	
		for (final File fileEntry : folder.listFiles()) {
		  if (fileEntry.isDirectory()) {
		    // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
		    listFilesForFolder(fileEntry);
		  } else {
		    if (fileEntry.isFile()) {
		      temp = fileEntry.getName();
		      if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("java")) {
			  	  fileList.add(folder.getAbsolutePath() + "\\" + fileEntry.getName());
		      }
		    }		
		  }
		}
		return fileList;
	}
}