package b01.tests;

import java.util.Properties;

import b01.foc.Application;
import b01.foc.Globals;
import b01.foc.admin.FocGroup;
import b01.l3.DriverFactory;
import b01.l3.FileIOFactory;
import b01.l3.FocAppGroup;
import b01.l3.Instrument;
import b01.l3.InstrumentDesc;
import b01.l3.L3Main;
import b01.l3.connector.LisConnectorFactory;
import b01.l3.connector.dbConnector.lisConnectorTables.LisConnectorModule;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.vitek.bci.VitekBCIDriver;
import b01.l3.drivers.vitek.bci.VitekBCIFrame;
import b01.l3.drivers.vitek.bci.VitekBCIReceiver;

public class MainTesterViteck2 {

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

			DriverFactory.getInstance().addDriver("b01.l3.drivers.vitek.bci.VitekBCIDriver", b01.l3.drivers.vitek.bci.VitekBCIDriver.class);
			DriverFactory.getInstance().addDriver("b01.l3.drivers.vitek.bci.VitekBCIEmulator", b01.l3.drivers.vitek.bci.VitekBCIEmulator.class);

			FileIOFactory.getInstance().addFileIO("b01.l3.connector.fileConnector.DefaultFileIO", b01.l3.connector.fileConnector.DefaultFileIO.class);

			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.fileConnector.LisFileConnector", b01.l3.connector.fileConnector.LisFileConnector.class);
			LisConnectorFactory.getInstance().addLisConnector("b01.l3.connector.dbConnector.LisDBConnector", b01.l3.connector.dbConnector.LisDBConnector.class);

			//This is where we prepare the Driver and start listening
			
			//Preparation of the Instrument Driver and Frame
			Properties props = new Properties();
			props.put("instrument.code","VITEK2");
			props.put("instrument.name","VITEK2");
//			props.put("instrument.driver", "b01.l3.drivers.roches.cobas.u601701.CobasU601701Driver");
			props.put("instrument.driver", "b01.l3.drivers.vitek.bci.VitekBCIDriver");
			Instrument instr = new Instrument(props);
			instr.setPropertyString(InstrumentDesc.FLD_SERIAL_PORT_NAME, "9999");
			VitekBCIDriver driver = (VitekBCIDriver) instr.getDriver();
//			CobasU601701Driver driver = (CobasU601701Driver) instr.getDriver();

			//Prepare a message And Send It
			//-----------------------------
			/*
			L3Message message = new L3Message();
			L3Sample sample = new L3Sample("11111");
			sample.setPatientId("22222");
			sample.setSexe("M");
			sample.setFirstName("Testing");
			sample.setLastName("Tests");
			sample.setMiddleInitial("N");
			message.addSample(sample);
			L3Test test = sample.addTest();
			test.setLabel("an");
			driver.send(message);
			*/
			
			//Loop if you want to wait for reception of results from port
			//-----------------------------------------------------------
			/*
			driver.connect();
			Thread.sleep(4000);

			int idx=0; 
			while(true){
				Thread.sleep(1000);
				Globals.logString("Looping ..."+idx); 
				idx++; 
			}
			*/

			//If you want to Simulate a reception of results using one frame only
			//-------------------------------------------------------------------
			/*
			VitekBCIReceiver receiver = (VitekBCIReceiver) driver.getDriverReceiver();
			VitekBCIFrame    frame    = new VitekBCIFrame(instr);

			frame.setDataWithFrame(new StringBuffer(resultsFromVitec2));
			frame.extractDataFromConcatenatedFrame();
			receiver.unitTesting_treatResultFrame(frame);
			receiver.unitTesting_PrintMessage();
			*/
  		//-------------------------------------------------------------------
			
			//If you want to Simulate a reception of multiple frames
			//------------------------------------------------------
			VitekBCIReceiver receiver = (VitekBCIReceiver) driver.getDriverReceiver();
			VitekBCIFrame    frame    = new VitekBCIFrame(instr);

			frame.setDataWithFrame(new StringBuffer(resultsFromVitec_Array[0]));
			receiver.received(frame);
			
			frame.setDataWithFrame(new StringBuffer(resultsFromVitec_Array[1]));
			receiver.received(frame);
			
			frame.setDataWithFrame(new StringBuffer(resultsFromVitec_Array[2]));
			receiver.received(frame);
			//------------------------------------------------------
			
//			
//			extractionDone = frame.extractAnswerFromBuffer(buffer);
//			if(extractionDone) receiver.received(frame);
//			for(int i=0; i<resultsFromVitec2.length; i++) {
//				String message = resultsFromVitec2[i];
//				buffer = new StringBuffer(message);
//				buffer.append(YumizenP8000Frame.CR);
//				extractionDone = frame.extractAnswerFromBuffer(buffer);
//				if(extractionDone) receiver.received(frame);
//			}
//			buffer = new StringBuffer();
//			buffer.append(YumizenP8000Frame.FS);
//			buffer.append(YumizenP8000Frame.CR);
//			extractionDone = frame.extractAnswerFromBuffer(buffer);
//			if(extractionDone) receiver.received(frame);

//OBX|1|ED|RBC^RBC||^IM^PNG^Base64^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|||N|||F|||20190702124143||||902M2SH00193[CR]OBR|41|1907020003|1907020003|PLT^PLT^HALIA|||20190702115043||||||||||||||||||F|||||||[CR]ORC|SC|1907020003|1907020003|1907020003|A||||||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|ED|PLT^PLT||^IM^PNG^Base64^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|||N|||F|||20190702124143||||902M2SH00193[CR]NTE|1||PLT interference[CR]OBR|42|1907020003|1907020003|BAS%^BAS%^HALIA|||20190702115043||||||||||||||||||F|||||||[CR]ORC|SC|1907020003|1907020003|1907020003|A||||||||W00003||||||||WARD00003^^^^^^^^^W00003[CR]TQ1|||||||20190702115043||R[CR]OBX|1|ED|BAS%^BAS%||^IM^PNG^Base64^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|||N|||F|||20190702124143||||902M2SH00193[CR][FS][CR]


			
			
			
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

	private static String resultsFromVitec2 = "mtrsl|iiV2|is00001791932C|itSU|pi07082018|pnTaouk, Halim|si|s01062|s110/15/2019|s310/15/2019|ci07082018|c01062|ctANY|cnANY|ta|rtAST-N204|rr366547|t11|o1klepne|o2Klebsiella pneumoniae ssp pneumoniae|afQUINOLONES|apDECREASED SUSCEPTIBILITY|apWILD|afFOSFOMYCIN|apRESISTANT|afBETA-LACTAMS|apWILD (PENICILLINASE)|apACQUIRED PENICILLINASE|afFURANES|apWILD|afAMINOGLYCOSIDES|apWILD|afTRIMETHOPRIM/SULFONAMIDES|apTRIMETHOPRIM RESISTANT|apWILD|ra|a1esbl|a2ESBL|a3Neg|a4-|an-|ra|a1am|a2Ampicillin|a3>=32|a4R|anR|ra|a1amc|a2Amoxicillin/Clavulanic Acid|a34|a4S|anS|ra|a1tzp|a2Piperacillin/Tazobactam|a3<=4|a4S|anS|ra|a1tax|a2Cefotaxime|a3<=1|a4S|anS|ra|a1taz|a2Ceftazidime|a3<=1|a4S|anS|ra|a1fep|a2Cefepime|a3<=1|a4S|anS|ra|a1etp|a2Ertapenem|a3<=0.5|a4S|anS|ra|a1imi|a2Imipenem|a3<=0.25|a4S|anS|ra|a1mem|a2Meropenem|a3<=0.25|a4S|anS|ra|a1an|a2Amikacin|a3<=2|a4S|anS|ra|a1gm|a2Gentamicin|a3<=1|a4S|anS|ra|a1cip|a2Ciprofloxacin|a3<=0.25|a4S|anS|ra|a1nor|a2Norfloxacin|a3<=0.5|a4S|anS|ra|a1fos|a2Fosfomycin|a3>=256|a4R|anR|ra|a1ftn|a2Nitrofurantoin|a3<=16|a4S|anS|ra|a1sxt|a2Trimethoprim/Sulfamethoxazole|a3<=20|a4S|anS|zz|";
	
	private static String resultsFromVitec_Array[] = {
			String.valueOf(AstmFrame.ENQ),
			String.valueOf(AstmFrame.STX)+"1mtrsl|iiV2|is00001791932C|itSU|pi07082018|pnTaouk, Halim|si|s01062|s110/15/2019|s310/15/2019|ci07082018|c01062|ctANY|cnANY|ta|rtAST-N204|rr366547|t11|o1klepne|o2Klebsiella pneumoniae ssp pneumoniae|afQUINOLONES|apDECREASED SUSCEPTIBILITY|ap"+String.valueOf(AstmFrame.ETB)+"5A"+String.valueOf(AstmFrame.CR)+String.valueOf(AstmFrame.LF),
			String.valueOf(AstmFrame.STX)+"2WILD|afFOSFOMYCIN|apRESISTANT|afBETA-LACTAMS|apWILD (PENICILLINASE)|apACQUIRED PENICILLINASE|afFURANES|apWILD|afAMINOGLYCOSIDES|apWILD|afTRIMETHOPRIM/SULFONAMIDES|apTRIMETHOPRIM RESISTANT|apWILD|ra|a1esbl|a2ESBL|a3Neg|a4-|an-|ra|a1am|a2Ampi"+String.valueOf(AstmFrame.ETB)+"3F"+String.valueOf(AstmFrame.CR)+String.valueOf(AstmFrame.LF),
			String.valueOf(AstmFrame.STX)+"3cillin|a3>=32|a4R|anR|ra|a1amc|a2Amoxicillin/Clavulanic Acid|a34|a4S|anS|ra|a1tzp|a2Piperacillin/Tazobactam|a3<=4|a4S|anS|ra|a1tax|a2Cefotaxime|a3<=1|a4S|anS|ra|a1taz|a2Ceftazidime|a3<=1|a4S|anS|ra|a1fep|a2Cefepime|a3<=1|a4S|anS|ra|a1etp|a2"+String.valueOf(AstmFrame.ETB)+"F9"+String.valueOf(AstmFrame.CR)+String.valueOf(AstmFrame.LF)
	};
}
