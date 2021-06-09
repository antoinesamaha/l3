package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.desc.field.FNumField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.l3.data.L3SampleDesc;
import b01.l3.data.L3TestDesc;

public class LisTestDesc extends FocDesc{

  public static final int FLD_SEQUENCE_ID      = 1;
  public static final int FLD_SAMPLE_ID        = 2;
  public static final int FLD_TEST_CODE        = 3;
  public static final int FLD_TEST_DESC        = 4;
  public static final int FLD_STATUS           = 5;
  public static final int FLD_ANALYSER_CODE    = 6;
  public static final int FLD_RESULT           = 7;
  public static final int FLD_UNIT             = 8;
  public static final int FLD_MESSAGE          = 9;
  public static final int FLD_ACTUAL_TEST_CODE = 10;
  public static final int FLD_ACTUAL_ANALYSER_CODE = 11;
  public static final int FLD_ALARM 			   = 12;
  public static final int FLD_PRIORITY             = 13;
  public static final int FLD_VERIFICATION_PENDING = 14;
  public static final int FLD_RESULT_NOTES         = 15;
  
  public static final int STATUS_TEST_TO_READ              = 0;
  public static final int STATUS_TEST_READ_BY_L3           = 1;
  public static final int STATUS_TEST_RESULT_UPDATED_BY_L3 = 2;
  public static final int STATUS_TEST_RESULT_COMMITED      = 3;
  public static final int STATUS_BLOCKED_FOR_ERROR         = 9;

  public LisTestDesc() {
    super(LisTest.class, FocDesc.DB_RESIDENT, "LISTEST", true);
    setGuiBrowsePanelClass(LisTestGuiBrowsePanel.class);
    
    FField focFld = new FIntField("SEQ_ID", "ID", FLD_SEQUENCE_ID, false, 15);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FIntField ("ACTUAL_TEST_ID", "Actual Test Id", FLD_ACTUAL_TEST_CODE, false, 15);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FIntField("SAMPLE_ID", "Sample Id", FLD_SAMPLE_ID, true, L3SampleDesc.SAMPLE_ID_LENGTH);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("TEST_CODE", "Test Code", FLD_TEST_CODE, true, 15);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("TEST_DESC", "Test Desc", FLD_TEST_DESC, false, 50);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FMultipleChoiceField mltipleChoiceFld = newStatusField(FLD_STATUS);
    addField(mltipleChoiceFld);
    
    focFld = new FCharField("ANALYZER_CODE", "Analyzer Code", FLD_ANALYSER_CODE, false, 10);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("ACTUAL_ANALYZER_CODE", "Actual Analyzer Code", FLD_ACTUAL_ANALYSER_CODE, false, 10);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FMultipleChoiceField alarmFld = new FMultipleChoiceField("ALARM", "Alarm", FLD_ALARM, false, 3);
    L3TestDesc.fillMultipleChoiceAlarmField(alarmFld);
    addField(alarmFld);
    
    focFld = new FNumField("RESULT", "Result", FLD_RESULT, false, L3TestDesc.LEN_VALUE, L3TestDesc.LEN_VALUE_DECIMAL);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("NOTES", "Notes", FLD_RESULT_NOTES, false, L3TestDesc.LEN_VALUE_NOTES);
    addField(focFld);
    
    focFld = new FCharField("UNIT", "Unit", FLD_UNIT, false, L3TestDesc.LEN_UNIT_LABEL);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("MESSAGE", "Message", FLD_MESSAGE, false, 512);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("PRIORITY", "Priority", FLD_PRIORITY, false, 1);
    addField(focFld);	
    
    focFld = new FBoolField("VERIFICATION_PENDING", "Verif.Pending", FLD_VERIFICATION_PENDING, false);
    addField(focFld);	
  }

  public static FMultipleChoiceField newStatusField(int fldID){
    FMultipleChoiceField mltipleChoiceFld = new FMultipleChoiceField("STATUS", "Status", fldID, false, L3SampleDesc.LEN_STATUS);
	  mltipleChoiceFld.addChoice(STATUS_TEST_TO_READ             , "0:SampleToRead");
	  mltipleChoiceFld.addChoice(STATUS_TEST_READ_BY_L3          , "1:SampleRedByL3");
	  mltipleChoiceFld.addChoice(STATUS_TEST_RESULT_UPDATED_BY_L3, "2:ResultUpdatedByL3");
	  mltipleChoiceFld.addChoice(STATUS_TEST_RESULT_COMMITED     , "3:ResultCommited");
	  mltipleChoiceFld.addChoice(STATUS_BLOCKED_FOR_ERROR        , "9:Blocked for Error");
	  mltipleChoiceFld.setSortItems(true);
    return mltipleChoiceFld;
  }
  
  private static FocList list = null;
  public static FocList getList(int mode){
    list = getInstance().getList(list, mode);
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    if(list.getListOrder() == null){
      FocListOrder order = new FocListOrder(FLD_SAMPLE_ID);
      list.setListOrder(order);
    }
    return list;    
  }
  
  private static FocDesc focDesc = null;
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/ LisTestDesc();
    }
    return focDesc;
  }
}
