package b01.l3.drivers.astm;

import b01.l3.L3Frame;

public class AstmEmulatorReceiver extends AstmReceiver{

	public AstmEmulatorReceiver(AstmEmulator driver) {
		super(driver);
	}

  protected void parsePatientFrame(StringBuffer data){
  }

  protected void parseOrderFrame(StringBuffer data){
  }
  
  protected void parseResultFrame(StringBuffer data){
  }
  
  protected void initMessage(){
  }
  
  protected void sendMessageBackToInstrument(){
  }
  
  public void received(L3Frame frame) {
    ((AstmEmulator) getDriver()).getRobot().predefinedFrame_Treat(frame);
    super.received(frame); 
  }
}
