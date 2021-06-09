package b01.l3.drivers.unit;

import java.util.Properties;

import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;

public class EmulatorDriver extends DriverSerialPort implements	L3SerialPortListener {
	private int numcall = 0;
	private String[] hardCodedFrameList;
	private L3Frame answerFrame = null;
	private long TIME_OUT = 5000;

  public EmulatorDriver(){
		this.hardCodedFrameList = null;
		this.answerFrame = null;
		numcall = 0;
		setDriverReceiver(this);
  }
  
  public void init(Instrument instrument, Properties props) throws Exception {
  	super.init(instrument, props);
  	newSerialPort(props);
    
  }
  
	@Override
	public void connect() throws Exception {
		super.connect();
		getL3SerialPort().setAnswerFrame(answerFrame);
	}

	public void setTimeOut(long timeOut){
		this.TIME_OUT = timeOut;
	}
	
	//Can be overwriden 
	public void replyToFrame(L3Frame frame){
  	if(numcall < hardCodedFrameList.length){
  		try {
				send(hardCodedFrameList[numcall]);
			} catch (Exception e) {
				getInstrument().logException(e);
			}
  	}
		numcall++;
	}
	
	//Called when we have input data on the serial port
	public void received(L3Frame frame) {
		L3Message message = new L3Message();
  	L3Sample sample = new L3Sample(frame.getDataWithFrame().toString());
  	message.addSample(sample);
  	//notifyListeners(message);
  	replyToFrame(frame);
	}

	public void send(L3Message message) throws Exception {
		for(int i=0; i<hardCodedFrameList.length; i++){
			sendAndWaitForResponse(hardCodedFrameList[i], TIME_OUT);		
		}
	}

	public void setHardCodedFrameList(String[] hardCodedFrameList) {
		this.hardCodedFrameList = hardCodedFrameList;
	}

	public void setAnswerFrame(L3Frame answerFrame) {
		this.answerFrame = answerFrame;
	}

  @Override
  protected void synchronizeSequenceID() throws Exception {
    
  }
  
	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
		// TODO Auto-generated method stub
		
	}
}