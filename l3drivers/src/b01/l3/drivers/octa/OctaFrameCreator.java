package b01.l3.drivers.octa;

import b01.l3.Instrument;
import b01.l3.data.L3Message;
import b01.l3.data.L3Sample;
import b01.l3.data.L3SampleTestJoinFilter;
import b01.l3.drivers.astm.AstmFrame;
import b01.l3.drivers.astm.AstmFrameCreator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
public class OctaFrameCreator extends AstmFrameCreator {

    private static final int FRAME_SIZE = 240;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private String appendSpaces(String str, int length) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.insert(0, " ");
        }
        if (sb.length() > length) {
            sb.setLength(length); // Trim to length if it exceeds
        }
        return sb.toString();
    }

    public int computeAge(Date dateOfBirth) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public String buildAckFrame() {
        return String.valueOf(AstmFrame.STX) + String.valueOf(AstmFrame.ACK) + String.valueOf(AstmFrame.ETX);
    }

    public String buildNAckFrame() {
        return String.valueOf(AstmFrame.STX) + String.valueOf(AstmFrame.ACK) + String.valueOf(AstmFrame.ETX);
    }

    public String buildEotFrame() {
        return String.valueOf(AstmFrame.STX) + String.valueOf(AstmFrame.EOT) + String.valueOf(AstmFrame.ETX);
    }

    public String buildOrderFrame(Instrument instrument, L3Message messageReadyToSend) {
        int size = messageReadyToSend != null ? messageReadyToSend.getNumberOfSamples() : 0;

        instrument.logString("Instrument : sendASampleAnsweringInquiry 3 size : " + size);

        if (size > 0) {
            instrument.logString("Instrument : sendASampleAnsweringInquiry 4 nbrSamples : " + messageReadyToSend.getNumberOfSamples());
            for (int i = 0; i < messageReadyToSend.getNumberOfSamples(); i++) {
                L3Sample sample = messageReadyToSend.getSample(i);
                //messageReadyToSend.getTestList().setSample(sample);

                StringBuffer messageToSend = new StringBuffer();
                messageToSend.append(AstmFrame.STX);
                messageToSend.append("@");
                messageToSend.append("0000");
                messageToSend.append(appendSpaces(sample.getId(), 15));
                String patientName = sample.getMiddleInitial() != null && sample.getMiddleInitial().length() > 0 ? sample.getFirstName() + " " + sample.getMiddleInitial() + " " + sample.getLastName() : sample.getFirstName() + " " +  sample.getLastName();
                messageToSend.append(appendSpaces(patientName, 30));
                messageToSend.append(sdf.format(sample.getDateOfBirth()));
                messageToSend.append(appendSpaces(sample.getSexe(), 1));

                //Age
                int age = computeAge(sample.getDateOfBirth());
                String ageStr = String.format("%03d", age); // e.g., "007"
                messageToSend.append(ageStr);

                //Department
                messageToSend.append("A2345678901234Z");
                messageToSend.append(sdf.format(sample.getDateAndTime()));
                //Concentration
                messageToSend.append("00000");
                messageToSend.append("                              "); // 30 spaces
                messageToSend.append("                              "); // 30 spaces
                messageToSend.append("                              "); // 30 spaces
                messageToSend.append("                              "); // 30 spaces
                messageToSend.append("                              "); // 30 spaces
                messageToSend.append(AstmFrame.ETX);

                // Read only one record and suppose we are doing the Hb1Ac test
                return messageToSend.toString();
            }
        } else {
            return buildEotFrame();
        }
        return null;
    }

}
