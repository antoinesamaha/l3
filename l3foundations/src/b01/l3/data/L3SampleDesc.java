/*
 * Created on Jun 5, 2006
 */
package b01.l3.data;

import b01.foc.db.DBIndex;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FCharField;
import b01.foc.desc.field.FDateField;
import b01.foc.desc.field.FDateTimeField;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FIntField;
import b01.foc.desc.field.FMultipleChoiceField;
import b01.foc.list.FocLink;
import b01.foc.list.FocLinkSimple;

/**
 * @author 01Barmaja
 */
public class L3SampleDesc extends FocDesc {

	public static final int FLD_ID = 1;
  public static final int FLD_PATIENT_ID = 2;
	public static final int FLD_LAST_NAME = 3;
	public static final int FLD_FIRST_NAME = 4;
  public static final int FLD_SEXE = 5;
	public static final int FLD_MIDDLE_INITIAL = 6;
	public static final int FLD_DATE_AND_TIME = 7;
	public static final int FLD_LIQUIDE_TYPE = 8;
	public static final int FLD_TEST_LIST = 11;
  public static final int FLD_RESULT_CONFIRMED = 12;
  public static final int FLD_OK_TO_BE_SENT= 13;
  public static final int FLD_ENTRY_DATE= 14;
  public static final int FLD_AGE = 16;
  
  public static final int FLD_INSTRUMENT_MESSAGE_LIST = 17;
    
  public static final int FLD_DATE_OF_BIRTH = 18;
  
  public static final String FNAME_ID = "ID";
  
  public static final int SAMPLE_NOTIFICATION_UNEXPECTED_SEQUENECE = 0;//"Unexpected sequence";
  public static final int SAMPLE_NOTIFICATION_ERROR_IN_RESULT = 1;//"Error in result";
  public static final int SAMPLE_NOTIFICATION_ERROR_WHILE_SENDING = 2;//"Error while Sending";
  public static final int SAMPLE_NOTIFICATION_TERMINATED_SUCCEFULLY =3;// "Terminated succefully";
  public static final int SAMPLE_NOTIFICATION_SAMPLE_ORIGIN_NOT_IN_L3 =4;
  
  public static final int SAMPLE_ID_LENGTH    = 15;
  public static final int PATIENT_ID_LENGTH   = 15;
  public static final int LEN_LAST_NAME       = 30;
  public static final int LEN_FIRST_NAME      = 30;
  public static final int LEN_MIDDLE_INITIAL  = 1;
  public static final int LEN_SEXE            = 1;
  public static final int LEN_AGE             = 3;
  public static final int LEN_STATUS          = 1;
  public static final int LEN_LIQUIDE_TYPE    = 1;
  public static final int LEN_NOTIFICATION_MESSAGE   = 50;
  
  public static FCharField newSampleIDFIeld(int fldID, String fieldName){
    return new FCharField(fieldName, "Id", fldID, true, SAMPLE_ID_LENGTH);
  }
  
	public L3SampleDesc(){
		super(L3Sample.class, FocDesc.DB_RESIDENT, "L3SAMPLE", true);

    FField focFld = addReferenceField();
    
    focFld = newSampleIDFIeld(FLD_ID, FNAME_ID);
    addField(focFld);
    focFld = new FCharField("PATIENT_ID", "Patient id", FLD_PATIENT_ID, false, PATIENT_ID_LENGTH);
    addField(focFld);
    focFld = new FCharField ("LAST_NAME", "Last name", FLD_LAST_NAME, false, 30);
    addField(focFld);
    focFld = new FCharField ("FIRST_NAME", "First name", FLD_FIRST_NAME, false, 30);
    addField(focFld);
    focFld = new FCharField("MIDDLE_INITIAL", "Middle initial", FLD_MIDDLE_INITIAL, false, 1);
    addField(focFld);
    focFld = new FCharField ("SEXE", "Sexe", FLD_SEXE, false,1);
    addField(focFld);
    //focFld = new FCharField("DATE_AND_TIME", "Date and Time", FLD_DATE_AND_TIME, false, 30);// a corriger
    //addField(focFld);
    FMultipleChoiceField liqTypeFld = new FMultipleChoiceField("LIQUIDE_TYPE", "Liquide type", FLD_LIQUIDE_TYPE, false, 2);
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_EMPTY, L3Sample.LIQUID_TYPE_EMPTY_TITLE);
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_SERUM, L3Sample.LIQUID_TYPE_SERUM_TITLE);    
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_CSF, L3Sample.LIQUID_TYPE_CSF_TITLE);
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_URIN, L3Sample.LIQUID_TYPE_URIN_TITLE);
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_BODY_FLUID, L3Sample.LIQUID_TYPE_BODY_FLUID_TITLE);
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_STOOL, L3Sample.LIQUID_TYPE_STOOL_TITLE);//Sels
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_SUPERNATENT, L3Sample.LIQUID_TYPE_SUPERNATENT_TITLE);   
    liqTypeFld.addChoice(L3Sample.LIQUID_TYPE_OTHERS, L3Sample.LIQUID_TYPE_OTHERS_TITLE);
    addField(liqTypeFld);    

    focFld = new FBoolField("RESULT_CONFIRMED", "Result confirmed", FLD_RESULT_CONFIRMED, false);
    addField(focFld);
    focFld = new FBoolField("OK_TO_BE_SENT", "Ok to be sent", FLD_OK_TO_BE_SENT, false);
    addField(focFld);
    focFld= new FDateTimeField("ENTRY_DATE", "Entry date", FLD_ENTRY_DATE, false);
    addField(focFld);
    focFld = new FIntField ("AGE", "Age", FLD_AGE, false, 3);
    addField(focFld);
    focFld= new FDateTimeField("DATE_OF_BIRTH", "Date of Birth", FLD_DATE_OF_BIRTH, false);
    addField(focFld);
        
    DBIndex index = new DBIndex("BY_DATE", this , false); 
    index.addField(FLD_ENTRY_DATE);
    indexAdd(index);      
	}
	
	private static FocLink linkSimple = null;
	
	public static FocLink getFocLinkSimple(){
		if(linkSimple == null){
			linkSimple = new FocLinkSimple(L3Sample.getFocDesc());
		}
		return linkSimple;
	}
}
