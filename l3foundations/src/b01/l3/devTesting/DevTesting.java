package b01.l3.devTesting;

import java.sql.Statement;
import java.util.Properties;

import b01.foc.Globals;
import b01.foc.desc.FocConstructor;
import b01.l3.DriverFactory;
import b01.l3.DriverSerialPort;
import b01.l3.IDriver;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.L3ConfigInfo;
import b01.l3.exceptions.L3DataIsEmptyException;
import b01.l3.exceptions.L3Exception;

public class DevTesting {
	
	private void sql_InsertSample() throws Exception {
		int sampleRef = 1;
		int test1Ref  = 1;
		int test2Ref  = 2;
		
		
		/*For Instrument 123 testing
		int instrumentREF = 123;
		*/
		
		int instrumentREF = 183;//BCS XP
		
		{
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer();
			request.append("DELETE FROM L3TEST WHERE SAMPLE_REF ="+sampleRef);
			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);
		}
		
		{
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer();
			request.append("DELETE FROM L3SAMPLE WHERE REF ="+sampleRef);
			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);
		}
		

		
		{
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer();
			/*For Instrument 123 testing
			request.append("INSERT INTO L3SAMPLE (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE,DATE_OF_BIRTH) VALUES ");
			request.append(                "(" + sampleRef+",'998','111','TST_BARMAJA','TST_L3','F','F',-1,0,0,TO_DATE ('2013-10-04 18:47:00' , 'YYYY-MM-DD HH24:MI:SS'),69,TO_DATE ('1943-12-28 00:00:00' , 'YYYY-MM-DD HH24:MI:SS'))");
			*/
			request.append("INSERT INTO L3SAMPLE (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE,DATE_OF_BIRTH) VALUES ");
			request.append(                "(" + sampleRef+",'998','111','TEST_LAST','TEST_FIRST','F','F',-1,0,0,TO_DATE ('2013-10-04 18:47:00' , 'YYYY-MM-DD HH24:MI:SS'),69,TO_DATE ('1943-12-28 00:00:00' , 'YYYY-MM-DD HH24:MI:SS'))");
			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);
		}

		{
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer();
			/*For Instrument 123 testing
			request.append("INSERT INTO L3TEST (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM,PRIORITY,VERIFICATION_PENDING) VALUES ");
			request.append(                "(" + test1Ref+",'X900','pH',0.0,0,'','',0,0,0,"+instrumentREF+",0,"+sampleRef+",0,'R',0)");
			*/
			request.append("INSERT INTO L3TEST (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM,PRIORITY,VERIFICATION_PENDING) VALUES ");
			request.append(                "(" + test1Ref+",'702','PT1.sec.TS',0.0,0,'','',0,0,0,"+instrumentREF+",0,"+sampleRef+",0,'R',0)");
			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);
		}
		
//		{
//			Statement stmt = Globals.getDBManager().lockStatement();
//			StringBuffer request = new StringBuffer();
//			request.append("INSERT INTO L3TEST (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM,PRIORITY,VERIFICATION_PENDING) VALUES ");
//			request.append(                "(" + test2Ref+",'400','MALB',0.0,0,'','',0,0,0,83,0,"+sampleRef+",0,'R',0)");
//			String req = request.toString();
//			Globals.logString(req);
//			stmt.executeUpdate(req);
//			Globals.getDBManager().unlockStatement(stmt);
//		}		
	}

//	INSERT INTO L3SAMPLE (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE,DATE_OF_BIRTH) VALUES (889613,'1363787','126069','Sabra','Hayfa','F','F',-1,0,0,TO_DATE ('2013-07-09 18:47:00' , 'YYYY-MM-DD HH24:MI:SS'),69,TO_DATE ('1943-12-28 00:00:00' , 'YYYY-MM-DD HH24:MI:SS'))
//	SELECT L3TEST_SEQUENCE.nextval from dual
//	INSERT INTO L3TEST (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM,PRIORITY,VERIFICATION_PENDING) VALUES (10669966,'19','MON%',0.0,0,'','',0,0,0,7,0,889613,0,'R',0)
//	SELECT L3TEST_SEQUENCE.nextval from dual
//	INSERT INTO L3TEST (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM,PRIORITY,VERIFICATION_PENDING) VALUES (10669967,'4','WBC',0.0,0,'','',0,0,0,7,0,889613,0,'R',0)

	public void test_InsertSampleThenOneTestForThatSample1() {
		try {
			sql_InsertSample();
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
	
	public void test_DriverU601() {
		try {
			
			//sql_InsertSample();
			
			Properties props = new Properties();
			props.setProperty("instrument.code"  ,"U601");
			props.setProperty("instrument.name"  ,"U601");
			props.setProperty("instrument.driver","b01.l3.drivers.roches.cobas.u601.CobasU601Driver");

			Instrument instr = new Instrument(props);	
			instr.setPropertyString(InstrumentDesc.FLD_SERIAL_PORT_NAME, "6500");
			
			IDriver driver = instr.getDriver();
			
			driver.connect();
			
			while(true){
				Thread.sleep(1000);
			}

			/*
			InstrumentDesc instDesc = new InstrumentDesc();
			FocConstructor constr   = new FocConstructor(instDesc, null);
			Instrument     inst     = new Instrument(constr);
			
			Class driverClass = DriverFactory.getInstance().getDriver("b01.l3.drivers.roches.cobas.u601.CobasU601Driver");
			IDriver driver = (IDriver) driverClass.newInstance();
			if (driver != null) {
				Properties props = new Properties();
				props.put("serialPort.name", "8899");
				driver.init(null, props);
			}
			*/
			
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
}
