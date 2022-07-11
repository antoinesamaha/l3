/*
 * Created on May 7, 2006
 */
package b01.l3;

/**
 * @author 01Barmaja
 */
public abstract class L3Frame implements Cloneable{
  
  public abstract void createDataWithFrame() throws Exception;
  public abstract void extractDataFromFrame() throws Exception;;
  public abstract boolean extractAnswerFromBuffer(StringBuffer buffer);//Used when we have the message in the serial port buffer and we want to deduce the serial port 
  
  private StringBuffer data = null;
  private StringBuffer dataWithFrame = null;
  private Instrument instrument = null;
  private boolean    doNotAnswerThatFrameBecauseReceivedAnotherMessage = false;

  public L3Frame(Instrument instrument){
    data = new StringBuffer();
    this.instrument = instrument;
  }
  
  public void dispose(){
    data = null;
    dataWithFrame = null;
    instrument = null;
  }

  public Object clone() throws CloneNotSupportedException{
    L3Frame zClone = (L3Frame)super.clone();
    if(data != null){
      zClone.setData(new StringBuffer(data));
    }
    if(dataWithFrame != null){
      zClone.setData(new StringBuffer(dataWithFrame));
    }
    return zClone; 
  }

	public Instrument getInstrument() {
		return instrument;
	}
	
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
	
  public StringBuffer getData() {
    return data;
  }
  
  public void logString(String str){
  	Instrument instr = getInstrument();  	
  	if(instr != null){
  		instr.logString(str);	
  	}
  }

  public void logException(Exception e){
  	Instrument instr = getInstrument();  	
  	if(instr != null){
  		instr.logException(e);	
  	}
  }

  public void setData(StringBuffer data) {
    this.data = data;
  }

  public StringBuffer getDataWithFrame() {
    return dataWithFrame;
  }
  
  public void setDataWithFrame(StringBuffer dataWithFrame) {
    this.dataWithFrame = dataWithFrame;
  }  
  
  private static void append2Buffer(StringBuffer buffer, String inc) {
    buffer.append(inc);
  }

  private static void append2Buffer(StringBuffer buffer, char inc) {
    buffer.append(inc);
  }

  public void append2Data(String inc, int maxLength) {
	  if(inc != null){
		    if(inc.length() > maxLength){
		      append2Buffer(data, inc.substring(0, maxLength));
		    }else{
		      append2Buffer(data, inc);
		    }
	  }
  }

  public void append2Data(String inc) {
    append2Buffer(data, inc);
  }

  public void append2Data(char inc) {
    append2Buffer(data, inc);
  }
  
  public void append2Data(char inc, int count) {
  	for(int i=0; i<count; i++) {
  		append2Data(inc);
  	}
  }

  public void append2DataWithFrame(String inc) {
    append2Buffer(dataWithFrame, inc);
  }

  public void append2DataWithFrame(char inc) {
    append2Buffer(dataWithFrame, inc);
  }
  
  public void append2DataWithFrame(StringBuffer buffer){
    dataWithFrame.append(buffer);
  }
  
  public boolean isDoNotAnswerThatFrameBecauseReceivedAnotherMessage() {
  	return doNotAnswerThatFrameBecauseReceivedAnotherMessage;
  }
  
  public void setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(boolean doNotAnswerThatFrameBecauseReceivedAnotherMessage) {
  	this.doNotAnswerThatFrameBecauseReceivedAnotherMessage = doNotAnswerThatFrameBecauseReceivedAnotherMessage;
  }
}
