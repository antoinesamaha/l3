/*
 * Created on Jul 4, 2005
 */
package b01.l3.data;

import java.sql.Date;

import b01.foc.Globals;
import b01.foc.desc.*;
import b01.foc.list.*;
import b01.foc.list.filter.*;
import b01.foc.property.FProperty;
import b01.foc.gui.*;
import b01.foc.desc.field.*;
import b01.l3.Instrument;
import b01.l3.exceptions.L3Exception;

/**
 * @author 01Barmaja
 */
public class L3SampleTestJoinFilter extends FocListFilter{

	private FocList focList = null;
  public static final String CONDITION_CODE_ENTRY_DATE = "ENTRY_DATE";
  public static final String CONDITION_CODE_INSTRUMENT = "INST";
  public static final String CONDITION_CODE_STATUS     = "STATUS";
  public static final String CONDITION_CODE_BLOCKED    = "BLOCKED";
  public static final String CONDITION_CODE_SAMPLE_ID  = "SAMPLE_ID";
  
  public L3SampleTestJoinFilter(FocConstructor constr) {
    super(constr);
    setFilterLevel(FocListFilter.LEVEL_DATABASE);
  }
  
  public void dispose(){
  	super.dispose();
  	focList = null;
  }

  public void setFocList(FocList focList){
  	this.focList = focList;
  }
  
  @Override
	public FocList getFocList() {
		return focList;
	}

  public void setInstrumentEquals(Instrument instrument){
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null){
        if(cond.getFieldPrefix().compareTo(CONDITION_CODE_INSTRUMENT) == 0){
          ObjectCondition objectCond = (ObjectCondition)cond;
          objectCond.setObject(this, instrument);
        }
      }
    }
  }

  public void setStatus(int operation, int status){
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null){
        if(cond.getFieldPrefix().compareTo(CONDITION_CODE_STATUS) == 0){
          MultipleChoiceCondition objectCond = (MultipleChoiceCondition) cond;
          objectCond.setToValue(this, operation, status);
        }
      }
    }
  }

  public void setStatusEquals(int status){
  	setStatus(MultipleChoiceCondition.OPERATION_EQUALS, status);
  }
  
  public void setBlockedEquals(boolean blocked){
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null){
        if(cond.getFieldPrefix().compareTo(CONDITION_CODE_BLOCKED) == 0){
          BooleanCondition boolCond = (BooleanCondition) cond;
          if(blocked){
            boolCond.setValue(this, BooleanCondition.VALUE_TRUE);
          }else{
            boolCond.setValue(this, BooleanCondition.VALUE_FALSE);
          }
        }
      }
    }
  }
  
  public void setInstrumentStatus(Instrument instr, int status){
  	setInstrumentEquals(instr);
  	setStatusEquals(status);
  	setBlockedEquals(false);
  }

  public void setInstrumentStatusAndSample(Instrument instr, String sampleID, int status){
  	setInstrumentEquals(instr);
  	setStatusEquals(status);
  	setSampleID(sampleID);
  	setBlockedEquals(false);
  }

  public void setSampleID(String sampleID){
  	if(sampleID != null){
	    for(int i=0; i<filterDesc.getConditionCount(); i++){
	      FilterCondition cond = filterDesc.getConditionAt(i);
	      if(cond != null){
	        if(cond.getFieldPrefix().compareTo(CONDITION_CODE_SAMPLE_ID) == 0){
	          StringCondition strCond = (StringCondition) cond;
	          if(strCond != null){
	          	strCond.setToValue(this, StringCondition.OPERATION_EQUALS, sampleID);
	          }
	        }
	      }
	    }
  	}
  }

	public void setEntryDate(int operation, Date date1, Date date2) throws Exception{
		DateCondition cond = (DateCondition) findFilterCondition(CONDITION_CODE_ENTRY_DATE);
		if(cond == null){
			throw new L3Exception("Condition not found EXCEPTION : "+CONDITION_CODE_ENTRY_DATE);
		}
    cond.setOperator(this, operation);
    cond.setFirstDate(this, date1);
    cond.setLastDate(this, date2);
  }

	public void makeForCurrentDateOnly(){
    FilterDesc filterDesc = getFilterDesc();
    for(int i=0; i<filterDesc.getConditionCount(); i++){
      FilterCondition cond = filterDesc.getConditionAt(i);
      if(cond != null){
        if(cond.getFieldPrefix().compareTo(CONDITION_CODE_ENTRY_DATE) == 0){
          DateCondition dateCond = (DateCondition)cond;
          //dateCond.setValue(this, getCurrentdate(.));
          dateCond.setOperator(this, DateCondition.OPERATOR_EQUALS);
          dateCond.setFirstDate(this, Globals.getApp().getSystemDate());
        }
      }
    }
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
 
	public static FPanel newBrowsePanel(FocList list, int viewID) {
    return null;
  }

	public void addTestToSample(L3SampleTestJoin join, L3Sample sample){
		String testLabel = join.getPropertyString(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL);
		L3Test test = sample.findTest(testLabel);
		if(test == null){
			Globals.logString("Adding test "+testLabel+" to sample "+sample.getId());
			test = sample.addTest();
		}

		FocFieldEnum enumer = new FocFieldEnum(L3Test.getFocDesc(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
    while(enumer != null && enumer.hasNext()){
    	FField field = enumer.nextField();

      FProperty tarProp = test.getFocProperty(field.getID());
      FProperty srcProp = join.getFocProperty(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + field.getID());
      tarProp.copy(srcProp);
    }
	}
	
  public void addAllTestsToSameSample(L3Sample sample){
  	FocList list = getFocList();
  	if(list != null){
  		for(int i=0; i<list.size(); i++){
  			L3SampleTestJoin join = (L3SampleTestJoin) list.getFocObject(i);

  			if(join != null){
  				addTestToSample(join, sample);
  			}
  		}
  	}
  }
	
  public L3Message convertToMessage(){
  	L3Message message = null;
  	FocList   list    = getFocList();
  	if(list != null){
  		Globals.logString("L3SampleTestJoinList size is "+list.size());
  		for(int i=0; i<list.size(); i++){
  			L3SampleTestJoin join = (L3SampleTestJoin) list.getFocObject(i);

  			if(join != null){
  				if(message == null){
  					message = new L3Message();
  				}
	  			
  				String sampleID = join.getPropertyString(L3SampleDesc.FLD_ID);
  				
  				L3Sample sample = message.findSample(sampleID);
  				if(sample == null){
  					sample = new L3Sample(sampleID);
  					//sample.setSampleAsNonDatabaseResident();
  					sample.setSampleTestListAsLoaded();  					
  					message.addSample(sample);

    				FocFieldEnum enumer = new FocFieldEnum(L3Sample.getFocDesc(), FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
  	        while(enumer != null && enumer.hasNext()){
  	        	FField field = enumer.nextField();
  	
  	          FProperty tarProp = sample.getFocProperty(field.getID());
  	          FProperty srcProp = join.getFocProperty(field.getID());
  	          tarProp.copy(srcProp);
  	        }
  				}
  				
  				addTestToSample(join, sample);
  			}
  		}
  	}
  	return message;
  }
	
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      focDesc = new FocDesc(L3SampleTestJoinFilter.class, FocDesc.NOT_DB_RESIDENT, "SAMPLE_FILTER", true);

      focDesc.addReferenceField();
      getFilterDesc().fillDesc(focDesc, 1);
    }
    return focDesc;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FILTER DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FilterDesc filterDesc = null;

  public static FilterDesc getFilterDesc(){
    if(filterDesc == null){
      filterDesc = new FilterDesc(L3SampleTestJoinDesc.getInstance());

      StringCondition strCond =  new StringCondition (FFieldPath.newFieldPath(L3SampleDesc.FLD_ID), CONDITION_CODE_SAMPLE_ID);
      filterDesc.addCondition(strCond);

      strCond =  new StringCondition (FFieldPath.newFieldPath(L3SampleDesc.FLD_LAST_NAME), "LAST_NAME");
      filterDesc.addCondition(strCond);

      strCond =  new StringCondition (FFieldPath.newFieldPath(L3SampleDesc.FLD_FIRST_NAME), "FIRST_NAME");
      filterDesc.addCondition(strCond);

      DateCondition dateCond =  new DateCondition (FFieldPath.newFieldPath(L3SampleDesc.FLD_ENTRY_DATE), "ENTRY_DATE");
      filterDesc.addCondition(dateCond);
      
      strCond =  new StringCondition (FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_LABEL), "TEST");
      filterDesc.addCondition(strCond);
      
      BooleanCondition boolCond =  new BooleanCondition (FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_BLOCKED), CONDITION_CODE_BLOCKED);
      filterDesc.addCondition(boolCond);

      boolCond =  new BooleanCondition (FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_RESULT_OK), "RESULT_OK");
      filterDesc.addCondition(boolCond);

      MultipleChoiceCondition multiCond =  new MultipleChoiceCondition (FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_STATUS), CONDITION_CODE_STATUS);
      filterDesc.addCondition(multiCond);
      
      ObjectCondition objCond = new ObjectCondition(FFieldPath.newFieldPath(L3SampleTestJoinDesc.FLD_TEST_FIELDS_START + L3TestDesc.FLD_DISPATCH_INSTRUMENT), CONDITION_CODE_INSTRUMENT);
      filterDesc.addCondition(objCond);
    }
    return filterDesc;
  }

  public FilterDesc getThisFilterDesc(){
    return L3SampleTestJoinFilter.getFilterDesc();
  }
}



