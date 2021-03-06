package datax;

public class ErrorInfo {
	String errorOccurred;
	int timeOccurred, timeToCorrectResponse, repCount, repComplete;
	int[] timeToRespond;
	String[] repairOrdered;
	boolean repairAttempted;
	
	public ErrorInfo() {
		errorOccurred = null;
		timeOccurred = 0;
		timeToRespond = null;
		timeToCorrectResponse = 99999;
		repCount = 0;
		repairOrdered = null;
		repComplete = 99999;
		repairAttempted = false;
	}
	
	public ErrorInfo(int time, String error) {
		errorOccurred = error;
		timeOccurred = time;
		timeToRespond = new int[30];
		timeToCorrectResponse = 99999;
		repCount = 0;
		repairOrdered = new String[30];
		repComplete = 99999;
		repairAttempted = false;
	}
}
