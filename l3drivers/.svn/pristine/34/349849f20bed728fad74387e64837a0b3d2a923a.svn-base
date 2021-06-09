package b01.l3.drivers.helena.junior24;

import b01.foc.util.ASCII;
import b01.l3.Instrument;
import b01.l3.L3Frame;

public class Junior24Frame extends L3Frame {

  public static final int FRAME_TYPE_DATA  = 1;
  public static final int FRAME_TYPE_EOT   = 2;
  public static final int FRAME_TYPE_NONE  = 9;
  
  private static final String LFCR = ""+ASCII.LF+ASCII.CR;
  private static final String LFCREOT = ""+ASCII.LF+ASCII.CR+ASCII.EOT;
  
  private int type = FRAME_TYPE_NONE;
  
  public Junior24Frame(Instrument instrument) {
    super(instrument);
  }

  public void dispose(){
    super.dispose();
  }

  @Override
  public void createDataWithFrame() throws Exception {
  }
 
  private boolean extractAnswerFromBufferUsingTerminator(StringBuffer buffer, String str, String terminator){
  	boolean extractionDone = false;

    int eIdx = str.indexOf(terminator);
    if(eIdx >= 0){
    	if(terminator.equals(LFCR)){
    		type = FRAME_TYPE_DATA;
        StringBuffer response = new StringBuffer(buffer.subSequence(0, eIdx+terminator.length()));
        setDataWithFrame(response);      
    	}else{
    		type = FRAME_TYPE_EOT;
    	}
    	
      buffer.replace(0, eIdx+terminator.length(), "");
      extractionDone = true;
    }
    
  	return extractionDone;
  }
  
  @Override
  public boolean extractAnswerFromBuffer(StringBuffer buffer) {
    String str = buffer.toString();
    
    boolean extractionDone = false;
    
    extractionDone = extractAnswerFromBufferUsingTerminator(buffer, str, LFCREOT);
    if(!extractionDone){
    	extractionDone = extractAnswerFromBufferUsingTerminator(buffer, str, LFCR);
    }
    if(!extractionDone){
    	extractionDone = extractAnswerFromBufferUsingTerminator(buffer, str, ""+ASCII.EOT);
    }
    return extractionDone;
  }

  @Override
  public void extractDataFromFrame() throws Exception {
  	setData(getDataWithFrame());
  }
  
  public int getType(){
  	return type;
  }
}
