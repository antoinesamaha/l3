package b01.l3.drivers.coulter;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.foc.util.Int2ByteConverter;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.drivers.coulter.dataBlockStructure.IDataBlock;
import b01.l3.emulator.EmulatorRobot;
import b01.l3.emulator.IEmulator;

public class CoulterEmulator extends CoulterDriver implements IEmulator {
  Random rand;
  EmulatorRobot robot = null;
  
  public void init(Instrument instrument, Properties props) throws Exception {
    super.init(instrument, props);
    
    setDriverReceiver(new CoulterEmulatorReceiver(this));
    CoulterFrame answerFrame = new CoulterFrame(getInstrument());
    answerFrame.setFrameCharBeforeBlockCount(ASCII.ENQ);
    getL3SerialPort().setAnswerFrame(answerFrame);
    rand = new Random();
    robot = new EmulatorRobot(this, props);
  }

  public void dispose() {
    if (robot != null) {
      robot.dispose();
      robot = null;
    }
  }
  
  public IDataBlock getDataBlockStructure(){
  	CoulterDriver driver = null;
  	try{
  		Instrument relInstr = robot.getRelatedInstrument();
  		driver = (CoulterDriver) relInstr.getDriver();
  	}catch(Exception e){
  		getInstrument().logException(e);
  	}
  	return driver.getDataBlockStructure();
  }
  
  public void sendMessageWithResults(L3Message message) throws Exception {
    resetFrameArray();
    buildFrameArray(message, false);
    sendFramesArray(true);
  }

  public EmulatorRobot getRobot() {
    return robot;
  }

  @Override
  public void buildFrameArray(L3Message message, boolean fromDriver) throws Exception {
    Int2ByteConverter conv = null;
    
    // SYN frame
    // -----------
    CoulterFrame frame = CoulterFrame.newSynchronousFrame(getInstrument());
    addFrame(frame);
    // -----------
    
    // Data Block Frame Number
    //------------------
    CoulterFrame dataBlockCountFrame = CoulterFrame.newDataBlockCountFrame(getInstrument());
    addFrame(dataBlockCountFrame);
    //------------------
    
    int nbrOfDataFrames = 0;
    
    // Data Block Stucture Frame
    //-----------------
    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sample = (L3Sample) sIter.next();
      if (sample != null) {
        frame = CoulterFrame.newDataBlockFrame(getInstrument(), nbrOfDataFrames+1);

        IDataBlock dbStructure = getDataBlockStructure();
        frame.append2Data(dbStructure.format(sample).toString());
        
        nbrOfDataFrames = addAndSplitDataFrame(frame, nbrOfDataFrames);
      }
    }
    //-----------------
    
    conv = new Int2ByteConverter(nbrOfDataFrames);
    dataBlockCountFrame.append2Data(conv.getHighByte());
    dataBlockCountFrame.append2Data(conv.getLowByte());
    
    // SYN frame
    frame = CoulterFrame.newSynchronousFrame(getInstrument());
    addFrame(frame);
    // -------------
  }

  public void waitForDLE() throws Exception{
  }
  
  public String testMaps_getInstCode(String lisCode){
  	String str = null; 
  	try{
  		str = ((DriverSerialPort)getRobot().getRelatedInstrument().getDriver()).testMaps_getInstCode(lisCode);
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  	return str;
  }
}
