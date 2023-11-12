/*
 * Created on Jun 29, 2019
 */
package b01.l3.drivers.horiba.yumizenP8000;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import b01.foc.Globals;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.astm.AstmDriver;
import b01.l3.drivers.astm.AstmFrame;

/**
 * @author 01Barmaja
 */
public class YumizenP8000Receiver implements L3SerialPortListener, YumizenP8000Const {
	protected AstmDriver driver  = null;
	protected L3Message  message = null;
	protected L3Sample   sample  = null;
	protected L3Test     test    = null;

	private MSHLineReader headerLineReader  = null;
	private PIDLineReader patientLineReader = null;
	private SPMLineReader sampleLineReader  = null;
	private OBXLineReader resultLineReader  = null;

	public YumizenP8000Receiver(AstmDriver driver) {
		this.driver = driver;
		
		message = null;
		sample  = null;
		test    = null;

		headerLineReader  = new MSHLineReader();
		patientLineReader = new PIDLineReader();
		sampleLineReader  = new SPMLineReader();
		resultLineReader  = new OBXLineReader(driver);
	}

	public void disposeMessage() {
		if (message != null) {
			message.dispose();
		}
		message = null;
		sample = null;
		test = null;
	}

	public void dispose() {
		driver = null;

		disposeMessage();

		if (headerLineReader != null) {
			headerLineReader.dispose();
			headerLineReader = null;
		}
		
		if (patientLineReader != null) {
			patientLineReader.dispose();
			patientLineReader = null;
		}
		
		if (sampleLineReader != null) {
			sampleLineReader.dispose();
			sampleLineReader = null;
		}
		
		if (resultLineReader != null) {
			resultLineReader.dispose();
			resultLineReader = null;
		}

	}

	public AstmDriver getDriver() {
		return driver;
	}
	
	private void sendMessageBackToInstrument() {
		if(driver != null && message != null) {
			driver.notifyListeners(message);
		}
	}
	
	private L3Message getMessage() {
		if(message == null) {
			message = new L3Message();
		}
		return message;
	}
	
	private L3Sample getSample() {
		return sample;
	}
	
	private L3Test getTest() {
		return test;
	}
	
	public void addResult() {
		//Make sure we have the sample
		String sampleID = sampleLineReader.getSampleID();
		if(sampleID != null && !sampleID.trim().equals("")) {
			
			if(sample == null || !sample.getId().equals(sampleID)) {
				//In this case I need to add or find my sample
				sample = getMessage().findSample(sampleID);
				if(sample == null) {
					sample = new L3Sample(sampleLineReader.getSampleID());
					sample.setFirstName(patientLineReader.getFirstName());
					sample.setLastName(patientLineReader.getLastName());
					sample.setMiddleInitial(patientLineReader.getMidInitial());
					message.addSample(sample);
				}
			}
			
			if(sample != null) {
				String status = resultLineReader.getStatus();
				if(status.equals("F")) {
					String testLabel = resultLineReader.getTestCode();
					if(testLabel != null && !testLabel.equals("")) {
						testLabel = testLabel.trim();
						
						test = sample.findTest(testLabel);
						if(test == null){
							test = sample.addTest();
							test.setLabel(testLabel);
						}
						if(test != null) {
							String valueStr = resultLineReader.getValue();
							try {
								//Numerical
								double value = Double.valueOf(valueStr);
								test.setValue(value);								
								test.setUnitLabel(resultLineReader.getUnitLabel());
								test.setResultOk(true);
							} catch (Exception e) {
								test.setValue(0);								
								String message = "Could not parse Double:"+valueStr+" for test:"+testLabel;
								test.setNotificationMessage(message);
								test.setResultOk(false);
							}
						}
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.connection.L3SerialPortListener#received(b01.l3.L3Frame)
	 */
	public void received(L3Frame frame) {
		try {
			YumizenP8000Frame f = (YumizenP8000Frame) frame;
			if (f != null) {
				int type = f.getType();
				switch(type) {
					case TYPE_MSH: {
						StringBuffer data = frame.getDataWithFrame();
						headerLineReader.scanTokens(data);

						if(patientLineReader != null) patientLineReader.reset();
						if(sampleLineReader != null) sampleLineReader.reset();
						if(resultLineReader != null) resultLineReader.reset();
					} break;
					case TYPE_PID: {
						StringBuffer data = frame.getDataWithFrame();
						patientLineReader.scanTokens(data);
					} break;
					case TYPE_SPM: {
						StringBuffer data = frame.getDataWithFrame();
						sampleLineReader.scanTokens(data);
					} break;
					case TYPE_OBX: {
						StringBuffer data = frame.getDataWithFrame();
						resultLineReader.scanTokens(data);
						addResult();
					} break;					
					case TYPE_FS: {
						sendMessageBackToInstrument();
						disposeMessage();
						
						StringBuffer buffer = new StringBuffer();
						buffer.append(String.valueOf(AstmFrame.VT));
						
				    Date date = new Date(System.currentTimeMillis()+Calendar.getInstance().get(Calendar.DST_OFFSET));
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				    
				    String dateFormatted = sdf.format(date);
				    String controlId = headerLineReader.getControlId();
				    
						buffer.append("MSH|^~\\&|^|^|YP8K|^|"+dateFormatted+"||ACK|"+controlId+"|P|2.5||||||");
						buffer.append(String.valueOf(AstmFrame.CR));
						buffer.append("MSA|AA|"+controlId);
						buffer.append(String.valueOf(AstmFrame.CR));
						buffer.append(String.valueOf(AstmFrame.FS));
						buffer.append(String.valueOf(AstmFrame.CR));
						driver.send(buffer.toString());
					} break;					
				}
			}
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
}
