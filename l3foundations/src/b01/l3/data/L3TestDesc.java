/*
 * Created on Jun 5, 2006
 */
package b01.l3.data;

import java.awt.Color;

import b01.foc.Globals;
import b01.foc.db.SQLUpdate;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FNumField;
import b01.foc.desc.field.FObjectField;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;

/**
 * @author 01Barmaja
 */
public class L3TestDesc extends FocDesc {

	public static final int FLD_LABEL                = 1;
	public static final int FLD_DESCRIPTION          = 2;
	public static final int FLD_VALUE                = 3;
	public static final int FLD_RESULT_OK            = 4;
	public static final int FLD_UNIT_LABEL           = 5;
	public static final int FLD_MESSAGE              = 6;
	public static final int FLD_STATUS               = 7;
	public static final int FLD_BLOCKED              = 8;
	public static final int FLD_SUGGESTED_INSTRUMENT = 9;
	public static final int FLD_DISPATCH_INSTRUMENT  = 10;
	public static final int FLD_RECEIVE_INSTRUMENT   = 11;
	public static final int FLD_SAMPLE               = 12;
	public static final int FLD_ALARM                = 13;
	public static final int FLD_PRIORITY             = 14;
	public static final int FLD_VERIFICATION_PENDING = 15;
	public static final int FLD_VALUE_NOTES          = 16;//Values in text added for the Cobas U601 because all results are descriptive.
	
	public static final String TABLE_NAME          = "L3TEST"     ;
	
	public static final String FNAME_SAMPLE_PREFIX = "SAMPLE_"    ;
	public static final String FNAME_VALUE         = "VALUE"      ;
	public static final String FNAME_VALUE_NOTES   = "NOTES"      ;
	public static final String FNAME_RESULT_OK     = "RESULT_OK"  ;
	public static final String FNAME_STATUS        = "STATUS"     ;
	public static final String FNAME_BLOCKED       = "BLOCKED"    ;
	public static final String FNAME_MESSAGE       = "MESSAGE"    ;
	public static final String FNAME_UNIT          = "UNIT_LABEL" ;
	public static final String FNAME_ALARM         = "ALARM"      ;
	public static final String FNAME_PRIORITY             = "PRIORITY"             ;
	public static final String FNAME_VERIFICATION_PENDING = "VERIFICATION_PENDING" ;
	
	public static final int LEN_TEST_LABEL       = 15;
  public static final int LEN_TEST_DESCRIPTION = 25;	
  public static final int LEN_VALUE_NOTES      = 40;
  public static final int LEN_VALUE            = 10;
  public static final int LEN_VALUE_DECIMAL    = 4 ;
  public static final int LEN_UNIT_LABEL       = 10;
  public static final int LEN_MESSAGE          = 100;
	
  public static final int TEST_STATUS_AVAILABLE_IN_L3 = 0;
  //public static final int SAMPLE_STATUS_READY_TO_BE_SENT = 1;
  public static final int TEST_STATUS_SENDING_TO_INSTRUMENT = 2;
  public static final int TEST_STATUS_ANALYSING = 3;
  public static final int TEST_STATUS_RESULT_AVAILABLE = 4;
  public static final int TEST_STATUS_COMMITED_TO_LIS = 5;  
  public static final int TEST_STATUS_NOT_IN_USE = 6;
  public static final int TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED = 7;
  public static final int TEST_STATUS_NOT_IN_LIS_WHEN_COMMITING_RESULT = 8;
  public static final int TEST_STATUS_MULTIPLE_LIS_ENTRIES_UPON_COMMIT = 9;
  public static final int TEST_STATUS_ERROR_WIHLE_COMMIT_TO_LIS        = 10;
  public static final int TEST_STATUS_NOT_ASSIGNED = 99;
  
  public static final int TEST_RESULT_LESS_THAN = -1;
  public static final int TEST_RESULT_EMPTY_ALARM = 0;
  public static final int TEST_RESULT_GREATER_THAN = 1;
  
  public static final double VALUE_NULL = -99999;
  
	public L3TestDesc(){
		super(L3Test.class, FocDesc.DB_RESIDENT, TABLE_NAME, true);

    FField focFld = addReferenceField();
    
    focFld = new FCharField("LABEL", "Label", FLD_LABEL, true, LEN_TEST_LABEL);
    addField(focFld);
    focFld = new FCharField("DESCRIP", "Description", FLD_DESCRIPTION, false, LEN_TEST_DESCRIPTION);
    addField(focFld);
    focFld = new FNumField (FNAME_VALUE, "Value", FLD_VALUE, false, LEN_VALUE, LEN_VALUE_DECIMAL);
    addField(focFld);
    focFld = new FCharField (FNAME_VALUE_NOTES, "Notes", FLD_VALUE_NOTES, false, LEN_VALUE_NOTES);
    addField(focFld);
    focFld = new FBoolField(FNAME_RESULT_OK, "Result ok", FLD_RESULT_OK, false);
    addField(focFld);
    focFld = new FCharField(FNAME_UNIT, "Unit label", FLD_UNIT_LABEL, false, 10);
    addField(focFld);
    focFld = new FCharField(FNAME_MESSAGE, "Message", FLD_MESSAGE, false, LEN_MESSAGE);
    addField(focFld);	
    
    FMultipleChoiceField statusFld = new FMultipleChoiceField(FNAME_STATUS, "Status", FLD_STATUS, false, 2);
    fillMultipleChoiceStatusField(statusFld);
    addField(statusFld);

    FBoolField blockedFld = new FBoolField(FNAME_BLOCKED, "Blocked", FLD_BLOCKED, false);
    addField(blockedFld);
    
    FObjectField objFld = new FObjectField("INST_SUGG", "Suggested Instr.", FLD_SUGGESTED_INSTRUMENT, false, Instrument.getFocDesc(), "I_SUGG");
    objFld.setComboBoxCellEditor(InstrumentDesc.FLD_CODE);
    objFld.setSelectionList(Instrument.getList(FocList.NONE));
    objFld.setWithList(false);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setDisplayNullValues(false);
    addField(objFld);

    objFld = new FObjectField("INSTRUMENT", "Dispatch Instr.", FLD_DISPATCH_INSTRUMENT, false, Instrument.getFocDesc(), "INSTR_", this, InstrumentDesc.FLD_TEST_LIST);
    objFld.setComboBoxCellEditor(InstrumentDesc.FLD_CODE);
    objFld.setSelectionList(Instrument.getList(FocList.NONE));
    objFld.setWithList(false);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setDisplayNullValues(false);
    addField(objFld);

    objFld = new FObjectField("INST_REC", "Reception Instr.", FLD_RECEIVE_INSTRUMENT, false, Instrument.getFocDesc(), "I_REC");
    objFld.setComboBoxCellEditor(InstrumentDesc.FLD_CODE);
    objFld.setSelectionList(Instrument.getList(FocList.NONE));
    objFld.setWithList(false);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    objFld.setDisplayNullValues(false);
    addField(objFld);

    objFld = new FObjectField("SAMPLE", "Sample", FLD_SAMPLE, true, L3Sample.getFocDesc(), FNAME_SAMPLE_PREFIX, this, L3SampleDesc.FLD_TEST_LIST);
    objFld.setComboBoxCellEditor(L3SampleDesc.FLD_ID);
    objFld.setSelectionList(null);
    objFld.setWithList(false);
    objFld.setNullValueMode(FObjectField.NULL_VALUE_ALLOWED_AND_SHOWN);
    addField(objFld);
    
    FMultipleChoiceField alarmFld = new FMultipleChoiceField(FNAME_ALARM, "Alarm", FLD_ALARM, false, 3);
    fillMultipleChoiceAlarmField(alarmFld);
    addField(alarmFld);
    
    focFld = new FCharField(FNAME_PRIORITY, "Priority", FLD_PRIORITY, false, 1);
    addField(focFld);	
    
    focFld = new FBoolField(FNAME_VERIFICATION_PENDING, "Verif.Pending", FLD_VERIFICATION_PENDING, false);
    addField(focFld);	

    statusFld.addListener(new FPropertyListener(){
			public void dispose() {
			}

			public void propertyModified(FProperty property) {
        FocObject test = (FocObject) property.getFocObject();
        if(test != null){
	        int fldShift = property.getFocField().getID() > L3SampleTestJoinDesc.FLD_TEST_FIELDS_START ? L3SampleTestJoinDesc.FLD_TEST_FIELDS_START : 0;
	        if (!test.getPropertyBoolean(fldShift+FLD_BLOCKED)){
	          switch (test.getPropertyInteger(fldShift+FLD_STATUS)) {
	          case L3TestDesc.TEST_STATUS_AVAILABLE_IN_L3:
	            test.getFocProperty(fldShift+L3TestDesc.FLD_LABEL).setBackground(Color.CYAN);
	            break;
	          case L3TestDesc.TEST_STATUS_SENDING_TO_INSTRUMENT:
	            test.getFocProperty(fldShift+L3TestDesc.FLD_LABEL).setBackground(Color.GREEN);
	            break;
	          case L3TestDesc.TEST_STATUS_ANALYSING:
	            test.getFocProperty(fldShift+L3TestDesc.FLD_LABEL).setBackground(Color.YELLOW);
	            break;
	          case L3TestDesc.TEST_STATUS_RESULT_AVAILABLE:
	            test.getFocProperty(fldShift+L3TestDesc.FLD_LABEL).setBackground(Color.LIGHT_GRAY);
	            break;
	          case L3TestDesc.TEST_STATUS_COMMITED_TO_LIS:
	            test.getFocProperty(fldShift+L3TestDesc.FLD_LABEL).setBackground(Color.GRAY);
	            break;
	          case L3TestDesc.TEST_STATUS_NOT_IN_USE:
	            test.getFocProperty(fldShift+L3TestDesc.FLD_LABEL).setBackground(Color.DARK_GRAY);
	            break;
	          default:
	            break;
	          }
	        }
        }
			}
    });
    
    blockedFld.addListener(new FPropertyListener(){
			public void dispose() {
			}

			public void propertyModified(FProperty property) {
        if(property != null){
          FocObject sample = (FocObject) property.getFocObject();
          int fldShift = property.getFocField().getID() > L3SampleTestJoinDesc.FLD_TEST_FIELDS_START ? L3SampleTestJoinDesc.FLD_TEST_FIELDS_START : 0;

          if (sample.getPropertyBoolean(fldShift + FLD_BLOCKED)){
            sample.getFocProperty(fldShift + FLD_LABEL).setBackground(Color.RED);
          }
        }
			}
    });
	}

	public static L3Test newL3Test(){
		FocConstructor constr = new FocConstructor(L3Test.getFocDesc(), null);
		L3Test test = (L3Test) constr.newItem();
		constr.dispose();
		return test;
	}

	public static L3Test newL3Test(int ref){
		L3Test test = newL3Test();
		test.setReference(ref);
		test.load();
		return test;
	}

	public static void updateStatus(int ref, int status){
		try{
			L3Test test = newL3Test(ref);
			test.setStatus(status);
			SQLUpdate update = new SQLUpdate(L3Test.getFocDesc(), test);
			update.addQueryField(FLD_STATUS);
			update.execute();
			test.dispose();
			/*
			Statement stm = Globals.getDBManager().lockStatement();
			String req = "UPDATE "+TABLE_NAME+" SET "+FNAME_STATUS+"="+status+" WHERE (REF="+ref+")";
			Globals.logString(req);
			stm.execute(req);
			Globals.getDBManager().unlockStatement(stm);
			*/
		}catch(Exception e){
			Globals.logException(e);
		}
	}
	
	public static void updateBlockedFlag(int ref, boolean blockedValue){
		try{
			L3Test test = newL3Test(ref);
			test.setBlocked(blockedValue);
			SQLUpdate update = new SQLUpdate(L3Test.getFocDesc(), test);
			update.addQueryField(FLD_BLOCKED);
			update.execute();
			test.dispose();
		}catch(Exception e){
			Globals.logException(e);
		}
	}
	
	public static void fillMultipleChoiceStatusField(FMultipleChoiceField statusFld){
    statusFld.addChoice(TEST_STATUS_AVAILABLE_IN_L3, "1-Available in L3");
    statusFld.addChoice(TEST_STATUS_SENDING_TO_INSTRUMENT, "2-Sending");
    statusFld.addChoice(TEST_STATUS_ANALYSING, "3-Waiting for result");
    statusFld.addChoice(TEST_STATUS_RESULT_AVAILABLE, "4-Result available");
    statusFld.addChoice(TEST_STATUS_COMMITED_TO_LIS, "5-Result commited");
    statusFld.addChoice(TEST_STATUS_NOT_IN_USE, "6-Not in use");
    statusFld.addChoice(TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED, "7-Originally not in L3");
    statusFld.addChoice(TEST_STATUS_NOT_IN_LIS_WHEN_COMMITING_RESULT, "8-Not in LIS when commiting results");
    statusFld.addChoice(TEST_STATUS_ERROR_WIHLE_COMMIT_TO_LIS, "10-Error while commit to LIS");    
    statusFld.addChoice(TEST_STATUS_NOT_ASSIGNED, "X-Not Assigned ");
	}
	
	public static void fillMultipleChoiceAlarmField(FMultipleChoiceField alarmFld){
		alarmFld.addChoice(TEST_RESULT_LESS_THAN, "Less than");
		alarmFld.addChoice(TEST_RESULT_EMPTY_ALARM, "");
		alarmFld.addChoice(TEST_RESULT_GREATER_THAN, "Higher than");
	}
	    
}
