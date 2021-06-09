package b01.l3.drivers.helena.junior24;

import java.util.Iterator;
import java.util.Random;

import b01.foc.util.ASCII;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.exceptions.L3CheckSumException;

public class Junior24Frame_AccordingToDocumentation extends L3Frame {

  public static final int FRAME_TYPE_ACK   = 1;
  public static final int FRAME_TYPE_NACK  = 2;
  public static final int FRAME_TYPE_DATA  = 3;
  public static final int FRAME_TYPE_NONE  = 4;
  
  private static String crlf = null;
  private static Random rand;

  private int type = -1;

  public Junior24Frame_AccordingToDocumentation(Instrument instrument) {
    super(instrument);
    crlf = ""+ASCII.CR+ASCII.LF;
    rand = new Random();
  }

  public Junior24Frame_AccordingToDocumentation(Instrument instrument, int type){
    this(instrument);
    this.type = type;
  }
  
  public static Junior24Frame_AccordingToDocumentation newAckFrame(Instrument instrument){
    Junior24Frame_AccordingToDocumentation frame = new Junior24Frame_AccordingToDocumentation(instrument, FRAME_TYPE_ACK);
    return frame;
  }
  
  public static Junior24Frame_AccordingToDocumentation newNackFrame(Instrument instrument){
    Junior24Frame_AccordingToDocumentation frame = new Junior24Frame_AccordingToDocumentation(instrument, FRAME_TYPE_NACK);
    return frame;
  }
  
  public void dispose(){
    super.dispose();
  }
  
  public int getType(){
    return type;
  }

  public void setType(int type){
    this.type = type;
  }
  
  public static Junior24Frame_AccordingToDocumentation newDataFrame(Instrument instrument, int codeAnalyse, /*String typeAnalyse,*/ L3Sample sample){
    Junior24Frame_AccordingToDocumentation frame = new Junior24Frame_AccordingToDocumentation(instrument, FRAME_TYPE_DATA);
    
    frame.append2Data(String.valueOf(codeAnalyse));
    frame.append2Data(crlf);
    /*frame.append2Data(typeAnalyse);
    frame.append2Data(crlf);*/
    frame.append2Data(sample.getId());
    frame.append2Data(crlf);
   
    Iterator tIter = sample.testIterator(); 
    while(tIter != null && tIter.hasNext()){
      L3Test test = (L3Test) tIter.next();      
      String testLabel = test.getLabel();
      
      double normalTest = rand.nextDouble();
      String normalTestValue = String.valueOf(normalTest).substring(0, 5);
      
      double testValue = test.getValue();
      
      frame.append2Data(testLabel);
      frame.append2Data(ASCII.SPACE);
      frame.append2Data(normalTestValue);
      frame.append2Data(ASCII.SPACE);
      frame.append2Data(String.valueOf(testValue), 4);
      frame.append2Data(crlf);
    }
    
    return frame;
  }
  
  @Override
  public void createDataWithFrame() throws Exception {
    StringBuffer buffer = new StringBuffer();
    if(type == FRAME_TYPE_ACK){
      buffer.append(ASCII.ACK);
    }else if(type == FRAME_TYPE_NACK){
      buffer.append(ASCII.NACK);
    }else{
      buffer.append(ASCII.STX);
      StringBuffer bufferForChecksum = new StringBuffer();
      bufferForChecksum.append(String.valueOf(getData()));
      String checkSum = String.valueOf(computeChecksum(bufferForChecksum));
      buffer.append(bufferForChecksum);
      buffer.append(ASCII.ETX);
      buffer.append(checkSum);
    }
    setDataWithFrame(buffer);    
  }

  public char computeChecksum(StringBuffer strBuffer){
    char char1 = strBuffer.charAt(0);
    for (int i = 1; i < strBuffer.length(); i++){
      char char2 = strBuffer.charAt(i);
      char1= (char)(char1 | char2);
    }
    return char1;
  }
  
  @Override
  public boolean extractAnswerFromBuffer(StringBuffer buffer) {
    String str = buffer.toString();
    
    boolean extractionDone = false;
    String start = String.valueOf(ASCII.STX);
    String end = String.valueOf(ASCII.ETX);
    int sIdx = str.lastIndexOf(start);
    int eIdx = -1;
    if(sIdx >= 0){
      eIdx = str.lastIndexOf(end);
    }
    
    /*
    //CheckSum came after ETX
    if(eIdx > 0 && eIdx > sIdx){
      firstTime++;
    }
    if (firstTime == 2){
      StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx+2));
      setDataWithFrame(response);
      buffer.replace(0, eIdx+2, "");
      extractionDone = true;
      firstTime = 0;
    }
    */
    return extractionDone;
  }

  @Override
  public void extractDataFromFrame() throws Exception {
    StringBuffer dataWithFrame = getDataWithFrame();
    if (dataWithFrame.length()>0){
      String str = dataWithFrame.toString();
      
      if (str.startsWith(String.valueOf(ASCII.ACK))){
        setType(Junior24Frame_AccordingToDocumentation.FRAME_TYPE_ACK);
      }else if (str.startsWith(String.valueOf(ASCII.NACK))){
        setType(Junior24Frame_AccordingToDocumentation.FRAME_TYPE_NACK);
      }else{
        setType(Junior24Frame_AccordingToDocumentation.FRAME_TYPE_DATA);
        StringBuffer data = new StringBuffer(dataWithFrame.substring(1, dataWithFrame.length()-2));
        setData(data);
        
        //Verifying the checksum
        StringBuffer bufferForChecksum = new StringBuffer(dataWithFrame);  
        bufferForChecksum.replace(0, 1, "");      
        bufferForChecksum.replace(dataWithFrame.length() - 3, dataWithFrame.length(), "");
        char realCheckSum = dataWithFrame.charAt(dataWithFrame.length() - 1);
        
        char computedCheckSum = computeChecksum(bufferForChecksum);
        
        if(realCheckSum != computedCheckSum){
          logString("buffer that was checked:"+bufferForChecksum);
          throw new L3CheckSumException("Checksum exception: expected:"+String.valueOf(computedCheckSum)+" found:"+String.valueOf(realCheckSum));
        }else{
          //logString("Checksum FINE: expected:"+String.valueOf(computedCheckSum)+" found:"+String.valueOf(realCheckSum));
        }
        //----------------------
      }
    }
  }
}
