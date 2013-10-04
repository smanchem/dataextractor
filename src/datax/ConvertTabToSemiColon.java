package datax;

import java.io.*;

public class ConvertTabToSemiColon {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File oldFile = new File(".\\src\\p_march2_pm1_ntd_t.txt");
		if(!oldFile.exists()) {
			System.out.println("File: " + oldFile.getName() + ", not found!");
			System.exit(1);
		}
		
		File newFile = new File(".\\src\\correct_p_march2_pm1_ntd_t.txt");
		if(newFile.exists())
			newFile.delete();
		else {
			try {
				newFile.createNewFile();
			} catch (IOException ioe) {
				System.err.println(ioe);
				System.exit(1);
			}
		}
		
		BufferedReader readFile = new BufferedReader(new FileReader(oldFile));
		BufferedWriter writeFile = new BufferedWriter(new FileWriter(newFile));
		
		String line;
		
		while ((line = readFile.readLine()) != null) {
			String[] eachLine = null;
			eachLine = line.split("\t");
			String newLine = "";
			for (int i = 0; i < eachLine.length; i++){
				newLine = newLine + eachLine[i] + ";";
			}
			writeFile.write(newLine);
			writeFile.newLine();
		}
		
		readFile.close();
		writeFile.close();
		return;
	}

}
