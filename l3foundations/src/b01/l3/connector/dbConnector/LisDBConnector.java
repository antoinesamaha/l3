package b01.l3.connector.dbConnector;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.desc.FocDesc;
import b01.foc.desc.field.FMultipleChoiceItem;
import b01.foc.list.FocList;
import b01.foc.property.FDateTime;
import b01.foc.property.FList;
import b01.foc.property.FMultipleChoice;
import b01.foc.property.FString;
import b01.l3.Instrument;
import b01.l3.PoolKernel;
import b01.l3.connector.L3IConnector;
import b01.l3.connector.LisConnector;
import b01.l3.connector.LisConnectorDesc;
import b01.l3.connector.dbConnector.lisConnectorTables.LisInstrumentMessageDesc;
import b01.l3.connector.dbConnector.lisConnectorTables.LisSampleDesc;
import b01.l3.connector.dbConnector.lisConnectorTables.LisTestDesc;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3SampleDesc;
import b01.l3.data.L3SampleMessageJoin;
import b01.l3.data.L3Test;
import b01.l3.data.L3TestDesc;
import b01.l3.exceptions.L3Exception;

public class LisDBConnector implements L3IConnector, Runnable {
	
  private LisConnector lisConnector = null;
  
  public static final int TIME_DELAY = 5000; 
  private long checkDelay = TIME_DELAY;
  private boolean stop = false;
  private Thread polling = null;
  
  public LisDBConnector() {
  } 

  public void dispose(){
    if(lisConnector != null){
    	lisConnector = null;
    }
	}
	 
  public void setLisConnector(LisConnector lisConnector){
  	this.lisConnector = lisConnector;
  }
    	
  public boolean connect() throws Exception{
    startPolling();
    return false;
  }
  
	public boolean disconnect() throws Exception{
    stopPolling();
    return false;
  }
	
	public String getName() {
		FString name = (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_NAME);
	  return (name!= null) ? name.getString() :"";
	}

  public void setName(String name) {
		FString n= (FString) lisConnector.getFocProperty(LisConnectorDesc.FLD_NAME);
	  if(n != null){
	    n.setString(name);
	  }
	}

  //MESSAGE BEGIN
  public void postMessagesToLis(FocList instrMessageList) throws Exception {
    if(instrMessageList != null){
      for(int i=0; i < instrMessageList.size(); i++){
        L3SampleMessageJoin l3Join = (L3SampleMessageJoin) instrMessageList.getFocObject(i);
        
        int sampleID = -1;
        String instrumentCode = l3Join.getInstrumentCode();
        try{
        	sampleID = Integer.valueOf(l3Join.getSampleId());
        }catch(Exception e){
        	Globals.logException(e);
        	l3Join.updateCommitedField(L3TestDesc.TEST_STATUS_ERROR_WIHLE_COMMIT_TO_LIS);
        }
        
        if(sampleID > 0 && instrumentCode != null && instrumentCode.trim().compareTo("") != 0){
          Globals.getDBManager().beginTransaction();
          try{
	        	instrumentCode = instrumentCode.trim();
	        	
		      	StringBuffer whereSection = new StringBuffer(" WHERE ");
		      	whereSection.append(LisInstrumentMessageDesc.FNAME_SAMPLE_ID + " = " + sampleID);
		      	whereSection.append(" AND ");
		      	whereSection.append(LisInstrumentMessageDesc.FNAME_INSTRUMENT_CODE + " = '" + instrumentCode+"'");
		
		      	StringBuffer selectRequest = new StringBuffer("SELECT ");
		      	selectRequest.append(LisInstrumentMessageDesc.FNAME_SAMPLE_ID+", "+LisInstrumentMessageDesc.FNAME_INSTRUMENT_CODE+", "+LisInstrumentMessageDesc.FNAME_MESSAGE);
		      	selectRequest.append(" FROM ");
		      	selectRequest.append(LisInstrumentMessageDesc.getInstance().getStorageName());
		      	selectRequest.append(whereSection.toString());
		
		      	boolean lineExists = false;
		      	//Select to see if the line exists
	          Statement stmt = Globals.getDBManager().lockStatement();
	          try{
		          String selectRequestStr = selectRequest.toString();
		          if(ConfigInfo.isLogDBSelectActive() && ConfigInfo.isLogDBRequestActive()) Globals.logString(selectRequestStr);
		        	ResultSet resSet = stmt.executeQuery(selectRequestStr);
		
		        	while(resSet != null && resSet.next()){
		        		lineExists = true;
		        	}
		        	Globals.getDBManager().unlockStatement(stmt);
		        	stmt = null;
	          }catch(Exception e){
		        	Globals.getDBManager().unlockStatement(stmt);
		        	stmt = null;
		        	throw new L3Exception(e);
	          }
	
	          String message = l3Join.getMessage();
	          message = message.replaceAll("'", "''");
	          
	        	if(lineExists){
	        		stmt = Globals.getDBManager().lockStatement();
	        		try{
		  	        StringBuffer update  = new StringBuffer("UPDATE " + LisInstrumentMessageDesc.getInstance().getStorageName());
		  	        update.append(" SET ");
		  	        update.append(LisInstrumentMessageDesc.FNAME_MESSAGE + " = '" + message + "', ");
		  	        update.append(LisInstrumentMessageDesc.FNAME_STATUS + " = " + LisTestDesc.STATUS_TEST_RESULT_UPDATED_BY_L3 + " ");
		  	        update.append(whereSection);
		  	        
		  	      	if(ConfigInfo.isLogDBRequestActive()){
		  	      		Globals.logString(update);
		  	      	}
		  	        stmt.executeUpdate(update.toString());
		        		Globals.getDBManager().unlockStatement(stmt);
		        		stmt = null;
	        		}catch(Exception e){
	  	        	Globals.getDBManager().unlockStatement(stmt);
	  	        	stmt = null;
	  	        	throw new L3Exception(e);
	        		}
	        	}else{
	        		stmt = Globals.getDBManager().lockStatement();
	        		try{
		  	        StringBuffer insert = new StringBuffer("INSERT INTO " + LisInstrumentMessageDesc.getInstance().getStorageName()+" ");
		  	        insert.append("(");
		  	        insert.append(LisInstrumentMessageDesc.FNAME_SAMPLE_ID+",");
		  	        insert.append(LisInstrumentMessageDesc.FNAME_INSTRUMENT_CODE+",");
		  	        insert.append(LisInstrumentMessageDesc.FNAME_MESSAGE+",");
		  	        insert.append(LisInstrumentMessageDesc.FNAME_STATUS);
		  	        insert.append(") VALUES (");
		  	        insert.append(sampleID+",");
		  	        insert.append("'"+instrumentCode+"',");
		  	        insert.append("'"+message+"',");
		  	        insert.append(LisTestDesc.STATUS_TEST_RESULT_UPDATED_BY_L3);
		  	        insert.append(")");
		
		  	      	if(ConfigInfo.isLogDBRequestActive()){
		  	      		Globals.logString(insert.toString());
		  	      	}
		  	        stmt.executeUpdate(insert.toString());
		        		Globals.getDBManager().unlockStatement(stmt);
		        		stmt = null;
	        		}catch(Exception e){
	  	        	Globals.getDBManager().unlockStatement(stmt);
	  	        	stmt = null;
	  	        	throw new L3Exception(e);
	        		}
	        	}
	
	        	l3Join.updateCommitedField(L3TestDesc.TEST_STATUS_COMMITED_TO_LIS);
	        	Globals.getDBManager().commitTransaction();
          }catch(Exception e){
          	Globals.getDBManager().rollbackTransaction(null);
          	throw new L3Exception(e);
          }
        }
      }
    }
  }
  //MESSAGE END
  
  public void postSampleToLis(L3Sample sample) throws Exception {
  	if(sample != null && sample.getId() != null && sample.getId().trim().compareTo("") != 0){
      FocDesc lisConnectorTestDesc   = LisTestDesc.getInstance();
      
      String testTableName    = lisConnectorTestDesc.getStorageName();
      String testStatus       = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_STATUS).getName();
      String testAlarm        = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_ALARM).getName();
      String testVerificationPending = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_VERIFICATION_PENDING).getName();
      String testResult       = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_RESULT).getName();
      //NOTES-B
      String testResultNotes  = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_RESULT_NOTES).getName();
      //NOTES-E      
      String testUnit         = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_UNIT).getName();
      String testActualAnalyserCode = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_ACTUAL_ANALYSER_CODE).getName();
      String testLabel        = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_TEST_CODE).getName();
      String testMessage      = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_MESSAGE).getName();
      String testSampleId     = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_SAMPLE_ID).getName();
      String sequenceIdFld    = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_SEQUENCE_ID).getName();
      
      Iterator<L3Test> testIter = (Iterator<L3Test>) sample.testIterator();
      while(testIter.hasNext()){

        L3Test l3Test = testIter.next();
        
      	Statement stmt = null;

        try{
	        
	        if(l3Test.isResultOk()){
	        	StringBuffer whereSection  = new StringBuffer(" WHERE "+ testLabel + " = \'" + l3Test.getLabel()+ "\'");
	        	whereSection.append(" AND "+ testSampleId + " = " + sample.getId());
	
	        	StringBuffer selectRequest = new StringBuffer("SELECT ");
	        	selectRequest.append(sequenceIdFld);
	        	selectRequest.append(" FROM ");
	        	selectRequest.append(testTableName);
	        	selectRequest.append(whereSection.toString());
	
	        	int sequence = -1;
	          stmt = Globals.getDBManager().lockStatement();
	          String selectRequestStr = selectRequest.toString();
	          if(ConfigInfo.isLogDBSelectActive() && ConfigInfo.isLogDBRequestActive()) Globals.logString(selectRequestStr);
	        	ResultSet resSet = stmt.executeQuery(selectRequestStr);
	
	        	while(resSet != null && resSet.next()){
	        		if(sequence == -1){
	        			sequence = resSet.getInt(1);
	        		}else{
	        			sequence = -999;
	        		}
	        	}
	        	Globals.getDBManager().unlockStatement(stmt);
	        	stmt = null;
	
	        	if(sequence == -1){
	        		l3Test.updateStatus(L3TestDesc.TEST_STATUS_NOT_IN_LIS_WHEN_COMMITING_RESULT);
	        	}else if(sequence == -999){
	        		l3Test.updateStatus(L3TestDesc.TEST_STATUS_MULTIPLE_LIS_ENTRIES_UPON_COMMIT);
	        	}else if(sequence >= 0){
	            Globals.getDBManager().beginTransaction();
	
	        		Instrument instr = (Instrument) l3Test.getPropertyObject(L3TestDesc.FLD_RECEIVE_INSTRUMENT);
	        		String actualInstrument = instr != null ? instr.getCode() : "";
	
	        		int postedStatus = LisTestDesc.STATUS_TEST_RESULT_UPDATED_BY_L3;
	        		String postedMessage = l3Test.getPropertyString(L3TestDesc.FLD_MESSAGE);
	
	        		if(l3Test.isBlocked() || !l3Test.isResultOk()){
	        			postedStatus = LisTestDesc.STATUS_BLOCKED_FOR_ERROR;
	        			if(postedMessage.trim().compareTo("") == 0){
	        				postedMessage = "Error ";
	        				if(!l3Test.isResultOk()) postedMessage += " Result not OK";
	        			}
	        		}
	
	        		stmt = Globals.getDBManager().lockStatement();
	  	        StringBuffer update  = new StringBuffer("UPDATE " + testTableName);
	  	        update.append(" SET ");
	  	        update.append(testAlarm + " = " + l3Test.getAlarm() +", ");
	  	        String verifPendingFlg = l3Test.isVerificationPendingFlag() ? "1" : "0";
	  	        update.append(testVerificationPending+" = "+verifPendingFlg +", ");
	  	        //NOTES-B
	  	        if(l3Test.getValue() == L3TestDesc.VALUE_NULL){
	  	        	update.append(testResult + " = null, ");
	  	        }else{
	  	        //NOTES-E
	  	        	update.append(testResult + " = " + l3Test.getValue() +", ");
	  	        //NOTES-B	  	        	
	  	        }
	  	        update.append(testResultNotes + " = \'" + l3Test.getValueNotes() + "\', ");
	  	        //NOTES-E
	  	        if (l3Test.getUnitLabel() != null && l3Test.getUnitLabel() != ""){
	  	        	update.append(testUnit + " = '" + l3Test.getUnitLabel() +"', ");
	  		      }
	  	        update.append(testActualAnalyserCode + " = \'" + actualInstrument +"\'"+", ");
	  	        update.append(testMessage + " = \'" + postedMessage +"\'"+", "); 
	  	        update.append(testStatus + " = " + postedStatus);
	  	        update.append(" WHERE "+ sequenceIdFld + " = " + sequence);
	  	      	if(ConfigInfo.isLogDBRequestActive()){
	  	      		Globals.logString(update);
	  	      	}
	  	        stmt.executeUpdate(update.toString());
	        		Globals.getDBManager().unlockStatement(stmt);
	        		stmt = null;
	
	        		l3Test.updateStatus(L3TestDesc.TEST_STATUS_COMMITED_TO_LIS);
	            Globals.getDBManager().commitTransaction();
	        	}
	        }
        }catch(Exception e){
        	Globals.logException(e);
        	
        	l3Test.updateStatus(L3TestDesc.TEST_STATUS_NOT_IN_USE);
        	l3Test.updateNotificationMessage("While posting to LIS" );
        	l3Test.updateBlocked(true);
        	if(stmt != null){
        		Globals.getDBManager().unlockStatement(stmt);
        		stmt = null;
        	}
        	throw e;
        }
      }
  	}
  }
  
  public void getDataFromDB(){
    L3Message message = null;
    L3Sample sample   = null;
    try{
      FocDesc lisConnectorSampleDesc = LisSampleDesc.getInstance();
      FocDesc lisConnectorTestDesc   = LisTestDesc.getInstance();
     
      String sampleTableName    = lisConnectorSampleDesc.getStorageName();
      String testTableName      = lisConnectorTestDesc.getStorageName();
      String sampleId           = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_SAMPLE_ID).getName();
      String patientId          = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_PATIENT_ID).getName();
      String firstName          = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_PATIENT_FIRST_NAME).getName();
      String lastName           = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_PATIENT_LAST_NAME).getName();
      String middleInitial      = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_PATIENT_MIDDLE_INITIAL).getName();          
      String liquidType         = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_LIQUID_TYPE).getName();
      String ageFldName         = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_PATIENT_AGE).getName();
      String sexFldName         = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_PATIENT_SEX).getName();
      String dateAndTimeFldName = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_COLLECTION_DATE).getName();
      String dateOfBirthFldName = lisConnectorSampleDesc.getFieldByID(LisSampleDesc.FLD_DATE_OF_BIRTH).getName();
      String testStatus         = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_STATUS).getName();
      String testLabel          = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_TEST_CODE).getName();
      String messageFldName     = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_MESSAGE).getName();
      String analyserCode       = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_ANALYSER_CODE).getName();
      String testPriority       = lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_PRIORITY).getName();
      
      String selectRequestFromLisDB = "SELECT * FROM "+ sampleTableName + " M, " + testTableName + " D WHERE "+ "M."+sampleId+" = "+ "D."+sampleId+" AND D."+ testStatus+" = "+ LisTestDesc.STATUS_TEST_TO_READ +" ORDER BY M."+sampleId;

      if(ConfigInfo.isLogDBRequestActive() && ConfigInfo.isLogDBSelectActive()){
      	Globals.logString(selectRequestFromLisDB);
      }

      Statement stmt = Globals.getDBManager().lockStatement();      
      ResultSet resultSet = stmt.executeQuery(selectRequestFromLisDB);
      
      String resultSampleid = null;       
      while(resultSet.next()){
        String newResultSampleId = resultSet.getString(sampleId);
        if(resultSampleid == null || resultSampleid.compareTo(newResultSampleId) != 0){
          if (message == null){
            message = new L3Message();
          }
          sample = new L3Sample(resultSampleid);
          //sample.setSampleAsNonDatabaseResident();
          sample.setSampleTestListAsLoaded();
          message.addSample(sample);   
          
          sample.setId(resultSet.getString(sampleId));
          
          String firstNameValue = resultSet.getString(firstName);
          firstNameValue = firstNameValue.replaceAll("'", "''");
          sample.setFirstName(firstNameValue);
          
          String lastNameValue = resultSet.getString(lastName);
          lastNameValue = lastNameValue.replaceAll("'", "''");
          sample.setLastName(lastNameValue);
          
          sample.setMiddleInitial(resultSet.getString(middleInitial));

          String patientIDValue = resultSet.getString(patientId);
          sample.setPatientId(patientIDValue);
          
          sample.setAge(resultSet.getInt(ageFldName));
          sample.setSexe(resultSet.getString(sexFldName));

          FDateTime entryDate = (FDateTime) sample.getFocProperty(L3SampleDesc.FLD_ENTRY_DATE);
          entryDate.setSqlString(resultSet.getString(dateAndTimeFldName));
          
          FDateTime dobDate = (FDateTime) sample.getFocProperty(L3SampleDesc.FLD_DATE_OF_BIRTH);
          dobDate.setSqlString(resultSet.getString(dateOfBirthFldName));
          
          String lisLiquidTypeValue = resultSet.getString(liquidType);
          if(lisLiquidTypeValue != null){
          	FMultipleChoice multiChoice = (FMultipleChoice) sample.getFocProperty(L3SampleDesc.FLD_LIQUIDE_TYPE);

          	boolean liquidTypeFound = false;
            Iterator iter = multiChoice.getChoiceIterator();
            while (iter != null && iter.hasNext() && !liquidTypeFound) {
              FMultipleChoiceItem item = (FMultipleChoiceItem) iter.next();
              if (item != null){
              	String capitalItem = new String(item.getTitle());
              	if(capitalItem.toUpperCase().compareTo(lisLiquidTypeValue.toUpperCase()) == 0){
              		multiChoice.setInteger(item.getId());
              		liquidTypeFound = true;
              	}
              }
            }

            if(!liquidTypeFound){
	          	if(lisLiquidTypeValue.compareTo("SPRNT") == 0){
	          		multiChoice.setInteger(L3Sample.LIQUID_TYPE_SUPERNATENT);
	          	}else if(lisLiquidTypeValue.compareTo("SER") == 0 || lisLiquidTypeValue.compareTo("PLAS") == 0){
	          		multiChoice.setInteger(L3Sample.LIQUID_TYPE_SERUM);
	          	}else if(lisLiquidTypeValue.startsWith("UR")){//Starts with covers UR and UR24h for Modular
	          		multiChoice.setInteger(L3Sample.LIQUID_TYPE_URIN);          
	          	}else if(lisLiquidTypeValue.compareTo("FLU") == 0){
	          		multiChoice.setInteger(L3Sample.LIQUID_TYPE_OTHERS);
	          	}
            }
          }
          
          resultSampleid = newResultSampleId;          
        }
        L3Test test = sample.addTest();
        test.setPropertyString(L3TestDesc.FLD_LABEL, resultSet.getString(lisConnectorTestDesc.getFieldByID(LisTestDesc.FLD_TEST_CODE).getName()));
        String analyserCodeValue = resultSet.getString(analyserCode);
        if(analyserCodeValue != null && analyserCodeValue.trim().compareTo("") != 0){
        	Instrument instr = PoolKernel.getInstrumentForAnyPool(analyserCodeValue);
        	test.setPropertyObject(L3TestDesc.FLD_SUGGESTED_INSTRUMENT, instr);
        }
        String testPriorityValue = resultSet.getString(testPriority);
        if(testPriorityValue != null){
        	test.setPriority(testPriorityValue);
        }
      }
      Globals.getDBManager().unlockStatement(stmt);
      
      if (message != null){
        Globals.getDBManager().beginTransaction();
        try{
	        lisConnector.treatMessage(message);   
	        Iterator<L3Sample> sampleIter = message.sampleIterator();
	        while(sampleIter != null && sampleIter.hasNext()){
	          L3Sample l3Sample = sampleIter.next();
	          
	          FList testListProp = (FList) l3Sample.getFocProperty(L3SampleDesc.FLD_TEST_LIST);
	          FocList testList = testListProp.getListWithoutLoad();

	          Iterator testIter = (Iterator)testList.focObjectIterator();
	          while(testIter != null && testIter.hasNext()){
	            L3Test l3Test = (L3Test) testIter.next();
	            
	            int newStatus = LisTestDesc.STATUS_TEST_READ_BY_L3;
	            String newMessage = "";
	            if(l3Test.getPropertyObject(L3TestDesc.FLD_DISPATCH_INSTRUMENT) == null){
	            	newStatus = LisTestDesc.STATUS_BLOCKED_FOR_ERROR;
	            	newMessage = l3Test.getPropertyString(L3TestDesc.FLD_MESSAGE);
	            }
	            
	            String update = "UPDATE "+ testTableName+ " SET "+ testStatus+" = "+newStatus+", "+messageFldName+" = '"+newMessage+"' WHERE "+ sampleId+" = "+ l3Sample.getId()+" AND "+ testLabel+" = \'" + l3Test.getLabel()+"\'";
	            Globals.logString(update);
	            stmt = Globals.getDBManager().lockStatement();
	            stmt.executeUpdate(update);
	            Globals.getDBManager().unlockStatement(stmt);
	          }
	        }	        
        }catch(Exception e){
        	Globals.getDBManager().commitTransaction();
        	throw(e);
        }
        Globals.getDBManager().commitTransaction();
      }
    }catch(Exception e){
      Globals.logException(e);
    }    
    if(message != null){
    	message.dispose();
    	message = null;
    }
  }  
  
  public void startPolling(){
    stop = false;
    if (polling == null){
      polling = new Thread(this);
      polling.start();
    }
  }
  
  public void stopPolling(){
    stop = true;
  }
  
  public void run() {
    try {
      while(true){
        if (!stop){
        	lisConnector.setBusyReading(true);
          getDataFromDB();
          lisConnector.setBusyReading(false);
        }
        Thread.sleep(checkDelay);
      }
    }catch (InterruptedException e) {
      Globals.logException(e);
      lisConnector.setBusyReading(false);
    }
  }
    
}