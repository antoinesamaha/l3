package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.list.FocList;
import b01.l3.data.L3SampleDesc;

public class LisInstrumentMessageDesc extends FocDesc{

  public static final int FLD_INSTRUMENT_CODE = 1;
  public static final int FLD_MESSAGE         = 2;
  public static final int FLD_STATUS          = 3;
  public static final int FLD_SAMPLE_ID       = 4;
  
  public static final String FNAME_SAMPLE_ID       = "SAMPLE_ID"      ;
  public static final String FNAME_INSTRUMENT_CODE = "INSTRUMENT_CODE";
  public static final String FNAME_MESSAGE         = "MESSAGE"        ;
  public static final String FNAME_STATUS          = "STATUS"         ;
  
  public LisInstrumentMessageDesc() {
    super(LisInstrumentMessage.class, FocDesc.DB_RESIDENT, "LIS_INSTR_MESSAGE", true);

    FIntField focFld = new FIntField(FNAME_SAMPLE_ID, "Sample Id", FLD_SAMPLE_ID, true, L3SampleDesc.SAMPLE_ID_LENGTH);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    FField fField = new FCharField(FNAME_INSTRUMENT_CODE, "Instrument Code", FLD_INSTRUMENT_CODE, true, 10);
    fField.setMandatory(true);
    addField(fField);
    
    fField = new FCharField(FNAME_MESSAGE, "Message", FLD_MESSAGE, false, 250);
    addField(fField);
    
    FMultipleChoiceField mltipleChoiceFld = new FMultipleChoiceField(FNAME_STATUS, "Status", FLD_STATUS, false, L3SampleDesc.LEN_STATUS);
    mltipleChoiceFld.addChoice(LisTestDesc.STATUS_TEST_TO_READ             , "0:SampleToRead");
    mltipleChoiceFld.addChoice(LisTestDesc.STATUS_TEST_READ_BY_L3          , "1:SampleRedByL3");
    mltipleChoiceFld.addChoice(LisTestDesc.STATUS_TEST_RESULT_UPDATED_BY_L3, "2:ResultUpdatedByL3");
    mltipleChoiceFld.addChoice(LisTestDesc.STATUS_TEST_RESULT_COMMITED     , "3:ResultCommited");
    mltipleChoiceFld.addChoice(LisTestDesc.STATUS_BLOCKED_FOR_ERROR        , "9:Blocked for Error");
    mltipleChoiceFld.setSortItems(true);
    addField(mltipleChoiceFld);
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static FocList list = null;
  public static FocList getList(int mode){
    list = getInstance().getList(list, mode);
    list.setDirectlyEditable(true);
    list.setDirectImpactOnDatabase(false);
    return list;    
  }
    
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/LisInstrumentMessageDesc();
    }
    return focDesc;
  }
}
