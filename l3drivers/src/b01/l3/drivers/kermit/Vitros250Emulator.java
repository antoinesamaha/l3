package b01.l3.drivers.kermit;

import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.drivers.unit.EmulatorDriver;

public class Vitros250Emulator extends EmulatorDriver {
	
  public void init(Instrument instrument, Properties props) throws Exception {
  	super.init(instrument, props);
    setAnswerFrame(new KermitFrame(getInstrument()));
  }
  
	public void replyToFrame(L3Frame frame){
		try{
			KermitFrame kFrame = (KermitFrame) frame ; 
			if(kFrame != null){
				kFrame.extractDataFromFrame();
				switch(kFrame.getType()){
				case KermitFrame.FRAME_TYPE_SESSION_START:{
					KermitFrame resFrame = new KermitFrame(getInstrument(), kFrame.getSequence(), KermitFrame.FRAME_TYPE_YES);
					resFrame.append2Data("~  @"+(char)45+"#N1");
					resFrame.createDataWithFrame();
					send(resFrame.getDataWithFrame().toString());
					}					
				break;
				default:{
					KermitFrame resFrame = new KermitFrame(getInstrument(), kFrame.getSequence(), KermitFrame.FRAME_TYPE_YES);
					resFrame.createDataWithFrame();
					send(resFrame.getDataWithFrame().toString());					
					}
				break;
				}
			}
		}catch(Exception e){
			getInstrument().logException(e);
		}
	}
}
