/*
 * Created on Jun 14, 2006
 */
package b01.l3.drivers.kermit;

import java.util.Calendar;
import java.util.StringTokenizer;

import b01.foc.Globals;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;

/**
 * @author 01Barmaja
 */
public class Vitros250Receiver implements L3SerialPortListener{

  protected Vitros250Driver driver     = null;
  protected L3Message       message    = null;
  protected L3Sample        sample     = null;
  protected StringBuffer    testString = null;
  protected StringBuffer    dataMultipleLinesConcatenation = null;
  
  public Vitros250Receiver(Vitros250Driver driver){
    this.driver = driver;
    message    = null;
    sample     = null;
    testString = null;
  }
  
  public void dispose(){
    driver = null;
    if(message != null){
      message.dispose();
      message = null;
    }
    if(sample != null){
      sample.dispose();
      sample = null;
    }
    testString = null;    
  }
  
  private void parseTestString(){
    if(testString != null){
      int n = 0;
      driver.getInstrument().logString("Test String to parse:"+testString);
      
      StringTokenizer tokenizer = new StringTokenizer(testString.toString(), "}");
      while(tokenizer.hasMoreTokens()){
      	String token = tokenizer.nextToken();

        char id     = token.charAt(n+0);
        String res  = token.substring(n+1, n+10);
        String err  = token.substring(n+11, n+12);
        driver.getInstrument().logString("  id="+id+" res="+res+" err="+err);
        
        String testLabel = driver.getTestLabelFromIndex(id - 32);
        if(testLabel != null && testLabel.compareTo("") != 0){
        	L3Test test = sample.addTest();
					test.setLabel(testLabel);							
          test.setResultOk(true);
          test.setValue(0);
          if(res.compareTo("NO RESULT") == 0){
            test.setResultOk(false);
            test.setValue(0);
          }else{
          	/*Markk results as OK even if we have an error
	          if(err.compareTo("0") != 0){
	            test.setResultOk(false);
	          }*/
            String resT = res.trim();
            double resD = 0;
            if(resT != null && resT.compareTo("") != 0){
            	resD = Double.valueOf(resT).doubleValue();
            }
            test.setValue(resD);
          }
        }
      }
    }
  }
  
  protected int treatResultFrame(KermitFrame frame){
  	int frameTypeToReturn = KermitFrame.FRAME_TYPE_YES;
  	if(frame.getType() == KermitFrame.FRAME_TYPE_ERROR){
  		frameTypeToReturn = KermitFrame.FRAME_TYPE_NONE;
  	}else if(frame.getType() == KermitFrame.FRAME_TYPE_FILE_START){
      dataMultipleLinesConcatenation = new StringBuffer("");
  	}else if(frame.getType() == KermitFrame.FRAME_TYPE_SESSION_START){
      driver.reserve();
      message = new L3Message();
    }else if(frame.getType() == KermitFrame.FRAME_TYPE_BREAK){
      driver.release();
    }else if(frame.getType() == KermitFrame.FRAME_TYPE_DATA){
    	dataMultipleLinesConcatenation.append(frame.getData());    	
    }else if(frame.getType() == KermitFrame.FRAME_TYPE_FILE_END){
    	
      StringBuffer data = dataMultipleLinesConcatenation;
      String stringContainingTests = null;
        
      if(sample == null){
      	int indexOfDiesesToReplace = -1;
      	do{
	      	indexOfDiesesToReplace = data.indexOf("##");
	      	if(indexOfDiesesToReplace >= 0){
	      		data.replace(indexOfDiesesToReplace, indexOfDiesesToReplace+2, "#");
	      	}
      	}while(indexOfDiesesToReplace >= 0);
      	
      	driver.getInstrument().logString("Data String to parse:"+data);
        //In this case we know we are starting a new sample
        String hoursStr = data.substring(0, 2);
        String minStr = data.substring(2, 4);
        String secStr = data.substring(4, 6);
        String monthsStr = data.substring(6, 8);
        String dayStr = data.substring(8, 10);
        String sampleID = data.substring(25, 40);
        String fluidStr = data.substring(40, 41);

        int hours = Integer.valueOf(hoursStr);
        int min = Integer.valueOf(minStr);
        int sec = Integer.valueOf(secStr);
        int months = Integer.valueOf(monthsStr);
        int day = Integer.valueOf(dayStr);
        int fluid = Integer.valueOf(fluidStr);
        switch(fluid){
        case 1:
          fluid = L3Sample.LIQUID_TYPE_SERUM;
          break;
        case 2:
          fluid = L3Sample.LIQUID_TYPE_CSF;
          break;
        case 3:
          fluid = L3Sample.LIQUID_TYPE_URIN;
          break;
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), months, day, hours, min, sec);
        
        long dateAndTime = calendar.getTime().getTime();
                
        sampleID = sampleID.trim();
        if(sampleID.endsWith("Y") || sampleID.endsWith("y")){
        	String temp = sampleID;
        	sampleID = temp.substring(0, temp.length()-1);
        }
        sample = new L3Sample(sampleID.trim());
        sample.setDateAndTime(dateAndTime);
        sample.setLiquidType(fluid);
        message.addSample(sample);
        
        testString = new StringBuffer();
        
        stringContainingTests = data.substring(49, data.length()); 
      }else{
        stringContainingTests = data.substring(0, data.length());
      }
      
      int endOfSample = stringContainingTests.indexOf("]");
      int endOfTest = stringContainingTests.indexOf("|");
      
      int end = endOfTest >= 0 ? endOfTest : endOfSample;
        
      if(end >= 0){
        stringContainingTests = stringContainingTests.substring(0, end);
      }
      
      testString.append(stringContainingTests);
      
      if(end >= 0){      	
        parseTestString();
        driver.notifyListeners(message);
        sample     = null;
        testString = null;
      }
      
    }
    return frameTypeToReturn;
  }
  
  /* (non-Javadoc)
   * @see b01.l3.connection.L3SerialPortListener#received(b01.l3.L3Frame)
   */
  public void received(L3Frame frame) {
    try{
      KermitFrame kf = (KermitFrame)frame;
      if(kf != null){
        kf.extractDataFromFrame();
        
        switch(treatResultFrame(kf)){
        case KermitFrame.FRAME_TYPE_YES:
          driver.sendYesFrame(kf.getSequence(), kf.getType() == KermitFrame.FRAME_TYPE_SESSION_START);          
          break;
        default:
        	
          //driver.sendYesFrame(kf.getSequence(), kf.getType() == KermitFrame.FRAME_TYPE_SESSION_START);          
          break;        	
          
        }
     }
    }catch(Exception e){
    	Globals.logException(e);
    }
  }
}