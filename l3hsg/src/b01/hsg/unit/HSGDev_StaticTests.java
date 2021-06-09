package b01.hsg.unit;

import java.sql.Date;
import java.sql.Statement;

import b01.foc.Globals;
import b01.foc.db.DBManager;
import b01.foc.property.FDate;

public class HSGDev_StaticTests {
	private static int testTableSequence = 1;

	private void sql_InsertTest(int sampleID, String analyzerCode, String test)
			throws Exception {
		Statement stmt = Globals.getDBManager().lockStatement();
		StringBuffer request = new StringBuffer("INSERT INTO LISTEST ");
		request.append("(SEQ_ID, SAMPLE_ID, TEST_CODE, STATUS, ANALYZER_CODE, RESULT, UNIT, MESSAGE) ");
		request.append("VALUES ");
		request.append("(" + (testTableSequence++) + "," + sampleID + ",'"
				+ test + "',0,'" + analyzerCode + "',0,'','')");
		String req = request.toString();
		Globals.logString(req);
		stmt.executeUpdate(req);
		Globals.getDBManager().unlockStatement(stmt);
	}

	private void sql_InsertSample(int sampleID, String sampleType,
			String firstName, String lastName, String midInit, int age,
			String sex) throws Exception {
		Statement stmt = Globals.getDBManager().lockStatement();
		String currDateSQL = "'"
				+ FDate.convertDateToSQLString(new Date(System
						.currentTimeMillis())) + "'";
		if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE) {
			currDateSQL = "TO_DATE('"
					+ FDate.convertDateToSQLString(new Date(System
							.currentTimeMillis())) + "','DD-mon-YY')";
		}

		StringBuffer request = new StringBuffer("INSERT INTO LISSAMPLE ");
		request.append("(CURRENT_DATE_TIME, SAMPLE_ID, SAMPLE_TYPE, COLLECTION_DATE, FIRST_NAME, LAST_NAME, MIDDLE_INITIAL, AGE, SEX) ");
		request.append("VALUES ");
		request.append("(" + currDateSQL + "," + sampleID + ",'" + sampleType
				+ "'," + currDateSQL + ",'" + firstName + "','" + lastName
				+ "','" + midInit + "'," + age + ",'" + sex + "')");
		String req = request.toString();
		Globals.logString(req);
		stmt.executeUpdate(req);
		Globals.getDBManager().unlockStatement(stmt);
	}

	public void test_InsertSampleThenOneTestForThatSample1() {
		try {
			Globals.getDBManager().beginTransaction();
			int sampleID = 999;
			sql_InsertSample(sampleID, "Serum", "TST_Double", "TST_Double",
					"K", 52, "M");
			sql_InsertTest(sampleID, "", "400");
			sql_InsertTest(sampleID, "", "427");
			Globals.getDBManager().commitTransaction();

			Thread.sleep(40000);

		} catch (Exception e) {
			Globals.logException(e);
		}
	}
}
