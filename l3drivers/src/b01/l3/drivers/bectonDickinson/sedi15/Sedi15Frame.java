package b01.l3.drivers.bectonDickinson.sedi15;

import b01.foc.util.ASCII;
import b01.l3.Instrument;
import b01.l3.L3Frame;
                                                                                
public class Sedi15Frame extends L3Frame{
  public static final int FRAME_TYPE_SOH    = 1;
  public static final int FRAME_TYPE_EOT    = 2;
  public static final int FRAME_TYPE_ACK    = 3;
  public static final int FRAME_TYPE_NACK   = 4;
  public static final int FRAME_TYPE_RESULT = 5;
  public static final int fRAME_TYPE_NONE   = 6;
  public static final int FRAME_TYPE_DATA   = 7;
  
  private int type = -1;
  
  public Sedi15Frame(Instrument instrument) {
    super(instrument);
  }

  public Sedi15Frame(Instrument instrument, int type){
    super(instrument);
    this.type = type;
  }
  
  public static Sedi15Frame newStartOfHeadingFrame(Instrument instrument){
    Sedi15Frame frame = new Sedi15Frame(instrument, FRAME_TYPE_SOH);
    return frame;
  }

  public static Sedi15Frame newEndOfTransmissionFrame(Instrument instrument){
    Sedi15Frame frame = new Sedi15Frame(instrument, FRAME_TYPE_EOT);
    return frame;
  }
  
  public static Sedi15Frame newAcknowlegeFrame(Instrument instrument){
    Sedi15Frame frame = new Sedi15Frame(instrument, FRAME_TYPE_ACK);
    return frame;
  }
  
  public static Sedi15Frame newNotAcknowlegeFrame(Instrument instrument){
    Sedi15Frame frame = new Sedi15Frame(instrument, FRAME_TYPE_NACK);
    return frame;
  }
  
  public static Sedi15Frame newDataFrame(Instrument instrument, int codeAnalyse, String sampleID, StringBuffer strBuffer){
    Sedi15Frame frame = new Sedi15Frame(instrument, FRAME_TYPE_DATA);
    //Insert record format
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
    char lastChar = str.charAt(str.length()-1);
    //Globals.logString(" lastChar = "+(int)lastChar);
    if(lastChar == ASCII.SOH || lastChar == ASCII.EOT || lastChar == ASCII.ACK || lastChar == ASCII.NACK){
      buffer.replace(0, str.length(), "");
      extractionDone = true;
      setDataWithFrame(new StringBuffer(String.valueOf(lastChar)));
    }else{
      String start = String.valueOf(ASCII.STX);
      String end = String.valueOf(ASCII.ETX);
      int sIdx = str.lastIndexOf(start);
      int eIdx = -1;
      if(sIdx >= 0){
        eIdx = str.lastIndexOf(end);
      }
      //Globals.logString("received : "+str);
      //Globals.logString("sIdx : "+sIdx);
      //Globals.logString("eIdx : "+eIdx);
      if(eIdx > 0 && eIdx > sIdx){
        StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx+1));
        setDataWithFrame(response);
        buffer.replace(0, eIdx+1, "");
        extractionDone = true;
      }
    }
    return extractionDone;
  }

  @Override
  public void extractDataFromFrame() throws Exception {

    StringBuffer dataWithFrame = getDataWithFrame();
    if (dataWithFrame.length()>0){
      String str = dataWithFrame.toString();
      
      if(str.startsWith(String.valueOf(ASCII.SOH))){
        setType(Sedi15Frame.FRAME_TYPE_SOH);
      }else if (str.startsWith(String.valueOf(ASCII.EOT))){
        setType(Sedi15Frame.FRAME_TYPE_EOT);
      }else if (str.startsWith(String.valueOf(ASCII.ACK))){
        setType(Sedi15Frame.FRAME_TYPE_ACK);
      }else if (str.startsWith(String.valueOf(ASCII.NACK))){
        setType(Sedi15Frame.FRAME_TYPE_NACK);
      }else{
        StringBuffer data = new StringBuffer(dataWithFrame.substring(3, dataWithFrame.length()-4));
        setData(data);
        
        //sequence = Integer.valueOf((String)dataWithFrame.substring(1, 2)).intValue();
        //type = dataWithFrame.charAt(2);
        type = Sedi15Frame.FRAME_TYPE_RESULT;
        
        //Verifying the checksum
        StringBuffer bufferForChecksum = new StringBuffer(dataWithFrame);  
        bufferForChecksum.replace(0, 1, "");      
        bufferForChecksum.replace(dataWithFrame.length() - 5, dataWithFrame.length(), "");
        char[] realCheckSum = new char[2];
        realCheckSum[0] = dataWithFrame.charAt(dataWithFrame.length() - 4);
        realCheckSum[1] = dataWithFrame.charAt(dataWithFrame.length() - 3);
        
        //char[] computedCheckSum = computeChecksum(bufferForChecksum);
        
        /*if(realCheckSum[0] != computedCheckSum[0] || realCheckSum[1] != computedCheckSum[1]){
          logString("buffer that was checked:"+bufferForChecksum);
          throw new L3CheckSumException("Checksum exception: expected:"+String.valueOf(computedCheckSum)+" found:"+String.valueOf(realCheckSum));
        }*/
      }
    }
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }
}
