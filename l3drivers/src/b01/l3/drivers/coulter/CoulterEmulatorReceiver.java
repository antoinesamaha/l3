package b01.l3.drivers.coulter;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.L3Frame;

public class CoulterEmulatorReceiver extends CoulterReceiver {
  
	String dleFrame = null;
	
  public CoulterEmulatorReceiver(CoulterDriver driver){
    super(driver);
    dleFrame = ""+ASCII.DLE+'A';
  }

  protected char get_ENQ_or_SYN_ForReplyToStartComunication(){
    return CoulterFrame.FRAME_TYPE_ENQ;
  }
  
  protected void afterLastAckSent() throws Exception {
    super.afterLastAckSent();
    if(lastAckSent){
    	try{
    		Thread.sleep(50);
    	}catch(Exception e){
    		Globals.logException(e);
    	}
  		driver.send(dleFrame);
    }
  }
  
  public void received(L3Frame frame) {
    ((CoulterEmulator) getDriver()).getRobot().predefinedFrame_Treat(frame);
    super.received(frame);
  }
  
  protected void parseDataFrame(StringBuffer data){
  }
  
  protected void initMessage(){
  }
  
  protected void sendMessageBackToInstrument(){
  }
  
}