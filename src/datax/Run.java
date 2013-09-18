package datax;

public class Run {
	int medium; 						// 1 = text, 2 = voice
	String fse1, fse2, p1, p2;	// the 4 different files(location) in each run
	
	public Run(int medium, String fse1, String fse2, String p1, String p2) {
		this.medium = medium;
		this.fse1 = fse1;
		this.fse2 = fse2;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public int getMedium() {
		return medium;
	}
	public String getfse1() {
		return fse1;
	}
	public String getfse2() {
		return fse2;
	}
	public String getp1() {
		return p1;
	}
	public String getp2() {
		return p2;
	}
}
