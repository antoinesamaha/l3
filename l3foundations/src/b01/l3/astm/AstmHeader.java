/*
 * Created on May 7, 2006
 */
package b01.l3.astm;

import java.util.ArrayList;

/**
 * @author 01Barmaja
 */
public class AstmHeader extends AstmRecord{
  public final static int FIELD_RECORD_TYPE = 0;
  public final static int FIELD_DELIMITERS_DEFINITION = 1;
  public final static int FIELD_MESSAGE_CONTROL_ID = 2;
  public final static int FIELD_ACCESS_PASSWORD = 3;
  public final static int FIELD_SENDER_NAME = 4;
  public final static int FIELD_SENDER_ADDRESS = 5;  
  public final static int FIELD_RESERVED = 6;
  public final static int FIELD_SENDER_TELEPHONE_NB = 7;
  public final static int FIELD_CHARACTERISTICS_OF_SENDER = 8;
  public final static int FIELD_RECEIVER_ID = 9;
  public final static int FIELD_COMMENTS_OR_SPECIAL_INSTRUCTIONS = 10;
  public final static int FIELD_PROCESSING_ID = 11;
  public final static int FIELD_ASTM_VERSION_NB = 12;
  public final static int FIELD_DATE_AND_TIME_OF_MESSAGE = 13;
  public final static int FIELD_ARRAY_LENGTH = 14;
  
  private ArrayList<AstmPatient> arrayList = null;
  
  public AstmHeader(){
    super(FIELD_ARRAY_LENGTH);
    arrayList = new ArrayList<AstmPatient>();
  }
  
  public void addPatient(AstmPatient patient){
    arrayList.add(patient);
  }

  public int getPatientCount(){
    return arrayList.size();
  }
  
  public AstmPatient getPatient(int i){
    return (AstmPatient) arrayList.get(i);
  }  
}