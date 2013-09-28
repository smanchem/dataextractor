package datax;

public class RunType {
	String fileName;
	String fileLoc;
	int leg;		// leg = 1 or 2
	int delay;		// 0 = no, 1 = yes
	int medium;		// 1 = text, 2 = voice
	
	public RunType(String fileLoc, int leg) {
		this.fileLoc = fileLoc;
		this.leg = leg;
		
		if (fileLoc.contains("_ntd_")) {
			this.delay = 0;
		} else {
			this.delay = 1;
		}
		
		if (fileLoc.contains("voice")) {
			this.medium = 2;			
			this.fileName = fileLoc.substring((fileLoc.indexOf("voice")+6));
		} else {
			this.medium = 1;
			this.fileName = fileLoc.substring((fileLoc.indexOf("text")+5));
		}		
	}
}
