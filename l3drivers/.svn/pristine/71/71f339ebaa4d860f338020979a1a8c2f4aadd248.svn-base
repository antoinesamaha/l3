package b01.l3.drivers.coulter;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.foc.util.Byte2IntConverter;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;

public class CoulterReceiver implements L3SerialPortListener{

  protected CoulterDriver driver = null;
  protected L3Message     message = null;
  protected L3Sample      sample  = null;
  protected L3Test        test    = null;
 
  protected CoulterFrame ackFrame  = null;
  protected CoulterFrame nackFrame = null;
  private CoulterFrame synFrame    = null;
  private CoulterFrame enqFrame    = null;
  
  private int numberOfBlocks = 0;
  private int nextBlockNumber = 0;
  private StringBuffer blockDataCumulated = null;
  
  private   boolean firstTime   = true ;
  protected boolean lastAckSent = false;
  
  public CoulterReceiver(CoulterDriver driver){
    this.driver = driver;
    message     = null;
    sample      = null;
    test        = null;
  }
  
  public void disposeMessage(){
    if(message != null){
      message.dispose();      
    }
    message = null;
    sample  = null;
    test    = null;
  }
  
  public void dispose(){
    driver = null;
    disposeMessage();
    if(ackFrame != null){
      ackFrame.dispose();
      ackFrame = null;
    }
    if(nackFrame != null){
      nackFrame.dispose();
      nackFrame = null;
    }
    if(synFrame != null){
      synFrame.dispose();
      synFrame = null;
    }
  }
  
  public CoulterDriver getDriver(){
    return driver;
  }
  
  private CoulterFrame getAckFrame(){
    if(ackFrame == null){
      ackFrame = CoulterFrame.newAcknowlegFrame(driver.getInstrument());
      try{
        ackFrame.createDataWithFrame();
      }catch(Exception e){
        ackFrame.logException(e);
      }
    }
    return ackFrame;
  }

  private CoulterFrame getNAckFrame(){
    if(ackFrame == null){
      ackFrame = CoulterFrame.newNotAcknowlegFrame(driver.getInstrument());
      try{
        ackFrame.createDataWithFrame();
      }catch(Exception e){
        ackFrame.logException(e);
      }
    }
    return ackFrame;
  }

  private CoulterFrame getSynFrame(){
    if(synFrame == null){
      synFrame = CoulterFrame.newSynchronousFrame(driver.getInstrument());
      try{
        synFrame.createDataWithFrame();
      }catch(Exception e){
        synFrame.logException(e);
      }
    }
    return synFrame;
  }
  
  private CoulterFrame getEnqFrame(){
    if(enqFrame == null){
      enqFrame = CoulterFrame.newEnquiryFrame(driver.getInstrument());
      try{
        enqFrame.createDataWithFrame();
      }catch(Exception e){
        enqFrame.logException(e);
      }
    }
    return enqFrame;
  }
  
  protected void parseDataFrame(StringBuffer data) throws Exception{
    if(sample == null){
      FocConstructor constr = new FocConstructor(L3Sample.getFocDesc(), null);
      sample = (L3Sample)constr.newItem();//new L3Sample(constr);
    }
    driver.getDataBlockStructure().parse(sample, data.toString());
    if(message == null) driver.getInstrument().logString(" Receiver message = null in parseDataFrame(...)");
    message.addSample(sample);
  }

  protected void initMessage(){
    message = new L3Message();
  }
  
  protected void sendMessageBackToInstrument(){
    driver.notifyListeners(message);
  }
  
  protected char get_ENQ_or_SYN_ForReplyToStartComunication(){
    return CoulterFrame.FRAME_TYPE_SYN;
  }
  
  protected void afterLastAckSent() throws Exception{
    driver.release();
  }
  
  protected int treatResultFrame(CoulterFrame frame){
    int frameTypeToReturn = CoulterFrame.FRAME_TYPE_ACK;
    try{
      frame.extractDataFromFrame();
      StringBuffer data = frame.getData();
      if(frame.getType() == CoulterFrame.FRAME_TYPE_DLE){
      	frameTypeToReturn = CoulterFrame.FRAME_TYPE_NONE;
      	driver.setDLE(frame.getData().charAt(0));
      }else if(frame.getType() == get_ENQ_or_SYN_ForReplyToStartComunication() && firstTime){
        firstTime = false;
        if(driver.reserve()){
          //frameTypeToReturn = CoulterFrame.FRAME_TYPE_SYN; // BUSY/NOT READY TO RECEIVE
        	frameTypeToReturn = -1;
        }else{
          frameTypeToReturn = get_ENQ_or_SYN_ForReplyToStartComunication(); // GE AHEAD
          initMessage();
        }
      }else if (frame.getType() == CoulterFrame.FRAME_TYPE_DATABLOCK_NUMBER){
      	Byte2IntConverter b2i = new Byte2IntConverter(data.charAt(0), data.charAt(1));
      	numberOfBlocks = b2i.getIntValue();
      	nextBlockNumber = 1;
      	blockDataCumulated = new StringBuffer();
      	
      }else if(frame.getType() == CoulterFrame.FRAME_TYPE_DATA){
      	if(frame.getBlockNumber() != nextBlockNumber){
      		frameTypeToReturn = CoulterFrame.FRAME_TYPE_SYN;
      		disposeMessage();
      		initMessage();
      	}else{
        	blockDataCumulated.append(data);
        	nextBlockNumber++;
        	if(nextBlockNumber-1 == numberOfBlocks){
       			parseDataFrame(blockDataCumulated);
        	}
      	}
      }else if(frame.getType() == get_ENQ_or_SYN_ForReplyToStartComunication() && !firstTime){
        firstTime = true;
        frameTypeToReturn = CoulterFrame.FRAME_TYPE_ACK; // BLOCK RECEIVED OK - TRANSMISSION ACCEPTED
        sendMessageBackToInstrument();
        disposeMessage();
        lastAckSent = true;
        
      }else{
        frameTypeToReturn = CoulterFrame.FRAME_TYPE_NACK;
      }
    }catch(Exception e){
      driver.getInstrument().logException(e);
      frameTypeToReturn = CoulterFrame.FRAME_TYPE_NACK;
    }
    return frameTypeToReturn;
  }
  
  /* (non-Javadoc)
   * @see b01.l3.connection.L3SerialPortListener#received(b01.l3.L3Frame)
   */
  public void received(L3Frame frame) {
  	lastAckSent = false;
    try{
      CoulterFrame f = (CoulterFrame)frame;
      if(f != null){
        switch(treatResultFrame(f)){
        case CoulterFrame.FRAME_TYPE_ACK:
          driver.send(getAckFrame().getDataWithFrame().toString());
          break;
        case CoulterFrame.FRAME_TYPE_NACK:
          driver.send(getNAckFrame().getDataWithFrame().toString());
          break;          
        case CoulterFrame.FRAME_TYPE_NONE:
          break;
        case CoulterFrame.FRAME_TYPE_SYN:
          driver.send(getSynFrame().getDataWithFrame().toString());
          break;
        case CoulterFrame.FRAME_TYPE_ENQ:
          driver.send(getEnqFrame().getDataWithFrame().toString());
          break;
        }
      }
      if(lastAckSent){
      	afterLastAckSent();
      }
    }catch(Exception e){
      Globals.logException(e);
    }
  }
}