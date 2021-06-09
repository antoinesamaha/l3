package b01.l3.drivers.dadeBehring.bct;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.l3.Instrument;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

public class BCTFrameCreator extends AstmFrameCreator{
  protected int frameSequence = 0;

	public BCTFrameCreator(){
	}
	
	public void dispose(){
	}
	
  protected void resetSequence(){
    frameSequence = -1;
  }
  
  protected int getNextSequence(){
    frameSequence++;
    if(frameSequence == 8) frameSequence = 0;
    return frameSequence;
  }
	
  public AstmFrame newEnquiryFrame(Instrument instrument){
    BCTFrame frame = new BCTFrame(instrument, AstmFrame.SEQUENCE_IRRELEVANT, AstmFrame.FRAME_TYPE_ENQ);
    return frame;
  }
  
  public AstmFrame newOrderFrame (Instrument instrument, int sequence, L3Sample sample, String testString) throws Exception{
    BCTFrame frame = new BCTFrame(instrument, sequence, AstmFrame.FRAME_TYPE_NONE);
    
    frame.append2Data("J ");
    
    //BARCODE
    String sampleID = sample.getId();// +"-"+sample.getFirstName()+"-"+sample.getLastName();
    frame.append2Data(sampleID, 23);
    frame.append2Data(ASCII.SPACE);

    ///////////////////////
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    if(sample.getEntryDate().getTime() < 24*60*60*1000){
      Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"+sample.getEntryDate().getTime());
      frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
    }else{
      frame.append2Data(sdf.format(sample.getEntryDate()));
    }
    
    frame.append2Data(",");
    
    sdf = new SimpleDateFormat("HH:mm");
    frame.append2Data(sdf.format(sample.getEntryDate()));
    frame.append2Data(ASCII.SPACE);
    /////////////
    
    frame.append2Data("1");	// additionnal request
    frame.append2Data(ASCII.SPACE);

    frame.append2Data(testString);
    return frame;
  }

  public AstmFrame newEndOfTransmissionFrame(Instrument instrument){
    AstmFrame frame = new AstmFrame(instrument, AstmFrame .SEQUENCE_IRRELEVANT, AstmFrame .FRAME_TYPE_EOT);
    return frame;
  }
  
  public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
    resetSequence();
    
    // ENQ frame
    // -----------
    getNextSequence();
    AstmFrame frame = newEnquiryFrame(driver.getInstrument());
    driver.addFrame(frame);
    // -----------
    
    // Patient Frame
    int sampleSequence = 1;

    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sam = (L3Sample) sIter.next();
      if (sam != null) {

      	String testString = "";

      	Iterator tIter = sam.testIterator(); 
        while(tIter != null && tIter.hasNext()){
          L3Test test = (L3Test) tIter.next();
          if(test != null){
            // Order Frame
            String instrCode = driver.testMaps_getInstCode(test.getLabel());
            if(instrCode != null){
            	testString += instrCode+" ";
            }
          }
        }

        frame = newOrderFrame(driver.getInstrument(), getNextSequence(), sam, testString.trim());
        driver.addFrame(frame);
        
        sampleSequence++;
      }
    }

    // EOT frame
    frame = newEndOfTransmissionFrame(driver.getInstrument());
    driver.addFrame(frame);
    // -------------
  }
}
