package b01.l3.drivers.alcor.ised;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import b01.foc.Globals;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

public class ISEDFrameCreator extends AstmFrameCreator {
	
	private static final int FRAME_SIZE = 240;
	
  protected void addAndSplitDataFrame(AstmDriver driver, AstmFrame frame){
  	AstmFrame remainingFrame = frame;
  	int sequence = 1;
    while(remainingFrame.getData().length() > FRAME_SIZE){
      AstmFrame splitFrame = new AstmFrame(driver.getInstrument(), sequence++, AstmFrame.FRAME_TYPE_CONTINUITY);
      splitFrame.setIntermediateFrame(true);
      splitFrame.setData(new StringBuffer(remainingFrame.getData().substring(0, FRAME_SIZE)));
      driver.addFrame(splitFrame);
      remainingFrame.getData().replace(0, FRAME_SIZE, "");
    }

    remainingFrame.setSequence(sequence);
    driver.addFrame(remainingFrame);
  }
	
  public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
    
    // ENQ frame
    // -----------
    AstmFrame frame = newEnquiryFrame(driver.getInstrument());
    driver.addFrame(frame);
    // -----------
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    // BIG frame
    frame = new AstmFrame(driver.getInstrument(), 1, AstmFrame.FRAME_TYPE_CONTINUITY);
    
    frame.append2Data("H|\\^&|||Alcor^iSED^01.00A^01||||||||1|"+sdf.format(new Date(System.currentTimeMillis())));
    frame.append2Data(AstmFrame.CR);
    
    // Patient Frame
    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sam = (L3Sample) sIter.next();
      if (sam != null) {
      	
      	//FullName
      	String fullName = "";
      	if(sam.getFirstName() != null && !sam.getFirstName().isEmpty()) {
      		fullName = sam.getFirstName();
      	}
      	if(sam.getLastName() != null && !sam.getLastName().isEmpty()) {
      		if(!fullName.isEmpty()) fullName += " ";
      		fullName += sam.getLastName();
      	}
      	//Birth date
      	String birthDate = sdf.format(sam.getDateOfBirth());
      	
      	//Patient
      	frame.append2Data("P|1||"+sam.getPatientId()+"||"+fullName+"||"+birthDate+"|"+sam.getSexe()+"||||||||||||||||||||||||||");
      	//-------
      	frame.append2Data(AstmFrame.CR);

      	String collectionDate = "";
        if(sam.getEntryDate().getTime() < 24*60*60*1000){
          Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"+sam.getEntryDate().getTime());
          collectionDate = sdf.format(Globals.getApp().getSystemDate());
        }else{
        	collectionDate = sdf.format(sam.getEntryDate());
        }

      	//Order
      	frame.append2Data("O|1|"+sam.getId()+"||^^^ESR|||"+collectionDate+"|||||||"+collectionDate+"||||||||||||||||");
        frame.append2Data(AstmFrame.CR);
      }
      frame.append2Data("L|1|N");
      frame.append2Data(AstmFrame.CR);
    }
   
    addAndSplitDataFrame(driver, frame);
    
    // Last frame
    //frame = AstmFrame.newLastFrame(getInstrument(), getNextSequence(), 1);
    //addFrame(frame);
    // -------------

    // EOT frame
    frame = newEndOfTransmissionFrame(driver.getInstrument());
    driver.addFrame(frame);
    // -------------
  }
}
