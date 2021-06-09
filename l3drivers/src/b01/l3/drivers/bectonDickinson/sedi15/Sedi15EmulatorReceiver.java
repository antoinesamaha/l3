package b01.l3.drivers.bectonDickinson.sedi15;

import b01.l3.L3Frame;

public class Sedi15EmulatorReceiver extends Sedi15Receiver{

  public Sedi15EmulatorReceiver(Sedi15Driver driver) {
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
    ((Sedi15Emulator) getDriver()).getRobot().predefinedFrame_Treat(frame);
    super.received(frame); 
  }


}
