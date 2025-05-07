package b01.l3.drivers.vitek.bci;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import b01.foc.list.FocList;
import b01.foc.util.ASCII;
import b01.l3.Instrument;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

/**
 * 
 * @author Antoine SAMAHA
 * 
 * mtmpr|pi324234|pnBrown|pb1996/12/02|psM|soLAB1|si|ci123123|rtHIV|qd1
 *
 */
public class VitekBCIFrameCreator extends AstmFrameCreator{

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	public VitekBCIFrameCreator(){
	}
  
  public VitekBCIFrame newLastFrame(Instrument instrument){
  	VitekBCIFrame frame = new VitekBCIFrame(instrument, AstmFrame.FRAME_TYPE_LAST);
    return frame;
  }

  public VitekBCIFrame newEndOfTransmissionFrame(Instrument instrument){
  	VitekBCIFrame frame = new VitekBCIFrame(instrument, AstmFrame .FRAME_TYPE_EOT);
    return frame;
  }
  
  public VitekBCIFrame newOrderFrame(Instrument instrument, L3Sample sam){
  	VitekBCIFrame frame = new VitekBCIFrame(instrument, AstmFrame.FRAME_TYPE_ORDER);

  	StringBuffer buffer = new StringBuffer();
  	
    FocList testList = sam.getTestList();
    for(int t=0; t<testList.size(); t++) {
    	L3Test test = (L3Test) testList.getFocObject(t);

    	//mtmpr|pi34234|pnBrown|pb1996/12/02|psM|soLAB1|si|ci123123|rtHIV|qd1
    	buffer.append(ASCII.RS);
    	buffer.append("mtmpr");
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("pi");
    	buffer.append(sam.getPatientId());
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("pn");
    	buffer.append(sam.getFirstName()+" "+sam.getLastName());
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("pb");
    	buffer.append(sdf.format(sam.getDateOfBirth()));
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("ps");
    	buffer.append(sdf.format(sam.getSexe()));
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("so");
    	buffer.append("L3");
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("si");
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("ci");
    	buffer.append(sam.getId());
    	buffer.append(AstmFrame.FIELD_SEPERATOR);
    	
    	buffer.append("rt");
    	buffer.append(test.getLabel());
    	buffer.append(AstmFrame.FIELD_SEPERATOR);

    	buffer.append("qd1");
    }
    
    frame.setData(buffer);
  	
    return frame;
  }
  
  public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sam = (L3Sample) sIter.next();
      if (sam != null) {
        // ENQ frame
        // -----------
        AstmFrame frame = newEnquiryFrame(driver.getInstrument());
        driver.addFrame(frame);
        // -----------

        frame = newOrderFrame(driver.getInstrument(), sam);
        driver.addFrame(frame);
        
        // <ETX> frame
        // ----------------
        frame = newLastFrame(driver.getInstrument());
        driver.addFrame(frame);
        // ----------------
        
        // <EOT> frame
        // ----------------
        frame = newEndOfTransmissionFrame(driver.getInstrument());
        driver.addFrame(frame);
        // ----------------
      }
    }
  }
}
