package datax;

import java.io.*;
import java.util.*;

public class Extractor {
	public static void extract(Run run, File fsedata, File pdata) {
		RunType runT1 = new RunType(run.fse1, 1);
		RunType runT2 = new RunType(run.fse2, 2);
		RunType runT3 = new RunType(run.p1, 1);
		RunType runT4 = new RunType(run.p2, 2);
		
		RunStatsInRed runStatsRed = new RunStatsInRed();
		
// Call the Data Extractor Function
		
			try {
				extractFSEInfo(runT1, fsedata, runStatsRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			try {
				extractFSEInfo(runT2, fsedata, runStatsRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			
			try {
				extractPInfo(runT3, pdata, runStatsRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
			try {
				extractPInfo(runT4, pdata, runStatsRed);
			} catch (IOException ioe) {
				System.err.println(ioe);
				return;
			}
		}		
	
	private static void extractFSEInfo(RunType runT, File fsedata, RunStatsInRed runStatsRed) throws IOException{
// Read the inFile line by line and print everything to console and outFile.
		BufferedReader reader = new BufferedReader(new FileReader(runT.fileLoc));
		BufferedWriter feedbackWriter = new BufferedWriter(new FileWriter(fsedata, true));
		
		ErrorStats[] errorStatsList = new ErrorStats[15];
		
		
		feedbackWriter.write(runT.fileName + ",," + runT.leg + ","+ runT.delay + "," + runT.medium + "," );
		
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
		String[] wrongRepair = new String[10];
		String repair = null;
		int errorCount = 0;
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
					case "connection_check": feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);	
											 connectionCheckCount++;
											 if(state.effect.equals("icon_appears")){
												iconApp = state.time;
												iconClose = 0;
											 } else if (state.effect.equals("icon_closed")) {
														iconClose = state.time;
														numMissed++;
														iconApp = 0;
													}else if (state.effect.equals("confirmed")) {
															iconConf = state.time;
															feedbackWriter.write(";Time to respond: " + (iconConf-iconApp));
															connCheckTime = connCheckTime + iconConf-iconApp;
															iconApp = iconConf = 0;
														}
											 
											 break;
					case "logging_task": feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);
										 
										 logCount++;
										 if (state.effect.equals("missed"))
											logMissedCount++;
										 continue;
					case "detector": errInf[errorCount -1] = new ErrorInfo(state.time, state.effect);
									 break;
					default: break;
				}
				
				switch(state.effect.substring(0,6)) {
					case "phase ": feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);									
									if (state.phase.equals("RED")) {
										errBegin = state.time;
										errorCount++;
										repBegin = 9999;
										repComplete = 0;
									} else if (state.phase.equals("RED_REPAIR") && !wrongRep) {
												errInf[errorCount -1].repairAttempted = true;
												errInf[errorCount -1].repCount++;
												repBegin = state.time;
												errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
												feedbackWriter.write(";Time to respond: " + errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1]);												
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
															}
									
									break;
					case "repair":  if (state.effect.equals("repair task finished")) break;
									else {										
										feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);										
										//System.out.println(errInf[errorCount -1].repCount);
										//System.out.println(state.effect.substring(7));									
										errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount -1] = state.effect.substring(8);
										//System.out.println(errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount]);
										repair = state.effect.substring(8);
										correctResponseTime = state.time - errInf[errorCount -1].timeOccurred;										
										if (errInf[errorCount -1].errorOccurred.equals(repair)) {
													feedbackWriter.write(";Time for Correct Reponse: " + correctResponseTime);
													errInf[errorCount -1].timeToCorrectResponse = correctResponseTime;
										}
										//System.out.println(repair + ":" + errInf[errorCount -1].errorOccurred);
										
										break;
									}
					default: break;
				}	
				
			}
		}
		
		feedbackWriter.write("SUMMARY");
		
		
		feedbackWriter.write("Total number of Connection Checks missed: "+numMissed);
		
		
		if (connectionCheckCount != 0) {
			feedbackWriter.write("Connection Checks miss percentage: " + (double)((numMissed*100)/connectionCheckCount) + "%");
			
		} else {
			feedbackWriter.write("Number of Connection Checks during simulation = 0");
			
		}
		if (connectionCheckCount-numMissed == 0) {
			feedbackWriter.write("Missed all Connection Checks");
			
		} else {
			feedbackWriter.write("Average response time when responded: " + (double)(connCheckTime/(connectionCheckCount-numMissed)));
			
		}
		
		feedbackWriter.write("Total number of Logging Tasks missed: "+logMissedCount);
		
		if (logCount != 0) {
			feedbackWriter.write("Logging Task miss percentage: " + (double)((logMissedCount*100)/logCount) + "%");
		
		} else {
			feedbackWriter.write("Number of Logging Tasks during simulation = 0");
			
		}
		
		//System.out.println("Error \t"+"Occured At "+" Time to Respond "+" No. of Repairs Ordered "+" Time to Complete "+" Repair Ordered");
		//System.out.println(errInf[0].errorOccurred + "; " + errInf[0].timeOccurred + "; " + errInf[0].timeToRespond + "; " + (errInf[0].repCount) + "; " + errInf[0].repComplete + "; " + errInf[0].repairOrdered[0] + "; " + errInf[0].repairOrdered[1] );
		//feedbackWriter.write("ERROR OCCURRED;"+"AT;"+"CORRECT REPAIR AT;"+"NUM REPAIRS;"+"TIME TO COMPLETE");
		
		String repCompTime, timeCorrResp;
		for (int i = 0; i < errorCount; i++) {
			errorStatsList[i] = new ErrorStats();
			if(errInf[i].repComplete == 9999) {
				repCompTime = "Repair Failed";
				timeCorrResp = "Never";
				errorStatsList[i].correctRepair = false;
			} else {
				repCompTime = Integer.toString(errInf[i].repComplete);
				timeCorrResp = Integer.toString(errInf[i].timeToCorrectResponse);
				errorStatsList[i].timeInRed = errInf[i].timeToCorrectResponse - errInf[i].timeOccurred;
				errorStatsList[i].incorrectRepairs = errInf[i].repCount;
			}
			//System.out.println(errInf[i].errorOccurred + ";" + errInf[i].timeOccurred + ";" + timeCorrResp + ";" + (errInf[i].repCount) + ";" + repCompTime);
			feedbackWriter.write("Error Occurred: "+errInf[i].errorOccurred + "; At: " + errInf[i].timeOccurred + "; Time for Correct Repair: " + timeCorrResp + "; Number of Repairs Attempted: " +  (errInf[i].repCount) + "; Repair Completed At: " + repCompTime);
			
			errorStatsList[i].Error = errInf[i].errorOccurred;
			
			
				for (int j = 0; j < errInf[i].repCount; j++) {
					feedbackWriter.write("At "+errInf[i].timeToRespond[j] + " seconds; Attempted Repair: " + errInf[i].repairOrdered[j]);
					
				}
			
		}
		reader.close();
		feedbackWriter.close();
		//clicksWriter.close();
		return;
	}

	private static void extractPInfo(RunType runT, File pdata, RunStatsInRed runStatsRed) throws IOException{
		// Read the inFile line by line and print everything to console and outFile.
				BufferedReader reader = new BufferedReader(new FileReader(runT.fileLoc));
				BufferedWriter feedbackWriter = new BufferedWriter(new FileWriter(pdata, true));
				
				feedbackWriter.write("");
				
				
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
				String[] wrongRepair = new String[10];
				String repair = null;
				int errorCount = 0;
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
							case "connection_check": feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);	
													 connectionCheckCount++;
													 if(state.effect.equals("icon_appears")){
														iconApp = state.time;
														iconClose = 0;
													 } else if (state.effect.equals("icon_closed")) {
																iconClose = state.time;
																numMissed++;
																iconApp = 0;
															}else if (state.effect.equals("confirmed")) {
																	iconConf = state.time;
																	feedbackWriter.write(";Time to respond: " + (iconConf-iconApp));
																	connCheckTime = connCheckTime + iconConf-iconApp;
																	iconApp = iconConf = 0;
																}
													 
													 break;
							case "logging_task": feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);
												 
												 logCount++;
												 if (state.effect.equals("missed"))
													logMissedCount++;
												 continue;
							case "detector": errInf[errorCount -1] = new ErrorInfo(state.time, state.effect);
											 break;
							default: break;
						}
						
						switch(state.effect.substring(0,6)) {
							case "phase ": feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);									
											if (state.phase.equals("RED")) {
												errBegin = state.time;
												errorCount++;
												repBegin = 9999;
												repComplete = 0;
											} else if (state.phase.equals("RED_REPAIR") && !wrongRep) {
														errInf[errorCount -1].repairAttempted = true;
														errInf[errorCount -1].repCount++;
														repBegin = state.time;
														errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1] = repBegin - errBegin;
														feedbackWriter.write(";Time to respond: " + errInf[errorCount -1].timeToRespond[errInf[errorCount -1].repCount -1]);												
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
																	}
											
											break;
							case "repair":  if (state.effect.equals("repair task finished")) break;
											else {										
												feedbackWriter.write(state.time+";"+state.component+";"+state.effect+";"+state.phase);										
												//System.out.println(errInf[errorCount -1].repCount);
												//System.out.println(state.effect.substring(7));									
												errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount -1] = state.effect.substring(8);
												//System.out.println(errInf[errorCount -1].repairOrdered[errInf[errorCount -1].repCount]);
												repair = state.effect.substring(8);
												correctResponseTime = state.time - errInf[errorCount -1].timeOccurred;										
												if (errInf[errorCount -1].errorOccurred.equals(repair)) {
															feedbackWriter.write(";Time for Correct Reponse: " + correctResponseTime);
															errInf[errorCount -1].timeToCorrectResponse = correctResponseTime;
												}
												//System.out.println(repair + ":" + errInf[errorCount -1].errorOccurred);
												
												break;
											}
							default: break;
						}	
						
					}
				}
				
				feedbackWriter.write("SUMMARY");
				
				
				feedbackWriter.write("Total number of Connection Checks missed: "+numMissed);
				
				
				if (connectionCheckCount != 0) {
					feedbackWriter.write("Connection Checks miss percentage: " + (double)((numMissed*100)/connectionCheckCount) + "%");
					
				} else {
					feedbackWriter.write("Number of Connection Checks during simulation = 0");
					
				}
				if (connectionCheckCount-numMissed == 0) {
					feedbackWriter.write("Missed all Connection Checks");
					
				} else {
					feedbackWriter.write("Average response time when responded: " + (double)(connCheckTime/(connectionCheckCount-numMissed)));
					
				}
				
				feedbackWriter.write("Total number of Logging Tasks missed: "+logMissedCount);
				
				if (logCount != 0) {
					feedbackWriter.write("Logging Task miss percentage: " + (double)((logMissedCount*100)/logCount) + "%");
				
				} else {
					feedbackWriter.write("Number of Logging Tasks during simulation = 0");
					
				}
				
				//System.out.println("Error \t"+"Occured At "+" Time to Respond "+" No. of Repairs Ordered "+" Time to Complete "+" Repair Ordered");
				//System.out.println(errInf[0].errorOccurred + "; " + errInf[0].timeOccurred + "; " + errInf[0].timeToRespond + "; " + (errInf[0].repCount) + "; " + errInf[0].repComplete + "; " + errInf[0].repairOrdered[0] + "; " + errInf[0].repairOrdered[1] );
				//feedbackWriter.write("ERROR OCCURRED;"+"AT;"+"CORRECT REPAIR AT;"+"NUM REPAIRS;"+"TIME TO COMPLETE");
				
				String repCompTime, timeCorrResp;
				for (int i = 0; i < errorCount; i++) {
					if(errInf[i].repComplete == 9999) {
						repCompTime = "Repair Failed";
						timeCorrResp = "Never";
					} else {
						repCompTime = Integer.toString(errInf[i].repComplete);
						timeCorrResp = Integer.toString(errInf[i].timeToCorrectResponse);
					}
					//System.out.println(errInf[i].errorOccurred + ";" + errInf[i].timeOccurred + ";" + timeCorrResp + ";" + (errInf[i].repCount) + ";" + repCompTime);
					feedbackWriter.write("Error Occurred: "+errInf[i].errorOccurred + "; At: " + errInf[i].timeOccurred + "; Time for Correct Repair: " + timeCorrResp + "; Number of Repairs Attempted: " +  (errInf[i].repCount) + "; Repair Completed At: " + repCompTime);
					
						for (int j = 0; j < errInf[i].repCount; j++) {
							feedbackWriter.write("At "+errInf[i].timeToRespond[j] + " seconds; Attempted Repair: " + errInf[i].repairOrdered[j]);
							
						}
					
				}
				reader.close();
				feedbackWriter.close();
				//clicksWriter.close();
				return;
			}

}
