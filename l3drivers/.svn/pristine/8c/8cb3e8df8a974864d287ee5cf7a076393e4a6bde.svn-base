package b01.l3.drivers.helena.junior24;

import b01.l3.L3Frame;

public class Junior24EmulatorReceiver extends Junior24Receiver_AccordingToDocumentation{
  
  public Junior24EmulatorReceiver(Junior24Driver driver) {
    super(driver);
  }
  
  @Override
  public void parseResultFrame(StringBuffer data) {
  }

  @Override
  protected void initMessage() {
  }

  @Override
  protected void sendMessageBackToInstrument() {
  }

  @Override
  public void received(L3Frame frame) {
    ((Junior24Emulator) getDriver()).getRobot().predefinedFrame_Treat(frame);
    super.received(frame); 
  }
}