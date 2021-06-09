package b01.l3.drivers.roches;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;

import b01.l3.Instrument;
import b01.l3.MessageListener;
import b01.l3.PoolKernel;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.unit.EmulatorDriver;
import junit.framework.TestCase;

public class UnitCobas400IntegraDriver extends TestCase {
	public int count =0;
	private Throwable exception = null;
	private Throwable thr = null;
	
	private final static String COBAS_CODE = "Cobas";
	private final static String COBAS_PORT = "COM1";
	private final static String EMULATOR_CODE = "Emulator";
	private final static String EMULATOR_PORT = "COM4";
	
	private PoolKernel createPool() throws Exception{
		PoolKernel pool = new PoolKernel();
		
		Properties props = new Properties();
		props.put("instrument.code", COBAS_CODE);
		
		props.put("instrument.name", COBAS_CODE);
    props.put("instrument.driver", "b01.l3.drivers.roches.Cobas400IntegraDriver");

		props.put("serialPort.name", COBAS_PORT);
		props.put("serialPort.baudrate", "4800");
		props.put("serialPort.databits", "8");
		props.put("serialPort.stopbit", "1");
		props.put("serialPort.parity", "NONE");
		
		props.put("test.glucose", "71");
		props.put("test.lactose", "222");

		Instrument instr = new Instrument(props);			
				
		props.put("instrument.code", EMULATOR_CODE);
		props.put("instrument.name", EMULATOR_CODE);
    props.put("instrument.driver", "b01.l3.drivers.unit.EmulatorDriver");
		props.put("serialPort.name", EMULATOR_PORT);
		Instrument emulator = new Instrument(props);
		EmulatorDriver emulatorDriver = (EmulatorDriver) emulator.getDriver();
		emulatorDriver.setAnswerFrame(new Cobas400Frame(emulator));
		
		pool.addInstrument(instr);
		pool.addInstrument(emulator);
		
		return pool;
	}
	
	public void aaatestSend() throws Throwable {
		
    Cobas400IntegraDriver.RESPONSE_TIME_OUT=10000000;
    Cobas400IntegraDriver.TIME_DELAY_BETWEEN_RESULT_REQUESTS = 3000;

    PoolKernel pool = createPool();

    pool.addMessageListener(new MessageListener(){
			public void messageReceived(L3Message message) {
				
				if(message.getInstrumentCode().compareTo(EMULATOR_CODE) == 0){					
					
					Iterator iter = message.sampleIterator();
					while(iter != null && iter.hasNext()){
						L3Sample sample = (L3Sample)iter.next();
						String frameFullContent = sample.getId();
						switch(count){
						case 0:
							try{
								assertEquals(frameFullContent,String.valueOf((char)Cobas400IntegraDriver.SOH)+(char)Cobas400IntegraDriver.LF+"14 COBAS INTEGRA    00"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF);
							 	
							}catch(Throwable e){
								
								exception = e;
							}
							break;
						case 1:
							
							try{
								System.out.println("count avant assert2"+ count);  
								assertEquals(frameFullContent,String.valueOf((char)Cobas400IntegraDriver.SOH)+(char)Cobas400IntegraDriver.LF+"14 COBAS INTEGRA    10"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+"53 idsmp1          29/07/2006"+Cobas400IntegraDriver.LF+"54   0  0 S"+Cobas400IntegraDriver.LF+"55 111"+Cobas400IntegraDriver.LF+"55 222"+Cobas400IntegraDriver.LF+"53 idsmp2          29/07/2006"+Cobas400IntegraDriver.LF+"54   0  0 S"+Cobas400IntegraDriver.LF+"55 111"+Cobas400IntegraDriver.LF+"55 222"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+"1"+Cobas400IntegraDriver.LF+"656"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF);
								System.out.println("count apres assert2 "+ count);
								//assertEquals(frameFullContent,String.valueOf((char)Cobas400IntegraDriver.SOH)+(char)Cobas400IntegraDriver.LF+"14 COBAS INTEGRA    10"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+"53 idsmp1          29/07/2006"+Cobas400IntegraDriver.LF+"54   0  0 S"+Cobas400IntegraDriver.LF+"55 111"+Cobas400IntegraDriver.LF+"55 222"+Cobas400IntegraDriver.LF+"53 idsmp2          29/07/2006"+Cobas400IntegraDriver.LF+"54   0  0 S"+Cobas400IntegraDriver.LF+"55 111"+Cobas400IntegraDriver.LF+"55 222"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+"1"+Cobas400IntegraDriver.LF+"656"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF);
								
							
							}catch(Throwable e){
								
								System.out.println("catch 2"+Cobas400IntegraDriver.SOH);
								exception = e;
								
							}
							break;
							
							//assertEquals(frameFullContent,String.valueOf((char)Cobas400IntegraDriver.SOH)+(char)Cobas400IntegraDriver.LF+"14 COBAS INTEGRA    10"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+"53 idsmp1          29/07/2006"+Cobas400IntegraDriver.LF+"54   0  0 S"+Cobas400IntegraDriver.LF+"55 111"+Cobas400IntegraDriver.LF+"55 222"+Cobas400IntegraDriver.LF+"53 idsmp2          29/07/2006"+Cobas400IntegraDriver.LF+"54   0  0 S"+Cobas400IntegraDriver.LF+"55 111"+Cobas400IntegraDriver.LF+"55 222"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+"1"+Cobas400IntegraDriver.LF+"656"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF);
						}
						//b01.foc.Globals.logString(" UnitMessageListener = "+frameFullContent);
/*						
						
						14 COBAS INTEGRA    10
						
						53 idsmp1          29/07/2006
						54   0  0 S
						55 111
						55 222
						53 idsmp2          29/07/2006
						54   0  0 S
						55 111
						55 222
						
						1
						656
						
	*/					
						
						
					}
					count++;
				
				}
				
			}
    });

    Instrument instrument = pool.getInstrument(COBAS_CODE); 
    Cobas400IntegraDriver instrumentDriver = (Cobas400IntegraDriver) instrument.getDriver();
    instrumentDriver.setDisableLoopingForResults(true);
    
    Instrument emulator = pool.getInstrument(EMULATOR_CODE); 
		EmulatorDriver emulatorDriver = (EmulatorDriver) emulator.getDriver();

    String[] frm = {
    		
    		String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
									"09 INTEGRA 30-1051  00"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
									Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+"1"+Cobas400IntegraDriver.LF+"299"+
									Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF
									,
				
				String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
    							"09 INTDGRA 30-1051  19"+
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
    							"96 00"+
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+
    							"1"+Cobas400IntegraDriver.LF+
    							"557"+Cobas400IntegraDriver.LF+
    							Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF,
    };
    emulatorDriver.setHardCodedFrameList(frm);

    pool.connectAllInstruments();

    //Filling a test message
		//----------------------
		L3Message mess = new L3Message();
		
    L3Sample sample  = new L3Sample("idsmp1");
    sample.setFirstName("fname1");
    sample.setLastName("lname1");
    sample.setMiddleInitial("mid1");
    sample.setLiquidType(2);
    mess.addSample(sample);
    
    Calendar cal = Calendar.getInstance();
    cal.set(2006, 06, 29, 18, 15);
    sample.setDateAndTime(cal.getTimeInMillis());
    
    L3Test test = sample.addTest();
    test.setLabel("glucose");
    test.setValue(1);
    test.setResultOk(true);
    
    test = sample.addTest();
    test.setLabel("lactose");
    test.setValue(2);
    test.setResultOk(false);
   
    
    sample  = new L3Sample("idsmp2");
    sample.setFirstName("fname1");
    sample.setLastName("lname1");
    sample.setMiddleInitial("mid1");
    sample.setLiquidType(2);
    mess.addSample(sample);
    
    cal = Calendar.getInstance();
    cal.set(2006, 06, 29, 18, 15);
    sample.setDateAndTime(cal.getTimeInMillis());
    
    test = sample.addTest();
    test.setLabel("glucose");    
    test.setValue(1);
    test.setResultOk(true);   
    test = sample.addTest();
    test.setLabel("lactose");  
    test.setValue(2);
    test.setResultOk(false);
    mess.setInstrumentCode(EMULATOR_CODE);
    System.out.println("Instrument code au debut is "+ mess.getInstrumentCode()+ count);
    count = 0;
    exception = null;
    pool.send(COBAS_CODE, mess);
    while (count<2 && exception == null){
    	Thread.sleep(1000);
    };
    
    if(exception != null){
    	throw exception;
    }
    System.out.println ("Fin");
	}
	//####################################################################
	public void testReceive() throws Throwable {
		
    Cobas400IntegraDriver.RESPONSE_TIME_OUT=10000000;
    Cobas400IntegraDriver.TIME_DELAY_BETWEEN_RESULT_REQUESTS = 3000;
    
    PoolKernel pool = createPool();

    pool.addMessageListener(new MessageListener(){
			public void messageReceived(L3Message message) {
				Iterator iter = message.sampleIterator();
				while(iter != null && iter.hasNext()){
					L3Sample sample = (L3Sample)iter.next();
					String frameFullContent = sample.getId();
					b01.foc.Globals.logString(" UnitMessageListener = "+frameFullContent);
				}
			}
    });

    Instrument instrument = pool.getInstrument(COBAS_CODE); 
    
    Instrument emulator = pool.getInstrument(EMULATOR_CODE); 
		EmulatorDriver emulatorDriver = (EmulatorDriver) emulator.getDriver();

    String[] frm = {
    		
    		String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
				"09 INTEGRA 30-1051  00"+Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
				Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+"1"+Cobas400IntegraDriver.LF+"299"+
				Cobas400IntegraDriver.LF+Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF,
				
				
				String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
    							"09 INTDGRA 30-1051  04"+
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
    							"53 Order numero1## 27/06/1998 URL"+
    							Cobas400IntegraDriver.LF+
    							"55  71"+
    							Cobas400IntegraDriver.LF+
    							"00  0.700000E+30ouG/ML  84   0  22   0   0.000000E+00"+
    							    							
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+
    							"1"+Cobas400IntegraDriver.LF+
    							"459"+Cobas400IntegraDriver.LF+
    							Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF/*,
    							
    							
    							String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
    							"09 INTDGRA 30-1051  04"+
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
    							"53 Order numero1## 27/06/1998 URL"+
    							Cobas400IntegraDriver.LF+
    							"55  71"+
    							Cobas400IntegraDriver.LF+
    							"00  0.000000E+00G/ML    84   0  22   0   0.000000E+00"+
    							    							
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+
    							"0"+Cobas400IntegraDriver.LF+
    							"284"+Cobas400IntegraDriver.LF+
    							Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF,
    							
    							
    							String.valueOf(Cobas400IntegraDriver.SOH)+String.valueOf(Cobas400IntegraDriver.LF)+
    							"09 INTDGRA 30-1051  04"+
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.STX+Cobas400IntegraDriver.LF+
    							"53 Order numero1## 27/06/1998 URL"+
    							Cobas400IntegraDriver.LF+
    							"55  71"+
    							Cobas400IntegraDriver.LF+
    							"00  0.000000E+00G/ML    84   0  22   0   0.000000E+00"+
    							    							
    							Cobas400IntegraDriver.LF+Cobas400IntegraDriver.ETX+Cobas400IntegraDriver.LF+
    							"1"+Cobas400IntegraDriver.LF+
    							"285"+Cobas400IntegraDriver.LF+
    							Cobas400IntegraDriver.EOT+Cobas400IntegraDriver.LF*/
    							};
    emulatorDriver.setHardCodedFrameList(frm);

    pool.connectAllInstruments();
    
    instrument.addMessageListener(new MessageListener(){
			public void messageReceived(L3Message message) {
				count++;
				System.out.println("dans message received de instrument");
				System.out.println(message.toStringBuffer());
				
			//	System.out.println("Instrument code = "+ message.getInstrumentCode());;
				try{					
					assertEquals(message.getInstrumentCode(), "Cobas");				 					 	
				}catch(Throwable d){				
					thr = d;
				}				
			
				Iterator sampelItt = message.sampleIterator();
				L3Sample smp;
				L3Test tst;
				while (sampelItt.hasNext()){
					smp = (L3Sample)sampelItt.next();
					//System.out.println("Sample id :" + smp.getId());
					try{					
						assertEquals(smp.getId(), "Order numero1##");				 					 	
					}catch(Throwable d){				
						thr = d;
					}	
					
					try{
						//System.out.println("Sample date : "+ smp.getDateAndTime());
						//assertEquals(smp.getDateAndTime(),9.01531857796E+11);
						
					}catch(Throwable d){				
						thr = d;
					}	
					
					Iterator testItt = smp.testIterator();
					while (testItt.hasNext()){
						tst = (L3Test)testItt.next();
						//System.out.println("test label : "+tst.getLabel());
						try{					
							assertEquals(tst.getLabel(), "glucose");				 					 	
						}catch(Throwable d){				
							thr = d;
						}	
						//System.out.println("test value : "+tst.getValue());
						try{					
							assertEquals(tst.getValue(),7.0E29 );				 					 	
						}catch(Throwable d){				
							thr = d;
						}
						//System.out.println("test unit label : " + tst.getUnitLabel());
						try{					
							assertEquals(tst.getUnitLabel(),"ouG/ML" );				 					 	
						}catch(Throwable d){				
							thr = d;
						}
						
					}
				}
			}
    }); 
    
    thr=null;
    while (count<1 && thr==null){
    	System.out.println("cont "+count);
    	Thread.sleep(1000);
    }
    
    if (thr!=null){
    	throw thr;
    }
    
    
    /*
    //Cobas400IntegraDriver cbdr = new Cobas400IntegraDriver();
    
		
    System.out.println ("Fin");
		//fail("Not yet implemented");
		 *
		 */
	}
}
