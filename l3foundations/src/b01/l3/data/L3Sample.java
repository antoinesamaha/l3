/*
 * Created on Jun 14, 2006
 */
package b01.l3.data;

import java.awt.Color;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;

import b01.foc.Globals;
import b01.foc.db.SQLDelete;
import b01.foc.db.SQLFilter;
import b01.foc.db.SQLSelectExistance;
import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.gui.FPanel;
import b01.foc.list.FocList;
import b01.foc.list.FocListElement;
import b01.foc.list.FocListIterator;
import b01.foc.property.FBoolean;
import b01.foc.property.FDateTime;
import b01.foc.property.FInt;
import b01.foc.property.FList;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;
import b01.foc.property.FString;
import b01.l3.Instrument;
import b01.l3.connector.dbConnector.lisConnectorTables.LisTestDesc;

/**
 * @author 01Barmaja
 */
public class L3Sample extends FocObject {
  public static final int LIQUID_TYPE_EMPTY       = -1;
  public static final int LIQUID_TYPE_SERUM       = 1;
  public static final int LIQUID_TYPE_URIN        = 2;
  public static final int LIQUID_TYPE_CSF         = 3;
  public static final int LIQUID_TYPE_BODY_FLUID  = 4;
  public static final int LIQUID_TYPE_STOOL       = 5;
  public static final int LIQUID_TYPE_SUPERNATENT = 6;
  public static final int LIQUID_TYPE_OTHERS      = 7;
  
  public static final String LIQUID_TYPE_EMPTY_TITLE       = "-";
  public static final String LIQUID_TYPE_SERUM_TITLE       = "Serum";
  public static final String LIQUID_TYPE_URIN_TITLE        = "Urin";
  public static final String LIQUID_TYPE_CSF_TITLE         = "CSF";
  public static final String LIQUID_TYPE_BODY_FLUID_TITLE  = "Body fluid";
  public static final String LIQUID_TYPE_STOOL_TITLE       = "Stool";
  public static final String LIQUID_TYPE_SUPERNATENT_TITLE = "Suprnt";
  public static final String LIQUID_TYPE_OTHERS_TITLE      = "Others";
  
  public static final Color availableINL3Color = Color.CYAN;
  public static final Color resultAvailableColor = Color.LIGHT_GRAY;

  private String graph = null;
  
  private String rackNumber     = "";
  private String tubePosition   = "";
  
  private void initFocProperties(String id){
  	setPropertyString(L3SampleDesc.FLD_ID, id);
  	setPropertyMultiChoice(L3SampleDesc.FLD_LIQUIDE_TYPE, -1);
  	setPropertyDate(L3SampleDesc.FLD_ENTRY_DATE,Globals.getApp().getSystemDate());
  } 
  
  public L3Sample(String id){
  	super(getFocDesc());
  	newFocProperties();
  	initFocProperties(id);
    setEntryDate(Globals.getApp().getSystemDate());
  }

  public L3Sample(FocConstructor constr) {
   super(constr);
   newFocProperties();
 	 initFocProperties("");   
   setEntryDate(Globals.getApp().getSystemDate());
  } 
  
  public void dispose(){
  	super.dispose();  	
  }
  
  public void setGraph(String graph){
    this.graph = graph;
  }

  public String getGraph(){
    return graph;
  }

  public void setSampleAsNonDatabaseResident(){
  	setDbResident(false);
		FList testListProp = (FList) getFocProperty(L3SampleDesc.FLD_TEST_LIST);
		FocList testList = testListProp.getListWithoutLoad();
		testList.setLoaded(true);
  }

  public void setSampleTestListAsLoaded(){
		FList testListProp = (FList) getFocProperty(L3SampleDesc.FLD_TEST_LIST);
		FocList testList = testListProp.getListWithoutLoad();
		testList.setLoaded(true);
  }
  
  public boolean existInDB(){
  	SQLSelectExistance selectExistance = new SQLSelectExistance(L3Sample.getFocDesc(), new StringBuffer(L3SampleDesc.FNAME_ID+"="+getId()));
  	selectExistance.execute();
  	boolean exists = selectExistance.getExist() == SQLSelectExistance.EXIST_YES;
  	selectExistance.dispose();
  	return exists;
  }
  
  public static L3Sample newDBSample(String id){
  	L3Sample sample = null;
  	FocList list = new FocList(L3SampleDesc.getFocLinkSimple());
  	SQLFilter sqlFilter = list.getFilter();
  	sqlFilter.putAdditionalWhere("SMPL_ID", L3SampleDesc.FNAME_ID+"="+id);
  	list.loadIfNotLoadedFromDB();

  	if(list.size() == 1){
  		sample = (L3Sample) list.getFocObject(0);
  		list.remove(sample);
  	}
  	list.dispose();
  	return sample;
  }
  
  public FocList getTestListWithoutLoad(){
  	FList list = (FList) getFocProperty(L3SampleDesc.FLD_TEST_LIST);
  	return list.getListWithoutLoad();
  }
  
  public FocList getTestList(){
  	FList list = (FList) getFocProperty(L3SampleDesc.FLD_TEST_LIST);
  	FocList focList = (list != null) ? list.getList() : null;
  	if(focList != null){
  		focList.loadIfNotLoadedFromDB();
  	}
  	return focList;
  }
  
  public L3Test findTest(String testlabel){
  	L3Test foundTest = null;
  	FocList testList = getTestList();
  	if(testList != null){
  		for(int i=0; i<testList.size() && foundTest == null; i++){
  			L3Test test = (L3Test) testList.getFocObject(i);
  			if(test.getLabel().compareTo(testlabel) == 0){
  				foundTest = test;
  			}
  		}
  	}
  	return foundTest;
  }
  
  public L3Test addTest(){
  	L3Test test = null;
  	FocList focList = getTestList();
    if(focList != null){
    	test = (L3Test) focList.newEmptyItem();
    }
    return test;
  }

  public void updateStatusForTests(final int status){
  	getTestList().iterate(new FocListIterator(){
			@Override
			public boolean treatElement(FocListElement element, FocObject focObj) {
				L3Test test = (L3Test) focObj;
				test.updateStatus(status);
				return false;
			}
  	});
  }
  
  public void updateBlockedForTests(final Boolean blocked){
  	getTestList().iterate(new FocListIterator(){
			@Override
			public boolean treatElement(FocListElement element, FocObject focObj) {
				L3Test test = (L3Test) focObj;
				test.updateBlocked(blocked);
				return false;
			}
  	});
  }
  
  public void updateNotificationMessageForTests(final String message){
  	getTestList().iterate(new FocListIterator(){
			@Override
			public boolean treatElement(FocListElement element, FocObject focObj) {
				L3Test test = (L3Test) focObj;
				test.updateNotificationMessage(message);
				return false;
			}
  	});
  }
  
  public void copyWithoutTests(L3Sample sample){
    setId(sample.getId());
    setPatientId(sample.getPatientId());
    setOrigin(sample.getOrigin());
    setLastName(sample.getLastName());
    setFirstName(sample.getFirstName());
    setMiddleInitial(sample.getMiddleInitial());
    setSexe(sample.getSexe());
    setAge(sample.getAge());
    setLiquidType(sample.getLiquidType());    
    setEntryDate(sample.getEntryDate());
    setDateOfBirth(sample.getDateOfBirth());
  }
  
  public void copy(L3Sample sample){
    copyWithoutTests(sample);
    if(sample.getReference().getInteger() > 0){
      setReference(sample.getReference().getInteger());
    }
    setLiquidType(sample.getLiquidType());
    FocList testList = sample.getTestList();
    for (int i=0;i<testList.size();i++){
      L3Test curTest = (L3Test)testList.getFocObject(i);
      if(getTestList().searchByProperyStringValue(L3TestDesc.FLD_LABEL, curTest.getLabel()) == null){
        L3Test test = this.addTest();
        test.copyAndBackup(curTest);
      }
    }
    setGraph(sample.getGraph());
  }  
  
  public void copyTestsFrom(L3Sample sample){
    HashMap<String, L3Test> newTestListMap = new HashMap<String, L3Test>();
    //setLiquidType(sample.getLiquidType());
    FocList newTestList = sample.getTestList();
    for (int i=0;i<newTestList.size();i++){
      newTestListMap.put(((L3Test)newTestList.getFocObject(i)).getLabel(), (L3Test)newTestList.getFocObject(i));
    }
    FocList oldTestList = getTestList();
    for (int i=0;i<oldTestList.size();i++){
      L3Test testToUpgrade = (L3Test)oldTestList.getFocObject(i);
      if (newTestListMap.containsKey(testToUpgrade.getLabel())){
        L3Test newTest = newTestListMap.get(testToUpgrade.getLabel());
        testToUpgrade.copyAndBackup(newTest);
      }
    }
    setGraph(sample.getGraph());
  }
  
  public void refreshSampleFrom(L3Sample sample){
    //setReference(sample.getReference().getInteger());
    copyTestsFrom(sample);
    if (sample.isOkToBeSent()){
      setOkToBeSent(sample.isOkToBeSent());
    }
    if (sample.isResultConfirmed()){
      setResultConfirmed(sample.isResultConfirmed());
    }
    if (getReference() != null )backup();
  }
    
  public void addTest2(L3Test t){
  	L3Test test= null;
  	FocList focList = getTestList();
    if(focList != null){
    	test= (L3Test) focList.newEmptyItem();
    	focList.add(test) ;
    	test.copy(t);
    }
    test.setCreated(true);
  }
  
  public void removeTest(L3Test test){
  	FocList focList = getTestList();
    if(focList != null){
    	focList.remove(test);
    }
  }
  
  public void remove(){
    SQLFilter deleteFilter = new SQLFilter(this,SQLFilter.FILTER_ON_SELECTED);
    StringBuffer deleteCondition = new StringBuffer("REF = "+ getReference().getInteger());
    deleteFilter.putAdditionalWhere("L3SAMPLE_DELETE", deleteCondition.toString());
    SQLDelete delete = new SQLDelete(getFocDesc(),deleteFilter);
    delete.execute();
    removeRelativeTests();
  }
  
  public void removeRelativeTests(){
    FocList testList = getTestList();
    for (int i = 0; i < testList.size(); i++){
      L3Test test = (L3Test)testList.getFocObject(i);
      test.remove();
    }
  }
  
  @SuppressWarnings("unchecked")
	public Iterator<L3Test> testIterator(){
  	Iterator<L3Test> iter = null;
  	FocList focList = getTestList();
    if(focList != null){
			iter = (Iterator<L3Test>)focList.focObjectIterator();
    }
    return iter;
  }
  
  public StringBuffer toStringBuffer(){
    StringBuffer buff = new StringBuffer();
    buff.append(getId()+" liq:"+getLiquidType()+"PatientId="+getPatientId()+" FirstName="+getFirstName()+" MidInitial="+getMiddleInitial()+" LastName="+getLastName()+" Sexe"+getSexe()+" Origin="+getOrigin()+"\n");
    int i=0;
    Iterator iter = testIterator();
    while(iter != null && iter.hasNext()){
      L3Test test = (L3Test) iter.next();
      buff.append("Test["+i+"]="+test.toStringBuffer());        
      buff.append("\n");
      i++;
    }
    return buff;
  }
  
  public long getDateAndTime() {
	  Date date = getPropertyDate(L3SampleDesc.FLD_ENTRY_DATE);
    return date != null ? date.getTime() : 0;
  }
  
  public void setDateAndTime(long dateAndTime) {
	  setPropertyDate(L3SampleDesc.FLD_ENTRY_DATE, new Date(dateAndTime));
  }
  
  public String getId() {
   FString id = (FString) getFocProperty(L3SampleDesc.FLD_ID);
    return (id != null) ? id.getString() :"";
 }
 
 public void setId(String id) {
   FString valId = (FString) getFocProperty(L3SampleDesc.FLD_ID);
    if(valId != null){
      valId.setString(id);
    }
 }
 
 public String getPatientId(){
   FString patientId = (FString)getFocProperty(L3SampleDesc.FLD_PATIENT_ID);
   return patientId != null ? patientId.getString() : "";
 }
 
 public void setPatientId(String patientId){
   FString patientIdProp = (FString)getFocProperty(L3SampleDesc.FLD_PATIENT_ID);
   if(patientIdProp != null){
     patientIdProp.setString(patientId);
   }
 }

 public String getOrigin(){
   FString origin = (FString)getFocProperty(L3SampleDesc.FLD_ORIGIN);
   return origin != null ? origin.getString() : "";
 }
 
 public void setOrigin(String origin){
   FString originProp = (FString)getFocProperty(L3SampleDesc.FLD_ORIGIN);
   if(originProp != null){
     originProp.setString(origin);
   }
 }
 public int getAge(){
	 return getPropertyInteger(L3SampleDesc.FLD_AGE);
 }
 
 public void setAge(int age){
	 setPropertyInteger(L3SampleDesc.FLD_AGE, age);
 }

 public String getSexe(){
   FString sexe = (FString)getFocProperty(L3SampleDesc.FLD_SEXE);
   return sexe != null ? sexe.getString() : "";
 }
 
 public void setSexe(String sexe){
   FString sexeProp = (FString)getFocProperty(L3SampleDesc.FLD_SEXE);
   if(sexeProp != null){
     sexeProp.setString(sexe);
   }
 }
  
  public int getLiquidType() {
    FMultipleChoice liqType = (FMultipleChoice) getFocProperty(L3SampleDesc.FLD_LIQUIDE_TYPE);
    return (liqType != null) ? liqType.getInteger():null;
  }
  
  public void setLiquidType(int liquidType) {
  	FInt liqType = (FInt) getFocProperty(L3SampleDesc.FLD_LIQUIDE_TYPE);
    if(liqType != null){
      liqType.setInteger(liquidType);
    }
  }
  
  public void setLiquidType(String liquidType) {
    FMultipleChoice liqType = (FMultipleChoice) getFocProperty(L3SampleDesc.FLD_LIQUIDE_TYPE);
    if(liqType != null){
      liqType.setString(liquidType);
    }
  }
  
  public String getFirstName() {
  	FString fName = (FString) getFocProperty(L3SampleDesc.FLD_FIRST_NAME);
    return (fName != null) ? fName.getString() :"";
  }
  
  public void setFirstName(String firstName) {
  	FString fName = (FString) getFocProperty(L3SampleDesc.FLD_FIRST_NAME);
    if(fName != null){
      fName.setString(firstName);
    }
  }
  
  public String getLastName() {
  	FString lName = (FString) getFocProperty(L3SampleDesc.FLD_LAST_NAME);
    return (lName != null) ? lName.getString() :"";
  }
  
  public void setLastName(String lastName) {
  	FString lName = (FString) getFocProperty(L3SampleDesc.FLD_LAST_NAME);
    if(lName != null){
      lName.setString(lastName);
    }
  }
  
  public String getMiddleInitial() {
  	FString mid = (FString) getFocProperty(L3SampleDesc.FLD_MIDDLE_INITIAL);
    return (mid != null) ? mid.getString() :"";
  }
  
  public void setMiddleInitial(String middleInitial) {
  	FString mid = (FString) getFocProperty(L3SampleDesc.FLD_MIDDLE_INITIAL);
    if(mid != null){
    	if(middleInitial == null || middleInitial.compareTo("null") == 0){
    		middleInitial = "";
    	}
      mid.setString(middleInitial);
    }
  }
  
  public boolean isResultConfirmed() {
    FBoolean confirmed = (FBoolean) getFocProperty(L3SampleDesc.FLD_RESULT_CONFIRMED);
    return (confirmed!= null) ? confirmed.getBoolean() :null;
  }
    
  public void setResultConfirmed(boolean confirmed) {
    FBoolean c = (FBoolean) getFocProperty(L3SampleDesc.FLD_RESULT_CONFIRMED);
    if(c != null){
      c.setBoolean(confirmed);
    }
  }
  
  public boolean isOkToBeSent() {
    FBoolean ok = (FBoolean) getFocProperty(L3SampleDesc.FLD_OK_TO_BE_SENT);
    return (ok!= null) ? ok.getBoolean() :null;
  }
    
  public void setOkToBeSent(boolean ok) {
    FBoolean o = (FBoolean) getFocProperty(L3SampleDesc.FLD_OK_TO_BE_SENT);
    if(o != null){
      o.setBoolean(ok);
    }
  }
  
  public Date getEntryDate(){
    FDateTime date = (FDateTime) getFocProperty(L3SampleDesc.FLD_ENTRY_DATE);
    return (date!=null) ? date.getDate() : null;
  }
  
  public void setEntryDate(Date d){
	FDateTime date = (FDateTime) getFocProperty(L3SampleDesc.FLD_ENTRY_DATE);
    if (date != null){
      date.setDate(d);
    }
  }
	  
  public Date getDateOfBirth(){
    FDateTime date = (FDateTime) getFocProperty(L3SampleDesc.FLD_DATE_OF_BIRTH);
    return (date!=null) ? date.getDate() : null;
  }
  
  public void setDateOfBirth(Date d){
	FDateTime date = (FDateTime) getFocProperty(L3SampleDesc.FLD_DATE_OF_BIRTH);
    if (date != null){
      date.setDate(d);
    }
  }
  
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LISTENERS
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  private static FPropertyListener blockedListener = null;
  
  public FPropertyListener getBlockedListener(){
    if (blockedListener == null){
      blockedListener = new FPropertyListener(){
        public void dispose() {
        }

        public void propertyModified(FProperty property) {
        }
      };
    }
    return blockedListener;
  }  
    
	//---------------------------------
	//    PANEL
	// ---------------------------------
	public FPanel newDetailsPanel(int viewID) {
		return new L3SampleGuiDetailsPanel(viewID, this);
	}
	
	public static FPanel newBrowsePanel(FocList list, int viewID) {
		return new L3SampleGuiBrowsePanel(list, viewID); 
	}
	
	//ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FOC
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getFocDesc() {
  	if (focDesc==null){
  		focDesc = new L3SampleDesc();
  	}
	  return focDesc;
	}

  //MESSAGE BEGIN
  public boolean hasInstrumentMessage(){
    boolean has = false;
    FocList instrumentMessageList = getInstrumentMessageList();
    if(instrumentMessageList != null){
    	has = instrumentMessageList.size() > 0;
    }
    return has;
  }

  public void pushMessageInternal(Instrument instrument, boolean append, String message){
    FocList instrumentMessageList = getInstrumentMessageList();
    L3InstrumentMessage foundInstrument = (L3InstrumentMessage) instrumentMessageList.searchByPropertyObjectValue(L3InstrumentMessageDesc.FLD_INSTRUMENT, instrument);
    L3InstrumentMessage instrMessage = null;
    
    if(foundInstrument != null){
      instrMessage = foundInstrument;
    }else if(message != null && message.compareTo("") != 0){
      instrMessage = (L3InstrumentMessage) instrumentMessageList.newEmptyItem();
      instrMessage.setMessage("");
    }
    if(instrMessage != null){
	    instrMessage.setL3Sample(this);
	    if(append){
	    	String strToSet = instrMessage.getMessage();
	    	if(strToSet.length() > 0) strToSet = strToSet + ", ";
	    	strToSet += message;
	    	instrMessage.setMessage(strToSet);
	    }else{
	    	instrMessage.setMessage(message);
	    }
	    instrMessage.setInstrument(instrument);
	    instrMessage.setStatus(L3TestDesc.TEST_STATUS_AVAILABLE_IN_L3);
    }
    //WE HAVE TO VALIDATE THE INSTRUMENT MESSAGE LIST
  }
  
  public void pushMessageByAppend(Instrument instrument, String message){
  	pushMessageInternal(instrument, true, message);
  }

  public void pushMessage(Instrument instrument, String message){
  	pushMessageInternal(instrument, false, message);
  }
  
  public FocList getInstrumentMessageList(){
    FocList list = getPropertyList(L3SampleDesc.FLD_INSTRUMENT_MESSAGE_LIST);
    list.setDirectImpactOnDatabase(false);
    return list;
  }

  public FocList getInstrumentMessageListWithoutLoad(){
    FList fList = (FList) getFocProperty(L3SampleDesc.FLD_INSTRUMENT_MESSAGE_LIST);
    FocList list = fList.getListWithoutLoad();
    return list;
  } 
  //MESSAGE END
  
  public void saveGraph(Instrument instrument){
    if(graph != null){
      //Create the object and load the reference 'REF' from the database if it exists
      L3InstrumentGraph graphDBLine = new L3InstrumentGraph(instrument.getCode(), getId());
      
      //Set the graph
      graphDBLine.setStatus(LisTestDesc.STATUS_TEST_RESULT_UPDATED_BY_L3);
      graphDBLine.setGraph(graph);
      graphDBLine.validate(false);
    }
  }

public String getRackNumber() {
	return rackNumber;
}

public void setRackNumber(String rackNumber) {
	this.rackNumber = rackNumber;
}

public String getTubePosition() {
	return tubePosition;
}

public void setTubePosition(String tubePosition) {
	this.tubePosition = tubePosition;
}
}
