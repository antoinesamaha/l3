package b01.hsg.unit;

import java.sql.Date;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties; //import gov.lanl.Utility.DateTime;

import b01.foc.Globals;
import b01.foc.calendar.FCalendar;
import b01.foc.db.DBManager;
import b01.foc.fUnit.FocTestSuite;
import b01.foc.property.FDate;
import b01.foc.property.FDateTime;
import b01.foc.util.ASCII;
import b01.hsg.unit.HSGUnitGlobals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.PoolKernel;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.coulter.CoulterDriver;
import b01.l3.drivers.helena.junior24.Junior24Driver;
import b01.l3.drivers.helena.junior24.Junior24Frame;
import b01.l3.drivers.abbott.modular.ModularDriver;
import b01.l3.unit.L3TestCase;

public class HSGDevDriver_Architect extends L3TestCase implements HSGUnitGlobals {

	protected DriverSerialPort driver = null;

	public HSGDevDriver_Architect(FocTestSuite testSuite, String functionName) {
		super(testSuite, functionName, CONNECTOR_ANALYSERS, CONNECTOR_EMULATORS);
	}

	public DriverSerialPort getDriver() {
		return driver;
	}

	private void insertTest(int ref, String testID, String testDescription) throws Exception {
		Statement stmt = Globals.getDBManager().lockStatement();
		StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
		request2.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM,PRIORITY,VERIFICATION_PENDING) ");
		request2.append("VALUES ");
		request2.append("("+ref+",'"+testID+"','"+testDescription+"',0.0,0,'','',0,0,0,7,0,21131,0,'S',0)");
		String req = request2.toString();
		Globals.logString(req);
		stmt.executeUpdate(req);
		Globals.getDBManager().unlockStatement(stmt);
	}
	
	public void test_DRIVER_Architect() {
		try {
			boolean executeSend = true;
			
			/*
			Globals.getDBManager().beginTransaction();
			Statement stmt = Globals.getDBManager().lockStatement();
			String currDateSQL = "2011-12-07";

			Globals.logString("CollectionDate: " + currDateSQL);

			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");
			request.append("(21131,'999890','PID123125','Saab','Edmond','G','M',1,0,0,'" + currDateSQL + "',70)");
			String req = request.toString();
			Globals.logString(req);
			if(executeSend) stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			insertTest(211309, "1", "HGB");
			insertTest(211310, "2", "HCT");
			insertTest(211311, "3", "RBC");
			insertTest(211312, "13", "LY#");
			
			Globals.getDBManager().commitTransaction();
*/
			Instrument instr = PoolKernel.getInstrumentForAnyPool("ARCHITECT");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread.sleep(240000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}
}
