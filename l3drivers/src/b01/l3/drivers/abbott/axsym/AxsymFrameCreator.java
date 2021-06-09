package b01.l3.drivers.abbott.axsym;

import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

public class AxsymFrameCreator extends AstmFrameCreator {
	public AstmFrame newPatientFrame (Instrument instrument, int sequence, int sequence_num, String patientId, String firstName, String lastName, String middleName){
		String concatLastName = lastName;
		if(firstName != null && firstName.length() > 0){
			concatLastName = concatLastName + " " + firstName.charAt(0);
		}
		return super.newPatientFrame (instrument, sequence, sequence_num, patientId, firstName, concatLastName, middleName);
	}
}
