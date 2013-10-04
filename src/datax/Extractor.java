package datax;

import java.io.*;
//import java.util.*;

public class Extractor {
	public static FSE_PioneerErrors[] fsePioneerError;
	public static void extract(Run run, File fsedata, File pdata) throws IOException {
		RunType runT1 = new RunType(run.fse1, 1);
		RunType runT2 = new RunType(run.fse2, 2);
		RunType runT3 = new RunType(run.p1, 1);
		RunType runT4 = new RunType(run.p2, 2);
		
		RunStatsInRed[] runStatsRed = new RunStatsInRed[4];
		for (int i = 0; i < 4; i++) {
			runStatsRed[i] = new RunStatsInRed();
		}
		
// Call the Data Extractor Function
		
			try {
				runStatsRed[2] = extractPInfo(runT3, pdata);
				//System.out.println("time in red is " + runStatsRed[2].totalTimeInRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			try {
				runStatsRed[3] = extractPInfo(runT4, pdata);
				//System.out.println("time in red is " + runStatsRed[3].totalTimeInRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			
			BufferedWriter feedbackWriter2 = new BufferedWriter(new FileWriter(pdata, true));
			for (int i = 0; i < 4; i++) {
				feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].fileName + "," + FSE_PioneerErrors.pioneerErrors[i].Error +"," + FSE_PioneerErrors.pioneerErrors[i].leg + ","+ FSE_PioneerErrors.pioneerErrors[i].delay + "," + FSE_PioneerErrors.pioneerErrors[i].medium + ",");
				feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].correctRepair +","+ FSE_PioneerErrors.pioneerErrors[i].timeInRed +","+ FSE_PioneerErrors.pioneerErrors[i].incorrectRepairs +",");
				feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].o2TankClicks +","+ FSE_PioneerErrors.pioneerErrors[i].o2FlowMeter +","+ FSE_PioneerErrors.pioneerErrors[i].n2TankClicks +","+ FSE_PioneerErrors.pioneerErrors[i].n2FlowMeter +","+ FSE_PioneerErrors.pioneerErrors[i].mixerFlowMeter +",");
				feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].co2HistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].o2HistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].n2HistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].tempHistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].humidityHistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].flowRates +",");
				feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].co2LogsMissed + "," + FSE_PioneerErrors.pioneerErrors[i].co2LogsMissedPercentage + "," + FSE_PioneerErrors.pioneerErrors[i].connChecksMissed + "," +  FSE_PioneerErrors.pioneerErrors[i].connChecksMissedPercentage + ",");
			}			
			
			// if (fsePioneerError[0] != null) System.out.println("Pioneer Errors = " + fsePioneerError[0].timeBegin);
			
			try {
				runStatsRed[0] = extractFSEInfo(runT1, fsedata);
				//System.out.println("time in red is " + runStatsRed[0].totalTimeInRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			try {
				runStatsRed[1] = extractFSEInfo(runT2, fsedata);
				//System.out.println("time in red is " + runStatsRed[1].totalTimeInRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			
			
			BufferedWriter feedbackWriter = new BufferedWriter(new FileWriter(fsedata, true));
			for (int i = 0; i<2; i++)
				feedbackWriter.write(runStatsRed[i].totalTimeInRed +","+ runStatsRed[1].co2LogsMissed +","+ runStatsRed[i].co2LogsMissedPercentage +","+ runStatsRed[i].connChecksMissed +","+ runStatsRed[i].connChecksMissedPercentage + ",");
			
			for (int i = 2; i<4; i++)
				feedbackWriter2.write(runStatsRed[i].totalTimeInRed +","+ runStatsRed[1].co2LogsMissed +","+ runStatsRed[i].co2LogsMissedPercentage +","+ runStatsRed[i].connChecksMissed +","+ runStatsRed[i].connChecksMissedPercentage + ",");
			feedbackWriter.close();
			feedbackWriter2.close();
	}		
	
	private static RunStatsInRed extractFSEInfo(RunType runT, File fsedata) throws IOException{
		System.out.println(runT.fileName);
		// Read the inFile line by line and print everything to console and outFile.
		BufferedReader reader = new BufferedReader(new FileReader(runT.fileLoc));
		BufferedWriter feedbackWriter = new BufferedWriter(new FileWriter(fsedata, true));
		RunStatsInRed runStatsRed = new RunStatsInRed();
		ErrorStats[] errorStatsList = new ErrorStats[15];
		
		String line = null;
		String[] eachLine = null;		
		SystemState state;
		int iconApp = 0, iconClose = 0, iconConf = 0, numMissed = 0;
		iconApp = iconClose;
		int errBegin = 0, repBegin = 9999, repComplete = 0;
		boolean wrongRep = false;
		int wrongRepCount = 0;
		int reactionTime = 0;
		wrongRepCount = reactionTime;
		int correctResponseTime = 0;
		int logMissedCount = 0, logCount = 0, connectionCheckCount = 0, connCheckTime = 0;
		String[] wrongRepair = new String[30];
		String repair = null;
		int errorCount = 0;
		boolean errorOnGoing = false;
		ErrorInfo[] errInf = new ErrorInfo[10];
		
		/*
		int pErr1Begin, pErr1End, pErr2Begin, pErr2End;
		
		if (fsePioneerError[0] != null && fsePioneerError[0].leg == runT.leg) {
			pErr1Begin = fsePioneerError[0].timeBegin;
			pErr1End = fsePioneerError[0].timeEnd;
			if (fsePioneerError[1] != null && fsePioneerError[1].leg == runT.leg) {
				pErr2Begin = fsePioneerError[1].timeBegin;
				pErr2End = fsePioneerError[1].timeEnd;
			} else if (fsePioneerError[2] != null && fsePioneerError[2].leg == runT.leg) {
				pErr2Begin = fsePioneerError[2].timeBegin;
				pErr2End = fsePioneerError[2].timeEnd;
			} else {
				pErr2Begin = fsePioneerError[3].timeBegin;
				pErr2End = fsePioneerError[3].timeEnd;
			}
		} else if (fsePioneerError[1] != null && fsePioneerError[1].leg == runT.leg) {
			pErr1Begin = fsePioneerError[1].timeBegin;
			pErr1End = fsePioneerError[1].timeEnd;
			if (fsePioneerError[2] != null && fsePioneerError[2].leg == runT.leg) {
				pErr2Begin = fsePioneerError[2].timeBegin;
				pErr2End = fsePioneerError[2].timeEnd;
			} else {
				pErr2Begin = fsePioneerError[3].timeBegin;
				pErr2End = fsePioneerError[3].timeEnd;
			}
		} else {
			pErr1Begin = fsePioneerError[2].timeBegin;
			pErr1End = fsePioneerError[2].timeEnd;
			pErr2Begin = fsePioneerError[3].timeBegin;
			pErr2End = fsePioneerError[3].timeEnd;
		}
		
		System.out.println("Pioneer Error Times: " + pErr1Begin +" "+ pErr1End +" "+ pErr2Begin +" "+ pErr2End);
		*/
		// Ignore first line of the log file
		line = reader.readLine();
		
		//clicksWriter.write("Time \t\t Event \t\t Effect");
		//clicksWriter.newLine();	
		
		// Read every line of the log file and tokenize it. Then extract the relevant values.			
		while((line = reader.readLine()) != null) {
			eachLine = line.split(";");
			if (eachLine.length == 14 ) {
				state = new SystemState(eachLine[0],eachLine[9],eachLine[10],eachLine[11], null, eachLine[13]);
				
				if (state.user.equals("OPERATOR")) {
					//clicksWriter.write(""+ state.time + "\t"+ state.component + "\t\t" + state.effect);
					//clicksWriter.newLine();					
				}
				
				// Check if any repair is going on and if it's incorrect repair. Record incorrect repair order.
				if (!wrongRep && (repBegin < 9999) && (repBegin + 60 < state.time)) {
					wrongRep = true;
					wrongRepair[wrongRepCount++] = repair;
					repair = null;
				}
				switch(state.component){
					case "connection_check": //feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);	
											 connectionCheckCount++;
											 if(state.effect.equals("icon_appears")){
												iconApp = state.time;
												iconClose = 0;
												if (errorOnGoing) {
													errorStatsList[errorCount-1].connChecksOccurred++;
												}
											 } else if (state.effect.equals("icon_closed")) {
														iconClose = state.time;
														numMissed++;
														iconApp = 0;
														if (errorOnGoing) {
															errorStatsList[errorCount-1].connChecksMissed++;
														}
													}else if (state.effect.equals("confirmed")) {
															iconConf = state.time;
															//feedbackWriter.write(";Time to respond: " + (iconConf-iconApp));
															connCheckTime = connCheckTime + iconConf-iconApp;
															iconApp = iconConf = 0;
														}
											 
											 break;
					case "logging_task": //feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);										 
										 logCount++;
										 if (errorOnGoing) {
												errorStatsList[errorCount-1].co2LogsOccurred++;
											}
										 if (state.effect.equals("missed")) {
											logMissedCount++;
											if (errorOnGoing) {
												errorStatsList[errorCount-1].co2LogsMissed++;
											}
										 }
										 continue;
					case "detector": errInf[errorCount -1] = new ErrorInfo(state.time, state.effect);
									 errorStatsList[errorCount -1] = new ErrorStats(state.time, state.effect);									 
									 break;
					case "ni_tank_display":	//System.out.println("Nitrogen Tank Display Clicked");
											if (errorOnGoing) {
												//System.out.println(errorStatsList[errorCount-1].n2TankClicks);
												errorStatsList[errorCount-1].n2TankClicks++;
											}
											break;
					case "ni_second":	if (errorOnGoing) {
												errorStatsList[errorCount-1].n2FlowMeter++;
											}
											break;
					case "ox_tank_display":	if (errorOnGoing) {
												errorStatsList[errorCount-1].o2TankClicks++;
											}
											break;
					case "ox_second":	if (errorOnGoing) {
											errorStatsList[errorCount-1].o2FlowMeter++;
										}
										break;
					case "mixer":	if (errorOnGoing) {
											errorStatsList[errorCount-1].mixerFlowMeter++;
										}
										break;
					case "graphic_monitor":	if (errorOnGoing) {
												switch(state.effect) {
												case "co_open": errorStatsList[errorCount-1].co2HistoryGraph++;
																//System.out.println(errorStatsList[errorCount-1].co2HistoryGraph);
																break;
												case "ox_open": errorStatsList[errorCount-1].o2HistoryGraph++;
																break;
												case "ni_open": errorStatsList[errorCount-1].n2HistoryGraph++;
																break;
												case "temp_open": errorStatsList[errorCount-1].tempHistoryGraph++;
																break;
												case "humid_open": errorStatsList[errorCount-1].humidityHistoryGraph++;
																break;
												default:	break;
												}										
									}
									break;
					case "possible_flow": 	if (errorOnGoing) {
												errorStatsList[errorCount-1].flowRates++;
											}
											break;
					default: break;
				}
				
				switch(state.effect.substring(0,6)) {
					case "phase ": //feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);									
									if (state.phase.equals("RED")) {
										errBegin = state.time;
										errorCount++;
										errorOnGoing = true;
										repBegin = 9999;
										repComplete = 0;
									} else if (state.phase.equals("RED_REPAIR") && !wrongRep) {
												errInf[errorCount -1].repairAttempted = true;
												errInf[errorCount -1].repCount++;
												repBegin = state.time;
												errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
												//feedbackWriter.write(";Time to respond: " + errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1]);												
											} else if (state.phase.equals("RED_REPAIR") && wrongRep) {
														repBegin = state.time;
														errInf[errorCount -1].repCount++;
														errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
														wrongRep = false;
													} else if (state.phase.equals("RED_NO_ERROR")) {
																repComplete = state.time;
																errInf[errorCount -1].repComplete = repComplete;
																errBegin = repComplete = 0;
																repBegin = 9999;
																errorOnGoing = false;
															}
									
									break;
					case "repair":  if (state.effect.equals("repair task finished")) break;
									else {										
										//feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);										
										//System.out.println(errInf[errorCount -1].repCount);
										//System.out.println(state.effect.substring(7));									
										errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount -1] = state.effect.substring(8);
										//System.out.println(errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount]);
										repair = state.effect.substring(8);
										correctResponseTime = state.time - errInf[errorCount -1].timeOccurred;										
										if (errInf[errorCount -1].errorOccurred.equals(repair)) {
													//feedbackWriter.write(";Time for Correct Reponse: " + correctResponseTime);
													errInf[errorCount -1].timeToCorrectResponse = correctResponseTime;
										}
										//System.out.println(repair + ":" + errInf[errorCount -1].errorOccurred);
										
										break;
									}
					default: break;
				}	
				
			}
		}
		
		//feedbackWriter.write("SUMMARY");
		
		
		//feedbackWriter.write("Total number of Connection Checks missed: "+numMissed);
		runStatsRed.connChecksMissed = numMissed;		
		
		if (connectionCheckCount != 0) {
			//feedbackWriter.write("Connection Checks miss percentage: " + (double)((numMissed*100)/connectionCheckCount) + "%");
			runStatsRed.connChecksMissedPercentage = (double)((numMissed*100)/connectionCheckCount);
			
		} else {
			//feedbackWriter.write("Number of Connection Checks during simulation = 0");
			
		}
		if (connectionCheckCount-numMissed == 0) {
			//feedbackWriter.write("Missed all Connection Checks");
			
		} else {
			//feedbackWriter.write("Average response time when responded: " + (double)(connCheckTime/(connectionCheckCount-numMissed)));
			
		}
		
		//feedbackWriter.write("Total number of Logging Tasks missed: "+logMissedCount);
		runStatsRed.co2LogsMissed = logMissedCount;
		runStatsRed.delay = runT.delay;
		
		if (logCount != 0) {
			//feedbackWriter.write("Logging Task miss percentage: " + (double)((logMissedCount*100)/logCount) + "%");
			runStatsRed.co2LogsMissedPercentage = (double)((logMissedCount*100)/logCount);
		
		} else {
			//feedbackWriter.write("Number of Logging Tasks during simulation = 0");
			
		}
		
		//System.out.println("Error \t"+"Occured At "+" Time to Respond "+" No. of Repairs Ordered "+" Time to Complete "+" Repair Ordered");
		//System.out.println(errInf[0].errorOccurred + "; " + errInf[0].timeOccurred + "; " + errInf[0].timeToRespond + "; " + (errInf[0].repCount) + "; " + errInf[0].repComplete + "; " + errInf[0].repairOrdered[0] + "; " + errInf[0].repairOrdered[1] );
		//feedbackWriter.write("ERROR OCCURRED;"+"AT;"+"CORRECT REPAIR AT;"+"NUM REPAIRS;"+"TIME TO COMPLETE");
		
		String repCompTime, timeCorrResp;
		for (int i = 0; i < errorCount; i++) {
			//errorStatsList[i] = new ErrorStats();
			errorStatsList[i].connChecksMissedPercentage = runStatsRed.connChecksMissedPercentage;
			errorStatsList[i].co2LogsMissedPercentage = runStatsRed.co2LogsMissedPercentage;
			if(errInf[i].repComplete == 9999) {
				repCompTime = "Repair Failed";
				timeCorrResp = "Never";
				errorStatsList[i].correctRepair = 0;
				errorStatsList[i].timeInRed = errInf[i].timeToCorrectResponse - errInf[i].timeOccurred;
				errorStatsList[i].incorrectRepairs = errInf[i].repCount;
			} else {
				repCompTime = Integer.toString(errInf[i].repComplete);
				timeCorrResp = Integer.toString(errInf[i].timeToCorrectResponse);
				errorStatsList[i].timeInRed = errInf[i].repComplete - errInf[i].timeOccurred;
				errorStatsList[i].incorrectRepairs = errInf[i].repCount;
				errorStatsList[i].correctRepair = 1;
			}
			//System.out.println(errInf[i].errorOccurred + ";" + errInf[i].timeOccurred + ";" + timeCorrResp + ";" + (errInf[i].repCount) + ";" + repCompTime);
			//feedbackWriter.write("Error Occurred: "+errInf[i].errorOccurred + "; At: " + errInf[i].timeOccurred + "; Time for Correct Repair: " + timeCorrResp + "; Number of Repairs Attempted: " +  (errInf[i].repCount) + "; Repair Completed At: " + repCompTime);
			
			errorStatsList[i].Error = errInf[i].errorOccurred;
				
			
		}
		int totalTimeInRed = 0;
		
		for (int i = 0; i < errorCount; i++) {
			//System.out.println(errorStatsList[i].co2HistoryGraph +","+ errorStatsList[i].o2HistoryGraph +","+ errorStatsList[i].n2HistoryGraph +","+ errorStatsList[i].tempHistoryGraph +","+ errorStatsList[i].humidityHistoryGraph +","+ errorStatsList[i].flowRates +",");
			//feedbackWriter.write("At "+errInf[i].timeToRespond[j] + " seconds; Attempted Repair: " + errInf[i].repairOrdered[j]);
			feedbackWriter.write(runT.fileName + "," + errorStatsList[i].Error +"," + runT.leg + ","+ runT.delay + "," + runT.medium + ",");
			feedbackWriter.write(errorStatsList[i].correctRepair +","+ errorStatsList[i].timeInRed +","+ errorStatsList[i].incorrectRepairs +",");
			feedbackWriter.write(errorStatsList[i].o2TankClicks +","+ errorStatsList[i].o2FlowMeter +","+ errorStatsList[i].n2TankClicks +","+ errorStatsList[i].n2FlowMeter +","+ errorStatsList[i].mixerFlowMeter +",");
			feedbackWriter.write(errorStatsList[i].co2HistoryGraph +","+ errorStatsList[i].o2HistoryGraph +","+ errorStatsList[i].n2HistoryGraph +","+ errorStatsList[i].tempHistoryGraph +","+ errorStatsList[i].humidityHistoryGraph +","+ errorStatsList[i].flowRates +",");
			feedbackWriter.write(errorStatsList[i].co2LogsMissed + "," + errorStatsList[i].co2LogsMissedPercentage + "," + errorStatsList[i].connChecksMissed + "," +  errorStatsList[i].connChecksMissedPercentage + ",");
			totalTimeInRed = totalTimeInRed + errorStatsList[i].timeInRed;
			//System.out.println(totalTimeInRed);
		}
		
	    runStatsRed.totalTimeInRed = totalTimeInRed;	    
	
		reader.close();
		feedbackWriter.close();
		//clicksWriter.close();
		return runStatsRed;
	}

	private static RunStatsInRed extractPInfo(RunType runT, File pdata) throws IOException{
		System.out.println(runT.fileName);
		// Read the inFile line by line and print everything to console and outFile.
				BufferedReader reader = new BufferedReader(new FileReader(runT.fileLoc));
				//BufferedWriter feedbackWriter = new BufferedWriter(new FileWriter(pdata, true));
				RunStatsInRed runStatsRed = new RunStatsInRed();
				ErrorStats[] errorStatsList = new ErrorStats[15];
				
				String line = null;
				String[] eachLine = null;		
				SystemState state;
				int iconApp = 0, iconClose = 0, iconConf = 0, numMissed = 0;
				iconApp = iconClose;
				int errBegin = 0, repBegin = 9999, repComplete = 0;
				boolean wrongRep = false;
				int wrongRepCount = 0;
				int reactionTime = 0;
				wrongRepCount = reactionTime;
				int correctResponseTime = 0;
				int logMissedCount = 0, logCount = 0, connectionCheckCount = 0, connCheckTime = 0;
				String[] wrongRepair = new String[30];
				String repair = null;
				int errorCount = 0;
				boolean errorOnGoing = false;
				ErrorInfo[] errInf = new ErrorInfo[10];
				
				// Ignore first line of the log file
				line = reader.readLine();
				
				//clicksWriter.write("Time \t\t Event \t\t Effect");
				//clicksWriter.newLine();	
				
				// Read every line of the log file and tokenize it. Then extract the relevant values.			
				while((line = reader.readLine()) != null) {
					eachLine = line.split(";");
					if (eachLine.length == 14 ) {
						state = new SystemState(eachLine[0],eachLine[9],eachLine[10],eachLine[11], null, eachLine[13]);
						
						if (state.user.equals("OPERATOR")) {
							//clicksWriter.write(""+ state.time + "\t"+ state.component + "\t\t" + state.effect);
							//clicksWriter.newLine();					
						}
						
						// Check if any repair is going on and if it's incorrect repair. Record incorrect repair order.
						if (!wrongRep && (repBegin < 9999) && (repBegin + 60 < state.time)) {
							wrongRep = true;
							wrongRepair[wrongRepCount++] = repair;
							repair = null;
						}
						switch(state.component){
							case "connection_check": //feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);	
													 connectionCheckCount++;
													 if(state.effect.equals("icon_appears")){
														iconApp = state.time;
														iconClose = 0;
														if (errorOnGoing) {
															errorStatsList[errorCount-1].connChecksOccurred++;
														}
													 } else if (state.effect.equals("icon_closed")) {
																iconClose = state.time;
																numMissed++;
																iconApp = 0;
																if (errorOnGoing) {
																	errorStatsList[errorCount-1].connChecksMissed++;
																}
															}else if (state.effect.equals("confirmed")) {
																	iconConf = state.time;
																	//feedbackWriter.write(";Time to respond: " + (iconConf-iconApp));
																	connCheckTime = connCheckTime + iconConf-iconApp;
																	iconApp = iconConf = 0;
																}
													 
													 break;
							case "logging_task": //feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);										 
												 logCount++;
												 if (errorOnGoing) {
														errorStatsList[errorCount-1].co2LogsOccurred++;
													}
												 if (state.effect.equals("missed")) {
													logMissedCount++;
													if (errorOnGoing) {
														errorStatsList[errorCount-1].co2LogsMissed++;
													}
												 }
												 continue;
							case "detector": errInf[errorCount -1] = new ErrorInfo(state.time, state.effect);
											 errorStatsList[errorCount -1] = new ErrorStats(state.time, state.effect);									 
											 break;
							case "ni_tank_display":	//System.out.println("Nitrogen Tank Display Clicked");
													if (errorOnGoing) {
														//System.out.println(errorStatsList[errorCount-1].n2TankClicks);
														errorStatsList[errorCount-1].n2TankClicks++;
													}
													break;
							case "ni_second":	if (errorOnGoing) {
														errorStatsList[errorCount-1].n2FlowMeter++;
													}
													break;
							case "ox_tank_display":	if (errorOnGoing) {
														errorStatsList[errorCount-1].o2TankClicks++;
													}
													break;
							case "ox_second":	if (errorOnGoing) {
													errorStatsList[errorCount-1].o2FlowMeter++;
												}
												break;
							case "mixer":	if (errorOnGoing) {
													errorStatsList[errorCount-1].mixerFlowMeter++;
												}
												break;
							case "graphic_monitor":	if (errorOnGoing) {
														switch(state.effect) {
														case "co_open": errorStatsList[errorCount-1].co2HistoryGraph++;
																		//System.out.println(errorStatsList[errorCount-1].co2HistoryGraph);
																		break;
														case "ox_open": errorStatsList[errorCount-1].o2HistoryGraph++;
																		break;
														case "ni_open": errorStatsList[errorCount-1].n2HistoryGraph++;
																		break;
														case "temp_open": errorStatsList[errorCount-1].tempHistoryGraph++;
																		break;
														case "humid_open": errorStatsList[errorCount-1].humidityHistoryGraph++;
																		break;
														default:	break;
														}										
											}
											break;
							case "possible_flow": 	if (errorOnGoing) {
														errorStatsList[errorCount-1].flowRates++;
													}
													break;
							default: break;
						}
						
						switch(state.effect.substring(0,6)) {
							case "phase ": //feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);									
											if (state.phase.equals("RED")) {
												errBegin = state.time;
												errorCount++;
												errorOnGoing = true;
												repBegin = 9999;
												repComplete = 0;
											} else if (state.phase.equals("RED_REPAIR") && !wrongRep) {
														errInf[errorCount -1].repairAttempted = true;
														errInf[errorCount -1].repCount++;
														repBegin = state.time;
														errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
														//feedbackWriter.write(";Time to respond: " + errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1]);												
													} else if (state.phase.equals("RED_REPAIR") && wrongRep) {
																repBegin = state.time;
																errInf[errorCount -1].repCount++;
																//System.out.println(errorCount);
																//System.out.println(errInf[errorCount -1].repCount);
																//System.out.println(errInf[errorCount -1].repCount -1);
																//System.out.println(errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1]);
																errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
																wrongRep = false;
															} else if (state.phase.equals("RED_NO_ERROR")) {
																		repComplete = state.time;
																		errInf[errorCount -1].repComplete = repComplete;
																		errBegin = repComplete = 0;
																		repBegin = 9999;
																		errorOnGoing = false;
																	}
											
											break;
							case "repair":  if (state.effect.equals("repair task finished")) break;
											else {										
												//feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);										
												//System.out.println(errInf[errorCount -1].repCount);
												//System.out.println(state.effect.substring(7));									
												errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount -1] = state.effect.substring(8);
												//System.out.println(errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount]);
												repair = state.effect.substring(8);
												correctResponseTime = state.time - errInf[errorCount -1].timeOccurred;										
												if (errInf[errorCount -1].errorOccurred.equals(repair)) {
															//feedbackWriter.write(";Time for Correct Reponse: " + correctResponseTime);
															errInf[errorCount -1].timeToCorrectResponse = correctResponseTime;
												}
												//System.out.println(repair + ":" + errInf[errorCount -1].errorOccurred);
												
												break;
											}
							default: break;
						}	
						
					}
				}
				
				//feedbackWriter.write("SUMMARY");
				
				
				//feedbackWriter.write("Total number of Connection Checks missed: "+numMissed);
				runStatsRed.connChecksMissed = numMissed;		
				
				if (connectionCheckCount != 0) {
					//feedbackWriter.write("Connection Checks miss percentage: " + (double)((numMissed*100)/connectionCheckCount) + "%");
					runStatsRed.connChecksMissedPercentage = (double)((numMissed*100)/connectionCheckCount);
					
				} else {
					//feedbackWriter.write("Number of Connection Checks during simulation = 0");
					
				}
				if (connectionCheckCount-numMissed == 0) {
					//feedbackWriter.write("Missed all Connection Checks");
					
				} else {
					//feedbackWriter.write("Average response time when responded: " + (double)(connCheckTime/(connectionCheckCount-numMissed)));
					
				}
				
				//feedbackWriter.write("Total number of Logging Tasks missed: "+logMissedCount);
				runStatsRed.co2LogsMissed = logMissedCount;
				runStatsRed.delay = runT.delay;
				
				if (logCount != 0) {
					//feedbackWriter.write("Logging Task miss percentage: " + (double)((logMissedCount*100)/logCount) + "%");
					runStatsRed.co2LogsMissedPercentage = (double)((logMissedCount*100)/logCount);
				
				} else {
					//feedbackWriter.write("Number of Logging Tasks during simulation = 0");
					
				}
				
				//System.out.println("Error \t"+"Occured At "+" Time to Respond "+" No. of Repairs Ordered "+" Time to Complete "+" Repair Ordered");
				//System.out.println(errInf[0].errorOccurred + "; " + errInf[0].timeOccurred + "; " + errInf[0].timeToRespond + "; " + (errInf[0].repCount) + "; " + errInf[0].repComplete + "; " + errInf[0].repairOrdered[0] + "; " + errInf[0].repairOrdered[1] );
				//feedbackWriter.write("ERROR OCCURRED;"+"AT;"+"CORRECT REPAIR AT;"+"NUM REPAIRS;"+"TIME TO COMPLETE");
				
				String repCompTime, timeCorrResp;
				for (int i = 0; i < errorCount; i++) {
					errorStatsList[i].fileName = runT.fileName;
					errorStatsList[i].leg = runT.leg;
					errorStatsList[i].medium = runT.medium;
					errorStatsList[i].delay = runT.delay;
					errorStatsList[i].connChecksMissedPercentage = runStatsRed.connChecksMissedPercentage;
					errorStatsList[i].co2LogsMissedPercentage = runStatsRed.co2LogsMissedPercentage;
					//errorStatsList[i] = new ErrorStats();
					if(errInf[i].repComplete == 9999) {
						repCompTime = "Repair Failed";
						timeCorrResp = "Never";
						errorStatsList[i].correctRepair = 0;
						errorStatsList[i].timeInRed = errInf[i].timeToCorrectResponse - errInf[i].timeOccurred;
						errorStatsList[i].incorrectRepairs = errInf[i].repCount;
					} else {
						repCompTime = Integer.toString(errInf[i].repComplete);
						timeCorrResp = Integer.toString(errInf[i].timeToCorrectResponse);
						errorStatsList[i].timeInRed = errInf[i].repComplete - errInf[i].timeOccurred;
						errorStatsList[i].incorrectRepairs = errInf[i].repCount;
						errorStatsList[i].correctRepair = 1;
					}
					//System.out.println(errInf[i].errorOccurred + ";" + errInf[i].timeOccurred + ";" + timeCorrResp + ";" + (errInf[i].repCount) + ";" + repCompTime);
					//feedbackWriter.write("Error Occurred: "+errInf[i].errorOccurred + "; At: " + errInf[i].timeOccurred + "; Time for Correct Repair: " + timeCorrResp + "; Number of Repairs Attempted: " +  (errInf[i].repCount) + "; Repair Completed At: " + repCompTime);
					
					errorStatsList[i].Error = errInf[i].errorOccurred;
					
					//System.out.println("Error Count in P = " + errInf[i].repCount);
						
						
					
				}
				int totalTimeInRed = 0;
				System.out.println("Error Count: " + errorCount);
				for (int i = 0; i < errorCount; i++) {					
					//System.out.println(errorStatsList[i].co2HistoryGraph +","+ errorStatsList[i].o2HistoryGraph +","+ errorStatsList[i].n2HistoryGraph +","+ errorStatsList[i].tempHistoryGraph +","+ errorStatsList[i].humidityHistoryGraph +","+ errorStatsList[i].flowRates +",");
					//feedbackWriter.write("At "+errInf[i].timeToRespond[j] + " seconds; Attempted Repair: " + errInf[i].repairOrdered[j]);
					switch(errorStatsList[i].Error) {
						case "NITROGEN_VALVE_LEAK" : FSE_PioneerErrors.pioneerErrors[0] = errorStatsList[i];
													 break;
						case "OXYGEN_VALVE_STUCK_OPEN" : FSE_PioneerErrors.pioneerErrors[1] = errorStatsList[i];
						 								 break;
						case "MIXER_BLOCK" : FSE_PioneerErrors.pioneerErrors[2] = errorStatsList[i];
						 					 break;
						case "OXYGEN_SENSOR_STARTS_LOWER_TH" : FSE_PioneerErrors.pioneerErrors[3] = errorStatsList[i];
						 									   break;
						default: break;
					}
					/*
					feedbackWriter.write(runT.fileName + "," + errorStatsList[i].Error +"," + runT.leg + ","+ runT.delay + "," + runT.medium + ",");
					feedbackWriter.write(errorStatsList[i].correctRepair +","+ errorStatsList[i].timeInRed +","+ errorStatsList[i].incorrectRepairs +",");
					feedbackWriter.write(errorStatsList[i].o2TankClicks +","+ errorStatsList[i].o2FlowMeter +","+ errorStatsList[i].n2TankClicks +","+ errorStatsList[i].n2FlowMeter +","+ errorStatsList[i].mixerFlowMeter +",");
					feedbackWriter.write(errorStatsList[i].co2HistoryGraph +","+ errorStatsList[i].o2HistoryGraph +","+ errorStatsList[i].n2HistoryGraph +","+ errorStatsList[i].tempHistoryGraph +","+ errorStatsList[i].humidityHistoryGraph +","+ errorStatsList[i].flowRates +",");
					feedbackWriter.write(errorStatsList[i].co2LogsMissed + "," + errorStatsList[i].co2LogsMissedPercentage + "," + errorStatsList[i].connChecksMissed + "," +  errorStatsList[i].connChecksMissedPercentage + ",");
					*/
					totalTimeInRed = totalTimeInRed + errorStatsList[i].timeInRed;
					
					//System.out.println(totalTimeInRed);
				}
				
			    runStatsRed.totalTimeInRed = totalTimeInRed;
			   // System.out.println("Total time in Red = " + runStatsRed.totalTimeInRed);
				
				
				for (int j = 0; j < errorCount; j++) {
					switch (errorStatsList[j].Error) {
						case "NITROGEN_VALVE_LEAK": 
							fsePioneerError[0] = new FSE_PioneerErrors();
							fsePioneerError[0].pioneerError = "NITROGEN_VALVE_LEAK";
							fsePioneerError[0].pioneerErrorType = "S1";
							fsePioneerError[0].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[0].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[0].leg = runT.leg;
							break;
						case "OXYGEN_VALVE_STUCK_OPEN" :
							fsePioneerError[1] = new FSE_PioneerErrors();
							fsePioneerError[1].pioneerError = "OXYGEN_VALVE_STUCK_OPEN";
							fsePioneerError[1].pioneerErrorType = "S2";
							fsePioneerError[1].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[1].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[1].leg = runT.leg;
							break;
						case "MIXER_BLOCK": 
							fsePioneerError[2] = new FSE_PioneerErrors();
							fsePioneerError[2].pioneerError = "MIXER_BLOCK";
							fsePioneerError[2].pioneerErrorType = "D1";
							fsePioneerError[2].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[2].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[2].leg = runT.leg;
							break;
						case "OXYGEN_SENSOR_STARTS_LOWER_TH" :
							fsePioneerError[3] = new FSE_PioneerErrors();
							fsePioneerError[3].pioneerError = "OXYGEN_SENSOR_STARTS_LOWER_TH";
							fsePioneerError[3].pioneerErrorType = "D2";
							fsePioneerError[3].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[3].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[3].leg = runT.leg;
							break;
						default: break;
					}							
					
				}
				reader.close();
				//feedbackWriter.close();
				//clicksWriter.close();				
				return runStatsRed;
		}

}
