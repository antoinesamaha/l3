package b01.l3.drivers.abbott.modular;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import b01.foc.Globals;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

public class CopyModularFrameCreator extends AstmFrameCreator{
  public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
    resetSequence();
    
    // ENQ frame
    // -----------
    getNextSequence();
    AstmFrame frame = newEnquiryFrame(driver.getInstrument());
    driver.addFrame(frame);
    // -----------
    
    // BIG frame
    frame = new AstmFrame(driver.getInstrument(), getNextSequence(), AstmFrame.FRAME_TYPE_HEADER);
    driver.addFrame(frame);
    
    frame.append2Data("|\\^&|||host^2||||||TSDWN^BATCH");
    frame.append2Data(AstmFrame.CR);
    
    // Patient Frame
    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sam = (L3Sample) sIter.next();
      if (sam != null) {
      	//Patient
      	frame = new AstmFrame(driver.getInstrument(), getNextSequence(), AstmFrame.FRAME_TYPE_PATIENT);
        driver.addFrame(frame);
      	
      	frame.append2Data("|1||PatID|||||");
        if(sam.getSexe().compareTo("F") == 0){
        	frame.append2Data("F||||||");
        }else{
        	frame.append2Data("M||||||");
        }
      	if(sam.getAge() > 0 && sam.getAge() < 150){
      		frame.append2Data(sam.getAge()+"^Y");	
      	}else{
      		frame.append2Data("^Y");
      	}
      	//-------
      	frame.append2Data(AstmFrame.CR);

      	String liquidTypeString = "1";
      	switch(sam.getLiquidType()){
      	case L3Sample.LIQUID_TYPE_CSF:
      		liquidTypeString = "3";
      		break;
      	case L3Sample.LIQUID_TYPE_BODY_FLUID:
      		liquidTypeString = "4";
      		break;
      	case L3Sample.LIQUID_TYPE_SERUM:
      		liquidTypeString = "1";
      		break;
      	case L3Sample.LIQUID_TYPE_URIN:
      		liquidTypeString = "2";
      		break;
      	case L3Sample.LIQUID_TYPE_STOOL:
      		liquidTypeString = "4";
      		break;
      	}
      	
      	//Order
      	frame = new AstmFrame(driver.getInstrument(), getNextSequence(), AstmFrame.FRAME_TYPE_ORDER);
        driver.addFrame(frame);
      	
      	frame.append2Data("|1|"+sam.getId()+"|"+sam.getId()+"^^^^S"+liquidTypeString+"^SC|");
      	
      	boolean isFirst = true;
        Iterator tIter = sam.testIterator(); 
        while(tIter != null && tIter.hasNext()){
          L3Test test = (L3Test) tIter.next();
          String machineTest = test != null ? driver.testMaps_getInstCode(test.getLabel()) : null;
          if(machineTest != null){
            // Order Frame
          	if(!isFirst){
          		frame.append2Data("\\");
          	}
          	frame.append2Data("^^^"+machineTest+"^");
          	isFirst = false;
            // ---------------
            //if (fromDriver == false){
              //Result Frame
              //frame = AstmFrame.newResultFrame(getInstrument(), getNextSequence(), 1, test.getLabel(), test.getUnitLabel(), test.getValue());
              //addFrame(frame);
              //----------------
            //}
          }
        }
        frame.append2Data("|R||");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if(sam.getDateAndTime() < 24*60*60*1000){
          frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
        }else{
          frame.append2Data(sdf.format(sam.getDateAndTime()));
        }
        //frame.append2Data("20000530143741");
        
        frame.append2Data("||||N||||1||||||||||O");
        //frame.append2Data("||||N||||"+liquidTypeString+"||||||||||O");
        frame.append2Data(AstmFrame.CR);
        
        frame.append2Data("C|1|L|"+sam.getFirstName()+" "+sam.getLastName()+"^^^^|G");
        //frame.append2Data("C|1|L|C1^C2^C3^C4^C5|G");
        
        frame.append2Data(AstmFrame.CR);
      }
      frame.append2Data("L|1|N");
    }

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
