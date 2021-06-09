/*
 * Created on May 7, 2006
 */
package b01.l3.astm;

import java.util.ArrayList;

/**
 * @author 01Barmaja
 */
public class AstmPatient extends AstmRecord{
  public final static int FIELD_RECORD_TYPE = 0;
  public final static int FIELD_SEQUENCE_NUMBER = 1;
  public final static int FIELD_PRACTICE_ASSIGNED_PATIENT_ID = 2;
  public final static int FIELD_LABORATORY_ASSIGNED_PATIENT_ID = 3;
  public final static int FIELD_PATIENT_ID_NO3 = 4;
  public final static int FIELD_PATIENT_NAME = 5;
  public final static int FIELD_PATIENT_FIRST_NAME = 6;
  public final static int FIELD_MOTHERS_MADEN_NAME = 7;
  public final static int FIELD_BIRTHDAY = 8;
  public final static int FIELD_PATIENT_SEX = 9;
  public final static int FIELD_PATIENT_RACE_ETHNIC_ORIGIN = 10;
  public final static int FIELD_PATIENT_ADDRESS = 11;
  public final static int FIELD_RESERVED = 12;
  public final static int FIELD_PATIENT_TEL_NB = 13;
  public final static int FIELD_ATTENDING_PHYSICIAN_ID = 14;
  public final static int FIELD_SPECIAL_FIELD1 = 15;
  public final static int FIELD_SPECIAL_FIELD2 = 16;
  public final static int FIELD_PATIENT_HEIGHT = 17;
  public final static int FIELD_PATIENT_WEIGHT = 18;
  public final static int FIELD_PATIENT_KNOWN_OR_SUSPECTED_DIAGNOSIS = 19;
  public final static int FIELD_PATIENT_ACTIVE_MEDICATION = 20;
  public final static int FIELD_PATIENTS_DIET = 21;
  public final static int FIELD_PRACTICE_FIELD1 = 22;
  public final static int FIELD_ADMISSION_AND_DISCHARGE_DATES = 23;
  public final static int FIELD_ADMISSION_STATUS = 24;
  public final static int FIELD_LOCATION = 25;
  public final static int FIELD_NATURE_OF_ALTERNATIVE_DIAGNOSTIC_CODE_AND_CLASSIFIERS = 26;
  public final static int FIELD_NATURE_OF_ALTERNATIVE_DIAGNOSTIC_CODE_AND_CLASSIFIERS2 = 27;
  public final static int FIELD_PATIENT_RELIGION = 28;
  public final static int FIELD_MARTIAL_STATUS = 29;
  public final static int FIELD_ISOLATION_STATUS = 30;
  public final static int FIELD_LANGUAGE = 31;
  public final static int FIELD_HOSPITAL_SERVICE = 32;
  public final static int FIELD_HOSPITAL_INSTITUTION = 33;
  public final static int FIELD_DOSAGE_CATEGORY = 34;
  public final static int FIELD_ARRAY_LENGTH = 35;

  private ArrayList<AstmOrder> orderArray = null;
  
  public AstmPatient(){
    super(FIELD_ARRAY_LENGTH);
    orderArray = new ArrayList<AstmOrder>();    
  }
  
  public void addOrder(AstmOrder order){
    orderArray.add(order);
  }

  public int getOrderCount(){
    return orderArray.size();
  }
  
  public AstmOrder getOrder(int i){
    return (AstmOrder) orderArray.get(i);
  }  
}
