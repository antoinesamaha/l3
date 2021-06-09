/*
 * Created on May 7, 2006
 */
package b01.l3.drivers.kermit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.exceptions.L3CheckSumException;
import b01.l3.exceptions.L3FrameLengthCheckException;
import b01.l3.exceptions.L3RequiredTestNotValidException;
import b01.l3.exceptions.L3SequenceNotAssignedException;
import b01.l3.exceptions.L3TypeNotAssignedException;

/**
 * @author 01Barmaja
 */
public class KermitFrame extends L3Frame{
  private int sequence = -1;
  private char type = FRAME_TYPE_NONE; 
  
  public final static char FRAME_TYPE_DATA          = 'D'; 
  public final static char FRAME_TYPE_YES           = 'Y';
  public final static char FRAME_TYPE_NO            = 'N';
  public final static char FRAME_TYPE_SESSION_START = 'S';
  public final static char FRAME_TYPE_BREAK         = 'B';
  public final static char FRAME_TYPE_FILE_START    = 'F';
  public final static char FRAME_TYPE_FILE_END      = 'Z';
  public final static char FRAME_TYPE_ERROR         = 'E';
  public final static char FRAME_TYPE_NONE          = 'X';

  public static final char SOH = 1;
  public static final char CR = 13;
  
  //private int  MAXL_analysor = 94;//Default value for Vitros 250  
  public static final int  MAXL = 96;
  public static final int  TIME = 50;//Time for the 
  public static final char NPAD = 0;//0 by analysor and PC can specify if it wants
  public static final char PADC = '@';//Ignored
  public static final char EOL  = CR;//We are directly using this character in the Frame so it is not enough too modify that here
  public static final char QCTL = '#';
  public static final char QBIN = 'N';
  public static final int  CHKT = 1;//Check sum number of digits used
  
  public KermitFrame(Instrument instrument){
  	super(instrument);
    sequence = -1;
    this.type = FRAME_TYPE_NONE;
  }
  
  public KermitFrame(Instrument instrument, int sequence){
  	super(instrument);
    this.sequence = sequence;
    this.type = FRAME_TYPE_NONE;
  }

  public KermitFrame(Instrument instrument, int sequence, char type){
  	super(instrument);
    this.sequence = sequence;
    this.type = type;
  }
  
  public void dispose(){
    super.dispose();    
  }

  public int getSequence() {
    return sequence;
  }
  
  public char getType() {
    return type;
  }
  
  private void fillStartData(){
    append2Data(VitrosUtil.chr(MAXL - 2));//MAXL
    append2Data(VitrosUtil.chr(TIME));//TIME
    append2Data(VitrosUtil.chr(NPAD));
    append2Data(VitrosUtil.ctl(PADC));
    append2Data(VitrosUtil.chr(EOL));
    append2Data(QCTL);
    append2Data(QBIN);
    append2Data(String.valueOf(CHKT));
    append2Data(VitrosUtil.chr(0));
  }

  public static KermitFrame newStartFrame(Instrument instrument){
    KermitFrame frame = new KermitFrame(instrument, 0, FRAME_TYPE_SESSION_START);
    frame.fillStartData();
    return frame;
  }

  public static KermitFrame newYesFrame(Instrument instrument, int sequence){
    KermitFrame frame = new KermitFrame(instrument, sequence, FRAME_TYPE_YES);
    return frame;
  }

  public static KermitFrame newYesToStartFrame(Instrument instrument){
    KermitFrame frame = new KermitFrame(instrument, 0, FRAME_TYPE_YES);
    frame.fillStartData();
    return frame;
  }  

  public static KermitFrame newFileStartFrame(Instrument instrument, int sequence, String fileName){
    KermitFrame frame = new KermitFrame(instrument, sequence, FRAME_TYPE_FILE_START);
    frame.append2Data('S');
    frame.append2Data(fileName);        
    return frame;
  }  

  public static KermitFrame newFileEndFrame(Instrument instrument, int sequence){
    KermitFrame frame = new KermitFrame(instrument, sequence, FRAME_TYPE_FILE_END);
    return frame;
  }  

  public static KermitFrame newBreakFrame(Instrument instrument, int sequence){
    KermitFrame frame = new KermitFrame(instrument, sequence, FRAME_TYPE_BREAK);
    return frame;
  }  

  //BDebug - TransactionIsolationIssue
  //private static BufferedWriter debugWriter = null;
  //EDebug - TransactionIssue
  
  public static KermitFrame newSampleFrame(Instrument instrument, int sequence, L3Message message, L3Sample sam, boolean onlyTIBC) throws Exception{
    KermitFrame frame = new KermitFrame(instrument, sequence, FRAME_TYPE_DATA);
    if(!onlyTIBC){
    	frame.addString(sam.getId(), 15);
    }else{
    	frame.addString(sam.getId()+"Y", 15);
    }
    
    switch(sam.getLiquidType()){
    case L3Sample.LIQUID_TYPE_EMPTY:          
    case L3Sample.LIQUID_TYPE_SERUM:
      frame.append2Data('1');
      break;
    case L3Sample.LIQUID_TYPE_CSF:
      frame.append2Data('2');
      break;
    case L3Sample.LIQUID_TYPE_URIN:
      frame.append2Data('3');
      break;
    default:
    	frame.append2Data('1');
    	break;
    }
    frame.append2Data('0');//NON STAT
    frame.append2Data(' ');//CUP Position is not specified. Should be manually
    frame.append2Data("1.000");//Dilution factor
    
    Vitros250Driver driver = (Vitros250Driver) instrument.getDriver();

    //BDebug - TransactionIsolationIssue
    /*
    if(debugWriter == null){
    	File debugFile = new File("c:\\transaction.txt");
    	debugFile.createNewFile();
    	debugWriter = new BufferedWriter(new FileWriter(debugFile, true));
    }
    String debugStr = "";
    */
    //EDebug - TransactionIsolationIssue
    
    int forDerivedTests = 0;
    
    for(forDerivedTests = 0; forDerivedTests<2; forDerivedTests++){
    	boolean isForDerivedTest = forDerivedTests == 1;
	    Iterator tIter = sam.testIterator(); 
	    while(tIter != null && tIter.hasNext()){
	      L3Test test = (L3Test) tIter.next();
	      if(test != null){
	        int idx = driver.getTestIndexFromLabel(test.getLabel());
	        if(idx >= 0){
	        	if(isForDerivedTest == driver.isDerivedTest(idx)){
		        	if(onlyTIBC == driver.isTestTIBC(test.getLabel()) /*|| driver.isTestPercentageDeSaturation(test.getLabel())*/){
		        		char c = VitrosUtil.chr(idx);
		        		frame.append2Data(c);
		        		//BDebug - TransactionIsolationIssue
		        		//if(debugWriter != null) debugStr = debugStr + c;
		        		//BDebug - TransactionIsolationIssue
		        		if(c == '#'){
		        			frame.append2Data(c);
		        			//BDebug - TransactionIsolationIssue
		        			//if(debugWriter != null) debugStr = debugStr + c;
		        			//BDebug - TransactionIsolationIssue
		        		}
		        	}
	        	}
	        }else{
	          throw new L3RequiredTestNotValidException("Required test '"+test.getLabel()+"' not valid!");
	        }
	      }
	    }
    }    
    
    //BDebug - TransactionIsolationIssue
    /*
    if(debugWriter != null && debugStr != null){
			debugWriter.write(debugStr+"|");
			debugWriter.newLine();
			debugWriter.flush();
    }
    */
    //EDebug - TransactionIsolationIssue
    
    if(sam.getLastName() != null && sam.getLastName().length() > 0){
      frame.append2Data('|');//The patiet name field is optional
      frame.addString(sam.getLastName(), 15);
      frame.addString(sam.getFirstName(), 9);
      frame.addString(sam.getMiddleInitial(), 1);
    }
    frame.append2Data(']');//End of sample character        
    
    return frame;
  }

  public void addString(String str, int size){
    int gap = size;
    if(str != null){
      if(str.length() > size){
        append2Data(str.substring(0, size));
        gap = 0;
      }else{
        append2Data(str);
        gap = size - str.length();
      }
    }
    for(int i=0; i<gap; i++){
      append2Data(' ');
    }
  }
  
  public char computeCheckSumChar(StringBuffer str){
    char check = 0;
    
    int sum = 0;
    char[] bts = new char[str.length()]; 
    str.getChars(0, str.length(), bts, 0);
    for(int i=0; i<bts.length; i++){
      sum += bts[i];
      //logString("sum = "+sum+" char :"+(char)bts[i]+" ascii :"+(int)bts[i]);
    }
    int and = (sum & 192) / 64;
    int and2 = (sum + and) & 63;        
    
    check = VitrosUtil.chr(and2);
    return check;
  }
  
  /* (non-Javadoc)
   * @see b01.l3.L3Frame#createDataWithFrame()
   */
  public void createDataWithFrame() throws Exception{
    //if(getData() == null || getData().length() == 0){
      //throw new L3DataIsEmptyException("Data is empty");
    //}else
    if(sequence < 0){
      throw new L3SequenceNotAssignedException("Sequence not assigned for frame: "+getData());
    }else if(type == FRAME_TYPE_NONE){
      throw new L3TypeNotAssignedException("Frame type not assigned for frame: "+getData());
    }else{
      StringBuffer strBuff = new StringBuffer();    
      strBuff.append(VitrosUtil.chr(sequence));
      strBuff.append(type);
      if(getData() != null){
        strBuff.append(getData());
      }
      strBuff.insert(0, VitrosUtil.chr(strBuff.length()+1));
      char check = computeCheckSumChar(strBuff);
      strBuff.append(check);
      strBuff.insert(0, SOH);
      strBuff.append(EOL); 
      setDataWithFrame(strBuff);
    }
  }

  /* (non-Javadoc)
   * @see b01.l3.L3Frame#extractDataFromFrame()
   */
  public void extractDataFromFrame() throws Exception{
    StringBuffer wf = getDataWithFrame();
    int len = wf.length();
    char checkSumChar = wf.charAt(len - 2);
    char lengthChar = wf.charAt(1);
    int lenToVerify = lengthChar - 32;
    
    StringBuffer strBuff = new StringBuffer(wf.substring(1, len-2));
    if(checkSumChar != computeCheckSumChar(strBuff)){
      throw new L3CheckSumException("Frame checksum "+checkSumChar+" should be "+computeCheckSumChar(strBuff));
    }else if(lenToVerify != len - 3){
      throw new L3FrameLengthCheckException("Frame length is not "+lenToVerify+" for frame:"+getDataWithFrame());
    }else{
      sequence = (int) wf.charAt(2);
      sequence = sequence - 32;
      type = wf.charAt(3);
      setData(new StringBuffer(wf.substring(4, len-2)));
    }
  }

  /* (non-Javadoc)
   * @see b01.l3.L3Frame#extractDataFromBuffer(java.lang.StringBuffer)
   */
  public boolean extractAnswerFromBuffer(StringBuffer buffer) {
    String str = buffer.toString();
    String start = String.valueOf(SOH);
    String end = String.valueOf(EOL);
    
    int sIdx = str.indexOf(start);
    int eIdx = -1;
    if(sIdx >= 0){
      eIdx = str.indexOf(end);
    }
    
    if(eIdx > 0){
      StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx+1));
      setDataWithFrame(response);
      buffer.replace(sIdx, eIdx+1, "");
    }
    
    //driver.getInstrument().logString("  start: "+sIdx+" end: "+eIdx+" "+(int)buffer.charAt(buffer.length()-1)+ " "+(int)end.charAt(0) );
    
    return eIdx > 0;    
  }
}