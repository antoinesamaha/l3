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

public class HSGDevDriver extends L3TestCase implements HSGUnitGlobals {

	protected DriverSerialPort driver = null;

	public HSGDevDriver(FocTestSuite testSuite, String functionName) {
		super(testSuite, functionName, CONNECTOR_ANALYSERS, CONNECTOR_EMULATORS);
	}

	public DriverSerialPort getDriver() {
		return driver;
	}

	public void test_DRIVER_CoulterSmallTest() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("COULTER-A");
			driver = new CoulterDriver();
			driver.setUnitTesting(true);
			driver.init(instr, new Properties());
			driver.connect();
			L3Message message = new L3Message();
			L3Sample sample = new L3Sample("49362");
			sample.setFirstName("Tony");
			sample.setLastName("Samaha");
			sample.setMiddleInitial("N");
			sample.setAge(34);
			sample.setSexe("M");
			L3Test test = sample.addTest();
			test.setLabel("EO");
			message.addSample(sample);

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);

					buffer = new StringBuffer("" + ASCII.DLE);
					buffer.append('A');
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);

				}
			});
			thread.start();

			driver.send(message);

			thread.sleep(30000);
			Globals.logString("After the 30000");
		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Coulter() {
		try {
			Globals.getDBManager().beginTransaction();

			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES (19223,'50574','','Haddad','Zahia','H','F',-1,0,0,TO_DATE
			// ('17-Jun-08' , 'DD-MON-YY'),52)
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request
					.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");

			// SimpleDateFormat dateFormat = new DateTime(2008, 6, 17, 11, 05,
			// 10);
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			java.util.Date date = dateFormat.parse("2008-06-17 11:05");

			request.append("(19223,'50574','','Haddad','Zahia','H','F',-1,0,0,"
					+ "\"" + dateFormat.format(date) + "\"" + ",52)");
			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF)
			// VALUES (191649,'8','RDW',0.0,0,'','',0,0,0,7,0,19223)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2.append("(191649,'8','RDW',0.0,0,'','',0,0,0,7,0,19223)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			Globals.getDBManager().commitTransaction();

			Instrument instr = PoolKernel.getInstrumentForAnyPool("COULTER-B");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// driver send ENQ
					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					// send 01
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					// send msg
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					// send ENQ
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);

					buffer = new StringBuffer("" + ASCII.DLE);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);

					buffer.append('A');
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
				}
			});

			thread.start();
			Thread.sleep(500000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_BCT1() {
		try {
			Thread.sleep(500);
			Globals.getDBManager().beginTransaction();

			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES (19223,'50574','','Haddad','Zahia','H','F',-1,0,0,TO_DATE
			// ('17-Jun-08' , 'DD-MON-YY'),52)
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request
					.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");

			// SimpleDateFormat dateFormat = new DateTime(2008, 6, 17, 11, 05,
			// 10);
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date = dateFormat.parse("2008-06-16 11:05:10");

			if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE) {
				request
						.append("(19087,'50308','','Murr','Leila','F','F',1,0,0,"
								+ "TO_DATE ('"
								+ dateFormat.format(date)
								+ "' , 'YYYY-MM-DD HH:MI:SS'), 54)");
			} else {
				request
						.append("(19087,'50308','','Murr','Leila','F','F',1,0,0,"
								+ "\""
								+ dateFormat.format(date)
								+ "\""
								+ ",54)");
			}

			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF)
			// VALUES (191649,'8','RDW',0.0,0,'','',0,0,0,7,0,19223)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2
					.append("(190518,'707','aPTT.PSL',0.0,0,'','',0,0,0,20,0,19087)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			Globals.getDBManager().commitTransaction();

			Instrument instr = PoolKernel.getInstrumentForAnyPool("BCT");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
				}
			});

			thread.start();
			Thread.sleep(500000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_BCT() {
		try {
			Thread.sleep(500);
			Globals.getDBManager().beginTransaction();

			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES
			// (97216,'147239','','Moltani','Surjan','N','M',1,0,0,TO_DATE
			// ('2009-01-20 07:00:00' , 'YYYY-MM-DD HH24:MI:SS'),50)
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request
					.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");

			// SimpleDateFormat dateFormat = new DateTime(2008, 6, 17, 11, 05,
			// 10);
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date = dateFormat.parse("2009-01-20 07:00:00");

			if (Globals.getDBManager().getProvider() == DBManager.PROVIDER_ORACLE) {
				request
						.append("(97216,'147239','','Moltani','Surjan','N','M',1,0,0,"
								+ "TO_DATE ('"
								+ dateFormat.format(date)
								+ "' , 'YYYY-MM-DD HH24:MI:SS'), 50)");
			} else {
				request
						.append("(97216,'147239','','Moltani','Surjan','N','M',1,0,0,"
								+ "\""
								+ dateFormat.format(date)
								+ "\""
								+ ",50)");
			}
			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM)
			// VALUES (1110622,'701','PT1.s.TS',0.0,0,'','',0,0,0,20,0,97216,0)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM) ");
			request2.append("VALUES ");
			request2
					.append("(1110622,'701','PT1.s.TS',0.0,0,'','',0,0,0,20,0,97216,0)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM)
			// VALUES (1110623,'703','PT1.INRC',0.0,0,'','',0,0,0,20,0,97216,0)
			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM) ");
			request2.append("VALUES ");
			request2
					.append("(1110623,'703','PT1.INRC',0.0,0,'','',0,0,0,20,0,97216,0)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM)
			// VALUES (1110624,'702','PT1.%.TS',0.0,0,'','',0,0,0,20,0,97216,0)
			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM) ");
			request2.append("VALUES ");
			request2
					.append("(1110624,'702','PT1.%.TS',0.0,0,'','',0,0,0,20,0,97216,0)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			Globals.getDBManager().commitTransaction();
			Instrument instr = PoolKernel.getInstrumentForAnyPool("BCT");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread1 = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
				}
			});

			thread1.start();
			Thread.sleep(10000);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM)
			// VALUES (1110625,'707','aPTT.PSL',0.0,0,'','',0,0,0,20,0,97216,0)
			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM) ");
			request2.append("VALUES ");
			request2
					.append("(1110625,'707','aPTT.PSL',0.0,0,'','',0,0,0,20,0,97216,0)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);
			Globals.getDBManager().commitTransaction();

			Thread.sleep(500000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_HelenaJunior24() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("JUNIOR24");
			driver = new Junior24Driver();
			driver.setUnitTesting(true);
			driver.init(instr, new Properties());
			driver.connect();

			/*
			 * Le 10 Avril 2008 NOM : JAMILEH BARACAT NO DOSSIER: 73 COURBE: 0 0
			 * 0 1 5 9 14 18 22 27 32 40 47 54 60 65 69 75 80 87 92 97 105 115
			 * 129 146 161 176 194 218 254 306 372 453 566 737 1004 1383 1853
			 * 2365 2854 3282 3624 3873 4029 4094 4068 3955 3736 3398 2950 2445
			 * 1956 1529 1175 891 672 510 395 317 263 227 206 194 192 -1 200 222
			 * 255 294 328 354 365 364 351 331 308 287 271 258 249 243 240 -1
			 * 240 244 257 280 314 359 410 462 515 563 605 638 667 695 721 747
			 * 770 788 799 801 792 769 732 687 638 589 544 502 465 432 401 375
			 * 355 341 332 328 -1 331 337 350 368 388 412 444 484 535 593 653
			 * 705 744 767 778 778 769 755 738 721 707 692 673 653 631 607 575
			 * 534 489 443 404 373 350 332 317 305 294 283 275 268 266 263 263
			 * 262 -1 266 267 271 275 281 287 294 301 310 321 331 345 358 372
			 * 383 395 404 414 423 430 433 433 430 427 423 415 407 398 389 382
			 * 375 370 364 356 350 345 341 338 338 338 341 341 342 341 337 327
			 * 308 283 254 225 195 169 146 125 109 93 80 69 59 51 43 38 34 32 28
			 * 26 24 23 23 24 24 23 22 20 18 18 18 20 20 19 18 17 17 17 18 18 0
			 * -1 0 FRACTION % G/L NORMALES % NORMALES G/L ALPHA 1 3.9 2.6 1.5 -
			 * 4.5 1.0 - 4.0 ALPHA 2 15.0+ 10.2 6.0 - 12.0 5.0 - 11.0
			 */
			String lfcr = "" + ASCII.LF + ASCII.CR;
			String lfcreot = lfcr + ASCII.EOT;
			StringBuffer buffer = new StringBuffer(
					"Le 10 Avril 2008           " + lfcr);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);
			buffer = new StringBuffer(
					"NOM       : JAMILEH BARACAT                       " + lfcr);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);
			buffer = new StringBuffer(
					"NO DOSSIER: 73                          " + lfcr);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);
			buffer = new StringBuffer(
					"COURBE: 0 0 0 1 5 9 14 18 22 27 32 40 47 54 60 65 69 75 80 87 92 97 105 115 129 146 161 176 194 218 254 306 372 453 566 737 1004 1383 1853 2365 2854 3282 3624 3873 4029 4094 4068 3955 3736 3398 2950 2445 1956 1529 1175 891 672 510 395 317 263 227 206 194 192 -1 200 222 255 294 328 354 365 364 351 331 308 287 271 258 249 243 240 -1 240 244 257 280 314 359 410 462 515 563 605 638 667 695 721 747 770 788 799 801 792 769 732 687 638 589 544 502 465 432 401 375 355 341 332 328 -1 331 337 350 368 388 412 444 484 535 593 653 705 744 767 778 778 769 755 738 721 707 692 673 653 631 607 575 534 489 443 404 373 350 332 317 305 294 283 275 268 266 263 263 262 -1 266 267 271 275 281 287 294 301 310 321 331 345 358 372 383 395 404 414 423 430 433 433 430 427 423 415 407 398 389 382 375 370 364 356 350 345 341 338 338 338 341 341 342 341 337 327 308 283 254 225 195 169 146 125 109 93 80 69 59 51 43 38 34 32 28 26 24 23 23 24 24 23 22 20 18 18 18 20 20 19 18 17 17 17 18 18 0 -1 0"
							+ lfcr);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);
			buffer = new StringBuffer(
					"FRACTION          %         G/L                  NORMALES %   NORMALES G/L"
							+ lfcr);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);
			buffer = new StringBuffer(
					"ALPHA 1          3.9        2.6                 1.5  -   4.5   1.0  -   4.0"
							+ lfcr);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);
			buffer = new StringBuffer(
					"ALPHA 2         15.0+      10.2                 6.0  -  12.0   5.0  -  11.0"
							+ lfcr);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);
			buffer = new StringBuffer(lfcreot);
			getDriver().getL3SerialPort().extractAnswerFromBuffer(buffer);

			Thread.sleep(3000);
		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Modular2() {
		try {
			Globals.getDBManager().beginTransaction();

			// sql_InsertSample()
			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES (21131,'53535','','Saab','Edmond','G','M',1,0,0,TO_DATE
			// ('24-Jun-08' , 'DD-MON-YY'),70)
			Statement stmt = Globals.getDBManager().lockStatement();
			String currDateSQL = "2008-07-02";
			// String currDateSQL = FDate.convertDateToSQLString(new
			// Date(System.currentTimeMillis()));

			Globals.logString("CollectionDate: " + currDateSQL);

			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request
					.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");
			request.append("(21131,'53535','','Saab','Edmond','G','M',1,0,0,'"
					+ currDateSQL + "',70)");
			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// sql_InsertTest(sampleID, "", "409");
			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF)
			// VALUES (211309,'408','Cl',0.0,0,'','',0,0,0,12,0,21131)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2.append("(211309,'408','Cl',0.0,0,'','',0,0,0,12,0,21131)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2.append("(211307,'430','K',0.0,0,'','',0,0,0,12,0,21131)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2
					.append("(211308,'412','CREA',0.0,0,'','',0,0,0,12,0,21131)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2
					.append("(211310,'440','UREA',0.0,0,'','',0,0,0,12,0,21131)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2.append("(211311,'434','Na',0.0,0,'','',0,0,0,12,0,21131)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2
					.append("(211312,'414','GLU',0.0,0,'','',0,0,0,12,0,21131)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2
					.append("(211313,'407','CO2-L',0.0,0,'','',0,0,0,12,0,21131)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			Globals.getDBManager().commitTransaction();

			Instrument instr = PoolKernel.getInstrumentForAnyPool("MODULAR");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [STX]1H|\^&|||H7600^1|||||host|RSUPL^REAL|P|1[CR]P|1|||||||M||||||70^Y[CR]O|1|[.][.][.][.][.][.][.][.]53535|0^5005^2^^S1^SC|^^^989^\^^^990^\^^^991^\^^^419^\^^^767^\^^^452^\^^^690^|R||20080623080846||||N||||1|||||||20080624091808|||F[CR]C|1|I|Edmond[.]Saab[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][ETB]C3[CR][LF]
					// [STX]2[.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^|G[CR]R|1|^^^989/|132|mmol/l||L||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|2|^^^990/|3.5|mmol/l||N||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|3|^^^991/|79|mmol/l||L||F||ROSY[.][.]|||ISE1[CR]C|1|I|45|I[CR]R|4|^^^419[ETB]BB[CR][LF]
					// [STX]3/|108|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|5|^^^767/|140|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|6|^^^452/|40.8|mmol/l||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|7|^^^690/|1.46|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]L|1|N[CR][ETX]B8[CR][LF]

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "1H|\\^&|||H7600^1|||||host|RSUPL^REAL|P|1"
									+ ASCII.CR
									+ "P|1|||||||M||||||70^Y"
									+ ASCII.CR
									+ "O|1|        53535|0^5005^2^^S1^SC|^^^989^\\^^^990^\\^^^991^\\^^^419^\\^^^767^\\^^^452^\\^^^690^|R||20080623080846||||N||||1|||||||20080624091808|||F"
									+ ASCII.CR
									+ "C|1|I|Edmond Saab                  "
									+ ASCII.ETB + "C3" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "2 ^                         ^                    ^               ^|G"
									+ ASCII.CR
									+ "R|1|^^^989/|132|mmol/l||L||F||ROSY  |||ISE1"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|2|^^^990/|3.5|mmol/l||N||F||ROSY  |||ISE1"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|3|^^^991/|79|mmol/l||L||F||ROSY  |||ISE1"
									+ ASCII.CR + "C|1|I|45|I" + ASCII.CR
									+ "R|4|^^^419" + ASCII.ETB + "BB"
									+ ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "3/|108|mg/dl||H||F||ROSY  |||P1" + ASCII.CR
							+ "C|1|I|0|I" + ASCII.CR
							+ "R|5|^^^767/|140|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|6|^^^452/|40.8|mmol/l||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|7|^^^690/|1.46|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR + "L|1|N"
							+ ASCII.CR + ASCII.ETX + "B8" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(100000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Modular3() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("MODULAR");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);

					// [STX]1H|\^&|||H7600^1|||||host|RSUPL^REAL|P|1[CR]P|1|||||||M||||||72^Y[CR]O|1|[.][.][.][.][.][.][.][.]52964|0^5006^2^^S1^SC|^^^989^\^^^990^\^^^991^\^^^419^\^^^767^\^^^452^\^^^690^\^^^19^|R||20080623000000||||N||||1|||||||20080623094142|||F[CR]C|1|I|Jean-Jacques[.]Hamati[.][.][.][ETB]05[CR][LF]
					// [STX]2[.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^LEGERE[.]HEMOLYSE[.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^|G[CR]R|1|^^^989/|134|mmol/l||N||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|2|^^^990/|5.3|mmol/l||N||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|3|^^^991/|101|mmol/l||N||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|4[ETB]B0[CR][LF]
					// [STX]3|^^^419/|37|mg/dl||N||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|5|^^^767/|104|mg/dl||N||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|6|^^^452/|24.3|mmol/l||N||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|7|^^^690/|0.73|mg/dl||N||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|8|^^^19/|3.17|mg/dl||H||F||ROSY[.][.]||[ETB]D9[CR][LF]
					// [STX]4|P1[CR]C|1|I|0|I[CR]L|1|N[CR][ETX]44[CR][LF]

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "1H|\\^&|||H7600^1|||||host|RSUPL^REAL|P|1"
									+ ASCII.CR
									+ "P|1|||||||M||||||72^Y"
									+ ASCII.CR
									+ "O|1|        52964|0^5006^2^^S1^SC|^^^989^\\^^^990^\\^^^991^\\^^^419^\\^^^767^\\^^^452^\\^^^690^\\^^^19^|R||20080623000000||||N||||1|||||||20080623094142|||F"
									+ ASCII.CR + "C|1|I|Jean-Jacques Hamati   "
									+ ASCII.ETB + "05" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "2        ^                         ^LEGERE HEMOLYSE     ^               ^|G"
									+ ASCII.CR
									+ "R|1|^^^989/|134|mmol/l||N||F||ROSY  |||ISE1"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|2|^^^990/|5.3|mmol/l||N||F||ROSY  |||ISE1"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|3|^^^991/|101|mmol/l||N||F||ROSY  |||ISE1"
									+ ASCII.CR + "C|1|I|0|I" + ASCII.CR + "R|4"
									+ ASCII.ETB + "B0" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "3|^^^419/|37|mg/dl||N||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|5|^^^767/|104|mg/dl||N||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|6|^^^452/|24.3|mmol/l||N||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|7|^^^690/|0.73|mg/dl||N||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|8|^^^19/|3.17|mg/dl||H||F||ROSY  ||"
							+ ASCII.ETB + "D9" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "4|P1" + ASCII.CR
							+ "C|1|I|0|I" + ASCII.CR + "L|1|N" + ASCII.CR
							+ ASCII.ETX + "44" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Modular4() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("MODULAR");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [STX]1H|\^&|||H7600^1|||||host|RSUPL^REAL|P|1[CR]P|1|||||||F||||||77^Y[CR]O|1|[.][.][.][.][.][.][.][.]61881|0^5010^2^^S1^SC|^^^989^\^^^990^\^^^991^\^^^675^\^^^419^\^^^767^\^^^706^\^^^18^\^^^679^\^^^770^\^^^452^\^^^413^\^^^158^\^^^687^\^^^685^\^^^690^\^^^294^\^^^479^\^[ETB]F7[CR][LF]
					// [STX]2^^19^\^^^965^|R||20080714000000||||N||||1|||||||20080714094209|||F[CR]C|1|I|Laurence[.]Andraos[.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^LEGERE[.]HEMOLYSE[.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^|G[CR]R|1|^^^989/|149|mmol/l||H||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|2|^^^990/|5.8|[ETB]5A[CR][LF]
					// [STX]3mmol/l||H||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|3|^^^991/|101|mmol/l||N||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|4|^^^675/|9.3|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|5|^^^419/|450|mg/dl||HH||F||ROSY[.][.]|||P1[CR]C|1|I|26|I[CR]R|6|^^^767/|105|mg/dl||N||F||ROSY[.][.]|||P1[CR]C|1[ETB]C6[CR][LF]
					// [STX]4|I|0|I[CR]R|7|^^^706/|10.4|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|8|^^^18/|1.29|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|9|^^^679/|5.1|g/dl||L||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|10|^^^770/|2.8|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|44|I[CR]R|11|^^^452/|26.6|mmol/l||N[ETB]AB[CR][LF]
					// [STX]5||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|12|^^^413/|2.8|g/dl||L||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|13|^^^158/|127|U/l||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|14|^^^687/|43|U/l||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|15|^^^685/|52|U/l||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|16|^^^690/|4[ETB]E3[CR][LF]
					// [STX]6.61|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|17|^^^294/|-0.12|mg/dl||LL||F||ROSY[.][.]|||P1[CR]C|1|I|27|I[CR]R|18|^^^479/|154|U/l||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|19|^^^19/|2.93|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|20|^^^965/|1.2|||N||F||ROSY[.][.]|||[CR]C|1|I|0|[ETB]96[CR][LF]
					// [STX]7I[CR]L|1|N[CR][ETX]60[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "1H|\\^&|||H7600^1|||||host|RSUPL^REAL|P|1"
									+ ASCII.CR
									+ "P|1|||||||F||||||77^Y"
									+ ASCII.CR
									+ "O|1|        61881|0^5010^2^^S1^SC|^^^989^\\^^^990^\\^^^991^\\^^^675^\\^^^419^\\^^^767^\\^^^706^\\^^^18^\\^^^679^\\^^^770^\\^^^452^\\^^^413^\\^^^158^\\^^^687^\\^^^685^\\^^^690^\\^^^294^\\^^^479^\\^"
									+ ASCII.ETB + "F7" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "2^^19^\\^^^965^|R||20080714000000||||N||||1|||||||20080714094209|||F"
									+ ASCII.CR
									+ "C|1|I|Laurence Andraos              ^                         ^LEGERE HEMOLYSE     ^               ^|G"
									+ ASCII.CR
									+ "R|1|^^^989/|149|mmol/l||H||F||ROSY  |||ISE1"
									+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
									+ "R|2|^^^990/|5.8|" + ASCII.ETB + "5A"
									+ ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "3mmol/l||H||F||ROSY  |||ISE1" + ASCII.CR
							+ "C|1|I|0|I" + ASCII.CR
							+ "R|3|^^^991/|101|mmol/l||N||F||ROSY  |||ISE1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|4|^^^675/|9.3|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|5|^^^419/|450|mg/dl||HH||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|26|I" + ASCII.CR
							+ "R|6|^^^767/|105|mg/dl||N||F||ROSY  |||P1"
							+ ASCII.CR + "C|1" + ASCII.ETB + "C6" + ASCII.CR
							+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "4|I|0|I" + ASCII.CR
							+ "R|7|^^^706/|10.4|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|8|^^^18/|1.29|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|9|^^^679/|5.1|g/dl||L||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|10|^^^770/|2.8|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|44|I" + ASCII.CR
							+ "R|11|^^^452/|26.6|mmol/l||N" + ASCII.ETB + "AB"
							+ ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "5||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|12|^^^413/|2.8|g/dl||L||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|13|^^^158/|127|U/l||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|14|^^^687/|43|U/l||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|15|^^^685/|52|U/l||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|16|^^^690/|4" + ASCII.ETB + "E3" + ASCII.CR
							+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "6.61|mg/dl||H||F||ROSY  |||P1" + ASCII.CR
							+ "C|1|I|0|I" + ASCII.CR
							+ "R|17|^^^294/|-0.12|mg/dl||LL||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|27|I" + ASCII.CR
							+ "R|18|^^^479/|154|U/l||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|19|^^^19/|2.93|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|20|^^^965/|1.2|||N||F||ROSY  |||" + ASCII.CR
							+ "C|1|I|0|" + ASCII.ETB + "96" + ASCII.CR
							+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "7I" + ASCII.CR
							+ "L|1|N" + ASCII.CR + ASCII.ETX + "60" + ASCII.CR
							+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Modular5() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("MODULAR");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);

					// [STX]1H|\^&|||H7600^1|||||host|RSUPL^REAL|P|1[CR]P|1|||||||F||||||29^Y[CR]O|1|[.][.][.][.][.][.][.][.]64855|0^5051^1^^S1^SC|^^^413^\^^^13^1\^^^142^1\^^^146^1|R||20080721091500||||N||||1|||||||20080730093955|||F[CR]C|1|I|Nathalie[.]Tannous[.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][ETB]36[CR][LF]
					// [STX]2[.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^|G[CR]R|1|^^^413/|4.8|g/dl||N||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|2|^^^13/1/not|1.25|ng/ml||H||F||ROSY[.][.]|||E11[CR]C|1|I|0|I[CR]R|3|^^^142/1/not|871.0|ug/dl||N||F||ROSY[.][.]|||E11[CR]C|1|I|0|I[CR]R|4|^^^146/1/not|33.73|nmol/[ETB]C9[CR][LF]
					// [STX]3l||N||F||ROSY[.][.]|||E11[CR]C|1|I|0|I[CR]L|1|N[CR][ETX]D6[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "1H|\\^&|||H7600^1|||||host|RSUPL^REAL|P|1"
									+ ASCII.CR
									+ "P|1|||||||F||||||29^Y"
									+ ASCII.CR
									+ "O|1|        64855|0^5051^1^^S1^SC|^^^413^\\^^^13^1\\^^^142^1\\^^^146^1|R||20080721091500||||N||||1|||||||20080730093955|||F"
									+ ASCII.CR
									+ "C|1|I|Nathalie Tannous              ^                    "
									+ ASCII.ETB + "36" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "2     ^                    ^               ^|G"
									+ ASCII.CR
									+ "R|1|^^^413/|4.8|g/dl||N||F||ROSY  |||P1"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|2|^^^13/1/not|1.25|ng/ml||H||F||ROSY  |||E11"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|3|^^^142/1/not|871.0|ug/dl||N||F||ROSY  |||E11"
									+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
									+ "R|4|^^^146/1/not|33.73|nmol/"
									+ ASCII.ETB + "C9" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "3l||N||F||ROSY  |||E11" + ASCII.CR + "C|1|I|0|I"
							+ ASCII.CR + "L|1|N" + ASCII.CR + ASCII.ETX + "D6"
							+ ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Modular() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("MODULAR");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);

					// [STX]1H|\^&|||H7600^1|||||host|RSUPL^REAL|P|1[CR]P|1|||||||U||||||^[CR]O|1|[.][.][.][.][.][.][.][.][.][.]922|0^5003^3^^S1^SC|^^^767^|R||||||N||||1|||||||20080918145927|||F[CR]C|1|I|ELIAS[.]LOUHAIBEH[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^180'[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[ETB]65[CR][LF]
					// [STX]2|G[CR]R|1|^^^767/|50|mg/dl||L||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]L|1|N[CR][ETX]A9[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "1H|\\^&|||H7600^1|||||host|RSUPL^REAL|P|1"
									+ ASCII.CR
									+ "P|1|||||||U||||||^"
									+ ASCII.CR
									+ "O|1|          922|0^5003^3^^S1^SC|^^^767^|R||||||N||||1|||||||20080918145927|||F"
									+ ASCII.CR
									+ "C|1|I|ELIAS LOUHAIBEH               ^                         ^180'                ^               ^"
									+ ASCII.ETB + "65" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "2|G" + ASCII.CR
							+ "R|1|^^^767/|50|mg/dl||L||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR + "L|1|N"
							+ ASCII.CR + ASCII.ETX + "A9" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Elecsys1() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("ELECSYS");
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [ENQ]
					// [STX]1H|\^&||||||||||P||[CR][ETX]05[CR][LF]
					// [STX]2P|1|||||||||||||||||||||||||||||||||[CR][ETX]3B[CR][LF]
					// [STX]3O|1|66230|874^0^1^^SAMPLE^NORMAL|ALL|R|20080724132022|||||X||||||||||||||O|||||[CR][ETX]44[CR][LF]
					// [STX]4R|1|^^^271^^0|6422|pg/ml|^|N||F|||20080724132400|20080724134220|[CR][ETX]D6[CR][LF]
					// [STX]5L|1[CR][ETX]3E[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "1H|\\^&||||||||||P||" + ASCII.CR + ASCII.ETX
							+ "05" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "2P|1|||||||||||||||||||||||||||||||||"
							+ ASCII.CR + ASCII.ETX + "3B" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "3O|1|66230|874^0^1^^SAMPLE^NORMAL|ALL|R|20080724132022|||||X||||||||||||||O|||||"
									+ ASCII.CR + ASCII.ETX + "44" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "4R|1|^^^271^^0|6422|pg/ml|^|N||F|||20080724132400|20080724134220|"
									+ ASCII.CR + ASCII.ETX + "D6" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "5L|1" + ASCII.CR
							+ ASCII.ETX + "3E" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Elecsys2() {
		try {

			Globals.getDBManager().beginTransaction();

			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES (70738,'116011','','Haddad','Takla','F','F',1,0,0,TO_DATE
			// ('2008-11-13 23:21:00' , 'YYYY-MM-DD HH24:MI:SS'),72)
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date = dateFormat.parse("2008-11-13 23:21:00");
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request
					.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");
			request
					.append("(70738,'120949','','Saydeh','Nouhad','G','F',1,0,0,"
							+ "\"" + dateFormat.format(date) + "\"" + ",72)");
			// ELECSYS->Message before send:Message:120949 liq:1PatientId=
			// FirstName=Nouhad MidInitial=G LastName=Saydeh SexeF

			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,ALARM,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF)
			// VALUES (792450,'439','Troponin',0,0.0,0,'','',0,0,0,11,0,70738)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,ALARM,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2
					.append("(792450,'439','Troponin',0,0.0,0,'','',0,0,0,11,0,70738)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			Globals.getDBManager().commitTransaction();

			Instrument instr = PoolKernel.getInstrumentForAnyPool("ELECSYS");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [ENQ]
					// [STX]1H|\^&||||||||||P||[CR][ETX]05[CR][LF]
					// [STX]2P|1|||||||||||||||||||||||||||||||||[CR][ETX]3B[CR][LF]
					// [STX]3O|1|120949|2148^0^2^^SAMPLE^NORMAL|ALL|R|20081124151648|||||X||||||||||||||O|||||[CR][ETX]B3[CR][LF]
					// [STX]4R|1|^^^201^^0|<0.010|ng/ml|0.000^0.030|<||F|||20081124152720|20081124153716|[CR][ETX]FF[CR][LF]
					// [STX]5C|1|I|50^Result[.]below[.]measuring[.]range|I[CR][ETX]CE[CR][LF]
					// [STX]6L|1[CR][ETX]3F[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(10);

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "1H|\\^&||||||||||P||" + ASCII.CR + ASCII.ETX
							+ "05" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "2P|1|||||||||||||||||||||||||||||||||"
							+ ASCII.CR + ASCII.ETX + "3B" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "3O|1|120949|2148^0^2^^SAMPLE^NORMAL|ALL|R|20081124151648|||||X||||||||||||||O|||||"
									+ ASCII.CR + ASCII.ETX + "B3" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "4R|1|^^^201^^0|<0.010|ng/ml|0.000^0.030|<||F|||20081124152720|20081124153716|"
									+ ASCII.CR + ASCII.ETX + "FF" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "5C|1|I|50^Result below measuring range|I"
							+ ASCII.CR + ASCII.ETX + "CE" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "6L|1" + ASCII.CR
							+ ASCII.ETX + "3F" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Elecsys3() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("ELECSYS");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [ENQ]
					// [STX]1H|\^&||||||||||P||[CR][ETX]05[CR][LF]
					// [STX]2P|1|||||||||||||||||||||||||||||||||[CR][ETX]3B[CR][LF]
					// [STX]3O|1|66230|874^0^1^^SAMPLE^NORMAL|ALL|R|20080724132022|||||X||||||||||||||O|||||[CR][ETX]44[CR][LF]
					// [STX]4R|1|^^^271^^0|6422|pg/ml|^|N||F|||20080724132400|20080724134220|[CR][ETX]D6[CR][LF]
					// [STX]5L|1[CR][ETX]3E[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "1H|\\^&||||||||||P||" + ASCII.CR + ASCII.ETX
							+ "05" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "2P|1|||||||||||||||||||||||||||||||||"
							+ ASCII.CR + ASCII.ETX + "3B" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "3O|1|66230|874^0^1^^SAMPLE^NORMAL|ALL|R|20080724132022|||||X||||||||||||||O|||||"
									+ ASCII.CR + ASCII.ETX + "44" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "4R|1|^^^271^^0|6422|pg/ml|^|N||F|||20080724132400|20080724134220|"
									+ ASCII.CR + ASCII.ETX + "D6" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "5L|1" + ASCII.CR
							+ ASCII.ETX + "3E" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Elecsys4() {
		try {

			Globals.getDBManager().beginTransaction();
			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES
			// (268354,'381052','','Ferneini','Yvette','N','F',1,0,0,TO_DATE
			// ('2010-03-08 20:05:00' , 'YYYY-MM-DD HH24:MI:SS'),70)
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			java.util.Date date = dateFormat.parse("2010-03-08 20:05:00");
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request
					.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");
			request
					.append("(268354,'381052','','Ferneini','Yvette','N','F',1,0,0,"
							+ "\"" + dateFormat.format(date) + "\"" + ",70)");
			// ELECSYS->Message before send:Message:120949 liq:1PatientId=
			// FirstName=Nouhad MidInitial=G LastName=Saydeh SexeF

			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);
			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM)
			// VALUES
			// (3179684,'439','Troponin',0.0,0,'','',0,0,0,11,0,268354,0)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM) ");
			request2.append("VALUES ");
			request2
					.append("(3179684,'439','Troponin',0.0,0,'','',0,0,0,11,0,268354,0)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			Globals.getDBManager().commitTransaction();

			Instrument instr = PoolKernel.getInstrumentForAnyPool("ELECSYS");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(1);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

				}
			});
			thread.start();
			Thread.sleep(30000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Elecsys() {
		try {
			Instrument instr = PoolKernel.getInstrumentForAnyPool("ELECSYS");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [ENQ]
					// [STX]1H|\^&||||||||||P||[CR][ETX]05[CR][LF]
					// [STX]2P|1|||||||||||||||||||||||||||||||||[CR][ETX]3B[CR][LF]
					// [STX]3O|1|381052|1136^0^2^^SAMPLE^NORMAL|ALL|R|20100308200231|||||X||||||||||||||O|||||[CR][ETX]94[CR][LF]
					// [STX]4R|1|^^^90^^0|0.013|ng/ml|0.000^0.014|N||F|||20100308201739|20100308202735|[CR][ETX]A9[CR][LF]
					// [STX]5L|1[CR][ETX]3E[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "1H|\\^&||||||||||P||" + ASCII.CR + ASCII.ETX
							+ "05" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "2P|1|||||||||||||||||||||||||||||||||"
							+ ASCII.CR + ASCII.ETX + "3B" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "3O|1|381052|1136^0^2^^SAMPLE^NORMAL|ALL|R|20100308200231|||||X||||||||||||||O|||||"
									+ ASCII.CR + ASCII.ETX + "94" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "4R|1|^^^90^^0|0.013|ng/ml|0.000^0.014|N||F|||20100308201739|20100308202735|"
									+ ASCII.CR + ASCII.ETX + "A9" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "5L|1" + ASCII.CR
							+ ASCII.ETX + "3E" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			Thread.sleep(300000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}

	public void test_DRIVER_Dispatcher() {
		FCalendar calendar = FCalendar.getDefaultCalendar();

		Date date = new Date(System.currentTimeMillis()
				+ Calendar.getInstance().get(Calendar.DST_OFFSET));
		long currentTimeSinceMidnight = FCalendar.getTimeSinceMidnight(date);

		boolean workTime = calendar.isWorkTime(currentTimeSinceMidnight);
		Globals.logString("worktime: " + workTime);
		Globals.logString("DST:"
				+ Calendar.getInstance().get(Calendar.DST_OFFSET));

		sleep(3);
	}

	public void test_DRIVER_COBASE411() {
		try {

			Globals.getDBManager().beginTransaction();

			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES (70738,'116011','','Haddad','Takla','F','F',1,0,0,TO_DATE
			// ('2008-11-13 23:21:00' , 'YYYY-MM-DD HH24:MI:SS'),72)
			
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date = dateFormat.parse("2008-11-13 23:21:00");
			Statement stmt = Globals.getDBManager().lockStatement();
			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");
			request.append("(70740,'120950','','TESTa','Testb','G','F',1,0,0,"+ "\"" + dateFormat.format(date) + "\"" + ",72)");
			// ELECSYS->Message before send:Message:120949 liq:1PatientId=
			// FirstName=Nouhad MidInitial=G LastName=Saydeh SexeF

			String req = request.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,ALARM,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF)
			// VALUES (792450,'439','Troponin',0,0.0,0,'','',0,0,0,11,0,70738)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2.append("(REF,LABEL,DESCRIP,ALARM,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF) ");
			request2.append("VALUES ");
			request2.append("(792450,'494','LH0',0,0.0,0,'','',0,0,0,21,0,70740)");
			req = request2.toString();
			Globals.logString(req);
			stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			Globals.getDBManager().commitTransaction();

			Instrument instr = PoolKernel.getInstrumentForAnyPool("COBAS-E411");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			/*
			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [ENQ]
					// [STX]1H|\^&||||||||||P||[CR][ETX]05[CR][LF]
					// [STX]2P|1|||||||||||||||||||||||||||||||||[CR][ETX]3B[CR][LF]
					// [STX]3O|1|120949|2148^0^2^^SAMPLE^NORMAL|ALL|R|20081124151648|||||X||||||||||||||O|||||[CR][ETX]B3[CR][LF]
					// [STX]4R|1|^^^201^^0|<0.010|ng/ml|0.000^0.030|<||F|||20081124152720|20081124153716|[CR][ETX]FF[CR][LF]
					// [STX]5C|1|I|50^Result[.]below[.]measuring[.]range|I[CR][ETX]CE[CR][LF]
					// [STX]6L|1[CR][ETX]3F[CR][LF]
					// [EOT]

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(10);

					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "1H|\\^&||||||||||P||" + ASCII.CR + ASCII.ETX
							+ "05" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "2P|1|||||||||||||||||||||||||||||||||"
							+ ASCII.CR + ASCII.ETX + "3B" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "3O|1|120949|2148^0^2^^SAMPLE^NORMAL|ALL|R|20081124151648|||||X||||||||||||||O|||||"
									+ ASCII.CR + ASCII.ETX + "B3" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "4R|1|^^^201^^0|<0.010|ng/ml|0.000^0.030|<||F|||20081124152720|20081124153716|"
									+ ASCII.CR + ASCII.ETX + "FF" + ASCII.CR
									+ ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "5C|1|I|50^Result below measuring range|I"
							+ ASCII.CR + ASCII.ETX + "CE" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX + "6L|1" + ASCII.CR
							+ ASCII.ETX + "3F" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			*/
			Thread.sleep(3000000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
		
	}
	
	
	
	//1H|\^&|||host^2|||||H7600|TSDWN^BATCH|P|1[CR]
	//P|1[CR]
	//O|1|[.][.][.][.][.][.][.]999888|^^^^S1^SC|^^^452^|R||20111207000000||||A||||1||||||||||O[CR]
	//C|1|L|Edmond[.]Saab^^^^|G[CR]
	//L|1|N[CR][ETX]51[CR][LF]
	public void test_DRIVER_CobasC311() {
		try {
			Globals.getDBManager().beginTransaction();

			boolean executeSend = true;
			
			// sql_InsertSample()
			// INSERT INTO L3SAMPLE
			// (REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE)
			// VALUES (21131,'53535','','Saab','Edmond','G','M',1,0,0,TO_DATE
			// ('24-Jun-08' , 'DD-MON-YY'),70)
			Statement stmt = Globals.getDBManager().lockStatement();
			String currDateSQL = "2011-12-07";
			// String currDateSQL = FDate.convertDateToSQLString(new
			// Date(System.currentTimeMillis()));

			Globals.logString("CollectionDate: " + currDateSQL);

			StringBuffer request = new StringBuffer("INSERT INTO L3SAMPLE ");
			request
					.append("(REF,ID,PATIENT_ID,LAST_NAME,FIRST_NAME,MIDDLE_INITIAL,SEXE,LIQUIDE_TYPE,RESULT_CONFIRMED,OK_TO_BE_SENT,ENTRY_DATE,AGE) ");
			request.append("VALUES ");
			request.append("(21131,'999888','','Saab','Edmond','G','M',1,0,0,'"
					+ currDateSQL + "',70)");
			String req = request.toString();
			Globals.logString(req);
			if(executeSend) stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			// sql_InsertTest(sampleID, "", "409");
			// INSERT INTO L3TEST
			// (REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF)
			// VALUES (211309,'408','Cl',0.0,0,'','',0,0,0,12,0,21131)
			stmt = Globals.getDBManager().lockStatement();
			StringBuffer request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM) ");
			request2.append("VALUES ");
			//request2.append("(211309,'443','C4-2',0.0,0,'','',0,0,0,22,0,21131,0)");
			request2.append("(211309,'412','CREA2',0.0,0,'','',0,0,0,22,0,21131,0)");
			req = request2.toString();
			Globals.logString(req);
			if(executeSend) stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);

			/*
			stmt = Globals.getDBManager().lockStatement();
			request2 = new StringBuffer("INSERT INTO L3TEST ");
			request2
					.append("(REF,LABEL,DESCRIP,VALUE,RESULT_OK,UNIT_LABEL,MESSAGE,STATUS,BLOCKED,I_SUGGREF,INSTR_REF,I_RECREF,SAMPLE_REF,ALARM) ");
			request2.append("VALUES ");
			request2.append("(211307,'37','K',0.0,0,'','',0,0,0,22,0,21131,0)");
			req = request2.toString();
			Globals.logString(req);
			if(executeSend) stmt.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stmt);
			*/

			Globals.getDBManager().commitTransaction();

			Instrument instr = PoolKernel.getInstrumentForAnyPool("COBAS-C311");
			instr.switchOn();
			driver = (DriverSerialPort) instr.getDriver();

			/*
			Thread thread = new Thread(new Runnable() {
				public void run() {
					StringBuffer buffer = null;
					sleep(3);
					// [STX]1H|\^&|||H7600^1|||||host|RSUPL^REAL|P|1[CR]P|1|||||||M||||||70^Y[CR]O|1|[.][.][.][.][.][.][.][.]53535|0^5005^2^^S1^SC|^^^989^\^^^990^\^^^991^\^^^419^\^^^767^\^^^452^\^^^690^|R||20080623080846||||N||||1|||||||20080624091808|||F[CR]C|1|I|Edmond[.]Saab[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][ETB]C3[CR][LF]
					// [STX]2[.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]^|G[CR]R|1|^^^989/|132|mmol/l||L||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|2|^^^990/|3.5|mmol/l||N||F||ROSY[.][.]|||ISE1[CR]C|1|I|0|I[CR]R|3|^^^991/|79|mmol/l||L||F||ROSY[.][.]|||ISE1[CR]C|1|I|45|I[CR]R|4|^^^419[ETB]BB[CR][LF]
					// [STX]3/|108|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|5|^^^767/|140|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|6|^^^452/|40.8|mmol/l||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]R|7|^^^690/|1.46|mg/dl||H||F||ROSY[.][.]|||P1[CR]C|1|I|0|I[CR]L|1|N[CR][ETX]B8[CR][LF]

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.ENQ);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "1H|\\^&|||H7600^1|||||host|RSUPL^REAL|P|1"
									+ ASCII.CR
									+ "P|1|||||||M||||||70^Y"
									+ ASCII.CR
									+ "O|1|        53535|0^5005^2^^S1^SC|^^^989^\\^^^990^\\^^^991^\\^^^419^\\^^^767^\\^^^452^\\^^^690^|R||20080623080846||||N||||1|||||||20080624091808|||F"
									+ ASCII.CR
									+ "C|1|I|Edmond Saab                  "
									+ ASCII.ETB + "C3" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(
							ASCII.STX
									+ "2 ^                         ^                    ^               ^|G"
									+ ASCII.CR
									+ "R|1|^^^989/|132|mmol/l||L||F||ROSY  |||ISE1"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|2|^^^990/|3.5|mmol/l||N||F||ROSY  |||ISE1"
									+ ASCII.CR
									+ "C|1|I|0|I"
									+ ASCII.CR
									+ "R|3|^^^991/|79|mmol/l||L||F||ROSY  |||ISE1"
									+ ASCII.CR + "C|1|I|45|I" + ASCII.CR
									+ "R|4|^^^419" + ASCII.ETB + "BB"
									+ ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer(ASCII.STX
							+ "3/|108|mg/dl||H||F||ROSY  |||P1" + ASCII.CR
							+ "C|1|I|0|I" + ASCII.CR
							+ "R|5|^^^767/|140|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|6|^^^452/|40.8|mmol/l||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR
							+ "R|7|^^^690/|1.46|mg/dl||H||F||ROSY  |||P1"
							+ ASCII.CR + "C|1|I|0|I" + ASCII.CR + "L|1|N"
							+ ASCII.CR + ASCII.ETX + "B8" + ASCII.CR + ASCII.LF);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
					buffer = new StringBuffer("" + ASCII.EOT);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);

					buffer = new StringBuffer("" + ASCII.ACK);
					getDriver().getL3SerialPort().extractAnswerFromBuffer(
							buffer);
					sleep(3);
				}
			});
			thread.start();
			*/
			Thread.sleep(100000);

		} catch (Exception e1) {
			Globals.logException(e1);
		}
	}
	
	
	
	
	
	
	
	
}
