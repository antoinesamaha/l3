package b01.l3.drivers.horiba.pentraML;

import java.sql.Date;
import java.util.Calendar;
import java.util.Iterator;

import b01.foc.calendar.FCalendar;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

public class PentraMLFrameCreator extends AstmFrameCreator {
	
	  public String getProfileForTestID(String testLabel){
		  String profile = "CBC";
		  
		  if(testLabel.startsWith("ERB")){
			  profile = "ERB";
		  }else if(		testLabel.startsWith("RET") 
				  || 	testLabel.equals("MFI")
				  || 	testLabel.equals("IRF")
				  || 	testLabel.equals("CRC")
				  || 	testLabel.equals("MRV")
				  || 	testLabel.equals("PIC")				  				  
				  ){
			  profile = "RET";
		  }else if(testLabel.contains("%") || testLabel.contains("#")){
			  profile = "DIFF";
		  }
		  return profile;
	  }

	  @Override
	  public AstmFrame newSpecificComments(Instrument instrument, int sequence, int sequence_num, L3Sample sam) throws Exception{
		  AstmFrame frame = null;

		  
	        boolean urgent = false;
	        Iterator tIter = sam.testIterator(); 
	        while(tIter != null && tIter.hasNext()){
	          L3Test test = (L3Test) tIter.next();
	          if(test != null){
	        	urgent = urgent || test.getPriority().equals("S");
	          }
	        }

			boolean putMLDisabled = urgent;
			if(!putMLDisabled){
				Date date = new Date(System.currentTimeMillis()+Calendar.getInstance().get(Calendar.DST_OFFSET));
				
				FCalendar calendar = FCalendar.getDefaultCalendar();
				if(calendar != null){
					boolean holiday  = calendar.isHoliday(date) || calendar.isWeekEnd(date);
					long currentTimeSinceMidnight = FCalendar.getTimeSinceMidnight(date);    
					boolean workTime = calendar.isWorkTime(currentTimeSinceMidnight);
					if(!workTime || holiday){
						//frame = newCommentFrame(instrument, sequence, sequence_num, sam);
						putMLDisabled = true;
					}
				}
			}
			
			if(putMLDisabled){
				frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_COMMENT);
			    
			    frame.append2Data(AstmFrame.FIELD_SEPERATOR);
			    frame.append2Data(String.valueOf(sequence_num));
			    frame.append2Data(AstmFrame.FIELD_SEPERATOR);
			    frame.append2Data("I");
			    frame.append2Data(AstmFrame.FIELD_SEPERATOR);
			    frame.append2Data("ML DISABLED");
			}
		  
		  return frame;		  
	  }
	  
//	public AstmFrame newPatientFrame (Instrument instrument, int sequence, int sequence_num, String sampleId, String patientId, String firstName, String lastName, String middleName){
//		String concatLastName = lastName;
//		if(firstName != null && firstName.length() > 0){
//			concatLastName = concatLastName + " " + firstName.charAt(0);
//		}
//		return super.newPatientFrame (instrument, sequence, sequence_num, sampleId, patientId, firstName, concatLastName, middleName);
//	}
}
