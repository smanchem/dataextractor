package datax;

import java.io.*;

public class Extractor {
	public static FSE_PioneerErrors[] fsePioneerError = new FSE_PioneerErrors[4];
	public static void extract(Run run, File fsedata, File pdata, File fsePioneerDelay) throws IOException {
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
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			try {
				runStatsRed[3] = extractPInfo(runT4, pdata);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			
			BufferedWriter feedbackWriter2 = new BufferedWriter(new FileWriter(pdata, true));
			for (int i = 0; i < 4; i++) {
				if (FSE_PioneerErrors.pioneerErrors[i] != null) {
					feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].fileName + "," + FSE_PioneerErrors.pioneerErrors[i].Error +"," + FSE_PioneerErrors.pioneerErrors[i].leg + ","+ FSE_PioneerErrors.pioneerErrors[i].delay + "," + FSE_PioneerErrors.pioneerErrors[i].medium + ",");
					feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].correctRepair +","+ FSE_PioneerErrors.pioneerErrors[i].timeInRed +","+ FSE_PioneerErrors.pioneerErrors[i].incorrectRepairs +",");
					feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].o2TankClicks +","+ FSE_PioneerErrors.pioneerErrors[i].o2FlowMeter +","+ FSE_PioneerErrors.pioneerErrors[i].n2TankClicks +","+ FSE_PioneerErrors.pioneerErrors[i].n2FlowMeter +","+ FSE_PioneerErrors.pioneerErrors[i].mixerFlowMeter +",");
					feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].co2HistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].o2HistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].n2HistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].tempHistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].humidityHistoryGraph +","+ FSE_PioneerErrors.pioneerErrors[i].flowRates +",");
					feedbackWriter2.write(FSE_PioneerErrors.pioneerErrors[i].co2LogsMissed + "," + FSE_PioneerErrors.pioneerErrors[i].co2LogsMissedPercentage + "," + FSE_PioneerErrors.pioneerErrors[i].connChecksMissed + "," +  FSE_PioneerErrors.pioneerErrors[i].connChecksMissedPercentage + ",");
					FSE_PioneerErrors.pioneerErrors[i] = null;
				} else {
					feedbackWriter2.write(",,,,,,,,,,,,,,,,,,,,,,,");
				}
			}			
						
			try {
				runStatsRed[0] = extractFSEInfo(runT1, fsedata);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			try {
				runStatsRed[1] = extractFSEInfo(runT2, fsedata);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			
			
			BufferedWriter feedbackWriter = new BufferedWriter(new FileWriter(fsedata, true));
			
			if (runStatsRed[0].delay == 1) {
				feedbackWriter.write(runStatsRed[0].totalTimeInRed +","+ runStatsRed[0].co2LogsMissed +","+ runStatsRed[0].co2LogsMissedPercentage +","+ runStatsRed[0].connChecksMissed +","+ runStatsRed[0].connChecksMissedPercentage + ",");
				feedbackWriter.write(runStatsRed[1].totalTimeInRed +","+ runStatsRed[1].co2LogsMissed +","+ runStatsRed[1].co2LogsMissedPercentage +","+ runStatsRed[1].connChecksMissed +","+ runStatsRed[1].connChecksMissedPercentage + ",");
			} else {
				feedbackWriter.write(runStatsRed[1].totalTimeInRed +","+ runStatsRed[1].co2LogsMissed +","+ runStatsRed[1].co2LogsMissedPercentage +","+ runStatsRed[1].connChecksMissed +","+ runStatsRed[1].connChecksMissedPercentage + ",");
				feedbackWriter.write(runStatsRed[0].totalTimeInRed +","+ runStatsRed[0].co2LogsMissed +","+ runStatsRed[0].co2LogsMissedPercentage +","+ runStatsRed[0].connChecksMissed +","+ runStatsRed[0].connChecksMissedPercentage + ",");
			}
			
			for (int i = 0; i < 4; i++) {
				if (fsePioneerError[i] != null) {
					feedbackWriter.write(fsePioneerError[i].timeInRed + "," + fsePioneerError[i].CO2LogsMissed + "," + fsePioneerError[i].CO2LogsMissedPercentage + "," + fsePioneerError[i].connChecksMissed + "," + fsePioneerError[i].connChecksMissedPercentage + ",");
					// System.out.print(fsePioneerError[i].timeInRed + "," + fsePioneerError[i].CO2LogsMissed + "," + fsePioneerError[i].CO2LogsMissedPercentage + "," + fsePioneerError[i].connChecksMissed + "," + fsePioneerError[i].connChecksMissedPercentage + ",");
				} else {
					feedbackWriter.write(",,,,,");
					// System.out.print(",,,,,");
				}
			}
			
			/*
			 * When Pioneer is in Red during Time Delay and No Time Delay phases, FSE activity.
			 * */
			FSE_PioneerErrors[] fseErrors = new FSE_PioneerErrors[2];
			fseErrors[0] = new FSE_PioneerErrors();
			fseErrors[1] = new FSE_PioneerErrors();
			for (int i = 0; i < 4; i++) {
				if (fsePioneerError[i] != null && fsePioneerError[i].delay == 0) {
					fseErrors[0].delay = 0;
					fseErrors[0].timeInRed = fseErrors[0].timeInRed + fsePioneerError[i].timeInRed;
					fseErrors[0].CO2LogsMissed = fseErrors[0].CO2LogsMissed + fsePioneerError[i].CO2LogsMissed;
					fseErrors[0].CO2LogsOccurred = fseErrors[0].CO2LogsOccurred + fsePioneerError[i].CO2LogsOccurred;
					fseErrors[0].connChecksMissed = fseErrors[0].connChecksMissed + fsePioneerError[i].connChecksMissed;
					fseErrors[0].connChecksOccurred = fseErrors[0].connChecksOccurred + fsePioneerError[i].connChecksOccurred;
					//feedbackWriter.write(fsePioneerError[i].timeInRed + "," + fsePioneerError[i].CO2LogsMissed + "," + fsePioneerError[i].CO2LogsMissedPercentage + "," + fsePioneerError[i].connChecksMissed + "," + fsePioneerError[i].connChecksMissedPercentage + ",");
					// System.out.print(fsePioneerError[i].timeInRed + "," + fsePioneerError[i].CO2LogsMissed + "," + fsePioneerError[i].CO2LogsMissedPercentage + "," + fsePioneerError[i].connChecksMissed + "," + fsePioneerError[i].connChecksMissedPercentage + ",");
				} else if (fsePioneerError[i] != null && fsePioneerError[i].delay == 1){
					fseErrors[1].delay = 1;
					fseErrors[1].timeInRed = fseErrors[1].timeInRed + fsePioneerError[i].timeInRed;
					fseErrors[1].CO2LogsMissed = fseErrors[1].CO2LogsMissed + fsePioneerError[i].CO2LogsMissed;
					fseErrors[1].CO2LogsOccurred = fseErrors[1].CO2LogsOccurred + fsePioneerError[i].CO2LogsOccurred;
					fseErrors[1].connChecksMissed = fseErrors[1].connChecksMissed + fsePioneerError[i].connChecksMissed;
					fseErrors[1].connChecksOccurred = fseErrors[1].connChecksOccurred + fsePioneerError[i].connChecksOccurred;
				} else {
					//feedbackWriter.write(",,,,,");
					// System.out.print(",,,,,");
				}
			}
			
			fseErrors[0].CO2LogsMissedPercentage = (double)((int)(((double)(fseErrors[0].CO2LogsMissed*100)/fseErrors[0].connChecksOccurred)*100))/100;
			fseErrors[0].connChecksMissedPercentage = (double)((int)(((double)(fseErrors[0].connChecksMissed*100)/fseErrors[0].connChecksOccurred)*100))/100;
			fseErrors[1].CO2LogsMissedPercentage = (double)((int)(((double)(fseErrors[1].CO2LogsMissed*100)/fseErrors[1].connChecksOccurred)*100))/100;		
			fseErrors[1].connChecksMissedPercentage = (double)((int)(((double)(fseErrors[1].connChecksMissed*100)/fseErrors[1].connChecksOccurred)*100))/100;
			BufferedWriter feedbackWriter3 = new BufferedWriter(new FileWriter(fsePioneerDelay, true));
			if (fseErrors[0].leg == runT1.delay) {
				feedbackWriter3.write(runT1.fileName + "," + fseErrors[0].timeInRed + "," + fseErrors[0].CO2LogsMissed + "," + fseErrors[0].CO2LogsMissedPercentage + "," + fseErrors[0].connChecksMissed + "," + fseErrors[0].connChecksMissedPercentage + ",");
				feedbackWriter3.write(runT2.fileName + "," + fseErrors[1].timeInRed + "," + fseErrors[1].CO2LogsMissed + "," + fseErrors[1].CO2LogsMissedPercentage + "," + fseErrors[1].connChecksMissed + "," + fseErrors[1].connChecksMissedPercentage + ",");
				feedbackWriter3.newLine();
			} else {
				feedbackWriter3.write(runT2.fileName + "," + fseErrors[0].timeInRed + "," + fseErrors[0].CO2LogsMissed + "," + fseErrors[0].CO2LogsMissedPercentage + "," + fseErrors[0].connChecksMissed + "," + fseErrors[0].connChecksMissedPercentage + ",");
				feedbackWriter3.write(runT1.fileName + "," + fseErrors[1].timeInRed + "," + fseErrors[1].CO2LogsMissed + "," + fseErrors[1].CO2LogsMissedPercentage + "," + fseErrors[1].connChecksMissed + "," + fseErrors[1].connChecksMissedPercentage + ",");
				feedbackWriter3.newLine();
			}
			

			if (runStatsRed[2].delay == 1) {
				feedbackWriter2.write(runStatsRed[2].totalTimeInRed +","+ runStatsRed[2].co2LogsMissed +","+ runStatsRed[2].co2LogsMissedPercentage +","+ runStatsRed[2].connChecksMissed +","+ runStatsRed[2].connChecksMissedPercentage + ",");
				feedbackWriter2.write(runStatsRed[3].totalTimeInRed +","+ runStatsRed[3].co2LogsMissed +","+ runStatsRed[3].co2LogsMissedPercentage +","+ runStatsRed[3].connChecksMissed +","+ runStatsRed[3].connChecksMissedPercentage + ",");
			} else {
				feedbackWriter2.write(runStatsRed[3].totalTimeInRed +","+ runStatsRed[3].co2LogsMissed +","+ runStatsRed[3].co2LogsMissedPercentage +","+ runStatsRed[3].connChecksMissed +","+ runStatsRed[3].connChecksMissedPercentage + ",");
				feedbackWriter2.write(runStatsRed[2].totalTimeInRed +","+ runStatsRed[2].co2LogsMissed +","+ runStatsRed[2].co2LogsMissedPercentage +","+ runStatsRed[2].connChecksMissed +","+ runStatsRed[2].connChecksMissedPercentage + ",");
			}
			feedbackWriter.close();
			feedbackWriter2.close();
			feedbackWriter3.close();
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
		int errBegin = 0, repBegin = 99999, repComplete = 0;
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
		int systemTime = 0;
		
		
		int[] pErr = {9,9};
		int j = 0;
		boolean pErr1 = false, pErr2 = false;
		boolean pErr1Ongoing = false, pErr2Ongoing = false;
		for (int i = 0; i < 4; i++) {
			if (fsePioneerError[i] != null && fsePioneerError[i].leg == runT.leg) {
				pErr[j++] = i;
				pErr1 = true;
			}
		}
		if (j == 2) {
			pErr2 = true;
		}
				
		// Ignore first line of the log file
		line = reader.readLine();
		
		// Read every line of the log file and tokenize it. Then extract the relevant values.			
		while((line = reader.readLine()) != null) {
			eachLine = line.split(";");
			if (eachLine.length == 14 ) {
				state = new SystemState(eachLine[0],eachLine[9],eachLine[10],eachLine[11], null, eachLine[13]);
				systemTime = state.time;
				if (pErr1 && (fsePioneerError[pErr[0]].timeBegin <= systemTime)) {
					if (fsePioneerError[pErr[0]].timeEnd >= systemTime) {
						pErr1Ongoing = true;
						if (state.phase.substring(0, 3).equals("RED")) {
							if (fsePioneerError[pErr[0]].prevTime < systemTime) {
								fsePioneerError[pErr[0]].timeInRed++;
								fsePioneerError[pErr[0]].prevTime = systemTime;
							}
						}						
					} else {
						pErr1Ongoing = false;			 
					}
				}
				if (pErr2 && (fsePioneerError[pErr[1]].timeBegin <= systemTime)) {
					if (fsePioneerError[pErr[1]].timeEnd >= systemTime) {
						pErr2Ongoing = true;
						if (state.phase.substring(0, 3).equals("RED")) {
							if (fsePioneerError[pErr[1]].prevTime < systemTime) {
								fsePioneerError[pErr[1]].timeInRed++;
								fsePioneerError[pErr[1]].prevTime = systemTime;
							}
						}
					} else {
						pErr2Ongoing = false;						 
					}
				}
				
				// Check if any repair is going on and if it's incorrect repair. Record incorrect repair order.
				if (!wrongRep && (repBegin < 99999) && (repBegin + 60 < state.time)) {
					wrongRep = true;
					wrongRepair[wrongRepCount++] = repair;
					repair = null;
				}
				
				switch(state.component){
					case "connection_check": if(state.effect.equals("icon_appears")){
												connectionCheckCount++;
												iconApp = state.time;
												iconClose = 0;
												if (errorOnGoing) {
													errorStatsList[errorCount-1].connChecksOccurred++;
												}
												if (pErr1Ongoing) {
													fsePioneerError[pErr[0]].connChecksOccurred++;
												}
												if (pErr2Ongoing) {
													fsePioneerError[pErr[1]].connChecksOccurred++;
												}
											 } else if (state.effect.equals("icon_closed")) {
														iconClose = state.time;
														numMissed++;
														iconApp = 0;
														if (errorOnGoing) {
															errorStatsList[errorCount-1].connChecksMissed++;
														}
														if (pErr1Ongoing) {
															fsePioneerError[pErr[0]].connChecksMissed++;
														} else if (pErr2Ongoing) {
															fsePioneerError[pErr[1]].connChecksMissed++;
														}
													}else if (state.effect.equals("confirmed")) {
															iconConf = state.time;
															connCheckTime = connCheckTime + iconConf-iconApp;
															iconApp = iconConf = 0;
														}
											 
											 break;
					case "logging_task": logCount++;
										 if (errorOnGoing) {
												errorStatsList[errorCount-1].co2LogsOccurred++;
										 }
										 if (pErr1Ongoing) {
											 fsePioneerError[pErr[0]].CO2LogsOccurred++;
										 }
										 if (pErr2Ongoing) {
											 fsePioneerError[pErr[1]].CO2LogsOccurred++;
										 } 
										 if (state.effect.equals("missed")) {
											logMissedCount++;
											if (errorOnGoing) {
												errorStatsList[errorCount-1].co2LogsMissed++;
											}
											if (pErr1Ongoing) {
												 fsePioneerError[pErr[0]].CO2LogsMissed++;
											 }
											if (pErr2Ongoing) {
												 fsePioneerError[pErr[1]].CO2LogsMissed++;
											 }
										 }
										 continue;
					case "detector": errInf[errorCount -1] = new ErrorInfo(state.time, state.effect);
									 errorStatsList[errorCount -1] = new ErrorStats(state.time, state.effect);									 
									 break;
					case "ni_tank_display":	// Nitrogen Tank Display Clicked 
											if (errorOnGoing) {
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
					case "phase ": if (state.phase.equals("RED")) {
										if (pErr1Ongoing) {
											 fsePioneerError[pErr[0]].prevTime = systemTime;
											 //System.out.println("FSE " + fsePioneerError[pErr[0]].pioneerErrorType +" Error Begin: " + fsePioneerError[pErr[0]].prevTime);
										}
										if (pErr2Ongoing) {
											 fsePioneerError[pErr[1]].prevTime = systemTime;
											 //System.out.println("FSE " + fsePioneerError[pErr[1]].pioneerErrorType + " Error Begin: " + fsePioneerError[pErr[1]].prevTime);
										}
										errBegin = state.time;
										errorCount++;
										errorOnGoing = true;
										repBegin = 99999;
										repComplete = 0;
									} else if (state.phase.equals("RED_REPAIR") && !wrongRep) {
												errInf[errorCount -1].repairAttempted = true;
												errInf[errorCount -1].repCount++;
												repBegin = state.time;
												errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;																								
											} else if (state.phase.equals("RED_REPAIR") && wrongRep) {
														repBegin = state.time;
														errInf[errorCount -1].repCount++;
														errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
														wrongRep = false;
													} else if (state.phase.equals("RED_NO_ERROR")) {
																repComplete = state.time;
																errInf[errorCount -1].repComplete = repComplete;
																errBegin = repComplete = 0;
																repBegin = 99999;
																errorOnGoing = false;
															}
									
									break;
					case "repair":  if (state.effect.equals("repair task finished")) break;
									else {										
										errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount -1] = state.effect.substring(8);
										repair = state.effect.substring(8);
										correctResponseTime = state.time - errInf[errorCount -1].timeOccurred;										
										if (errInf[errorCount -1].errorOccurred.equals(repair)) {										
													errInf[errorCount -1].timeToCorrectResponse = correctResponseTime;
										}
										break;
									}
					default: break;
				}	
				
			}
		}
		
		// CO2 Logs and Connection Checks while Pioneer is in Error
		if (pErr1) {
			fsePioneerError[pErr[0]].connChecksMissedPercentage = (double)((int)(((double)(fsePioneerError[pErr[0]].connChecksMissed*100)/fsePioneerError[pErr[0]].connChecksOccurred)*100))/100;
			fsePioneerError[pErr[0]].CO2LogsMissedPercentage = (double)((int)(((double)(fsePioneerError[pErr[0]].CO2LogsMissed*100)/fsePioneerError[pErr[0]].CO2LogsOccurred)*100))/100;
			// System.out.println ("pErr1 ConnCheck %: " + fsePioneerError[pErr[0]].connChecksMissedPercentage + "; CO2 Log %: " + fsePioneerError[pErr[0]].CO2LogsMissedPercentage);
			//System.out.println("Total time in Red in pErr1: " + fsePioneerError[pErr[0]].timeInRed);
			//System.out.println("FSE "+ fsePioneerError[pErr[0]].pioneerErrorType + " Error End: " + fsePioneerError[pErr[0]].prevTime);
		}
		if (pErr2) {
			fsePioneerError[pErr[1]].connChecksMissedPercentage = (double)((int)(((double)(fsePioneerError[pErr[1]].connChecksMissed*100)/fsePioneerError[pErr[1]].connChecksOccurred)*100))/100;
			fsePioneerError[pErr[1]].CO2LogsMissedPercentage = (double)((int)(((double)(fsePioneerError[pErr[1]].CO2LogsMissed*100)/fsePioneerError[pErr[1]].connChecksOccurred)*100))/100;
			// System.out.println ("pErr2 ConnCheck %: " + fsePioneerError[pErr[1]].connChecksMissedPercentage + "; CO2 Log %: " + fsePioneerError[pErr[1]].CO2LogsMissedPercentage);
			//System.out.println("Total time in Red in pErr2: " + fsePioneerError[pErr[1]].timeInRed);
			//System.out.println("FSE "+ fsePioneerError[pErr[1]].pioneerErrorType + " Error End: " + fsePioneerError[pErr[1]].prevTime);
		}
		
		// Total number of Connection Checks missed
		runStatsRed.connChecksMissed = numMissed;
		
		if (connectionCheckCount != 0) {
			// Connection Checks miss percentage
			runStatsRed.connChecksMissedPercentage = (double)((int)(((double)(numMissed*100)/connectionCheckCount)*100))/100;
			
		} else {
			// Number of Connection Checks during simulation = 0
			
		}
		if (connectionCheckCount-numMissed == 0) {
			// Missed all Connection Checks
			
		} else {
			// Average response time when responded: (double)(connCheckTime/(connectionCheckCount-numMissed)))			
		}
		
		// Total number of Logging Tasks missed
		runStatsRed.co2LogsMissed = logMissedCount;
		runStatsRed.delay = runT.delay;
		
		if (logCount != 0) {
			// Logging Task miss percentage
			runStatsRed.co2LogsMissedPercentage = (double)((int)(((double)(logMissedCount*100)/logCount)*100))/100;
		
		} else {
			// Number of Logging Tasks during simulation = 0
			
		}
		
		String repCompTime, timeCorrResp;
		
		for (int i = 0; i < errorCount; i++) {
			errorStatsList[i].connChecksMissedPercentage = (double)((int)(((double)(errorStatsList[i].connChecksMissed*100)/errorStatsList[i].connChecksOccurred)*100))/100;
			errorStatsList[i].co2LogsMissedPercentage = (double)((int)(((double)(errorStatsList[i].co2LogsMissed*100)/errorStatsList[i].co2LogsOccurred)*100))/100;
			if(errInf[i].repComplete == 99999) {
				repCompTime = "Repair Failed";
				timeCorrResp = "Never";
				errorStatsList[i].correctRepair = 0;
				errorStatsList[i].timeInRed = systemTime - errInf[i].timeOccurred;
				errorStatsList[i].incorrectRepairs = errInf[i].repCount;
			} else {
				repCompTime = Integer.toString(errInf[i].repComplete);
				timeCorrResp = Integer.toString(errInf[i].timeToCorrectResponse);
				errorStatsList[i].timeInRed = errInf[i].repComplete - errInf[i].timeOccurred;
				errorStatsList[i].incorrectRepairs = errInf[i].repCount - 1;
				errorStatsList[i].correctRepair = 1;
			}
			
			errorStatsList[i].Error = errInf[i].errorOccurred;			
		}
		int totalTimeInRed = 0;
		
		for (int i = 0; i < errorCount; i++) {
			feedbackWriter.write(runT.fileName + "," + errorStatsList[i].Error +"," + runT.leg + ","+ runT.delay + "," + runT.medium + ",");
			feedbackWriter.write(errorStatsList[i].correctRepair +","+ errorStatsList[i].timeInRed +","+ errorStatsList[i].incorrectRepairs +",");
			feedbackWriter.write(errorStatsList[i].o2TankClicks +","+ errorStatsList[i].o2FlowMeter +","+ errorStatsList[i].n2TankClicks +","+ errorStatsList[i].n2FlowMeter +","+ errorStatsList[i].mixerFlowMeter +",");
			feedbackWriter.write(errorStatsList[i].co2HistoryGraph +","+ errorStatsList[i].o2HistoryGraph +","+ errorStatsList[i].n2HistoryGraph +","+ errorStatsList[i].tempHistoryGraph +","+ errorStatsList[i].humidityHistoryGraph +","+ errorStatsList[i].flowRates +",");
			feedbackWriter.write(errorStatsList[i].co2LogsMissed + "," + errorStatsList[i].co2LogsMissedPercentage + "," + errorStatsList[i].connChecksMissed + "," +  errorStatsList[i].connChecksMissedPercentage + ",");
			totalTimeInRed = totalTimeInRed + errorStatsList[i].timeInRed;
		}
		if (errorCount < 6) {
			for (int i = (6 - errorCount); i > 0; i--) {
				feedbackWriter.write(",,,,,,,,,,,,,,,,,,,,,,,");
			}
		}
	    runStatsRed.totalTimeInRed = totalTimeInRed;	    
	    
		reader.close();
		feedbackWriter.close();
		return runStatsRed;
	}

	private static RunStatsInRed extractPInfo(RunType runT, File pdata) throws IOException{
		System.out.println(runT.fileName);
		// Read the inFile line by line and print everything to console and outFile.
				BufferedReader reader = new BufferedReader(new FileReader(runT.fileLoc));
				RunStatsInRed runStatsRed = new RunStatsInRed();
				ErrorStats[] errorStatsList = new ErrorStats[15];
				
				String line = null;
				String[] eachLine = null;		
				SystemState state;
				int iconApp = 0, iconClose = 0, iconConf = 0, numMissed = 0;
				iconApp = iconClose;
				int errBegin = 0, repBegin = 99999, repComplete = 0;
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
				int systemTime = 0;
				
				// Ignore first line of the log file
				line = reader.readLine();
				
				// Read every line of the log file and tokenize it. Then extract the relevant values.			
				while((line = reader.readLine()) != null) {
					eachLine = line.split(";");
					if (eachLine.length == 14 ) {
						state = new SystemState(eachLine[0],eachLine[9],eachLine[10],eachLine[11], null, eachLine[13]);
						systemTime = state.time;
						
						// Check if any repair is going on and if it's incorrect repair. Record incorrect repair order.
						if (!wrongRep && (repBegin < 99999) && (repBegin + 60 < state.time)) {
							wrongRep = true;
							wrongRepair[wrongRepCount++] = repair;
							repair = null;
						}
						switch(state.component){
							case "connection_check": if(state.effect.equals("icon_appears")){
														connectionCheckCount++;
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
																	connCheckTime = connCheckTime + iconConf-iconApp;
																	iconApp = iconConf = 0;
																}
													 
													 break;
							case "logging_task": logCount++;
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
							case "ni_tank_display":	// Nitrogen Tank Display Clicked
													if (errorOnGoing) {
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
							case "phase ":  if (state.phase.equals("RED")) {
												errBegin = state.time;
												errorCount++;
												errorOnGoing = true;
												repBegin = 99999;
												repComplete = 0;
											} else if (state.phase.equals("RED_REPAIR") && !wrongRep) {
														errInf[errorCount -1].repairAttempted = true;
														errInf[errorCount -1].repCount++;
														repBegin = state.time;
														errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
													} else if (state.phase.equals("RED_REPAIR") && wrongRep) {
																repBegin = state.time;
																errInf[errorCount -1].repCount++;
																errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
																wrongRep = false;
															} else if (state.phase.equals("RED_NO_ERROR")) {
																		repComplete = state.time;
																		errInf[errorCount -1].repComplete = repComplete;
																		errBegin = repComplete = 0;
																		repBegin = 99999;
																		errorOnGoing = false;
																	}
											
											break;
							case "repair":  if (state.effect.equals("repair task finished")) break;
											else {										
												errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount -1] = state.effect.substring(8);
												repair = state.effect.substring(8);
												correctResponseTime = state.time - errInf[errorCount -1].timeOccurred;										
												if (errInf[errorCount -1].errorOccurred.equals(repair)) {
															errInf[errorCount -1].timeToCorrectResponse = correctResponseTime;
												}
												break;
											}
							default: break;
						}	
						
					}
				}
				
				// Total number of Connection Checks missed				
				runStatsRed.connChecksMissed = numMissed;		
				
				if (connectionCheckCount != 0) {
					// Connection Checks miss percentage
					runStatsRed.connChecksMissedPercentage = (double)((int)(((double)(numMissed*100)/connectionCheckCount)*100))/100;
					
				} else {
					// Number of Connection Checks during simulation = 0
					
				}
				if (connectionCheckCount-numMissed == 0) {
					// Missed all Connection Checks
					
				} else {
					// Average response time when responded: " + (double)(connCheckTime/(connectionCheckCount-numMissed)))
					
				}
								
				runStatsRed.co2LogsMissed = logMissedCount;
				runStatsRed.delay = runT.delay;
				
				if (logCount != 0) {
					// Logging Task miss percentage
					runStatsRed.co2LogsMissedPercentage = (double)((int)(((double)(logMissedCount*100)/logCount)*100))/100;
				
				} else {
					// Number of Logging Tasks during simulation = 0
					
				}
				
				String repCompTime, timeCorrResp;
				
				for (int i = 0; i < errorCount; i++) {
					errorStatsList[i].fileName = runT.fileName;
					errorStatsList[i].leg = runT.leg;
					errorStatsList[i].medium = runT.medium;
					errorStatsList[i].delay = runT.delay;
					errorStatsList[i].connChecksMissedPercentage = (double)((int)(((double)(errorStatsList[i].connChecksMissed*100)/errorStatsList[i].connChecksOccurred)*100))/100;
					errorStatsList[i].co2LogsMissedPercentage = (double)((int)(((double)(errorStatsList[i].co2LogsMissed*100)/errorStatsList[i].co2LogsOccurred)*100))/100;
					if(errInf[i].repComplete == 99999) {
						repCompTime = "Repair Failed";
						timeCorrResp = "Never";
						errorStatsList[i].correctRepair = 0;
						errorStatsList[i].timeInRed = systemTime - errInf[i].timeOccurred;
						errorStatsList[i].incorrectRepairs = errInf[i].repCount;
					} else {
						repCompTime = Integer.toString(errInf[i].repComplete);
						timeCorrResp = Integer.toString(errInf[i].timeToCorrectResponse);
						errorStatsList[i].timeInRed = errInf[i].repComplete - errInf[i].timeOccurred;
						errorStatsList[i].incorrectRepairs = errInf[i].repCount - 1;
						errorStatsList[i].correctRepair = 1;
					}
					
					errorStatsList[i].Error = errInf[i].errorOccurred;					
				}
				int totalTimeInRed = 0;
				for (int i = 0; i < errorCount; i++) {					
					switch(errorStatsList[i].Error) {
						case "NITROGEN_VALVE_LEAK" : FSE_PioneerErrors.pioneerErrors[0] = new ErrorStats();
													 FSE_PioneerErrors.pioneerErrors[0] = errorStatsList[i];
													 break;
						case "OXYGEN_VALVE_STUCK_OPEN" : FSE_PioneerErrors.pioneerErrors[1] = new ErrorStats();
														 FSE_PioneerErrors.pioneerErrors[1] = errorStatsList[i];
														 break;
						case "MIXER_BLOCK" : FSE_PioneerErrors.pioneerErrors[2] = new ErrorStats();
											 FSE_PioneerErrors.pioneerErrors[2] = errorStatsList[i];
											 break;
						case "OXYGEN_SENSOR_STARTS_LOWER_TH" : FSE_PioneerErrors.pioneerErrors[3] = new ErrorStats();
															   FSE_PioneerErrors.pioneerErrors[3] = errorStatsList[i];
															   break;
						default: break;
					}
					totalTimeInRed = totalTimeInRed + errorStatsList[i].timeInRed;
				}
				
			    runStatsRed.totalTimeInRed = totalTimeInRed;
								
				for (int j = 0; j < errorCount; j++) {
					switch (errorStatsList[j].Error) {
						case "NITROGEN_VALVE_LEAK": 
							fsePioneerError[0] = new FSE_PioneerErrors();
							fsePioneerError[0].pioneerError = "NITROGEN_VALVE_LEAK";
							fsePioneerError[0].pioneerErrorType = "S1";
							fsePioneerError[0].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[0].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[0].leg = runT.leg;
							fsePioneerError[0].delay = runT.delay;
							//System.out.println("S1 Begin: " + fsePioneerError[0].timeBegin + "; End: " + fsePioneerError[0].timeEnd);
							break;
						case "OXYGEN_VALVE_STUCK_OPEN" :
							fsePioneerError[1] = new FSE_PioneerErrors();
							fsePioneerError[1].pioneerError = "OXYGEN_VALVE_STUCK_OPEN";
							fsePioneerError[1].pioneerErrorType = "S2";
							fsePioneerError[1].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[1].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[1].leg = runT.leg;
							fsePioneerError[1].delay = runT.delay;
							//System.out.println("S2 Begin: " + fsePioneerError[1].timeBegin + "; End: " + fsePioneerError[1].timeEnd);
							break;
						case "MIXER_BLOCK": 
							fsePioneerError[2] = new FSE_PioneerErrors();
							fsePioneerError[2].pioneerError = "MIXER_BLOCK";
							fsePioneerError[2].pioneerErrorType = "D1";
							fsePioneerError[2].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[2].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[2].leg = runT.leg;
							fsePioneerError[2].delay = runT.delay;
							//System.out.println("D1 Begin: " + fsePioneerError[2].timeBegin + "; End: " + fsePioneerError[2].timeEnd);
							break;
						case "OXYGEN_SENSOR_STARTS_LOWER_TH" :
							fsePioneerError[3] = new FSE_PioneerErrors();
							fsePioneerError[3].pioneerError = "OXYGEN_SENSOR_STARTS_LOWER_TH";
							fsePioneerError[3].pioneerErrorType = "D2";
							fsePioneerError[3].timeBegin = errorStatsList[j].timeOccurred;
							fsePioneerError[3].timeEnd = errorStatsList[j].timeOccurred + errorStatsList[j].timeInRed;
							fsePioneerError[3].leg = runT.leg;
							fsePioneerError[3].delay = runT.delay;
							//System.out.println("D2 Begin: " + fsePioneerError[3].timeBegin + "; End: " + fsePioneerError[3].timeEnd);
							break;
						default: break;
					}							
					
				}
				reader.close();				
				return runStatsRed;
		}

}
