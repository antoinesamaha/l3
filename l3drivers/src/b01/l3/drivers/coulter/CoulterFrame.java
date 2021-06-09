package b01.l3.drivers.coulter;

import b01.foc.Globals;
import b01.foc.util.ASCII;
import b01.foc.util.Byte2IntConverter;
import b01.foc.util.CRC16CCITT_2;
import b01.foc.util.Int2ByteConverter;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.exceptions.L3CheckSumException;

public class CoulterFrame extends L3Frame {
  public static final int FRAME_TYPE_SYN   = 0;
  public static final int FRAME_TYPE_ACK   = 1;
  public static final int FRAME_TYPE_NACK  = 2;
  public static final int FRAME_TYPE_DATA  = 3;
  public static final int FRAME_TYPE_ENQ   = 4;
  public static final int FRAME_TYPE_NONE  = 5;
  public static final int FRAME_TYPE_DLE   = 6;

  public static final int FRAME_TYPE_DATABLOCK_NUMBER = 8;
  public static final int FRAME_TYPE_TRANS_ACCEPT_READY_FOR_NEXT = 9;
  public static final int FRAME_TYPE_TRANS_ACCEPT_NOT_SEND_MORE  = 10;
  public static final int FRAME_TYPE_TRANS_REJECT_RETRY          = 11;
  public static final int FRAME_TYPE_TRANS_REJECT_ABANDON        = 12;
    
  public static final char FIELD_SEPERATOR = 32;
  public static final int  FRAME_EXACT_SIZE = 256;
  
  private int  type        = -1;
  private int  blockNumber = 0;
  private char frameCharBeforeBlockCount = ASCII.SYN; //ENQ for Emulator and SYN for Driver 
  private boolean waitingForBlockCountFrame = false;
  private int fieldCount = 0;
  
  public CoulterFrame(Instrument instrument) {
    super(instrument);
    this.type = FRAME_TYPE_SYN;
    fieldCount = 0;
  }
  
  public CoulterFrame(Instrument instrument, int type){
    this(instrument);
    this.type = type;
  }
  
  public static CoulterFrame newSynchronousFrame(Instrument instrument){
    CoulterFrame frame = new CoulterFrame(instrument, FRAME_TYPE_SYN);
    return frame;
  }
  
  public static CoulterFrame newEnquiryFrame(Instrument instrument){
    CoulterFrame frame = new CoulterFrame(instrument, FRAME_TYPE_ENQ);
    return frame;
  }
  
  public static CoulterFrame newAcknowlegFrame(Instrument instrument){
    CoulterFrame frame = new CoulterFrame(instrument, FRAME_TYPE_ACK);
    return frame;
  }
  
  public static CoulterFrame newNotAcknowlegFrame(Instrument instrument){
    CoulterFrame frame = new CoulterFrame(instrument, FRAME_TYPE_NACK);
    return frame;
  }

  public static CoulterFrame newDataBlockCountFrame(Instrument instrument){
    CoulterFrame frame = new CoulterFrame(instrument, FRAME_TYPE_DATABLOCK_NUMBER);
    return frame;
  }
  
  public static CoulterFrame newDataBlockFrame(Instrument instrument, int blockNumber){
    CoulterFrame frame = new CoulterFrame(instrument, FRAME_TYPE_DATA);
    frame.blockNumber = blockNumber;
    return  frame;
  }

  /*
	public static CoulterFrame preambleSection(CoulterFrame frame){
    for(int i=0; i<7; i++){
      frame.append2Data(ASCII.CR);
      frame.append2Data(ASCII.LF);
    }
    for(int i=0; i<2; i++){
      for(int j=0; j<7; j++){
        frame.append2Data(String.valueOf(ASCII.DASH));
      }
      frame.append2Data(ASCII.CR);
      frame.append2Data(ASCII.LF);
    }
    return frame;
  }

	public static CoulterFrame postambleSection(CoulterFrame frame){
    frame.append2Data(ASCII.DC1);
    frame.append2Data(ASCII.CR);
    frame.append2Data(ASCII.LF);
    frame.append2Data(ASCII.CR);
    frame.append2Data(ASCII.LF);
    for(int i=0; i<2; i++){
      for(int j=0; j<7; j++){
        frame.append2Data(String.valueOf(ASCII.DASH));
      }
      frame.append2Data(ASCII.CR);
      frame.append2Data(ASCII.LF);
    }
    return frame;
  }
  */
	public void appendHEaderSection_SOH(){
  	StringBuffer bufferForChecksum = new StringBuffer();
    bufferForChecksum.append(ASCII.SOH);
    Int2ByteConverter conv = new Int2ByteConverter(fieldCount);
    bufferForChecksum.append(conv.getHighByte());
    bufferForChecksum.append(conv.getLowByte());
    bufferForChecksum.append(ASCII.CR);
    bufferForChecksum.append(ASCII.LF);
    bufferForChecksum.append(getData());
    setData(bufferForChecksum);
	}
	
	public void appendFieldEnd(){
	  append2Data(ASCII.CR);
	  append2Data(ASCII.LF);
	  fieldCount++;
	}
	
  public static CoulterFrame newDLEFrame(Instrument instrument, int transmission){
    CoulterFrame frame = null;
    switch (transmission) {
    case 5:
      frame = new CoulterFrame(instrument, FRAME_TYPE_TRANS_ACCEPT_READY_FOR_NEXT);
      break;
    case 6:
      frame = new CoulterFrame(instrument, FRAME_TYPE_TRANS_ACCEPT_NOT_SEND_MORE);
      break;
    case 7:
      frame = new CoulterFrame(instrument, FRAME_TYPE_TRANS_REJECT_RETRY);
      break;
    case 8:
      frame = new CoulterFrame(instrument, FRAME_TYPE_TRANS_REJECT_ABANDON);
      break;
    default:
      break;
    }
    return frame;
  }

  @Override
  public void createDataWithFrame() throws Exception {
    StringBuffer buffer = new StringBuffer();    
    
    if(type == FRAME_TYPE_ENQ){
      buffer.append(ASCII.ENQ);
    }else if(type == FRAME_TYPE_SYN){
      buffer.append(ASCII.SYN);
    }else if(type == FRAME_TYPE_ACK){
      buffer.append(ASCII.ACK);
    }else if(type == FRAME_TYPE_NACK){
      buffer.append(ASCII.NACK);
    }else if (type == FRAME_TYPE_DATABLOCK_NUMBER){
      buffer.append(getData());
    }else if(type == FRAME_TYPE_TRANS_ACCEPT_READY_FOR_NEXT){
      buffer.append(ASCII.DLE);
      buffer.append('A');
    }else if(type == FRAME_TYPE_TRANS_ACCEPT_NOT_SEND_MORE){
      buffer.append(ASCII.DLE);
      buffer.append('B');
    }else if(type == FRAME_TYPE_TRANS_REJECT_RETRY){
      buffer.append(ASCII.DLE);
      buffer.append('C');
    }else if(type == FRAME_TYPE_TRANS_REJECT_ABANDON){
      buffer.append(ASCII.DLE);
      buffer.append('D');
    }else{
    	StringBuffer bufferForChecksum = getData();
      char[] checkSum = computeChecksum(bufferForChecksum);

      buffer.append(ASCII.STX);
      Int2ByteConverter conv = new Int2ByteConverter(blockNumber);
      buffer.append(conv.getHighByte());
      buffer.append(conv.getLowByte());
      
      buffer.append(bufferForChecksum);
      
      conv = new Int2ByteConverter(checkSum[0]);
      buffer.append(conv.getHighByte());
      buffer.append(conv.getLowByte());
      conv = new Int2ByteConverter(checkSum[1]);
      buffer.append(conv.getHighByte());
      buffer.append(conv.getLowByte());
      buffer.append(ASCII.ETX);
    }
    setDataWithFrame(buffer);    
  }

  public char[] computeChecksum(StringBuffer strBuffer){
  	CRC16CCITT_2 crc = new CRC16CCITT_2(strBuffer.toString());
  	char charCRC = crc.getCRC();
  	//Globals.logString(" sum : "+Integer.toHexString(charCRC));
  	char[] c = new char[2];
  	c[0] = (char) (charCRC >> 8);
  	c[1] = (char) (charCRC & 0x00FF);
  	
  	return c;
  }
  
  public int getType(){
    return type;
  }

  public void setType(int type){
    this.type = type;
  }

  @Override
  public boolean extractAnswerFromBuffer(StringBuffer buffer) {
    String str = buffer.toString();
    boolean extractionDone = false;
    char lastChar = str.charAt(str.length()-1);

    //    a- [ACK/NACK/ENQ] [DLE] 'char' a la fois
    // ou b- [ACK/NACK/ENQ] puis [DLE] 'char' 
    // ou c- [ACK/NACK/ENQ] [DLE] puis 'char' => il faut attendre le 'char'
    // ou d- [ACK/NACK/ENQ] puis [DLE] puis 'char' => il faut attendre le 'char'
    
    /* if(buffer.length() >= 2 && (buffer.charAt(0) == ASCII.DLE || buffer.charAt(1) == ASCII.DLE)){
    	int len   = buffer.length();
    	int start = buffer.charAt(0) == ASCII.DLE ? 0 : 1;
    	StringBuffer blockNumberString = new StringBuffer(buffer.substring(start, len));
    	setData(blockNumberString);
      setDataWithFrame(blockNumberString);
      buffer.replace(0, len, "");
      extractionDone = true;
      } */
    if (str.contains(""+ASCII.DLE)){
    	int len   = buffer.length();
    	int dleIdx = str.lastIndexOf(""+ASCII.DLE);
    	if (len > dleIdx+1){
			StringBuffer blockNumberString = new StringBuffer(buffer.substring(dleIdx, len));
			setData(blockNumberString);
			setDataWithFrame(blockNumberString);
			buffer.replace(0, len, "");
			extractionDone = true;
    	}
    }else if(lastChar == ASCII.ENQ || lastChar == ASCII.SYN || lastChar == ASCII.ACK || lastChar == ASCII.NACK){
      buffer.replace(0, str.length(), "");
      extractionDone = true;
      setDataWithFrame(new StringBuffer(String.valueOf(lastChar)));
      if(lastChar == getFrameCharBeforeBlockCount()){
        waitingForBlockCountFrame = true ;
      }
    }else if(waitingForBlockCountFrame){
      if (buffer.length() >= 2){
      	StringBuffer blockNumberString = new StringBuffer(buffer.substring(0, 2));
      	setData(blockNumberString);
        setDataWithFrame(blockNumberString);
        buffer.replace(0, 2, "");
        extractionDone = true;
        waitingForBlockCountFrame = false;
      }
    }else{
      String start = String.valueOf(ASCII.STX);
      String end = String.valueOf(ASCII.ETX);
      int sIdx = str.lastIndexOf(start);
      int eIdx = -1;
      if(sIdx >= 0){
        eIdx = str.lastIndexOf(end);
      }
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
      
      if(str.startsWith(String.valueOf(ASCII.ENQ))){
        setType(CoulterFrame.FRAME_TYPE_ENQ);
      }else if (str.startsWith(String.valueOf(ASCII.SYN))){
        setType(CoulterFrame.FRAME_TYPE_SYN);
      }else if (str.startsWith(String.valueOf(ASCII.ACK))){
        setType(CoulterFrame.FRAME_TYPE_ACK);
      }else if (str.startsWith(String.valueOf(ASCII.NACK))){
        setType(CoulterFrame.FRAME_TYPE_NACK);
      }else if (str.startsWith(String.valueOf(ASCII.DLE)) || str.charAt(1) == ASCII.DLE){
        setType(CoulterFrame.FRAME_TYPE_DLE);
        StringBuffer data = null;
        try{
        	if(str.startsWith(String.valueOf(ASCII.DLE))){
        		data = new StringBuffer(String.valueOf(dataWithFrame.charAt(1)));
        	}else{
        		data = new StringBuffer(String.valueOf(dataWithFrame.charAt(2)));
        	}
        }catch(Exception e){
    		Globals.logString("DLE frame does not have a character after DLE : "+str);
    		Globals.logException(e);
    	}
        setData((data != null) ? data : new StringBuffer(String.valueOf('A')));
      }else if (str.startsWith(String.valueOf(ASCII.STX))){
        StringBuffer data = new StringBuffer(dataWithFrame.substring(3, dataWithFrame.length()-5));
        setData(data);
        
        Byte2IntConverter conv = new Byte2IntConverter(dataWithFrame.charAt(1), dataWithFrame.charAt(2));
        blockNumber = conv.getIntValue();
        
        setType(CoulterFrame.FRAME_TYPE_DATA);

        //Verifying the checksum
        StringBuffer bufferForChecksum = new StringBuffer(dataWithFrame);
        bufferForChecksum.replace(dataWithFrame.length() - 5, dataWithFrame.length(), "");
        bufferForChecksum.replace(0, 3, "");      
        char[] realCheckSum = new char[2];
        conv = new Byte2IntConverter(dataWithFrame.charAt(dataWithFrame.length() - 5), dataWithFrame.charAt(dataWithFrame.length() - 4));
        realCheckSum[0] = (char)conv.getIntValue();
        conv = new Byte2IntConverter(dataWithFrame.charAt(dataWithFrame.length() - 3), dataWithFrame.charAt(dataWithFrame.length() - 2));
        realCheckSum[1] = (char)conv.getIntValue();
        
        char[] computedCheckSum = computeChecksum(bufferForChecksum);
        
        if(realCheckSum[0] != computedCheckSum[0] || realCheckSum[1] != computedCheckSum[1]){
          logString("buffer that was checked:"+ASCII.convertNonCharactersToDescriptions(bufferForChecksum.toString()));
          throw new L3CheckSumException("Checksum exception: expected:"+Integer.toHexString(computedCheckSum[0])+" "+Integer.toHexString(computedCheckSum[1])+" found:"+Integer.toHexString(realCheckSum[0])+" "+Integer.toHexString(realCheckSum[1]));
        }else{
          //logString("Checksum FINE: expected:"+String.valueOf(computedCheckSum)+" found:"+String.valueOf(realCheckSum));
        }
        //----------------------
      }else{
        setType(CoulterFrame.FRAME_TYPE_DATABLOCK_NUMBER);
      }
    }
  }

  public char getFrameCharBeforeBlockCount() {
    return frameCharBeforeBlockCount;
  }

  public void setFrameCharBeforeBlockCount(char frameCharBeforeBlockCount) {
    this.frameCharBeforeBlockCount = frameCharBeforeBlockCount;
  }

	public int getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
	}
}
