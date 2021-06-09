package b01;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.admin.FocGroup;
import b01.foc.gui.DisplayManager;
import b01.foc.menu.FMenuList;
import b01.l3.DriverFactory;
import b01.l3.FileIOFactory;
import b01.l3.FocAppGroup;
import b01.l3.L3Main;
import b01.l3.L3KernelModule;
import b01.l3.connector.LisConnectorFactory;
import b01.l3.connector.dbConnector.lisConnectorTables.LisConnectorModule;

public class Main {

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

			FileIOFactory.getInstance().addFileIO("b01.l3.connector.fileConnector.DefaultFileIO", b01.l3.connector.fileConnector.DefaultFileIO.class);

			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.fileConnector.LisFileConnector", b01.l3.connector.fileConnector.LisFileConnector.class);
			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.dbConnector.LisDBConnector", b01.l3.connector.dbConnector.LisDBConnector.class);

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
			Globals.logString("Build date : 04/11/2018");

			l3Main.executeBackgroundTasks();
			
			/*
			int idx=0; 
			while(true){ 
				Thread.sleep(1000);
				Globals.logString("Looping ..."+idx); 
				idx++; 
			}
			*/
		} catch (Exception e) {
			Globals.logException(e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public static void main(String[] args) {
		for(int i=0; i<255; i++){
			System.out.println("c["+i+"]="+((char)i));
		}
	}
	*/	
	
	
	
	/**
	 * 	 * TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING
	 * 	 * TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING
	 * 	 * TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING
	 * 	 * TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING TESTING
	 * @param args
	 */
	public static void mainTesting(String[] args) {
		try {
			// Globals.logString("Loading...");
			L3Main l3Main = new L3Main(args);
			Application app = l3Main.newApplicationVersionAndModuleDeclaration(args);
			FocGroup.setApplicationGroup(FocAppGroup.getFocDesc(), 1);
			app.declareModule(new LisConnectorModule());

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

			DriverFactory.getInstance().addDriver("b01.l3.drivers.hitachi.hitachi912.Hitachi912Driver", b01.l3.drivers.hitachi.hitachi912.Hitachi912Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.hitachi.hitachi912.Hitachi912Emulator", b01.l3.drivers.hitachi.hitachi912.Hitachi912Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.helena.junior24.Junior24Driver", b01.l3.drivers.helena.junior24.Junior24Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.helena.junior24.Junior24Emulator", b01.l3.drivers.helena.junior24.Junior24Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.bectonDickinson.sedi15.Sedi15Driver", b01.l3.drivers.bectonDickinson.sedi15.Sedi15Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.bectonDickinson.sedi15.Sedi15Emulator", b01.l3.drivers.bectonDickinson.sedi15.Sedi15Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.dadeBehring.bct.BCTDriver", b01.l3.drivers.dadeBehring.bct.BCTDriver.class);

			FileIOFactory.getInstance().addFileIO("b01.l3.connector.fileConnector.DefaultFileIO", b01.l3.connector.fileConnector.DefaultFileIO.class);

			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.fileConnector.LisFileConnector", b01.l3.connector.fileConnector.LisFileConnector.class);
			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.dbConnector.LisDBConnector", b01.l3.connector.dbConnector.LisDBConnector.class);

			app.login(null, true);

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
			Globals.logString("Build date : 03/06/2015");

			l3Main.executeBackgroundTasks();
			/*
			 * int idx=0; while(true){ Thread.sleep(1000);
			 * Globals.logString("Looping ..."+idx); idx++; }
			 */
		} catch (Exception e) {
			Globals.logException(e);
		}
	}

	
	
	
	
	
}
