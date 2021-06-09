/*
 * Created on May 7, 2006
 */
package b01.l3.astm;

import java.util.ArrayList;

/**
 * @author 01Barmaja
 */
public class AstmOrder extends AstmRecord{

  public static final int FIELD_RECORD_TYPE = 0;
  public static final int FIELD_SEQUENCE_NUMBER = 1;
  public static final int FIELD_SAMPLE_ID = 2;
  public static final int FIELD_INSTRUMENT_SPECIMEN_ID = 3;
  public static final int FIELD_UNIVERSAL_TEST_ID = 4;
  public static final int FIELD_PRIORITY = 5;
  public static final int FIELD_REQUESTED_DATE_AND_TIME = 6;
  public static final int FIELD_SPECIMEN_COLLECTION_DATE_AND_TIME = 7;
  public static final int FIELD_COLLECTION_END_TIME = 8;
  public static final int FIELD_COLLECTION_VOLUME = 9;
  public static final int FIELD_COLLECTOR_ID = 10;
  public static final int FIELD_ACTION_CODE = 11;
  public static final int FIELD_DANGER_CODE = 12;
  public static final int FIELD_RELEVANT_CLINICAL_INFORMATIONS = 13;
  public static final int FIELD_DATE_AND_TIME_SPECIMEN_RECEIVED = 14;
  public static final int FIELD_SPECIMEN_TYPE = 15;
  public static final int FIELD_ORDERING_PHYSICIAN = 16;
  public static final int FIELD_PHYSICIAN_TEL_NB = 17;
  public static final int FIELD_USER_FIELD1 = 18;
  public static final int FIELD_USER_FIELD2 = 19;
  public static final int FIELD_LABORATORY_FIELD1 = 20;
  public static final int FIELD_LABORATORY_FIELD2 = 21;
  public static final int FIELD_DATE_AND_TIME_RESULTS_REPORTED_OR_LAST_MODIFIED = 22;
  public static final int FIELD_INSTRUMENT_CHARGE_TO_COMPUTER_SYSTEM = 23;
  public static final int FIELD_INSTRUMENT_SECTION_ID = 24;
  public static final int FIELD_REPORT_TYPES = 25;
  public static final int FIELD_RESERVED = 26;
  public static final int FIELD_LOCATION_OR_WARD_OF_SPECIMEN_COLLACTION = 27;
  public static final int FIELD_NOSOCOMIAL_INFECTION_FLAG = 28;
  public static final int FIELD_SPECIMEN_SERVICE = 29;
  public static final int FIELD_SPECIMEN_INSTRUCTION = 30;
  public static final int FIELD_ARRAY_LENGTH = 31;
  
  private ArrayList<AstmResult> resultArray = null;
  
  public AstmOrder(){
    super(FIELD_ARRAY_LENGTH);
    resultArray = new ArrayList<AstmResult>();
  }
  
  public void addResult(AstmResult result){
    resultArray.add(result);
  }

  public int getResultCount(){
    return resultArray.size();
  }
  
  public AstmResult getResult(int i){
    return (AstmResult) resultArray.get(i);
  }  
}
