package b01.tests;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringTokenizer;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.admin.FocGroup;
import b01.foc.gui.DisplayManager;
import b01.foc.menu.FMenuList;
import b01.l3.DriverFactory;
import b01.l3.FileIOFactory;
import b01.l3.FocAppGroup;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.L3KernelModule;
import b01.l3.L3Main;
import b01.l3.connector.LisConnectorFactory;
import b01.l3.connector.dbConnector.lisConnectorTables.LisConnectorModule;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver;
import b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Frame;
import b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Receiver;
import b01.l3.drivers.roches.cobas.infinity.CobasInfinity_ResultLineReader;

public class MainTester2 {
	
	public static void mainMySQLs(String[] args) {
		try {
			// Globals.logString("Loading...");
			L3Main l3Main = new L3Main(args);
			Application app = l3Main.newApplicationVersionAndModuleDeclaration(args);
			FocGroup.setApplicationGroup(FocAppGroup.getFocDesc(), 1);
			app.declareModule(new LisConnectorModule());

			app.prepareDBForLogin(null);
			Statement stm = null;
			String req = null;
			int ref = 0;

			req = "DROP TABLE TEST_TABLE";
			stm = Globals.getDBManager().lockStatement();
			Globals.logString(req);
			try {
				stm.executeUpdate(req);
			} catch (Exception e) {
				Globals.logException(e);
			}
			Globals.getDBManager().unlockStatement(stm);

			req = "CREATE TABLE TEST_TABLE (REF INT NOT NULL AUTO_INCREMENT, THE_DATA VARCHAR(100), PRIMARY KEY (REF))";
			stm = Globals.getDBManager().lockStatement();
			Globals.logString(req);
			stm.executeUpdate(req);
			Globals.getDBManager().unlockStatement(stm);

			req = "INSERT INTO TEST_TABLE (THE_DATA) VALUES('Yalalal')";
			stm = Globals.getDBManager().lockStatement();
			Globals.logString(req);
			stm.execute(req, Statement.RETURN_GENERATED_KEYS);
			ResultSet resSet = stm.getGeneratedKeys();
			while (resSet != null && resSet.next()) {
				ref = resSet.getInt(1);
				Globals.logString("REF = " + ref);
			}
			Globals.getDBManager().unlockStatement(stm);

			req = "SELECT * FROM TEST_TABLE";
			stm = Globals.getDBManager().lockStatement();
			Globals.logString(req);
			stm.execute(req);
			resSet = stm.getResultSet();
			ResultSetMetaData meta = resSet.getMetaData();
			while (meta != null && resSet != null && resSet.next()) {
				for (int i = 1; i < meta.getColumnCount() + 1; i++) {
					System.out.print(resSet.getString(i) + ", ");
				}
				System.out.println();
			}
			Globals.getDBManager().unlockStatement(stm);

			// ALTER TABLE `l3hsgunit`.`test_table` MODIFY COLUMN `REF` INTEGER
			// DEFAULT NULL,
			// DROP PRIMARY KEY;

			// ALTER TABLE `l3hsgunit`.`test_table` MODIFY COLUMN `REF` INTEGER
			// NOT NULL DEFAULT NULL AUTO_INCREMENT;

		} catch (Exception e) {
			Globals.logException(e);
		}
	}

	public static void mainORacle(String[] args) {
		try {
			// Globals.logString("Loading...");
			L3Main l3Main = new L3Main(args);
			Application app = l3Main.newApplicationVersionAndModuleDeclaration(args);
			FocGroup.setApplicationGroup(FocAppGroup.getFocDesc(), 1);
			app.declareModule(new LisConnectorModule());

			app.prepareDBForLogin(null);
			Statement stm = null;

			/*
			 * stm = Globals.getDBManager().lockStatement(); stm.executeUpdate(
			 * "CREATE TABLE TEST_TABLE (REF NUMBER, THE_DATA VARCHAR2(100))");
			 * Globals.logString("Create table");
			 * Globals.getDBManager().unlockStatement(stm);
			 * 
			 * stm = Globals.getDBManager().lockStatement(); stm.executeUpdate(
			 * "CREATE SEQUENCE TEST_SEQ START WITH 1 INCREMENT BY 1 NOMAXVALUE"
			 * ); Globals.logString("Create sequence");
			 * Globals.getDBManager().unlockStatement(stm);
			 */

			int ref = 0;
			stm = Globals.getDBManager().lockStatement();
			stm.execute("SELECT TEST_SEQ.curval from dual");
			ResultSet resSet = stm.getResultSet();
			while (resSet != null && resSet.next()) {
				ref = resSet.getInt(1);
				Globals.logString("ID = " + ref);
				ref++;
			}
			Globals.getDBManager().unlockStatement(stm);

			stm = Globals.getDBManager().lockStatement();
			int[] cols = { 1 };
			stm.execute("INSERT INTO TEST_TABLE VALUES(" + ref + ", 'voila!')", cols);
			Globals.getDBManager().unlockStatement(stm);

		} catch (Exception e) {
			Globals.logException(e);
		}
	}

	public static void main(String[] args) {
		
		
		String token = "HH,27";
		if(token != null && token.length() > 0){
				String message = "";
		    StringTokenizer tok = new StringTokenizer(token, ",", false);      
		    while(tok.hasMoreTokens()){
		    	String currentMessage = null;
		      
		    	String subToken = tok.nextToken();
		      try {
		      	int alarmCode = Integer.valueOf(subToken);
						if(alarmCode > 0 && alarmCode < CobasInfinity_ResultLineReader.infinityAlarms.length){
							currentMessage = CobasInfinity_ResultLineReader.infinityAlarms[alarmCode];
						}
		      } catch(Exception e) {
		      	//currentMessage = subToken;
		      }

		      if (currentMessage != null && !currentMessage.isEmpty()) {
		      	if(!message.isEmpty()) message += ", ";
		      	message += currentMessage;
		      }
		    }

				int debug = 3;
				debug++;
		}

		
		
	}
	
	/**
	 * This is the Official main method
	 * @param args
	 */
	public static void main2(String[] args) {
		try {
			// Globals.logString("Loading...");
			L3Main l3Main = new L3Main(args);
			Application app = l3Main.newApplicationVersionAndModuleDeclaration(args);
			FocGroup.setApplicationGroup(FocAppGroup.getFocDesc(), 1);
			app.declareModule(new LisConnectorModule());

			//ONLY In TESTing
			app.prepareDBForLogin(null);
			
			DriverFactory.getInstance().addDriver("b01.l3.drivers.horiba.pentraML.PentraMLDriver", b01.l3.drivers.horiba.pentraML.PentraMLDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.horiba.pentraML.PentraMLEmulator", b01.l3.drivers.horiba.pentraML.PentraMLEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.axsym.AxsymDriver", b01.l3.drivers.abbott.axsym.AxsymDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.axsym.AxsymEmulator", b01.l3.drivers.abbott.axsym.AxsymEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.architect.ArchitectDriver", b01.l3.drivers.abbott.architect.ArchitectDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.architect.ArchitectEmulator", b01.l3.drivers.abbott.architect.ArchitectEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.architectPlus.ArchitectPlusDriver", b01.l3.drivers.abbott.architect.ArchitectDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.architectPlus.ArchitectPlusEmulator", b01.l3.drivers.abbott.architect.ArchitectEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.coulter.CoulterDriver", b01.l3.drivers.coulter.CoulterDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.coulter.CoulterEmulator", b01.l3.drivers.coulter.CoulterEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.hitachi.elecsys2010.ElecsysDriver", b01.l3.drivers.hitachi.elecsys2010.ElecsysDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.hitachi.elecsys2010.ElecsysEmulator", b01.l3.drivers.hitachi.elecsys2010.ElecsysEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.modular.ModularDriver", b01.l3.drivers.abbott.modular.ModularDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.abbott.modular.ModularEmulator", b01.l3.drivers.abbott.modular.ModularEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.c311.CobasC311Driver", b01.l3.drivers.roches.cobas.c311.CobasC311Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.c311.CobasC311Emulator", b01.l3.drivers.roches.cobas.c311.CobasC311Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.e411.CobasE411Driver", b01.l3.drivers.roches.cobas.e411.CobasE411Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.e411.CobasE411Emulator", b01.l3.drivers.roches.cobas.e411.CobasE411Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.u601.CobasU601Driver", b01.l3.drivers.roches.cobas.u601.CobasU601Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.u601.CobasU601Emulator", b01.l3.drivers.roches.cobas.u601.CobasU601Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.u601701.CobasU601701Driver", b01.l3.drivers.roches.cobas.u601701.CobasU601701Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.u601701.CobasU601701Emulator", b01.l3.drivers.roches.cobas.u601701.CobasU601701Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.cobas501.Cobas501Driver", b01.l3.drivers.roches.cobas.cobas501.Cobas501Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.cobas501.Cobas501Emulator", b01.l3.drivers.roches.cobas.cobas501.Cobas501Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.infinity.CobasInfinityDriver", b01.l3.drivers.roches.cobas.infinity.CobasInfinityDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.roches.cobas.infinity.CobasInfinityEmulator", b01.l3.drivers.roches.cobas.infinity.CobasInfinityEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.hitachi.hitachi912.Hitachi912Driver", b01.l3.drivers.hitachi.hitachi912.Hitachi912Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.hitachi.hitachi912.Hitachi912Emulator", b01.l3.drivers.hitachi.hitachi912.Hitachi912Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.helena.junior24.Junior24Driver", b01.l3.drivers.helena.junior24.Junior24Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.helena.junior24.Junior24Emulator", b01.l3.drivers.helena.junior24.Junior24Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.bectonDickinson.sedi15.Sedi15Driver", b01.l3.drivers.bectonDickinson.sedi15.Sedi15Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.bectonDickinson.sedi15.Sedi15Emulator", b01.l3.drivers.bectonDickinson.sedi15.Sedi15Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.dadeBehring.bct.BCTDriver", b01.l3.drivers.dadeBehring.bct.BCTDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.dadeBehring.bcs.BCSDriver", b01.l3.drivers.dadeBehring.bcs.BCSDriver.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.alcor.ised.ISEDDriver", b01.l3.drivers.alcor.ised.ISEDDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.alcor.ised.ISEDEmulator", b01.l3.drivers.alcor.ised.ISEDEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver", b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Emulator", b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Emulator.class);

			FileIOFactory.getInstance().addFileIO("b01.l3.connector.fileConnector.DefaultFileIO", b01.l3.connector.fileConnector.DefaultFileIO.class);

			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.fileConnector.LisFileConnector", b01.l3.connector.fileConnector.LisFileConnector.class);
			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.dbConnector.LisDBConnector", b01.l3.connector.dbConnector.LisDBConnector.class);

			/*
			app.login();

			if (app.getGuiNavigatorType() == DisplayManager.GUI_NAVIGATOR_MDI) {
				FMenuList mainMenu = new FMenuList("Root", 'R');
				L3KernelModule.declareMenu(mainMenu);
			} else if (app.getGuiNavigatorType() == DisplayManager.GUI_NAVIGATOR_MONOFRAME) {
				app.setDisableMenus(true);
			}

			app.entry();
			if (app.getLoginStatus() == Application.LOGIN_VALID && app.getGuiNavigatorType() != DisplayManager.GUI_NAVIGATOR_NONE) {
				// l3Main.automaticPurgeCheckAndExecution();
				l3Main.popupTheRightPanel();
			}
//			Globals.logString("Build date : 23/06/2015");
			Globals.logString("Build date : 10/03/2019");
			*/
			

//			l3Main.executeBackgroundTasks();
//			
//			int idx=0; 
//			while(true){ 
//				Thread.sleep(1000);
//				Globals.logString("Looping ..."+idx); 
//				idx++; 
//			}
			
			//Preparation of the Instrument Driver and Frame
			Properties props = new Properties();
			props.put("instrument.code","YP8K");
			props.put("instrument.name","YP8K");
			props.put("instrument.driver", "b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver");
			Instrument instr = new Instrument(props);
			instr.setPropertyString(InstrumentDesc.FLD_SERIAL_PORT_NAME, "10002");
			YumizenP8000Driver   driver   = (YumizenP8000Driver) instr.getDriver();
			driver.connect();

			Thread.sleep(4000);
			
			L3Message message = new L3Message();
			L3Sample sample = new L3Sample("123124");
			sample.setPatientId("989898");
			sample.setSexe("M");
			sample.setFirstName("Antoine");
			sample.setLastName("Samaha");
			sample.setMiddleInitial("N");
			message.addSample(sample);
			L3Test test = sample.addTest();
			test.setLabel("RDW-SD");
			driver.send(message);

			
			
			int idx=0; 
			while(true){
				Thread.sleep(1000);
				Globals.logString("Looping ..."+idx); 
				idx++; 
			}
			
			/*
			YumizenP8000Receiver receiver = (YumizenP8000Receiver) driver.getDriverReceiver();
			YumizenP8000Frame    frame    = new YumizenP8000Frame(instr, 0);
			//----------------------------------------------

			StringBuffer buffer = null;
			boolean      extractionDone = false;
			buffer = new StringBuffer(YumizenP8000Frame.VT);
			extractionDone = frame.extractAnswerFromBuffer(buffer);
			if(extractionDone) receiver.received(frame);
			for(int i=0; i<resultsFromYumizenP8000_2.length; i++) {
				String message = resultsFromYumizenP8000_2[i];
				buffer = new StringBuffer(message);
				buffer.append(YumizenP8000Frame.CR);
				extractionDone = frame.extractAnswerFromBuffer(buffer);
				if(extractionDone) receiver.received(frame);
			}
			buffer = new StringBuffer();
			buffer.append(YumizenP8000Frame.FS);
			buffer.append(YumizenP8000Frame.CR);
			extractionDone = frame.extractAnswerFromBuffer(buffer);
			if(extractionDone) receiver.received(frame);
			*/


//OBX|1|ED|RBC^RBC||^IM^PNG^Base64^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|||N|||F|||20190702124143||||902M2SH00193[CR]OBR|41|1907020003|1907020003|PLT^PLT^HALIA|||20190702115043||||||||||||||||||F|||||||[CR]ORC|SC|1907020003|1907020003|1907020003|A||||||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|ED|PLT^PLT||^IM^PNG^Base64^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|||N|||F|||20190702124143||||902M2SH00193[CR]NTE|1||PLT interference[CR]OBR|42|1907020003|1907020003|BAS%^BAS%^HALIA|||20190702115043||||||||||||||||||F|||||||[CR]ORC|SC|1907020003|1907020003|1907020003|A||||||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|ED|BAS%^BAS%||^IM^PNG^Base64^iVBORw0KGgoAAAANSUhEUgAAAQQAAAB2CAIAAACh93ojAAAHOUlEQVR42u2d708TdxzHeeYD41Mf+MAYn/knbMs2EhcZVPxBfQCDttAyMjRxI9uSTSo0Yv2BBYdCdMmExQSMTYWZDWG6FaZWhYLFNhEI2UyI+sC4jcQHxJnYfeMlN9ZKLce19+v1yoWc57X3be/7us+9uS93BUkAeEUBXwEAMgAgAwAyACADADIAIAMAMgAgAwAyACADgJ5kOHrs+LnzfVlOhYWF2a+czXS+72JDQ0N9/d4z3/Wo+85MiifV9/KKNj06OqqZDKe7zl69Ec1yKigoyH7lbKbJ+LTf719YWCgqtqn7zkyKJ9X38oo2raUMx463avg1BUP94XBYNCN0aeBI2yk6IjJoKYPP59Pwa2r2+RYXF6WWfGgrpSMig0UrQywxXVVVJbek+ZD/wsAV+iIyaCbDyY7TmnxN0fjM1q1bRVqQWzL3+x/7PvuSvogMlqsMIiQMDw+nNKaohDMlZLBYZhi5E3M6nemNcbjr6IvIYK3KIM6Rmpub0xuzfedu+iIyWCwzhH/r7e1Nb8wn+/b/fH2c7ogMFqoMXWe+nZmZSW9Me0dnd2+I7ogMFsoMjV7vy5cv0xtzZzz6VVML3REZrFIZRHp2u92vbczz5/84aj6mOyKDVTLDculZomTHLrojMlgmMyyTniX2N3z+068ReiQyWCIzLJeeJYKh/hMdZ+iRyGCJyrBcepb486+/a+r20iORwfyZIUN6JjYgg7UqQ+b0TGxABitlhozpmdiADBaqDJnTM7EBGSyUGTKn5/9iQymxARlMXRkiE4mamppsWsWIPWQweWa4NTbZ2dmZTasYsYcMJq8Mgbb2R48eZdMqRuwhg5kzw41ovLq6OstWMWIPGTSQoe1kR36+Jul+Ydk3zEaGRgazZgb5fmFZwm3FkMG0mWHp/cKyxOVyFW/fSe9EBrNlBo/Ho6B523eW0TuRwWyVQZyPKWjepw1fME4JGcyWGZTJEOr/gXFKyEBlYJwSMqgtg9iAHjKDMhkYp4QM+ZNB55WBcUrIoFAG6b02b968du3a7u5ueeHg4KBYuGbNmtLS0qdPn+Y/M1y7OVFbW6vsEzJOCRkUymC32x8+fDg7O7t+/Xp5YX19/ePHj8X80NBQXV1d/ivD9fF7b/wDN8YpIYPKMjx79izl7CjlNGnDhg35zwxjU/cVnyYxTgkZFMqQPp8igziDyn9lGI2Md3V1Kf6QjFNCBtVkSCQS8vKNGzfmPzMM/Hjl8uXLij8k45SQQTUZNm3aNDc3J+ZjsZgIFfmvDOe+P3/37t3VXA/h8YfIoIIM69atCwaDW7ZsESdIxcXF8/Pz+c8MR4+fePLkyWpkoDggg0muQH994EBy1VAckMEMY5O83oOrl4HigAxmqAzKxm+nU7pjV/9QmP6KDEb9e4bVXHFLYWFh4YOiEvorMhi1MoxN3VdLhiSPx0UGQ2eGSHQqEAioJcPusj2cKSGDUSvD4NVwX1+fWjKIM6WiYhtdFhkMmRn6LoZGRkaS6sGvlZDBqJWhs+vs7OxsUlW45oAMhpThUMtheSwtxQEZLC2DKpefKQ7IYIbMoMrlZ4oDMhi+MvwSmVTr8nM6RSUUB2QwznWGyERCxStuKfAoRGQwUmUYvzfd0tKSIxl4FCIyGCkzXBu92dPTkyMZuMUYMhipMlwIXlL3ilsKu3bbGZ2BDMbIDN+c6nzw4EHuZGAcKzIYpjI0eg++ePEimUsYuocMxsgMObrIkFIcbKU78AEZdF0ZRsemcneRAR+QwUiZIRqfaWpqSuYFhnYjg64rw+2JWJZPQWeABjKYPDNcDPXn9Peq6XDHAGTQaWVoPRFY5b3DFJwsORwOnhGKDLrLDI2N3qQW8MtWZNBXZbgRjbvdbk1kkOoDSiCDXjLDZHza7/cntQMlkEEvlSEY6g+Hw0mtYbyGUWSITCRMmxmafb7FxcWkDqA4GEKGW2OT5qwM0fiM0+lM6gOuTxtChkBbuzkzQ+jSwPDwcFI3SPmh9hVyoYglpuWF7773PrZoKIM4eop9Yc7K4HK5knpFiLFt2zYhgPj2xfxyadvj8aScX4klS3USLr319jvmU0gTGaSjp7Ezg3RwlQ6rSw+0ip/6rJ8CIkj5p/yhpOWC+fn5lBVsNlvt/8nRkrKyPW/ctNgv12+NL11NXkf2WTouLEXs5RU1RnqrDMFMbEU6TZWPJvIL5bZJR0+NZXC7Pa7qmiqHo6KiQvwsL6+Q56Xldrtdni8sLExZx+F0HT7s93q9H1VWSvPCLlFtxNmX+Ml87ubLy8vFt515HbFfxG5qDbSnryNe63A6KysrxToprxV7eUXtEe8v9r5oj/j52r4ktnLkWKv4XzGf1gbX0vcUm1b8iGQVZAAwDcgAgAwAyACADADIAIAMAMgAgAwAyACADADIAIAMAMgAgAwAyACADADIAIAMAMgAgAwAyACADADIAIAMAMgAgAwAyACADABm4F+HjwWYLajuLQAAAABJRU5ErkJggg==|||N|||F|||20190702124143||||902M2SH00193[CR][FS][CR]


			
			
			
//		buffer = new StringBuffer("PID|||P0002^^^LIS^PI||DOE^JOHN^^^^^||19601206|M|||Main Street^^Springfield^NY^65466^USA^ATC1|||||||ABC123|||||||||||||Y");
//		buffer.append(YumizenP8000Frame.CRy);
//		extractionDone = frame.extractAnswerFromBuffer(buffer);
//		if(extractionDone) receiver.received(frame);
//		
//		buffer = new StringBuffer("SPM|1|201604163002||EDTA||||MAIN LAB");
//		buffer.append(YumizenP8000Frame.CRy);
//		extractionDone = frame.extractAnswerFromBuffer(buffer);
//		if(extractionDone) receiver.received(frame);
//
//		buffer = new StringBuffer("OBX|1|NM|RDW-SD^RDW-SD||45.0|fl|||||F|||20160705100630||||Yumizen H2500-SPS");
//		buffer.append(YumizenP8000Frame.CRy);
//		extractionDone = frame.extractAnswerFromBuffer(buffer);
//		if(extractionDone) receiver.received(frame);

			
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
	
	private static String resultsFromYumizenP8000_2[] = {
			"MSH|^~\\&|HALIA|^^|^^|^^|20190702145923||OUL^R22^OUL_R22|HALIA20190702145923|P|2.5|||||||",
			"PID|||777788889999^^^LIS^PI||PAT 1907020003^PAT 1907020003^^^^^||20150728|M|||Main Street^^Springfield^NY^65466^USA^ATC1|||||||ABC123|||||||||||||Y",
			"PV1||N|WARD00003^^||||ATD^||||||||||^||ABC123|||||||||||||||||||||||||20190702115043|20190702115043",
			"SPM|1|777788889999||EDTA||||MAIN LAB",
			"OBR|1|1907020003|1907020003|PCT^PCT^HALIA|||20190702115043||||||||||||||||||F|||||||LABO",
			"ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003",
			"TQ1|||||||20190702115043||R",
			"OBX|1|NM|PCT^PCT||0.317|%|0.15 - 0.5||||F|||20190702124426||||902M2SH00193",
			"OBR|2|1907020003|1907020003|IMG%^IMG%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO",
			"ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003",
			"TQ1|||||||20190702115043||R",
			"OBX|1|NM|IMG%^IMG%||0.1|%|0.0 - 2.0||||F|||20190702124426||||902M2SH00193",
			
			"OBR|3|1907020003|1907020003|NEU#^NEU#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO",
			"ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003",
			"TQ1|||||||20190702115043||R",
			"OBX|1|NM|NEU#^NEU#||4.17|10\\S\\3/mm3|1.19 - 8.29||||F|||20190702124426||||902M2SH00193",
			"OBR|4|1907020003|1907020003|MCV^MCV^HALIA|||20190702115043||||||||||||||||||F|||||||LABO",
			"ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003",
			"TQ1|||||||20190702115043||R",
			"OBX|1|NM|MCV^MCV||80.1|fL|70.0 - 85.0||||F|||20190702124426||||902M2SH00193",
			"OBR|5|1907020003|1907020003|IMG#^IMG#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO",
			"ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003",
			"TQ1|||||||20190702115043||R",
			"OBX|1|NM|IMG#^IMG#||0.01|10\\S\\3/mm3|0.0 - 99.99||||F|||20190702124426||||902M2SH00193",
			"OBR|6|1907020003|1907020003|NEU%^NEU%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO",
			"ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003",
			//TQ1|||||||20190702115043||R[CR]OBX|1|NM|NEU%^NEU%||48.2|%|0.0 - 999.9||||F|||20190702124426||||902M2SH00193[CR]OBR|7|1907020003|1907020003|RDW-CV^RDW-CV^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|RDW-CV^RDW-CV||13.2|%|12.0 - 18.0||||F|||20190702124426||||902M2SH00193[CR]OBR|8|1907020003|1907020003|TNC^TNC^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|TNC^TNC||8.64|10\S\3/mm3|4.86 - 13.51||||F|||20190702124426||||902M2SH00193[CR]OBR|9|1907020003|1907020003|ALY%^ALY%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|ALY%^ALY%||0.8|%|0.0 - 4.0||||F|||20190702124426||||902M2SH00193[CR]OBR|10|1907020003|1907020003|ALY#^ALY#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|ALY#^ALY#||0.07|10\S\3/mm3|0.0 - 0.54||||F|||20190702124426||||902M2SH00193[CR]OBR|11|1907020003|1907020003|NRBC#^NRBC#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|NRBC#^NRBC#||0.00|10\S\3/mm3|0.0 - 999.9||||F|||20190702124426||||902M2SH00193[CR]OBR|12|1907020003|1907020003|RBC^RBC^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|RBC^RBC||4.55|10\S\6/mm3|3.84 - 5.07||||F|||20190702124426||||902M2SH00193[CR]OBR|13|1907020003|1907020003|MPV^MPV^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|MPV^MPV||12.7|fL|8.7 - 11.0|H~HH|||F|||20190702124426||||902M2SH00193[CR]OBR|14|1907020003|1907020003|MON#^MON#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|MON#^MON#||0.62|10\S\3/mm3|0.19 - 1.15||||F|||20190702124426||||902M2SH00193[CR]OBR|15|1907020003|1907020003|WBC^WBC^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|WBC^WBC||8.64|10\S\3/mm3|4.86 - 13.51||||F|||20190702124426||||902M2SH00193[CR]OBR|16|1907020003|1907020003|PLT^PLT^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|PLT^PLT||250|10\S\3/mm3|189.0 - 459.0||||F|||20190702124426||||902M2SH00193[CR]NTE|1||PLT interference[CR]OBR|17|1907020003|1907020003|IML#^IML#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|IML#^IML#||0.00|10\S\3/mm3|0.0 - 99.99||||F|||20190702124426||||902M2SH00193[CR]OBR|18|1907020003|1907020003|LIC%^LIC%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|LIC%^LIC%||0.4|%|0.0 - 3.0||||F|||20190702124426||||902M2SH00193[CR]OBR|19|1907020003|1907020003|MON%^MON%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|MON%^MON%||7.2|%|0.0 - 999.9||||F|||20190702124426||||902M2SH00193[CR]OBR|20|1907020003|1907020003|LIC#^LIC#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|LIC#^LIC#||0.03|10\S\3/mm3|0.0 - 99.99||||F|||20190702124426||||902M2SH00193[CR]OBR|21|1907020003|1907020003|IML%^IML%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|IML%^IML%||0.0|%|0.0 - 2.0||||F|||20190702124426||||902M2SH00193[CR]OBR|22|1907020003|1907020003|LYM#^LYM#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|LYM#^LYM#||3.18|10\S\3/mm3|1.13 - 8.09||||F|||20190702124426||||902M2SH00193[CR]OBR|23|1907020003|1907020003|HGB^HGB^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|HGB^HGB||12.4|g/dL|10.1 - 12.7||||F|||20190702124426||||902M2SH00193[CR]OBR|24|1907020003|1907020003|PDW^PDW^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|PDW^PDW||26.5|fL|11.0 - 18.0|H~HH|||F|||20190702124426||||902M2SH00193[CR]NTE|1||Instrument flag[CR]OBR|25|1907020003|1907020003|LYM%^LYM%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|LYM%^LYM%||36.8|%|0.0 - 999.9||||F|||20190702124426||||902M2SH00193[CR]OBR|26|1907020003|1907020003|RDW-SD^RDW-SD^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|RDW-SD^RDW-SD||39.2|fL|37.0 - 56.0||||F|||20190702124426||||902M2SH00193[CR]OBR|27|1907020003|1907020003|BAS%^BAS%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|BAS%^BAS%||1.0|%|0.0 - 999.9||||F|||20190702124426||||902M2SH00193[CR]OBR|28|1907020003|1907020003|NRBC%^NRBC%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|NRBC%^NRBC%||0.0|%|0.0 - 2.0||||F|||20190702124426||||902M2SH00193[CR]OBR|29|1907020003|1907020003|BAS#^BAS#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|BAS#^BAS#||0.09|10\S\3/mm3|0.01 - 0.06|H~HH|||F|||20190702124426||||902M2SH00193[CR]NTE|1||Panic value[CR]NTE|2||Basophilia[CR]OBR|30|1907020003|1907020003|MCH^MCH^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|MCH^MCH||27.2|pg|22.7 - 28.6||||F|||20190702124426||||902M2SH00193[CR]OBR|31|1907020003|1907020003|MAC%^MAC%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|MAC%^MAC%||1.6|%|0.0 - 20.0||||F|||20190702124426||||902M2SH00193[CR]OBR|32|1907020003|1907020003|MCHC^MCHC^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|MCHC^MCHC||34.0|g/dL|31.6 - 34.7||||F|||20190702124426||||902M2SH00193[CR]OBR|33|1907020003|1907020003|HCT^HCT^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|HCT^HCT||36.4|%|30.8 - 37.9||||F|||20190702124426||||902M2SH00193[CR]OBR|34|1907020003|1907020003|IMM#^IMM#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|IMM#^IMM#||0.02|10\S\3/mm3|0.0 - 99.99||||F|||20190702124426||||902M2SH00193[CR]OBR|35|1907020003|1907020003|MIC%^MIC%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|MIC%^MIC%||9.6|%|0.0 - 20.0||||F|||20190702124426||||902M2SH00193[CR]OBR|36|1907020003|1907020003|EOS#^EOS#^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|EOS#^EOS#||0.55|10\S\3/mm3|0.02 - 0.82||||F|||20190702124426||||902M2SH00193[CR]OBR|37|1907020003|1907020003|EOS%^EOS%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|EOS%^EOS%||6.4|%|0.0 - 999.9||||F|||20190702124426||||902M2SH00193[CR]NTE|1||Post association[CR]OBR|38|1907020003|1907020003|IMM%^IMM%^HALIA|||20190702115043||||||||||||||||||F|||||||LABO[CR]ORC|SC|1907020003|1907020003|1907020003|A||||20190702124727||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|NM|IMM%^IMM%||0.3|%|0.0 - 2.0||||F|||20190702124426||||902M2SH00193[CR]OBR|39|1907020003|1907020003|NEU%^NEU%^HALIA|||20190702115043||||||||||||||||||F|||||||[CR]ORC|SC|1907020003|1907020003|1907020003|A||||||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R",
			"OBX|1|ED|NEU%^NEU%||^IM^PNG^Base64^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|||N|||F|||20190702124142||||902M2SH00193",
			"OBR|40|1907020003|1907020003|RBC^RBC^HALIA|||20190702115043||||||||||||||||||F|||||||",
			"ORC|SC|1907020003|1907020003|1907020003|A||||||||W00003||||||||WARD00003^^^^^^^^^W00003",
			"TQ1|||||||20190702115043||R"
	};
	
	private static String resultsFromYumizenP8000[] = {
	"MSH|^~\\&|YP8K|^^|^^|^^|20160705100955||OUL^R22^OUL_R22|YP8K20160705100955|P|2.5|||||||",
	"PID|||P0002^^^LIS^PI||DOE^JOHN^^^^^||19601206|M|||Main Street^^Springfield^NY^65466^USA^ATC1|||||||ABC123|||||||||||||Y",
	"PV1||N|EMERGENCY^ROOM1^BED1||||ATD^DR HOUSE||||||||||^||ABC123|||||||||||||||||||||||||20160416090430|20160416090430",
	"SPM|1|201604163002||EDTA||||MAIN LAB",
	"OBR|1|L604163002|L604163002|RDW-SD^RDW-SD^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|RDW-SD^RDW-SD||45.0|fl|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|2|L604163002|L604163002|LIC#^LIC#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|LIC#^LIC#||0.01|10^3/mm3|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|3|L604163002|L604163002|LIC%^LIC%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|LIC%^LIC%||0.1|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|4|L604163002|L604163002|BAS#^BAS#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|BAS#^BAS#||0.00|10^3/mm3|0.0 - 0.2||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|5|L604163002|L604163002|ALY%^ALY%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|ALY%^ALY%||0.0|%|0.0 - 2.5||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|6|L604163002|L604163002|ALY#^ALY#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|ALY#^ALY#||0.00|10^3/mm3|0.0 - 0.25||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|7|L604163002|L604163002|EOS#^EOS#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|EOS#^EOS#||0.26|10^3/mm3|0.0 - 0.5||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|8|L604163002|L604163002|BAS%^BAS%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|BAS%^BAS%||0.0|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|9|L604163002|L604163002|PDW^PDW^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|PDW^PDW||12.7|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|10|L604163002|L604163002|MON%^MON%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MON%^MON%||6.6|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|11|L604163002|L604163002|EOS%^EOS%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|EOS%^EOS%||3.7|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|12|L604163002|L604163002|MCV^MCV^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MCV^MCV||095|?m3|82.0 - 98.0||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|13|L604163002|L604163002|MON#^MON#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MON#^MON#||0.46|10^3/mm3|0.2 - 0.8||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|14|L604163002|L604163002|IML%^IML%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|IML%^IML%||0.0|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|15|L604163002|L604163002|IMG%^IMG%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|IMG%^IMG%||0.0|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|16|L604163002|L604163002|IMG#^IMG#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|IMG#^IMG#||0.00|10^3/mm3|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|17|L604163002|L604163002|IML#^IML#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|IML#^IML#||0.00|10^3/mm3|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|18|L604163002|L604163002|LYM#^LYM#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|LYM#^LYM#||1.95|10^3/mm3|1.0 - 4.0||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|19|L604163002|L604163002|MCH^MCH^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MCH^MCH||30.0|pg|27.0 - 32.0||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|20|L604163002|L604163002|MIC%^MIC%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MIC%^MIC%||0.2|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|21|L604163002|L604163002|LYM%^LYM%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|LYM%^LYM%||28.3|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"NTE|1||Lymphoproliferative disorder or viral infection suspicion",
	"OBR|22|L604163002|L604163002|MPV^MPV^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MPV^MPV||08.5|?m3|7.4 - 10.8||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|23|L604163002|L604163002|MCHC^MCHC^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MCHC^MCHC||33|g/dL|32.0 - 36.3||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|24|L604163002|L604163002|NEU#^NEU#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|NEU#^NEU#||4.21|10^3/mm3|1.5 - 7.0||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|25|L604163002|L604163002|HCT^HCT^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|HCT^HCT||45.2|%|40.0 - 54.0||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|26|L604163002|L604163002|HGB^HGB^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|HGB^HGB||15|g/dL|13.0 - 17.0||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|27|L604163002|L604163002|NEU%^NEU%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|NEU%^NEU%||61.2|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|28|L604163002|L604163002|IMM#^IMM#^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|IMM#^IMM#||0.00|10^3/mm3|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|29|L604163002|L604163002|PCT^PCT^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|PCT^PCT||0.383|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|30|L604163002|L604163002|IMM%^IMM%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|IMM%^IMM%||0.0|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|31|L604163002|L604163002|MAC%^MAC%^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|MAC%^MAC%||29.0|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|32|L604163002|L604163002|RBC^RBC^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|RBC^RBC||4.72|10^6/mm3|4.5 - 6.5||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|33|L604163002|L604163002|RDW-CV^RDW-CV^YP8K|||20160416090400|||||||||DR	HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|RDW-CV^RDW-CV||14.8|%|||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|34|L604163002|L604163002|PLT^PLT^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|PLT^PLT||283|10^3/mm3|150.0 - 400.0||||F|||20160705100630||||Yumizen H2500-SPS",
	"OBR|35|L604163002|L604163002|WBC^WBC^YP8K|||20160416090400|||||||||DR HOUSE|||||||||F|||||||ruleResult",
	"ORC|SC|L604163002|L604163002|L604163002|A||||20160705100647||||||||||||",
	"TQ1|||||||20160416090430||S",
	"OBX|1|NM|WBC^WBC||6.98|10^3/mm3|4.0 - 10.0||||F|||20160705100630||||Yumizen H2500-SPS"
	};
}
