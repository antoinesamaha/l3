package b01.l3.drivers.cs2500;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import b01.foc.Globals;
import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

public class CS2500FrameCreator extends AstmFrameCreator {

	public AstmFrame newHeaderFrame(Instrument instrument, int sequence) {
		AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_HEADER);

		frame.append2Data(AstmFrame.FIELD_SEPERATOR);

		frame.append2Data(AstmFrame.REPEAT_DELIMITER);
		frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
		frame.append2Data(AstmFrame.ESCAPE_DELIMITER);

		for (int i = 0; i < 10; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}

		// frame.append2Data('P');
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data("E1394-97");

		return frame;
	}

	public AstmFrame newOrderFrame(Instrument instrument, int sequence, int sequence_num, String specimen,
			String testId, ArrayList<String> testArrayList, Date collectionDate, String priority) throws Exception {
		AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_ORDER);

		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data(String.valueOf(sequence_num));
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		{		
			frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
			frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
			for(int i=0; i<15-specimen.length(); i++) {
				frame.append2Data(" ");
			}
			frame.append2Data(specimen, 15);
			frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
			frame.append2Data("B");
		}
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		//frame.append2Data(specimen, 15);
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);

		if (testArrayList != null) {
			for (int i = 0; i < testArrayList.size(); i++) {
				if (i > 0)
					frame.append2Data(AstmFrame.REPEAT_DELIMITER);
				appendASingleTest(frame, testArrayList.get(i), getTestCodeLength(instrument));
			}

		} else {
			String instrCode = ((DriverSerialPort) instrument.getDriver()).testMaps_getInstCode(testId);
			appendASingleTest(frame, instrCode, getTestCodeLength(instrument));
//		    	for (int i=0; i<3; i++){
//		    		frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
//		    	}
//		    	String instrCode = ((DriverSerialPort)instrument.getDriver()).testMaps_getInstCode(testId);
//		    	frame.append2Data(instrCode, getTestCodeLength(instrument));    	
		}

		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		if (priority != null)
			frame.append2Data(priority, 1);
		for (int i = 0; i < 2; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if (collectionDate.getTime() < 24 * 60 * 60 * 1000) {
			frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
		} else {
			frame.append2Data(sdf.format(collectionDate));
		}

		for (int i = 0; i < 4; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}
		frame.append2Data('N');
		for (int i = 0; i < 14; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}
		frame.append2Data('O');
		return frame;
	}

}
