package b01.hsg.unit;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.fUnit.FocTestSuite;
import b01.foc.property.FDate;
import b01.hsg.unit.HSGUnitGlobals;
import b01.l3.Instrument;
import b01.l3.PoolKernel;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.coulter.CoulterEmulator;
import b01.l3.emulator.EmulatorRobot;
import b01.l3.unit.L3TestCase;

public class HSGDevComplete extends L3TestCase implements HSGUnitGlobals{
  
	private static int testTableSequence = 1;
	
  public HSGDevComplete(FocTestSuite testSuite, String functionName){
    super(testSuite, functionName, CONNECTOR_ANALYSERS, CONNECTOR_EMULATORS);
  }

  public void test_COMPLETE_ResetLISTablesAndLocks(){    
		unlockAllLockableObjects();
	  sql_DeleteLISTables();
  }

  public void test_COMPLETE_Connect(){
//    connector_ConnectConnectorDriverAndEmulator(INST_AXSYM, EMUL_AXSYM);
//    connector_ConnectConnectorDriverAndEmulator(INST_COULTER_A, EMUL_COULTER_A);
//    connector_ConnectConnectorDriverAndEmulator(INST_COULTER_B, EMUL_COULTER_B);
//    connector_ConnectConnectorDriverAndEmulator(INST_ARCHITECT, EMUL_ARCHITECT);
//    connector_ConnectConnectorDriverAndEmulator(INST_ELECSYS, EMUL_ELECSYS);
//    connector_ConnectConnectorDriverAndEmulator(INST_MODULAR, EMUL_MODULAR);
    
    connector_Edit(CONNECTOR_ANALYSERS);
    connector_ClickConnect();
    
    sleep(2);
  }
  
  public void test_COMPLETE_Disconnect(){

    connector_Edit(CONNECTOR_ANALYSERS);
    connector_ClickConnect();
  	
  	connector_DisconnectConnectorDriverAndEmulator(INST_AXSYM, EMUL_AXSYM);
//  	connector_DisconnectConnectorDriverAndEmulator(INST_COULTER_A, EMUL_COULTER_A);
//  	connector_DisconnectConnectorDriverAndEmulator(INST_COULTER_B, EMUL_COULTER_B);
//  	connector_DisconnectConnectorDriverAndEmulator(INST_ARCHITECT, EMUL_ARCHITECT);
//  	connector_DisconnectConnectorDriverAndEmulator(INST_ELECSYS, EMUL_ELECSYS);
//  	connector_DisconnectConnectorDriverAndEmulator(INST_MODULAR, EMUL_MODULAR);

  	sleep(1);
    button_ClickValidate("");
    sleep(2);
    button_ClickValidate("");
    sleep(2);
    button_ClickValidate("");
    sleep(2);
  }

  public void test_COMPLETE_InsertSomeLines(){
  	test_COMPLETE_InsertSomeLinesWithIndex(0);
  }

  public void test_COMPLETE_InsertSomeLines2(){
  	test_COMPLETE_InsertSomeLinesWithIndex(1);
  }

  public void test_COMPLETE_InsertSomeLinesWith1MinInterval(){
  	test_COMPLETE_InsertSomeLinesWithIndex(0);
  	try{
	  	Thread.sleep(60*1000);
	  	test_COMPLETE_InsertSomeLinesWithIndex(1);
  	}catch(Exception e){
  		fail();
  	}
  }

  public void test_COMPLETE_SendNewResults(){
  	Instrument instr = PoolKernel.getInstrumentForAnyPool(EMUL_COULTER_A);
  	try{
  		EmulatorRobot robot = ((CoulterEmulator) instr.getDriver()).getRobot();
  		L3Message message = robot.newResultMessageToSend();
  		
  		L3Sample sample = new L3Sample("5656");
  		message.addSample(sample);
  		
  		sample.setAge(23);
  		sample.setFirstName("MAFI");
  		sample.setLastName("JDID");
  		sample.setLiquidType(L3Sample.LIQUID_TYPE_SERUM);
  		
  		L3Test test = sample.addTest();
  		test.setLabel("2");
  		test.setValue(22);
  		
  		test = sample.addTest();
  		test.setLabel("3");
  		test.setValue(33);

  		test = sample.addTest();
  		test.setLabel("4");
  		test.setValue(44);

  		  		
  	}catch(Exception e){
  		Globals.logException(e);
  		fail();
  	}
  }
  
  private void test_COMPLETE_InsertSomeLinesWithIndex(int idx){
  	try{

  		//COULTER <25
  		//AxSYM >25
  		//Architect 100
  		//ElecSYS, Modular 400
  		
      Globals.getDBManager().beginTransaction();
      int sampleID = 90101+idx;
	  	sql_InsertSample(sampleID, "Serum", "TST_Rami", "TST_KEKKO", "K", 52, "M");
	    sql_InsertTest(sampleID, "", "25");
	    sql_InsertTest(sampleID, "", "26");
	    sql_InsertTest(sampleID, "", "27");
	    sql_InsertTest(sampleID, "", "28");
	    sql_InsertTest(sampleID, "", "29");
	    sql_InsertTest(sampleID, "", "30");
	    sql_InsertTest(sampleID, "", "31");
	    sql_InsertTest(sampleID, "", "32");
	    sql_InsertTest(sampleID, "", "33");
	    sql_InsertTest(sampleID, "", "34");
	    sql_InsertTest(sampleID, "", "35");
	    sql_InsertTest(sampleID, "", "36");
	    sql_InsertTest(sampleID, "", "37");
	    sql_InsertTest(sampleID, "", "38");
	    sql_InsertTest(sampleID, "", "39");
	    sql_InsertTest(sampleID, "", "40");
	    sql_InsertTest(sampleID, "", "ABCDE");
	    Globals.getDBManager().commitTransaction();
	
      Globals.getDBManager().beginTransaction();
      sampleID = 90201+idx;
	  	sql_InsertSample(sampleID, "Serum", "TST_Salwa", "TST_Chemali", "F", 21, "F");
	    sql_InsertTest(sampleID, "", "1");
	    sql_InsertTest(sampleID, "", "2");
	    sql_InsertTest(sampleID, "", "3");
	    sql_InsertTest(sampleID, "", "4");
	    sql_InsertTest(sampleID, "", "5");
	    sql_InsertTest(sampleID, "", "6");
	    sql_InsertTest(sampleID, "", "7");
	    sql_InsertTest(sampleID, "", "8");
	    sql_InsertTest(sampleID, "", "9");
	    sql_InsertTest(sampleID, "", "10");
	    sql_InsertTest(sampleID, "", "11");
	    sql_InsertTest(sampleID, "", "12");
	    sql_InsertTest(sampleID, "", "13");
	    sql_InsertTest(sampleID, "", "14");
	    sql_InsertTest(sampleID, "", "15");
	    sql_InsertTest(sampleID, "", "16");
	    sql_InsertTest(sampleID, "", "17");
	    sql_InsertTest(sampleID, "", "18");
	    sql_InsertTest(sampleID, "", "19");
	    sql_InsertTest(sampleID, "", "20");
	    sql_InsertTest(sampleID, "", "21");
	    sql_InsertTest(sampleID, "", "22");    	
	    sql_InsertTest(sampleID, "", "25");
	    sql_InsertTest(sampleID, "", "26");
	    sql_InsertTest(sampleID, "", "27");
	    sql_InsertTest(sampleID, "", "28");
	    sql_InsertTest(sampleID, "", "29");
	    sql_InsertTest(sampleID, "", "30");
	    sql_InsertTest(sampleID, "", "31");
	    sql_InsertTest(sampleID, "", "32");
	    sql_InsertTest(sampleID, "", "33");
	    sql_InsertTest(sampleID, "", "34");
	    sql_InsertTest(sampleID, "", "35");
	    sql_InsertTest(sampleID, "", "36");
	    sql_InsertTest(sampleID, "", "37");
	    sql_InsertTest(sampleID, "", "38");
	    sql_InsertTest(sampleID, "", "39");
	    sql_InsertTest(sampleID, "", "40");
	    Globals.getDBManager().commitTransaction();
	    
	    String forcedCoulter = HSGUnitGlobals.INST_COULTER_A;

      Globals.getDBManager().beginTransaction();
      sampleID = 90301+idx;
	  	sql_InsertSample(sampleID, "Serum", "TST_Linda", "TST_Salhab", "F", 34, "M");
	    sql_InsertTest(sampleID, forcedCoulter, "1");
	    sql_InsertTest(sampleID, forcedCoulter, "2");
	    sql_InsertTest(sampleID, forcedCoulter, "3");
	    sql_InsertTest(sampleID, forcedCoulter, "4");
	    sql_InsertTest(sampleID, forcedCoulter, "5");
	    sql_InsertTest(sampleID, forcedCoulter, "6");
	    sql_InsertTest(sampleID, forcedCoulter, "7");
	    sql_InsertTest(sampleID, forcedCoulter, "8");
	    sql_InsertTest(sampleID, forcedCoulter, "9");
	    sql_InsertTest(sampleID, forcedCoulter, "10");
	    sql_InsertTest(sampleID, forcedCoulter, "11");
	    sql_InsertTest(sampleID, forcedCoulter, "12");
	    sql_InsertTest(sampleID, forcedCoulter, "13");
	    sql_InsertTest(sampleID, forcedCoulter, "14");
	    sql_InsertTest(sampleID, forcedCoulter, "15");
	    sql_InsertTest(sampleID, forcedCoulter, "16");
	    sql_InsertTest(sampleID, forcedCoulter, "17");
	    sql_InsertTest(sampleID, forcedCoulter, "18");
	    sql_InsertTest(sampleID, forcedCoulter, "19");
	    sql_InsertTest(sampleID, forcedCoulter, "20");
	    sql_InsertTest(sampleID, forcedCoulter, "21");
	    sql_InsertTest(sampleID, forcedCoulter, "22");
	    Globals.getDBManager().commitTransaction();

      Globals.getDBManager().beginTransaction();
      sampleID = 90401+idx;
	  	sql_InsertSample( sampleID, "Serum", "TST_Raja", "TST_Kostantin", "F", 18, "M");
	    sql_InsertTest(sampleID, HSGUnitGlobals.INST_AXSYM, "1");
	    sql_InsertTest(sampleID, "", "2");
	    sql_InsertTest(sampleID, "", "3");
	    sql_InsertTest(sampleID, "", "4");
	    sql_InsertTest(sampleID, "", "5");
	    sql_InsertTest(sampleID, "", "6");
	    sql_InsertTest(sampleID, "", "7");
	    sql_InsertTest(sampleID, "", "8");
	    sql_InsertTest(sampleID, "", "9");
	    sql_InsertTest(sampleID, "", "10");
	    sql_InsertTest(sampleID, "", "11");
	    sql_InsertTest(sampleID, "", "12");
	    sql_InsertTest(sampleID, "", "13");
	    sql_InsertTest(sampleID, "", "14");
	    sql_InsertTest(sampleID, "", "15");
	    sql_InsertTest(sampleID, "", "16");
	    sql_InsertTest(sampleID, "", "17");
	    sql_InsertTest(sampleID, "", "18");
	    sql_InsertTest(sampleID, "", "19");
	    sql_InsertTest(sampleID, "", "20");
	    sql_InsertTest(sampleID, "", "21");
	    sql_InsertTest(sampleID, "", "22");
	    Globals.getDBManager().commitTransaction();

      Globals.getDBManager().beginTransaction();
      sampleID = 90501+idx;
	  	sql_InsertSample( sampleID, "Serum", "TST_Rima", "TST_Njeim", "K", 32, "M");
	    sql_InsertTest(sampleID, "", "100");
	    sql_InsertTest(sampleID, "", "101");
	    sql_InsertTest(sampleID, "", "102");
	    sql_InsertTest(sampleID, "", "103");
	    sql_InsertTest(sampleID, "", "104");
	    sql_InsertTest(sampleID, "", "105");
	    sql_InsertTest(sampleID, "", "106");
	    sql_InsertTest(sampleID, "", "107");
	    sql_InsertTest(sampleID, "", "108");
	    sql_InsertTest(sampleID, "", "109");
	    sql_InsertTest(sampleID, "", "110");
	    sql_InsertTest(sampleID, "", "111");
	    sql_InsertTest(sampleID, "", "112");
	    sql_InsertTest(sampleID, "", "113");
	    sql_InsertTest(sampleID, "", "114");
	    Globals.getDBManager().commitTransaction();

	    /*
      Globals.getDBManager().beginTransaction();
      for(int i=1; i<=43; i++){
      	int iTest = 400+i;
      	String test =  String.valueOf(iTest);
      	sampleID = 90000 + iTest;
  	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_SERUM_TITLE, "TST_Leia", "TST_Chalfoun", "T", 24, "F");
  	    sql_InsertTest(sampleID, "", test);
      }
	    Globals.getDBManager().commitTransaction();
	    */
	    
	    String prefix = "OP";
	    idx = 30;
	    
      Globals.getDBManager().beginTransaction();
      sampleID = 40601+idx;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_SERUM_TITLE, prefix+"_Leia", "TS_Chalfoun", "T", 24, "F");
	    sql_InsertTest(sampleID, "", "401");
	    sql_InsertTest(sampleID, "", "402");
	    sql_InsertTest(sampleID, "", "403");
	    sql_InsertTest(sampleID, "", "404");
	    sql_InsertTest(sampleID, "", "405");
	    sql_InsertTest(sampleID, "", "406");
	    sql_InsertTest(sampleID, "", "407");
	    sql_InsertTest(sampleID, "", "408");
	    sql_InsertTest(sampleID, "", "409");
	    sql_InsertTest(sampleID, "", "410");
	    Globals.getDBManager().commitTransaction();

	    /*
      Globals.getDBManager().beginTransaction();
      sampleID = 40701+idx;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_CSF_TITLE, prefix+"_Salim", "TS_Khailallah", "S", 45, "M");
	    sql_InsertTest(sampleID, "", "401");
	    sql_InsertTest(sampleID, "", "402");
	    sql_InsertTest(sampleID, "", "403");
	    sql_InsertTest(sampleID, "", "404");
	    sql_InsertTest(sampleID, "", "405");
	    sql_InsertTest(sampleID, "", "406");
	    sql_InsertTest(sampleID, "", "407");
	    sql_InsertTest(sampleID, "", "408");
	    sql_InsertTest(sampleID, "", "409");
	    sql_InsertTest(sampleID, "", "410");
	    sql_InsertTest(sampleID, "", "411");
	    sql_InsertTest(sampleID, "", "412");
	    sql_InsertTest(sampleID, "", "413");
	    sql_InsertTest(sampleID, "", "414");
	    sql_InsertTest(sampleID, "", "415");
	    sql_InsertTest(sampleID, "", "416");
	    sql_InsertTest(sampleID, "", "417");
	    sql_InsertTest(sampleID, "", "418");
	    sql_InsertTest(sampleID, "", "420");
	    sql_InsertTest(sampleID, "", "421");
	    sql_InsertTest(sampleID, "", "422");
	    sql_InsertTest(sampleID, "", "423");
	    sql_InsertTest(sampleID, "", "424");
	    sql_InsertTest(sampleID, "", "425");
	    sql_InsertTest(sampleID, "", "426");
	    sql_InsertTest(sampleID, "", "427");
	    sql_InsertTest(sampleID, "", "428");
	    sql_InsertTest(sampleID, "", "429");
	    sql_InsertTest(sampleID, "", "430");
	    sql_InsertTest(sampleID, "", "431");
	    sql_InsertTest(sampleID, "", "432");
	    sql_InsertTest(sampleID, "", "434");
	    sql_InsertTest(sampleID, "", "435");
	    sql_InsertTest(sampleID, "", "437");
	    sql_InsertTest(sampleID, "", "438");
	    sql_InsertTest(sampleID, "", "439");
	    sql_InsertTest(sampleID, "", "440");
	    sql_InsertTest(sampleID, "", "441");
	    sql_InsertTest(sampleID, "", "442");
	    sql_InsertTest(sampleID, "", "443");
	    Globals.getDBManager().commitTransaction();

      Globals.getDBManager().beginTransaction();
      //sampleID = 90801+idx;
      sampleID = 100+idx;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_CSF_TITLE, "TST_Helene", "TST_Jaquier", "S", 38, "F");
	    sql_InsertTest(sampleID, "", "603");
	    sql_InsertTest(sampleID, "", "604");
	    sql_InsertTest(sampleID, "", "605");
	    sql_InsertTest(sampleID, "", "606");
	    sql_InsertTest(sampleID, "", "607");
	    sql_InsertTest(sampleID, "", "608");
	    sql_InsertTest(sampleID, "", "609");
	    sql_InsertTest(sampleID, "", "610");
	    sql_InsertTest(sampleID, "", "611");
	    sql_InsertTest(sampleID, "", "612");
	    sql_InsertTest(sampleID, "", "613");
	    sql_InsertTest(sampleID, "", "614");
	    sql_InsertTest(sampleID, "", "615");
	    sql_InsertTest(sampleID, "", "616");
	    Globals.getDBManager().commitTransaction();
	     */
	    
      Globals.getDBManager().beginTransaction();
      //sampleID = 90801+idx;
      sampleID = 250+idx;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_CSF_TITLE, "TST_Helene", "TST_Jaquier", "S", 38, "F");
	    sql_InsertTest(sampleID, "", "600");
	    sql_InsertTest(sampleID, "", "601");
	    sql_InsertTest(sampleID, "", "602");
	    Globals.getDBManager().commitTransaction();

	    
      Globals.getDBManager().beginTransaction();
      sampleID = 91001+idx;
      sampleID = 6324;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_CSF_TITLE, "XXRony", "XXJalbout", "S", 38, "M");
	    sql_InsertTest(sampleID, "", "701");
	    Globals.getDBManager().commitTransaction();
	    
      Globals.getDBManager().beginTransaction();
      sampleID = 41101+idx;
      sampleID = 96301;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_SERUM_TITLE, prefix+"_AzizAS", "TS_FayadO", "K", 30, "F");
	    sql_InsertTest(sampleID, "", "414");
	    Globals.getDBManager().commitTransaction();

      Globals.getDBManager().beginTransaction();
      sampleID = 41101+idx;
      sampleID = 96302;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_URIN_TITLE, prefix+"_AzizAU", "TS_FayadO", "K", 30, "F");
	    sql_InsertTest(sampleID, "", "456");
	    Globals.getDBManager().commitTransaction();

	    /*
      Globals.getDBManager().beginTransaction();
      sampleID = 41101+idx;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_SERUM_TITLE, prefix+"_AzizA", "TS_FayadO", "K", 30, "F");
	    sql_InsertTest(sampleID, "", "418");
	    sql_InsertTest(sampleID, "", "409");
	    sql_InsertTest(sampleID, "", "414");
	    Globals.getDBManager().commitTransaction();

      Globals.getDBManager().beginTransaction();
      sampleID = 41102+idx;
	  	sql_InsertSample(sampleID, L3Sample.LIQUID_TYPE_SERUM_TITLE, prefix+"_ROGER", "TS_KIKKO", "K", 45, "M");
	    sql_InsertTest(sampleID, "", "418");
	    sql_InsertTest(sampleID, "", "409");
	    sql_InsertTest(sampleID, "", "414");
	    Globals.getDBManager().commitTransaction();
	    */
  	}catch(Exception e){
      Globals.logException(e);
      fail();
  	}
    sleep(3);
  }

  public void test_COMPLETE_InsertSample2Times(){
  	try{
      Globals.getDBManager().beginTransaction();
      int sampleID = 90101;
	  	sql_InsertSample(sampleID, "Serum", "TST_Rami", "TST_KEKKO", "K", 52, "M");
	    sql_InsertTest(sampleID, "", "15");
	    sql_InsertTest(sampleID, "", "16");
	    sql_InsertTest(sampleID, "", "17");
	    sql_InsertTest(sampleID, "", "18");
	    sql_InsertTest(sampleID, "", "19");
	    sql_InsertTest(sampleID, "", "20");
	    Globals.getDBManager().commitTransaction();

	    sleep(60);
	    
      Globals.getDBManager().beginTransaction();
	    sql_InsertTest(sampleID, "", "20");
	    sql_InsertTest(sampleID, "", "36");
	    sql_InsertTest(sampleID, "", "37");
	    sql_InsertTest(sampleID, "", "38");
	    sql_InsertTest(sampleID, "", "39");
	    sql_InsertTest(sampleID, "", "40");
	    sql_InsertTest(sampleID, "", "ABCDE");
	    Globals.getDBManager().commitTransaction();

  	}catch(Exception e){
      Globals.logException(e);
      fail();
  	}
    sleep(3);
  }
  
  
  public void test_COMPLETE_SelectSomeData(){
    Statement stmt = Globals.getDBManager().lockStatement();
    try{
    	//String req = "SELECT * FROM L3TEST WHERE SAMPLE_REF = 2558";
    	//String req = "SELECT * FROM LISTEST WHERE SAMPLE_ID = 6759";

    	//String req = "SELECT * from L3MESSAGE";
    	//String req = "SELECT * from LIS_INSTR_MESSAGE";

    	//String req = "SELECT * FROM INSTRUMENT ";
    	//
    	//String req = "UPDATE L3TEST SET STATUS=0 WHERE (SAMPLE_REF=2608)";
    	//String req = "UPDATE L3SAMPLE SET LAST_NAME='XXXX' WHERE (REF=2608)";
    	
    	String req = "INSERT INTO L3SAMPLE (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) VALUES (9416,'KABBABE RIZKALAH','','','','','',-1,0,0,TO_DATE ('30-Apr-08' , 'DD-MON-YY'),0)";
    	System.out.println("Request : " + req);
	    stmt.executeUpdate(req);
	    

	    /*
    	ResultSetMetaData meta = resultSet.getMetaData();
    	
    	for (int i=1; i<meta.getColumnCount()+1; i++){
    		System.out.print(", "+meta.getColumnName(i));
    	}
    	System.out.println();
    	
	    while(resultSet.next()){
	    	
	    	for (int i=1; i<meta.getColumnCount()+1; i++){
	    		System.out.print(", "+resultSet.getString(i));
	    	}
	    	System.out.println();
	    	
	    }
	    */
	    
    }catch(Exception e){
    	e.printStackTrace();
    }
    Globals.getDBManager().unlockStatement(stmt);
  }
  
  private void sql_DeleteLISTables(){
    Statement stmt = Globals.getDBManager().lockStatement();
    try{
    	String req = null;
    	
      req = "DELETE FROM LISTEST";
      Globals.logString(req);
      stmt.executeUpdate(req);
      req = "DELETE FROM LISSAMPLE";
      Globals.logString(req);
      stmt.executeUpdate(req);
      req = "DELETE FROM LIS_INSTR_MESSAGE";
      Globals.logString(req);
      stmt.executeUpdate(req);

      req = "DELETE FROM L3SAMPLE";
      Globals.logString(req);
      stmt.executeUpdate(req);
      req = "DELETE FROM L3TEST";
      Globals.logString(req);
      stmt.executeUpdate(req);
      req = "DELETE FROM L3MESSAGE";
      Globals.logString(req);
      stmt.executeUpdate(req);

    }catch(Exception e){
      Globals.logException(e);
      fail();
    }
    Globals.getDBManager().unlockStatement(stmt);
  }

  private void sql_InsertTest(int sampleID, String analyzerCode, String test) throws Exception{
    Statement stmt = Globals.getDBManager().lockStatement();
  	StringBuffer request = new StringBuffer("INSERT INTO LISTEST ");
    request.append("(SEQ_ID, SAMPLE_ID, TEST_CODE, STATUS, ANALYZER_CODE, RESULT, UNIT, MESSAGE) ");
    request.append("VALUES ");
    request.append("("+(testTableSequence++)+","+sampleID+",'"+test+"',0,'"+analyzerCode+"',0,'','')");
    String req = request.toString();
    Globals.logString(req);
    stmt.executeUpdate(req);
    Globals.getDBManager().unlockStatement(stmt);
  }
  
  private void sql_InsertSample(int sampleID, String sampleType, String firstName, String lastName, String midInit, int age, String sex) throws Exception{
    Statement stmt = Globals.getDBManager().lockStatement();
  	String currDateSQL = "'"+FDate.convertDateToSQLString(new Date(System.currentTimeMillis()))+"'";
  	if(Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE){
  		currDateSQL = "TO_DATE('"+FDate.convertDateToSQLString(new Date(System.currentTimeMillis()))+"','DD-mon-YY')";
  	}
  	
    StringBuffer request = new StringBuffer("INSERT INTO LISSAMPLE ");
    request.append("(CURRENT_DATE_TIME, SAMPLE_ID, SAMPLE_TYPE, COLLECTION_DATE, FIRST_NAME, LAST_NAME, MIDDLE_INITIAL, AGE, SEX) ");
    request.append("VALUES ");
    request.append("("+currDateSQL+","+sampleID+",'"+sampleType+"',"+currDateSQL+",'"+firstName+"','"+lastName+"','"+midInit+"',"+age+",'"+sex+"')");
    String req = request.toString();
    Globals.logString(req);
    stmt.executeUpdate(req);
    Globals.getDBManager().unlockStatement(stmt);
  }
  
  public void test_COMPLETE_InsertSampleThenOneTestForThatSample1(){
  	try{
      Globals.getDBManager().beginTransaction();
      int sampleID = 1000;
	  	sql_InsertSample(sampleID, "Serum", "TST_Double", "TST_Double", "K", 52, "M");
	    sql_InsertTest(sampleID, "", "26");
	    sql_InsertTest(sampleID, "", "27");
	    sql_InsertTest(sampleID, "", "28");
	    Globals.getDBManager().commitTransaction();

	    Thread.sleep(40000);
	    
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  }
  public void test_COMPLETE_InsertSampleThenOneTestForThatSample2(){
  	try{
	    
      Globals.getDBManager().beginTransaction();
      int sampleID = 1000;      
	    sql_InsertTest(sampleID, "", "40");
	    Globals.getDBManager().commitTransaction();
	    
	    Thread.sleep(20000);
  	}catch(Exception e){
  		Globals.logException(e);
  	}
  }
  
}
