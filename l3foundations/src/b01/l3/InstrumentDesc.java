/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import java.util.Iterator;

import javax.comm.SerialPort;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FMultipleChoiceFieldStringBased;
import b01.foc.list.FocLink;
import b01.foc.list.FocLinkOne2N;
import b01.foc.list.FocLinkSimple;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

/**
 * @author 01Barmaja
 */
public class InstrumentDesc extends FocDesc {

	public static final int FLD_CODE                         = 1;
	public static final int FLD_NAME                         = 2;
	public static final int FLD_CONNECTED                    = 3;
	public static final int FLD_DRIVER_CLASS_NAME            = 4;
	public static final int FLD_TEST_LIST                    = 5;
	public static final int FLD_PROPERTIES_FILE_PATH         = 6;
	public static final int FLD_SUPPORTED_TEST_LIST          = 7;
  public static final int FLD_MODE                         = 8;
  public static final int FLD_WAIT_FOR_RESULT_CONFIRMATION = 10;
  public static final int FLD_ON_HOLD                      = 12;
  
  public static final int FLD_DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND           = 13 ;
  public static final int FLD_DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH = 14 ;
  public static final int FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE          = 15 ;
  public static final int FLD_DELAY_TO_TRY_LATER                             = 16 ;
  public static final int FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE             = 17 ;
  
  public static final int FLD_SERIAL_PORT_NAME             = 18 ;
  public static final int FLD_SERIAL_BAUDE_RATE            = 19 ;
  public static final int FLD_SERIAL_PARITY                = 20 ;
  public static final int FLD_SERIAL_DATA_BITS             = 21 ;
  public static final int FLD_SERIAL_STOP_BIT              = 22 ;
  
  public static final int FLD_IS_EMULATOR                  = 23 ;
  public static final int FLD_RELATED_INSTRUMENT           = 24 ;
  public static final int FLD_SERVICE_PORT                 = 25 ;
  
  public static final int FLD_LAUNCHED                     = 26;
  
  public static final String FNAME_CODE                    = "CODE";
  public static final String FNAME_ON_HOLD                 = "ON_HOLD";
  
	public static final int INSTRUMENT_MODE_MANUEL           = 0;// may be declared in Instrument
	public static final int INSTRUMENT_MODE_AUTOMATIC        = 1;
	
  public static final int LEN_NAME  = 30;
  
	private FocLink instrumentSupportedTestLink = null;
	  
	public  FocLink getInstrumentSupportedTestLink(){
		if(instrumentSupportedTestLink == null){
			instrumentSupportedTestLink = new FocLinkOne2N(this , TestLabelMap.getFocDesc());
		}
		return instrumentSupportedTestLink; 
	}
	
  public static FCharField newCodeField(int fldID, String fieldName){
    return new FCharField(fieldName, "Code", fldID, true, 10);
  }
  
	public InstrumentDesc(){
		super(Instrument.class, FocDesc.DB_RESIDENT, "INSTRUMENT", true);

    FField focFld = addReferenceField();
    
    setConcurrenceLockEnabled(true);
    concurrenceLockView_AddField(FLD_NAME);
    
    focFld = newCodeField(FLD_CODE, FNAME_CODE);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("NAME", "Name", FLD_NAME, false, 30);
    addField(focFld);
    focFld = new FBoolField("CONNECTED", "Connected", FLD_CONNECTED, false);
    //focFld.addListener(getConnectedPropertyListener());
    addField(focFld);
    
    FMultipleChoiceFieldStringBased mcsFld = new FMultipleChoiceFieldStringBased("DRIVER_CLASS_NAME", "Driver class name", FLD_DRIVER_CLASS_NAME, false, 120);
    Iterator<String> iter = DriverFactory.getInstance().newClassNameIterator();
    while(iter != null && iter.hasNext()){
    	mcsFld.addChoice(iter.next());
    }
    addField(mcsFld);
    
    focFld = new FCharField("PROPERTIES_FILE_PATH", "Properties file", FLD_PROPERTIES_FILE_PATH, false, 250);
    addField(focFld);

    FMultipleChoiceField multiFld = new FMultipleChoiceField("MODE_O", "Mode", FLD_MODE, false, 1);
    multiFld.addChoice(INSTRUMENT_MODE_MANUEL, "Manuel");
    multiFld.addChoice(INSTRUMENT_MODE_AUTOMATIC, "Automatic");
    addField(multiFld);
    focFld = new FBoolField("WAIT_FOR_RESULT_CONFIRMATION", "Wait for result confirmation", FLD_WAIT_FOR_RESULT_CONFIRMATION, false);
    addField(focFld);
    
    focFld = new FBoolField(FNAME_ON_HOLD, "On hold", FLD_ON_HOLD, false);
    focFld.addListener(getOnHoldPropertyListener());
    addField(focFld);

    focFld = new FIntField("DLY_DB_SAMPLES_2SEND", "Polling delay for checking DB samples to send to instrument (ms)", FLD_DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND, false, 10);
    addField(focFld);
    focFld = new FIntField("DLY_SAMPLE_DISP_REFRESH", "Sample display list refresh delay (ms)", FLD_DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH, false, 10);
    addField(focFld);
    focFld = new FIntField("DLY_FOR_RESERVE_RETRY", "Delay for driver reserve retry (ms)", FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE, false, 10);
    addField(focFld);
    focFld = new FIntField("DLY_FOR_RETRY_LATER", "Delay for retry later comming from instrument (ms)", FLD_DELAY_TO_TRY_LATER, false, 10);
    addField(focFld);
    focFld = new FIntField("DLY_FOR_TIME_OUT", "Delay for driver time out waiting for response (ms)", FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE, false, 10);
    addField(focFld);
    
    focFld = new FCharField("COM_PORT", "Com port", FLD_SERIAL_PORT_NAME, false, 10);
    addField(focFld);
    
    FMultipleChoiceField mFld = new FMultipleChoiceField("COM_BAUDE_RATE", "Baude rate", FLD_SERIAL_BAUDE_RATE, false, 10);
    mFld.addChoice(1200, "1200");
    mFld.addChoice(2400, "2400");
    mFld.addChoice(4800, "4800");
    mFld.addChoice(9600, "9600");
    mFld.addChoice(19200, "19200");
    addField(mFld);

    mFld = new FMultipleChoiceField("COM_PARITY", "Parity", FLD_SERIAL_PARITY, false, 2);
    mFld.addChoice(SerialPort.PARITY_NONE , "NONE");
    mFld.addChoice(SerialPort.PARITY_EVEN , "EVEN");
    mFld.addChoice(SerialPort.PARITY_ODD  , "ODD");
    mFld.addChoice(SerialPort.PARITY_MARK , "MARK");
    mFld.addChoice(SerialPort.PARITY_SPACE, "SPACE");
    addField(mFld);
    
    mFld = new FMultipleChoiceField("COM_DATA_BITS", "Data bits", FLD_SERIAL_DATA_BITS, false, 2);
    mFld.addChoice(5, "5");
    mFld.addChoice(6, "6");
    mFld.addChoice(7, "7");
    mFld.addChoice(8, "8");
    addField(mFld);
    
    mFld = new FMultipleChoiceField("COM_STOP_BIT", "Stop bit", FLD_SERIAL_STOP_BIT, false, 2);
    mFld.addChoice(1, "1");
    mFld.addChoice(3, "15");
    mFld.addChoice(2, "2");
    addField(mFld);
    
    focFld = new FBoolField("IS_EMUL", "Is emulator for instrument", FLD_IS_EMULATOR, false);
    addField(focFld);

    FCharField cFld = new FCharField("RELATED_INSTR", "Related instrument", FLD_RELATED_INSTRUMENT, false, 30);
    addField(cFld);
    
    FIntField iFld = new FIntField("PORT", "Host port", FLD_SERVICE_PORT, false, 4);
    addField(iFld);
    
    focFld = new FBoolField("LAUNCHED", "Launched", FLD_LAUNCHED, false);
    focFld.setDBResident(false);
    addField(focFld);
	}
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

	private static FocList list = null;
  
  public FocList newList(){
    FocList list = null;
    FocLinkSimple link = new FocLinkSimple(Instrument.getFocDesc());
    
    list = new FocList(null, link, null);
    list.setDirectImpactOnDatabase(false);
    list.setDirectlyEditable(false);
    
    FocListOrder order = new FocListOrder(InstrumentDesc.FLD_CODE);
    list.setListOrder(order);
    
    return list;
  }
  
  public FocList getList(int mode){
    if(list == null){
      list = newList();
    }
    
    if(mode == FocList.LOAD_IF_NEEDED){
      list.loadIfNotLoadedFromDB();
    }

    return list;
  }
  
  //oooooooooooooooooooooooooooooooooo
	
  public FPropertyListener getOnHoldPropertyListener(){
		return new FPropertyListener(){
			public void dispose() {
			}

			public void propertyModified(FProperty property) {
				if(property != null){
      		Instrument instrument = (Instrument) property.getFocObject();
      		Instrument.adjustColor(instrument, FLD_LAUNCHED, FLD_CONNECTED, FLD_ON_HOLD);
        }
			}
		};
  }
}
