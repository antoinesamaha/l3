package b01.l3.drivers.kermit.unit;


import java.util.Calendar;
import java.util.Properties;

import java.util.Iterator;

import b01.l3.Instrument;
import b01.l3.MessageListener;
import b01.l3.PoolKernel;

import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.kermit.KermitFrame;
import b01.l3.drivers.kermit.Vitros250Driver;
import b01.l3.drivers.unit.EmulatorDriver;
import junit.framework.TestCase;


public class UnitVitros250Driver extends TestCase {
	
	private final static String DRIVER_CODE = "Vitros";  
	private final static String DRIVER_PORT = "COM2";
	private final static String EMULATOR_CODE = "Emulator";
	private final static String EMULATOR_PORT = "COM7";
	
	private static final int RESPONSE_FAIL = 999;
	
	private int responseCount = 0;
	private String failMessage = null;
	
	private PoolKernel pool = null;
	private Instrument instrument = null;
	private Instrument emulator = null;
	
	public UnitVitros250Driver(String name) {
		super(name);
	}
	
	private void initPoolIfNeeded() throws Exception{
		if(pool == null){
			//Properties initialisation		
			Properties props = new Properties();
			props.put("instrument.code", DRIVER_CODE);
			props.put("instrument.name", DRIVER_CODE);
	    props.put("instrument.driver", "b01.l3.drivers.kermit.Vitros250Driver");
	
			props.put("serialPort.name", DRIVER_PORT);
			props.put("serialPort.baudrate", "4800");
			props.put("serialPort.databits", "8");
			props.put("serialPort.stopbit", "1");
			props.put("serialPort.parity", "NONE");
	
			instrument = new Instrument(props);
			
			props.put("instrument.code", EMULATOR_CODE);
			props.put("instrument.name", EMULATOR_CODE);
			props.put("serialPort.name", EMULATOR_PORT);
	    props.put("instrument.driver", "b01.l3.drivers.unit.EmulatorDriver");			
			emulator = new Instrument(props);
			
			pool = new PoolKernel(false);//il faut new PoolPrincipal
			pool.addInstrument(instrument);
			pool.addInstrument(emulator);
			pool.connectAllInstruments();
	
			//Special Emilator init
			EmulatorDriver driver = (EmulatorDriver) emulator.getDriver();
			driver.getL3SerialPort().setAnswerFrame(new KermitFrame(emulator));
	
		}
	}
	
	private void localAssertion(String s1, String s2){
		if(s1.compareTo(s2) != 0){
			responseCount = RESPONSE_FAIL;
			failMessage = "got: >>>"+s1+"<<< expected >>>"+s2+"<<<";
		}
		//assertEquals(s1, s2);
	}
	
	public void testSend() throws Exception{
		Vitros250Driver.RESPONSE_TIMEOUT = 100000;
		
		PoolListenerForSendTest poolListener = new PoolListenerForSendTest();

		try{
			initPoolIfNeeded();					
			pool.addMessageListener(poolListener);

			//Filling a test message
			//----------------------
			L3Message message = new L3Message();
	    L3Sample sample  = new L3Sample("Patient11");
	    message.addSample(sample);
	    
	    Calendar cal = Calendar.getInstance();
	    cal.set(2006, 06, 29, 18, 15, 0);
	    sample.setDateAndTime(cal.getTimeInMillis());
	    
	    L3Test test = new L3Test("GLU");//ajouter foc desc
	    sample.addTest2(test);
	    test = new L3Test("URIC");    //ajouter foc desc
	    sample.addTest2(test);
	    //----------------------
	      
	    responseCount = 0;
	    instrument.sendWithDriverReservation(message);
	    while(responseCount < 5){
	    	Thread.sleep(1000);
	    }

	    pool.removeMessageListener(poolListener);
	    pool.dispose();
	    pool = null;

	    if(responseCount >= RESPONSE_FAIL){
	    	fail(failMessage);
	    }
	    
		}catch(Exception e){
			
			pool.removeMessageListener(poolListener);
			pool.dispose();
			pool = null;
			throw e;
		}
	}
	
	//-----------------------------------------------
	//- Message listener to verify the sending of L3Messsage at poool reception.  
	//-----------------------------------------------
	private class PoolListenerForSendTest implements MessageListener{
		public void messageReceived(L3Message message) {
			if(message.getInstrumentCode().compareTo(EMULATOR_CODE) == 0){
				Iterator iter = message.sampleIterator();
				while(iter != null && iter.hasNext()){
					L3Sample sample = (L3Sample)iter.next();
					String frameFullContent = sample.getId();
					
		    	switch(responseCount){
		    	case 0:
		    		localAssertion(frameFullContent, String.valueOf(", S~R @-#N1  "+KermitFrame.EOL));
		    		break;
		    	case 1:
		    		//localAssertion(frameFullContent.substring(4, 4), String.valueOf(",!FS11517560\""+Vitros250Driver.EOL));
		    		break;
		    	case 2:
		    		localAssertion(frameFullContent, String.valueOf("<\"DPatient11      0 .0001 \"]X"+KermitFrame.EOL));
		    		break;
		    	case 3:
		    		localAssertion(frameFullContent, String.valueOf("##ZB"+KermitFrame.EOL));
		    		break;
		    	case 4:
		    		localAssertion(frameFullContent, String.valueOf("#$B+"+KermitFrame.EOL));
		    		break;
		    	default:
		    		failMessage = "Response count exceeds expected number of answers";
		    		responseCount = RESPONSE_FAIL;
		    		break;
		    	}
				}
				responseCount++;
			}
		}
	}
	
	public void testReceive() throws Exception{
		PoolListenerForReceiveTest poolListener = new PoolListenerForReceiveTest();

		try{
			Vitros250Driver.RESPONSE_TIMEOUT = 1000000;
			initPoolIfNeeded();
			pool.addMessageListener(poolListener);

	    responseCount = 0;
	    EmulatorDriver driver = (EmulatorDriver) emulator.getDriver();
			
	    if(driver != null){
	    	driver.setTimeOut(100000);
	    
	    	
	    	
	    	/*
	    	D
	    	1222291216
	    	Test 10        
	    	Sample2        
	    	3
	    	0
	    	~
	    	4
	    	1.000
	    	 ;    85.0 ;0;0;0;IT}       ->32 GLU
	    	&;NO RESULT;0;6;0;MEPF}     ->38 AMYL
	    	/;    46.44;2;0;0;IT}       ->47 BUN
	    	.;     1.2 ;0;0;0;IT}       ->46 CREA
	    	2;     9.7 ;0;0;0;IT}       ->50 Ca
	    	-;   180.0 ;1;2;0;IT}       ->45 NH3
	    	);NO RESULT;0;6;1;ERITFC}   ->41 Na+
	    	(;   1000.0;0;5;3;ORITUC}   ->40 K+
	    	
	    	
	    	
	    	*/
	    }
	      
	    while(responseCount < 5){
	    	Thread.sleep(1000);
	    }
	    
	    pool.removeMessageListener(poolListener);
	    pool.dispose();
	    pool = null;
	
	    if(responseCount >= RESPONSE_FAIL){
	    	fail(failMessage);
	    }
	    
		}catch(Exception e){
	    pool.removeMessageListener(poolListener);
	    pool.dispose();
	    pool = null;
	    
			throw e;
		}
	}
	
	//-----------------------------------------------
	//- Message listener to verify the sending of L3Messsage at poool reception.  
	//-----------------------------------------------
	private class PoolListenerForReceiveTest implements MessageListener{
		public void messageReceived(L3Message message) {
			if(message.getInstrumentCode().compareTo(DRIVER_CODE) == 0){
				
				Iterator sIter = message.sampleIterator();
				while(sIter != null && sIter.hasNext()){
					L3Sample sam = (L3Sample)sIter.next();
					Iterator tIter = sam.testIterator();
					while(tIter != null && tIter.hasNext()){
						L3Test test = (L3Test)tIter.next();
						if(test != null){
							switch(responseCount){
							case 0:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "GLU85.0true");
								break;
							case 1:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "AMYL0.0false");
								break;
							case 2:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "BUN46.44true");
								break;
							case 3:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "CREA1.2true");
								break;
							case 4:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "Ca9.7true");
								break;
							case 5:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "NH3180.0false");
								break;
							case 6:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "Na+0.0false");
								break;
							case 7:
								localAssertion(test.getLabel()+test.getValue()+test.isResultOk(), "K+1000.0false");
								break;
							}
							responseCount++;
						}
					}
				}
			}
		}
	}
	
}