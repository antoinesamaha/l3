package b01.l3.connector.dbConnector.lisConnectorTables;

import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FDateField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.list.FocList;
import b01.foc.list.FocListOrder;
import b01.l3.data.L3SampleDesc;

public class LisSampleDesc extends FocDesc{

  public static final int FLD_CURRENT_DATE_TIME      = 1;
  public static final int FLD_SAMPLE_ID              = 2;
  public static final int FLD_LIQUID_TYPE            = 3;
  public static final int FLD_COLLECTION_DATE        = 4;
  public static final int FLD_PATIENT_FIRST_NAME     = 5;
  public static final int FLD_PATIENT_LAST_NAME      = 6;
  public static final int FLD_PATIENT_MIDDLE_INITIAL = 7;
  public static final int FLD_PATIENT_AGE            = 8;
  public static final int FLD_PATIENT_SEX            = 9;
  public static final int FLD_PATIENT_ID             =10;
  public static final int FLD_DATE_OF_BIRTH          =11;
  
  public LisSampleDesc() {
    super(LisSample.class, FocDesc.DB_RESIDENT, "LISSAMPLE", false);
    setGuiBrowsePanelClass(LisSampleGuiBrowsePanel.class);
    
    FField focFld = new FDateField("CURRENT_DATE_TIME", "Current Date Time", FLD_CURRENT_DATE_TIME, false);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("SAMPLE_ID", "Sample id", FLD_SAMPLE_ID, false, L3SampleDesc.SAMPLE_ID_LENGTH);
    focFld.setLockValueAfterCreation(true);
    focFld.setMandatory(true);
    addField(focFld);
    
    focFld = new FCharField("SAMPLE_TYPE", "Liquid Type", FLD_LIQUID_TYPE, false, 10);
    addField(focFld);
    
    focFld = new FDateField("COLLECTION_DATE", "Collection Date", FLD_COLLECTION_DATE, false);
    addField(focFld);

    focFld = new FDateField("DATE_OF_BIRTH", "Date of birth", FLD_DATE_OF_BIRTH, false);
    addField(focFld);

    focFld = new FCharField("FIRST_NAME", "First Name", FLD_PATIENT_FIRST_NAME, false, L3SampleDesc.LEN_FIRST_NAME);
    addField(focFld);
    
    focFld = new FCharField("LAST_NAME", "Last Name", FLD_PATIENT_LAST_NAME, false, L3SampleDesc.LEN_LAST_NAME);
    addField(focFld);
    
    focFld = new FCharField("MIDDLE_INITIAL", "Middle Initial", FLD_PATIENT_MIDDLE_INITIAL, false, L3SampleDesc.LEN_MIDDLE_INITIAL);
    addField(focFld);
    
    focFld = new FIntField("AGE", "Age", FLD_PATIENT_AGE, false, L3SampleDesc.LEN_AGE);
    addField(focFld);
    
    focFld = new FCharField("SEX", "Sex", FLD_PATIENT_SEX, false, L3SampleDesc.LEN_SEXE);
    addField(focFld);
    
    focFld = new FCharField("PATIENT_ID", "Patient id", FLD_PATIENT_ID, false, L3SampleDesc.PATIENT_ID_LENGTH);
    addField(focFld);
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
      focDesc = new /*XXX*/ LisSampleDesc();
    }
    return focDesc;
  }
}
