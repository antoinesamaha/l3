package b01.l3.drivers.bectonDickinson.sedi15;

import b01.foc.Globals;
import b01.l3.InstrumentDesc;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;

public class Sedi15Receiver implements L3SerialPortListener{
  protected Sedi15Driver driver  = null;
  protected L3Message    message = null;
  protected L3Sample     sample  = null;
  protected L3Test       test    = null;
  
  private Sedi15Frame ackFrame  = null;
  private Sedi15Frame nackFrame = null;
  
  public Sedi15Receiver (Sedi15Driver driver){
    this.driver = driver;
    message     = null;
    sample      = null;
    test        = null;
  }
  
  public void disposeMessage(){
    if(message != null){
      message.dispose();      
    }
    message = null;
    sample  = null;
    test    = null;
  }
  
  public void dispose(){
    driver = null;

    disposeMessage();

    if(ackFrame != null){
      ackFrame.dispose();
      ackFrame = null;
    }
    
    if(nackFrame != null){
      nackFrame.dispose();
      nackFrame = null;
    }
  }

  public Sedi15Driver getDriver(){
    return driver;
  }
  
  private Sedi15Frame getAckFrame(){ 
    if(ackFrame == null){
      ackFrame = Sedi15Frame.newAcknowlegeFrame(driver.getInstrument());
      try{
        ackFrame.createDataWithFrame();
      }catch(Exception e){
        ackFrame.logException(e);
      }
    }
    return ackFrame;
  }

  private Sedi15Frame getNackFrame(){ 
    if(nackFrame == null){
      nackFrame = Sedi15Frame.newNotAcknowlegeFrame(driver.getInstrument());
      try{
        nackFrame.createDataWithFrame();
      }catch(Exception e){
        nackFrame.logException(e);
      }
    }
    return nackFrame;
  }
  
  protected void initMessage(){
  	disposeMessage();
    message = new L3Message();
  }
  
  protected void sendMessageBackToInstrument(){
    driver.notifyListeners(message);
  }
  
  private void parseOneTest(L3Sample sample, String sediTestCode, String valueStr){
  	String lisTest = driver.testMaps_getLisCode(sediTestCode);
  	if(lisTest != null && lisTest.trim().compareTo("") != 0){
      L3Test test = sample.addTest();
  		test.setLabel(lisTest);
  		boolean resultOk = false;
  		double value = 0;
  		String message = "";
  		try{
  			value = Double.valueOf(valueStr);
  			resultOk = true;
  		}catch(Exception e){
  			driver.getInstrument().logException(e);
  			message = "Could not convert to numerical "+lisTest+" value : "+valueStr;
  			resultOk = false;
  		}
  		test.setValue(value);
  		test.setResultOk(resultOk);
  		test.setNotificationMessage(message);
  	}
  }
  
  public void parseResultFrame(StringBuffer data){
    String sampleID = data.substring(0, 17);
    sampleID = sampleID.trim();
    
    String res1 = data.substring(23, 27);
    res1 = res1.trim();
    
    String res2 = data.substring(29, 33);
    res2 = res2.trim();

    if(sampleID != null && sampleID.trim().compareTo("") != 0){
    	
      L3Sample sample = new L3Sample(sampleID);
      
      parseOneTest(sample, Sedi15Driver.SEDI_TEST_AFTER_1_HOUR, res1);
      parseOneTest(sample, Sedi15Driver.SEDI_TEST_AFTER_2_HOUR, res2);
      message.addSample(sample);
    }
  }

  protected int treatResultFrame(Sedi15Frame frame){
    int frameTypeToReturn = Sedi15Frame.FRAME_TYPE_ACK;
    try{
      frame.extractDataFromFrame();
      
      StringBuffer data = frame.getData();
      if(frame.getType() == Sedi15Frame.FRAME_TYPE_SOH){
        if(driver.reserve()){
          frameTypeToReturn = Sedi15Frame.FRAME_TYPE_NACK;
        }else{
          frameTypeToReturn = Sedi15Frame.FRAME_TYPE_ACK;
          initMessage();
        }
      }else if(frame.getType() == Sedi15Frame.FRAME_TYPE_EOT){
        frameTypeToReturn = Sedi15Frame.FRAME_TYPE_ACK;
        sendMessageBackToInstrument();
        disposeMessage();
        driver.release();
      }else if(frame.getType() == Sedi15Frame.FRAME_TYPE_RESULT){
        parseResultFrame(data);
      }else{
        frameTypeToReturn = Sedi15Frame.FRAME_TYPE_NACK;
      }
    }catch(Exception e){
      driver.getInstrument().logException(e);
      frameTypeToReturn = Sedi15Frame.FRAME_TYPE_NACK;
    }
    return frameTypeToReturn;
  }
  
  public void received(L3Frame frame) {
    try{
      Sedi15Frame f = (Sedi15Frame)frame;
      long timeOut = (long)driver.getInstrument().getPropertyInteger(InstrumentDesc.FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE);
      if(f != null){
        switch(treatResultFrame(f)){
        case Sedi15Frame.FRAME_TYPE_ACK:
          //driver.sendWithAnswerTimeOut(getAckFrame().getDataWithFrame().toString(), timeOut);
        	driver.send(getAckFrame().getDataWithFrame().toString());
        	//driver.send(getAckFrame().getDataWithFrame().toString());
          break;
        case Sedi15Frame.FRAME_TYPE_NACK:
          //driver.sendWithAnswerTimeOut(getNackFrame().getDataWithFrame().toString(), timeOut);
        	driver.send(getNackFrame().getDataWithFrame().toString());
          break;          
        case Sedi15Frame.fRAME_TYPE_NONE:
          break;
        }
      }
    }catch(Exception e){
       Globals.logException(e);
    }
  }
}
