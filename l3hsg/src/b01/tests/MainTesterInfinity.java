package b01.tests;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.admin.FocGroup;
import b01.foc.gui.DisplayManager;
import b01.foc.menu.FMenuList;
import b01.l3.*;
import b01.l3.connector.LisConnectorFactory;
import b01.l3.connector.dbConnector.lisConnectorTables.LisConnectorModule;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.octa.OctaDriver;
import b01.l3.drivers.octa.OctaFrameCreator;

public class MainTesterInfinity {

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

			DriverFactory.getInstance().addDriver("b01.l3.drivers.cs2500.CS2500Driver", b01.l3.drivers.cs2500.CS2500Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.cs2500.CS2500Emulator", b01.l3.drivers.cs2500.CS2500Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.maglumi.MaglumiDriver", b01.l3.drivers.maglumi.MaglumiDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.maglumi.MaglumiEmulator", b01.l3.drivers.maglumi.MaglumiEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.octa.OctaDriver", OctaDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.octa.OctaEmulator", b01.l3.drivers.octa.OctaEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.alcor.ised.ISEDDriver", b01.l3.drivers.alcor.ised.ISEDDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.alcor.ised.ISEDEmulator", b01.l3.drivers.alcor.ised.ISEDEmulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver", b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Driver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Emulator", b01.l3.drivers.horiba.yumizenP8000.YumizenP8000Emulator.class);

			DriverFactory.getInstance().addDriver("b01.l3.drivers.vitek.bci.VitekBCIDriver", b01.l3.drivers.vitek.bci.VitekBCIDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.vitek.bci.VitekBCIEmulator", b01.l3.drivers.vitek.bci.VitekBCIEmulator.class);

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
//			Globals.logString("Build date : 10/03/2019");
			Globals.logString("Build date : 21/12/2022");

			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(5000);
						SocketSender sender = new SocketSender(9088);
						if (true) {// Simulating a reception of result
							String message = String.valueOf(AstmFrame.STX);
                            message += "@00085516027        Michel A Berbery              02031973M052HSGLABS             0107202578.50g/dl    FREE FIELD 1                  FREE FIELD 2                  FREE FIELD 3                  FREE FIELD 4                  FREE FIELD 5                  ADM0107202507HbA1c%    Other Hb AHb A0     Hb A2     HbA1c#                                                      005.4001.8091.1002.3036.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0                                        000.0000.0000.0000.0000.0000.0000.0000.0001.0001.00                                                                                                                                                                                                                                      00";
							message += AstmFrame.ETX;
							sender.send(message);
						}
						if (false) {// Simulating a reception of enquiry
							String message = String.valueOf(AstmFrame.STX);
							message += "@5492999        ";
							message += AstmFrame.ETX;
							sender.send(message);
							Thread.sleep(2000);
							OctaFrameCreator creator = new OctaFrameCreator();
							sender.send(creator.buildAckFrame());
							Thread.sleep(2000);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

			l3Main.executeBackgroundTasks();

			Instrument instrument = null;
			L3Application l3App = L3Application.getAppInstance();
			if(l3App != null){
				l3App.setBackgroundTask(L3Application.BACKGROUND_TASK_DRIVER);
				instrument = l3App.getBackgroundInstrument();
			}

			OctaDriver driver = (OctaDriver) instrument.getDriver();

		} catch (Exception e) {
			Globals.logException(e);
		}
	}

	private static String resultsFromVitec2 = "mtrsl|iiV2|is00001791932C|itSU|pi07082018|pnTaouk, Halim|si|s01062|s110/15/2019|s310/15/2019|ci07082018|c01062|ctANY|cnANY|ta|rtAST-N204|rr366547|t11|o1klepne|o2Klebsiella pneumoniae ssp pneumoniae|afQUINOLONES|apDECREASED SUSCEPTIBILITY|apWILD|afFOSFOMYCIN|apRESISTANT|afBETA-LACTAMS|apWILD (PENICILLINASE)|apACQUIRED PENICILLINASE|afFURANES|apWILD|afAMINOGLYCOSIDES|apWILD|afTRIMETHOPRIM/SULFONAMIDES|apTRIMETHOPRIM RESISTANT|apWILD|ra|a1esbl|a2ESBL|a3Neg|a4-|an-|ra|a1am|a2Ampicillin|a3>=32|a4R|anR|ra|a1amc|a2Amoxicillin/Clavulanic Acid|a34|a4S|anS|ra|a1tzp|a2Piperacillin/Tazobactam|a3<=4|a4S|anS|ra|a1tax|a2Cefotaxime|a3<=1|a4S|anS|ra|a1taz|a2Ceftazidime|a3<=1|a4S|anS|ra|a1fep|a2Cefepime|a3<=1|a4S|anS|ra|a1etp|a2Ertapenem|a3<=0.5|a4S|anS|ra|a1imi|a2Imipenem|a3<=0.25|a4S|anS|ra|a1mem|a2Meropenem|a3<=0.25|a4S|anS|ra|a1an|a2Amikacin|a3<=2|a4S|anS|ra|a1gm|a2Gentamicin|a3<=1|a4S|anS|ra|a1cip|a2Ciprofloxacin|a3<=0.25|a4S|anS|ra|a1nor|a2Norfloxacin|a3<=0.5|a4S|anS|ra|a1fos|a2Fosfomycin|a3>=256|a4R|anR|ra|a1ftn|a2Nitrofurantoin|a3<=16|a4S|anS|ra|a1sxt|a2Trimethoprim/Sulfamethoxazole|a3<=20|a4S|anS|zz|";
	
	private static String resultsFromVitec_Array[] = {
			String.valueOf(AstmFrame.ENQ),
			String.valueOf(AstmFrame.STX)+"1mtrsl|iiV2|is00001791932C|itSU|pi07082018|pnTaouk, Halim|si|s01062|s110/15/2019|s310/15/2019|ci07082018|c01062|ctANY|cnANY|ta|rtAST-N204|rr366547|t11|o1klepne|o2Klebsiella pneumoniae ssp pneumoniae|afQUINOLONES|apDECREASED SUSCEPTIBILITY|ap"+String.valueOf(AstmFrame.ETB)+"5A"+String.valueOf(AstmFrame.CR)+String.valueOf(AstmFrame.LF),
			String.valueOf(AstmFrame.STX)+"2WILD|afFOSFOMYCIN|apRESISTANT|afBETA-LACTAMS|apWILD (PENICILLINASE)|apACQUIRED PENICILLINASE|afFURANES|apWILD|afAMINOGLYCOSIDES|apWILD|afTRIMETHOPRIM/SULFONAMIDES|apTRIMETHOPRIM RESISTANT|apWILD|ra|a1esbl|a2ESBL|a3Neg|a4-|an-|ra|a1am|a2Ampi"+String.valueOf(AstmFrame.ETB)+"3F"+String.valueOf(AstmFrame.CR)+String.valueOf(AstmFrame.LF),
			String.valueOf(AstmFrame.STX)+"3cillin|a3>=32|a4R|anR|ra|a1amc|a2Amoxicillin/Clavulanic Acid|a34|a4S|anS|ra|a1tzp|a2Piperacillin/Tazobactam|a3<=4|a4S|anS|ra|a1tax|a2Cefotaxime|a3<=1|a4S|anS|ra|a1taz|a2Ceftazidime|a3<=1|a4S|anS|ra|a1fep|a2Cefepime|a3<=1|a4S|anS|ra|a1etp|a2"+String.valueOf(AstmFrame.ETB)+"F9"+String.valueOf(AstmFrame.CR)+String.valueOf(AstmFrame.LF)
	};

	/* OCTA REAL RESULT
[STX]J00175477167[.][.][.][.][.][.][.][.]MELISSA[.]NARMIN[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]02031952F073SERUM[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]1405202569.00g/dl[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]ADM2005202506Albumin[.][.][.]Alpha[.]1[.][.][.]Alpha[.]2[.][.][.]Beta[.]1[.][.][.][.]Beta[.]2[.][.][.][.]Gamma[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]058.8004.6013.7009.0005.2008.70000000000000000000040.5703.1709.4506.2103.5906.0000000000000000000000SPIKE[.]1[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]002.500000000000000001.73000000000000000001.4301.00[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]00[ETX]
	
[STX]J00175477167        MALISSA NARMIN                02031952F073SERUM               1405202569.00g/dl                                                                                                                                                          ADM2005202506Albumin   Alpha 1   Alpha 2   Beta 1    Beta 2    Gamma                                             058.8004.6013.7009.0005.2008.70000000000000000000040.5703.1709.4506.2103.5906.0000000000000000000000SPIKE 1                                 002.500000000000000001.73000000000000000001.4301.00                                                                                                                                                                                                                                      00[ETX]


[STX]@00095492848[.][.][.][.][.][.][.][.]test[.]9[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]01011980M045[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]00000g/dl[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]TS[.]0206202507HbA1c%[.][.][.][.]Other[.]Hb[.]AHb[.]A0[.][.][.][.][.]Hb[.]A2[.][.][.][.][.]HbA1c#[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]005.2001.6092.0002.0033.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]000.0000.0000.0000.0000.0000.0000.0000.0001.0001.00[.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.][.]00[EOT]
  @00095492848        test 9                        01011980M045                            00000g/dl                                                                                                                                                          TS 0206202507HbA1c%    Other Hb AHb A0     Hb A2     HbA1c#                                                      005.2001.6092.0002.0033.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0000.0                                        000.0000.0000.0000.0000.0000.0000.0000.0001.0001.00                                                                                                                                                                                                                                      00[EOT]

	 */

}
