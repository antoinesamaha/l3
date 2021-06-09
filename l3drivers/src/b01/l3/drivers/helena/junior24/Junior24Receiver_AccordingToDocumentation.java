package b01.l3.drivers.helena.junior24;

import java.util.StringTokenizer;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmFrame;

public class Junior24Receiver_AccordingToDocumentation implements L3SerialPortListener{
  protected Junior24Driver   driver  = null;
  protected L3Message    message = null;
  protected L3Sample     sample  = null;
  protected L3Test       test    = null;
  
  private Junior24Frame_AccordingToDocumentation ackFrame = null;
  private Junior24Frame_AccordingToDocumentation nackFrame = null;
  
  public Junior24Receiver_AccordingToDocumentation(Junior24Driver driver){
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
  
  public Junior24Driver getDriver(){
    return driver;
  }
  
  protected void initMessage(){
    message = new L3Message();
  }
  
  protected void sendMessageBackToInstrument(){
    driver.notifyListeners(message);
  }
  
  public void parseResultFrame(StringBuffer data){
    String frameToken = String.valueOf(ASCII.LF);
    StringTokenizer strTokenizer = new StringTokenizer(data.toString(), frameToken, false);
    int position =0;
    while(strTokenizer.hasMoreTokens()){
      String str = strTokenizer.nextToken();
      
      if(position == 1){
        if(sample != null){
          if(sample.getId().compareTo(str.substring(0,4)) != 0){           
            sample = null;
            test = null;
          }
        }
        if(sample == null){
          sample = new L3Sample(str.substring(0,4));
          message.addSample(sample);
        }
      }
      
      if (position > 1){
        test = sample.addTest();
        String testToken = ""+ASCII.SPACE;
        StringTokenizer strTest = new StringTokenizer(str, testToken, true);
          String testLabel = strTest.nextToken();
          String value = strTest.nextToken();
          double testValue = 0.0;
          if (value.compareTo(" ") != 0){
            testValue = Double.valueOf(value.substring(0, value.length() -1));
          }
          if(testLabel != null && testLabel.trim().compareTo("") != 0){
            test.setLabel(testLabel);
            test.setValue(testValue);
          }
      }
      position++; 
    }
  }
  
  protected int treatResultFrame(Junior24Frame_AccordingToDocumentation frame){
    int frameTypeToReturn = Junior24Frame_AccordingToDocumentation.FRAME_TYPE_ACK;
    try{
      frame.extractDataFromFrame();
      StringBuffer data = frame.getData();
      
      if(frame.getType() == Junior24Frame.FRAME_TYPE_DATA){
        if(driver.reserve()){
          frameTypeToReturn = Junior24Frame_AccordingToDocumentation.FRAME_TYPE_NACK;
        }else{
          driver.reserve();
          initMessage();
          parseResultFrame(data);
          frameTypeToReturn = Junior24Frame_AccordingToDocumentation.FRAME_TYPE_ACK;
          sendMessageBackToInstrument();
          disposeMessage();
          driver.release();
        }
      }
    }catch(Exception e){
      driver.getInstrument().logException(e);
      frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
    }
    return frameTypeToReturn;
  }
  
  public void received(L3Frame frame) {
    try{
      Junior24Frame_AccordingToDocumentation f = (Junior24Frame_AccordingToDocumentation)frame;
      if(f != null){
        switch(treatResultFrame(f)){
        case Junior24Frame_AccordingToDocumentation.FRAME_TYPE_ACK:
          driver.send(getAckFrame().getDataWithFrame().toString());
          break;
        case Junior24Frame_AccordingToDocumentation.FRAME_TYPE_NACK:
          driver.send(getNackFrame().getDataWithFrame().toString());
          break;          
        case Junior24Frame_AccordingToDocumentation.FRAME_TYPE_NONE:
          break;
        }
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
  
  private Junior24Frame_AccordingToDocumentation getAckFrame(){ 
    if(ackFrame == null){
      ackFrame = Junior24Frame_AccordingToDocumentation.newAckFrame(driver.getInstrument());
      try{
        ackFrame.createDataWithFrame();
      }catch(Exception e){
        ackFrame.logException(e);
      }
    }
    return ackFrame;
  }
  
  private Junior24Frame_AccordingToDocumentation getNackFrame(){ 
    if(nackFrame == null){
      nackFrame = Junior24Frame_AccordingToDocumentation.newNackFrame(driver.getInstrument());
      try{
        nackFrame.createDataWithFrame();
      }catch(Exception e){
        nackFrame.logException(e);
      }
    }
    return nackFrame;
  }
}
