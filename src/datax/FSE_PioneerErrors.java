package datax;

public class FSE_PioneerErrors {
	String pioneerError;
	String pioneerErrorType;	// S1, S2, D1 or D2
	int leg; 					// 1 or 2
	int timeBegin;
	int timeEnd;
	int timeInRed;
	int prevTime;
	int CO2LogsMissed;
	int CO2LogsOccurred;
	int connChecksMissed;
	int connChecksOccurred;
	double CO2LogsMissedPercentage;
	double connChecksMissedPercentage;
	
	public static ErrorStats[] pioneerErrors = new ErrorStats[4];	
	public static ErrorStats[] fseErrors = new ErrorStats[12];
	
	public FSE_PioneerErrors() {
		timeInRed = 0;
	}
}
