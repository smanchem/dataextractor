package datax;

public class ErrorStats {
	String Error;
	String fileName;
	int leg, delay, medium;
	int correctRepair;		// 0 = no, 1 = yes
	int timeOccurred;
	int timeInRed;
	int incorrectRepairs;
	int o2TankClicks;
	int o2FlowMeter;
	int n2TankClicks;
	int n2FlowMeter;
	int mixerFlowMeter;
	int co2HistoryGraph;
	int o2HistoryGraph;
	int n2HistoryGraph;
	int tempHistoryGraph;
	int humidityHistoryGraph;
	int flowRates;
	int co2LogsMissed;
	int co2LogsOccurred;
	double co2LogsMissedPercentage;
	int connChecksMissed;
	int connChecksOccurred;
	double connChecksMissedPercentage;
	
	public ErrorStats(int time, String error) {
		Error = error;
		fileName = null;
		correctRepair = 0;
		timeOccurred = time;
		timeInRed = 0;
		incorrectRepairs = 0;
		o2TankClicks = 0;
		o2FlowMeter = 0;
		n2TankClicks = 0;
		n2FlowMeter = 0;
		mixerFlowMeter = 0;
		co2HistoryGraph = 0;
		o2HistoryGraph = 0;
		n2HistoryGraph = 0;
		tempHistoryGraph = 0;
		humidityHistoryGraph = 0;
		flowRates = 0;
		co2LogsMissed = 0;
		co2LogsOccurred = 0;
		co2LogsMissedPercentage = 0.0;
		connChecksMissed = 0;
		connChecksOccurred = 0;
		connChecksMissedPercentage = 0.0;
	}
	
	public ErrorStats() {
		Error = null;
		fileName = null;
		correctRepair = 0;
		timeOccurred = 0;
		timeInRed = 0;
		incorrectRepairs = 0;
		o2TankClicks = 0;
		o2FlowMeter = 0;
		n2TankClicks = 0;
		n2FlowMeter = 0;
		mixerFlowMeter = 0;
		co2HistoryGraph = 0;
		o2HistoryGraph = 0;
		n2HistoryGraph = 0;
		tempHistoryGraph = 0;
		humidityHistoryGraph = 0;
		flowRates = 0;
		co2LogsMissed = 0;
		co2LogsOccurred = 0;
		co2LogsMissedPercentage = 0.0;
		connChecksMissed = 0;
		connChecksOccurred = 0;
		connChecksMissedPercentage = 0.0;
	}
}

