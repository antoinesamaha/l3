package b01.tests;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Properties;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.admin.FocGroup;
import b01.foc.gui.DisplayManager;
import b01.foc.menu.FMenuList;
import b01.l3.DriverFactory;
import b01.l3.FileIOFactory;
import b01.l3.FocAppGroup;
import b01.l3.Instrument;
import b01.l3.L3KernelModule;
import b01.l3.L3Main;
import b01.l3.connection.socket.SocketPort;
import b01.l3.connector.LisConnectorFactory;
import b01.l3.connector.dbConnector.lisConnectorTables.LisConnectorModule;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3Test;
import b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver;
import b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Emulator;
import b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Frame;
import b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Receiver;

public class MainTester_YumizenP8000 {
	
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

	/**
	 * This is the Official main method
	 * @param args
	 */
	public static void main(String[] args) {
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
			
			testYumizenP8000();
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
		
	private static void testYumizenP8000() throws Exception {
		Properties props = new Properties();
		props.put("serialPort.name", "10002");
		SocketPort serverPort = new SocketPort(props);
		serverPort.openConnection();
//		serverPort.addEventListener(nw );
		
		//Preparation of the Instrument Driver and Frame
		props = new Properties();
		props.put("instrument.code","YP8K");
		props.put("instrument.name","YP8K");
		props.put("instrument.driver", "b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver");
		props.put("serialPort.name", "10002");
		Instrument instr = new Instrument(props);
		YumizenP8000Driver   driver   = (YumizenP8000Driver) instr.getDriver();
		//----------------------------------------------
		
		//Preparation of the Instrument Driver and Frame
		props = new Properties();
		props.put("instrument.code","YP8K_Emul");
		props.put("instrument.name","YP8K_Emul");
		props.put("instrument.driver", "b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Emulator");
		props.put("serialPort.name", "10002");
		Instrument instrEmul = new Instrument(props);
		YumizenP8000Emulator emmul = (YumizenP8000Emulator) instrEmul.getDriver();
		//----------------------------------------------
		
		L3Message message = new L3Message();
		L3Sample sample = new L3Sample("123123");
		sample.setFirstName("Antoine");
		sample.setLastName("Samaha");
		message.addSample(sample);
		L3Test test = sample.addTest();
		test.setLabel("RDW-SD");
		
		driver.send(message);
	}
	
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
