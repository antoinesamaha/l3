package b01.l3.drivers.coulter.dataBlockStructure;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import b01.foc.util.ASCII;
import b01.foc.util.Byte2IntConverter;
import b01.foc.util.Int2ByteConverter;
import b01.l3.data.L3Sample;
import b01.l3.drivers.coulter.CoulterDriver;
import b01.l3.drivers.coulter.CoulterFrame;

public class DataBlockStructure implements IDataBlock{

  private Map<String, CoulterField> groupFieldMap = null; 
  
  public final static int GROUP_GENERAL_INFO    = 1; 
  public static final int GROUP_CBC_PAREMETER   = 2;
  public static final int GROUP_DIFF_COUNT      = 3;
  public static final int GROUP_DIFF_PERCENT    = 4;
  //public static final int GROUP_COMMENT         = 5;
  //public static final int GROUP_FLAGS           = 6;
  public static final int GROUP_DEMOGRAPHICS    = 7;
//  // IF NONE ARE ENABLED THEY ARE NOT TRANSMITTED 8->12
//  public static final int GROUP_DF1_SCATTERPLOT = 8;
//  public static final int GROUP_DF2_SCATTERPLOT = 9;
//  public static final int GROUP_DIFF_HISTOGRAM  = 10;
//  public static final int GROUP_RBC_HISTOGRAM   = 11;
//  public static final int GROUP_PLT_HISTOGRAM   = 12;
//  // NOT TRANSMITTED IF THE OVERALL RETIC IS DISABLED 13->15
//  public static final int GROUP_RETIC_PARAMETER = 13;
//  // NOT TRANSMITTED IF THE OVERALL RETIC IS ENABLED AND RETIC GRAPHIC DISABLED 14-15
//  public static final int GROUP_DF5_SCATTERPLOT = 14;
//  public static final int GROUP_DF6_SCATTERPLOT = 15;
//  
  public DataBlockStructure(CoulterDriver driver){
    groupFieldMap = new LinkedHashMap<String, CoulterField>();

//===================================    
//========GENERAL INFORMATION========    
//===================================

    //  SEQUENCE
    CoulterField sequenceField = new CoulterField(GROUP_GENERAL_INFO, "SEQUENCE"){
      public void dispose() {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
          sample.setId(value);
        }
      }
      public void format(L3Sample sample, StringBuffer str) {
        if (str.length() >0){
          addTag(str);
          str.append(sample.getId());
          str.append(ASCII.CR);
          str.append(ASCII.LF);
        }
      }
    };
    addField(sequenceField);
    
    // SAMPLE ID
    CoulterField idField = new CoulterField(GROUP_GENERAL_INFO, "ID1"){
      public void dispose() {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
          sample.setId(value);
        }
      }
      public void format(L3Sample sample, StringBuffer str) {
        if (sample.getId() != null && sample.getId().length() >0){
          addTag(str);
          str.append(sample.getId());
          str.append(ASCII.CR);
          str.append(ASCII.LF);
        }
      }
    };
    addField(idField);  

//=====================================    
//============== CBC ==================
//=====================================
    //  WBC
    CoulterField cbcWBCField = new TestCoulterField(GROUP_CBC_PAREMETER, "WBC", driver.testMaps_getLisCode("WBC"));
    addField(cbcWBCField);
    
    //  RBC
    CoulterField cbcRBCField = new TestCoulterField(GROUP_CBC_PAREMETER, "RBC", driver.testMaps_getLisCode("RBC"));
    addField(cbcRBCField);
    
    //  HGB
    CoulterField cbcHGBField = new TestCoulterField(GROUP_CBC_PAREMETER, "HGB", driver.testMaps_getLisCode("HGB"));
    addField(cbcHGBField);
    
    //  HCT
    CoulterField cbcHCTField = new TestCoulterField(GROUP_CBC_PAREMETER, "HCT", driver.testMaps_getLisCode("HCT"));
    addField(cbcHCTField);
    
    //  MCV
    CoulterField cbcMCVField = new TestCoulterField(GROUP_CBC_PAREMETER, "MCV", driver.testMaps_getLisCode("MCV"));
    addField(cbcMCVField);
    
    //  MCH
    CoulterField cbcMCHField = new TestCoulterField(GROUP_CBC_PAREMETER, "MCH", driver.testMaps_getLisCode("MCH"));
    addField(cbcMCHField);
    
    //  MCHC
    CoulterField cbcMCHCField = new TestCoulterField(GROUP_CBC_PAREMETER, "MCHC", driver.testMaps_getLisCode("MCHC"));
    addField(cbcMCHCField);
    
    //  RDW
    CoulterField cbcRDWField = new TestCoulterField(GROUP_CBC_PAREMETER, "RDW", driver.testMaps_getLisCode("RDW"));
    addField(cbcRDWField);
    
    //  PLT
    CoulterField cbcPLTField = new TestCoulterField(GROUP_CBC_PAREMETER, "PLT", driver.testMaps_getLisCode("PLT"));
    addField(cbcPLTField);
    
    //  PCT
    CoulterField cbcPCTField = new TestCoulterField(GROUP_CBC_PAREMETER, "PCT", driver.testMaps_getLisCode("PCT"));
    addField(cbcPCTField);
    
    // MPV
    CoulterField cbcMPVField = new TestCoulterField(GROUP_CBC_PAREMETER, "MPV", driver.testMaps_getLisCode("MPV"));
    addField(cbcMPVField);
    
    // PDW
    CoulterField cbcPDWField = new TestCoulterField(GROUP_CBC_PAREMETER, "PDW", driver.testMaps_getLisCode("PDW"));
    addField(cbcPDWField);    
    
//  =====================================    
//  ============ DIFF COUNT =============
//  =====================================
    
    // LY#
    CoulterField diffCountLYField = new TestCoulterField(GROUP_DIFF_COUNT, "LY#", driver.testMaps_getLisCode("LY#"));
    addField(diffCountLYField);

    // MO#
    CoulterField diffCountMOField = new TestCoulterField(GROUP_DIFF_COUNT, "MO#", driver.testMaps_getLisCode("MO#"));
    addField(diffCountMOField);
    
    // NE#
    CoulterField diffCountNEField = new TestCoulterField(GROUP_DIFF_COUNT, "NE#", driver.testMaps_getLisCode("NE#"));
    addField(diffCountNEField);

    // EO#
    CoulterField diffCountEOField = new TestCoulterField(GROUP_DIFF_COUNT, "EO#", driver.testMaps_getLisCode("EO#"));
    addField(diffCountEOField);
    
    // BA#
    CoulterField diffCountBAField = new TestCoulterField(GROUP_DIFF_COUNT, "BA#", driver.testMaps_getLisCode("BA#"));
    addField(diffCountBAField);
    
//  =====================================    
//  =========== DIFF PERCENT ============
//  =====================================
    
    // LY%
    CoulterField diffPercentLYField = new TestCoulterField(GROUP_DIFF_PERCENT, "LY%", driver.testMaps_getLisCode("LY%"));
    addField(diffPercentLYField);

    // MO%
    CoulterField diffPercentMOField = new TestCoulterField(GROUP_DIFF_PERCENT, "MO%", driver.testMaps_getLisCode("MO%"));
    addField(diffPercentMOField);
    
    // NE%
    CoulterField diffPercentNEField = new TestCoulterField(GROUP_DIFF_PERCENT, "NE%", driver.testMaps_getLisCode("NE%"));
    addField(diffPercentNEField);

    // EO%
    CoulterField diffPercentEOField = new TestCoulterField(GROUP_DIFF_PERCENT, "EO%", driver.testMaps_getLisCode("EO%"));
    addField(diffPercentEOField);
    
    // BA%
    CoulterField diffPercentBAField = new TestCoulterField(GROUP_DIFF_PERCENT, "BA%", driver.testMaps_getLisCode("BA%"));
    addField(diffPercentBAField);
    
//  =====================================    
//  ========== DEMOGRAPHICS =============
//  =====================================
    
    //UF1
    CoulterField userFirstNameField = new CoulterField(GROUP_GENERAL_INFO, "UF1"){
      public void dispose() {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
          sample.setFirstName(value);
        }
      }
      public void format(L3Sample sample, StringBuffer str) {
        if (sample.getFirstName() != null && sample.getFirstName().length() >0){
          addTag(str);
          str.append(sample.getFirstName());
          str.append(ASCII.CR);
          str.append(ASCII.LF);
        }
      }
    };
    addField(userFirstNameField);

    //UF2
    CoulterField userLastNameField = new CoulterField(GROUP_GENERAL_INFO, "UF2"){
      public void dispose() {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
          sample.setLastName(value);
        }
      }
      public void format(L3Sample sample, StringBuffer str) {
        if (sample.getLastName()!= null && sample.getLastName().length() >0){
          addTag(str);
          str.append(sample.getLastName());
          str.append(ASCII.CR);
          str.append(ASCII.LF);
        }
      }
    };
    addField(userLastNameField);
    
    //UF3
    CoulterField userMiddleInitialField = new CoulterField(GROUP_GENERAL_INFO, "UF3"){
      public void dispose() {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
          sample.setMiddleInitial(value);
        }
      }
      public void format(L3Sample sample, StringBuffer str) {
        if (sample.getMiddleInitial() != null && sample.getMiddleInitial().length() >0){
          addTag(str);
          str.append(sample.getMiddleInitial());
          str.append(ASCII.CR);
          str.append(ASCII.LF);
        }
      }
    };
    addField(userMiddleInitialField);

    //SEX
    CoulterField userSexField = new CoulterField(GROUP_GENERAL_INFO, "SEX"){
      public void dispose() {}
      public void parse(L3Sample sample, String value) {
        if (value != null){
          sample.setSexe(value);
        }
      }
      public void format(L3Sample sample, StringBuffer str) {
        if (str.length() >0){  
          addTag(str);
          str.append(sample.getSexe());
          str.append(ASCII.CR);
          str.append(ASCII.LF);
        }
      }
    };
    addField(userSexField);
    
  }

  public void dispose(){
    if(groupFieldMap != null){
      for(int i=0; i<groupFieldMap.size(); i++){
        CoulterField gf = groupFieldMap.get(i);
        gf.dispose();
      }
      groupFieldMap.clear();
    }
    groupFieldMap = null;
  }
  
  public void addField(CoulterField field){
    groupFieldMap.put(field.getTag(), field);
  }
  
  public void parse(L3Sample sample, String value) throws Exception{
    //Fill L3 Sample in this function
    //For each DC1
    //    get the field count
    //    For each Field
    //        read the tag here, 
    //        then get the GroupField from the hashMap
    //        then call its parse function.
    StringTokenizer tokenizerGroup = new StringTokenizer(value, String.valueOf(ASCII.DC1), false);      
    //int groupCount = 0;
    int tokenNum =0;
    while(tokenizerGroup.hasMoreTokens()){
      String group = tokenizerGroup.nextToken();
      if (tokenNum > 0 && tokenNum < tokenizerGroup.countTokens()){
      	Byte2IntConverter b2i = new Byte2IntConverter(group.charAt(0), group.charAt(1));
      	int fieldCount = b2i.getIntValue();
        if (fieldCount >0){
          StringTokenizer tokenizerField = new StringTokenizer(group, String.valueOf(ASCII.LF)+String.valueOf(ASCII.CR), false);
          int num = -1;
          while(tokenizerField.hasMoreTokens()){
            String field = tokenizerField.nextToken();
            if(num >= 0){
              int tagEnd = field.indexOf(CoulterFrame.FIELD_SEPERATOR);
              String tag = field.substring(0, tagEnd);
              String data = field.substring(tagEnd, field.length());
              CoulterField coulterField = groupFieldMap.get(tag);
              if (coulterField != null){
                coulterField.parse(sample, data.trim());  
              }
            }
            num++;
          }
          //groupCount++;
        }
      }
    tokenNum++;
    }
  }
  
  public StringBuffer format(L3Sample sample){
    StringBuffer strBuffer = new StringBuffer();
    Int2ByteConverter conv = null;
    StringBuffer tempStrBuffer = new StringBuffer();
    Iterator<String> iter = groupFieldMap.keySet().iterator();
    
    int previousGroupIndex = 0;
    int realFieldCount = 0;
    int i=0;
    while(iter.hasNext()){
      String tag = iter.next();
      CoulterField coulterField = groupFieldMap.get(tag);
      int currentGroupIndex = coulterField.getGroupIndex();
      if (previousGroupIndex != currentGroupIndex || i == groupFieldMap.size()-1){
        
        StringTokenizer tokenizerField = new StringTokenizer(tempStrBuffer.toString(), String.valueOf(ASCII.LF)+String.valueOf(ASCII.CR), false);
        
        while(tokenizerField.hasMoreTokens()){
          tokenizerField.nextToken();
          realFieldCount++;
        }
        if (realFieldCount > 0){
          conv = new Int2ByteConverter(realFieldCount);
          strBuffer.append(ASCII.DC1);
          strBuffer.append(conv.getHighByte());
          strBuffer.append(conv.getLowByte());
          strBuffer.append(ASCII.CR);
          strBuffer.append(ASCII.LF);
          strBuffer.append(tempStrBuffer);
  
          tempStrBuffer.delete(0, tempStrBuffer.length());
          realFieldCount =0;
          
        }else{
          if (tag != "SEQUENCE" || i == groupFieldMap.size()-1){
          strBuffer.append(ASCII.DC1);
          conv = new Int2ByteConverter(0);
          strBuffer.append(conv.getHighByte());
          strBuffer.append(conv.getLowByte());
          strBuffer.append(ASCII.CR);
          strBuffer.append(ASCII.LF);
          }
        }
        previousGroupIndex = currentGroupIndex;
      }
      if(tag != "SEQUENCE"){
      coulterField.format(sample, tempStrBuffer);
      }
      i++;
    }
    return strBuffer;
  }
  
  //****************** Singleton *****************
/*  private static DataBlockStructure struct = null;
  public static DataBlockStructure getInstance(){
    if(struct == null){
      struct = new DataBlockStructure();
    }
    return struct; 
  }  
*/
}
