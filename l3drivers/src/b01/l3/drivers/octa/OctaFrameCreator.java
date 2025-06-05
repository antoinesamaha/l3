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

    public String buildOrderFrame(Instrument instrument, String sampleId) {
        L3SampleTestJoinFilter filter = instrument.getSampleListToSendAfterEnquiry(sampleId);
        filter.setActive(true);

        instrument.logString("Instrument : sendASampleAnsweringInquiry 2 ");

        L3Message messageReadyToSend = filter.convertToMessage();
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
                messageToSend.append("0001");
                messageToSend.append(appendSpaces(sampleId, 15));
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
        }
        return null;
    }

    /*
    public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {

        // ENQ frame
        // -----------
        AstmFrame frame = newEnquiryFrame(driver.getInstrument());
        driver.addFrame(frame);
        // -----------

        // BIG frame
        frame = new AstmFrame(driver.getInstrument(), 1, AstmFrame.FRAME_TYPE_CONTINUITY);

        frame.append2Data("H|\\^&|||host^2||||||TSDWN^BATCH");
        frame.append2Data(AstmFrame.CR);

        // Patient Frame
        Iterator sIter = message.sampleIterator();
        while (sIter != null && sIter.hasNext()) {
            L3Sample sam = (L3Sample) sIter.next();
            if (sam != null) {
                //Patient
                frame.append2Data("P|1||PatID|||||");
                if(sam.getSexe().compareTo("F") == 0){
                    frame.append2Data("F||||||");
                }else{
                    frame.append2Data("M||||||");
                }
                if(sam.getAge() > 0 && sam.getAge() < 150){
                    frame.append2Data(sam.getAge()+"^Y");
                }else{
                    frame.append2Data("^Y");
                }
                //-------
                frame.append2Data(AstmFrame.CR);

                String liquidTypeString = "1";
                switch(sam.getLiquidType()){
                    case L3Sample.LIQUID_TYPE_CSF:
                        liquidTypeString = "3";
                        break;
                    case L3Sample.LIQUID_TYPE_BODY_FLUID:
                        liquidTypeString = "5";
                        break;
                    case L3Sample.LIQUID_TYPE_SERUM:
                        liquidTypeString = "1";
                        break;
                    case L3Sample.LIQUID_TYPE_URIN:
                        liquidTypeString = "2";
                        break;
                    case L3Sample.LIQUID_TYPE_STOOL:
                    case L3Sample.LIQUID_TYPE_OTHERS:
                        liquidTypeString = "5";
                        break;
                    case L3Sample.LIQUID_TYPE_SUPERNATENT:
                        liquidTypeString = "4";
                        break;
                }

                //Order
                frame.append2Data("O|1|"+sam.getId()+"|"+sam.getId()+"^^^^S"+liquidTypeString+"^SC|");

                boolean isFirst = true;
                Iterator tIter = sam.testIterator();
                while(tIter != null && tIter.hasNext()){
                    L3Test test = (L3Test) tIter.next();
                    String machineTest = test != null ? driver.testMaps_getInstCode(test.getLabel()) : null;
                    if(machineTest != null){
                        // Order Frame
                        if(!isFirst){
                            frame.append2Data("\\");
                        }
                        frame.append2Data("^^^"+machineTest+"^");
                        isFirst = false;
                        // ---------------
                        //if (fromDriver == false){
                        //Result Frame
                        //frame = AstmFrame.newResultFrame(getInstrument(), getNextSequence(), 1, test.getLabel(), test.getUnitLabel(), test.getValue());
                        //addFrame(frame);
                        //----------------
                        //}
                    }
                }
                frame.append2Data("|R||");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if(sam.getEntryDate().getTime() < 24*60*60*1000){
                    Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"+sam.getEntryDate().getTime());
                    frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
                }else{
                    frame.append2Data(sdf.format(sam.getEntryDate()));
                }

                //frame.append2Data("||||N||||1||||||||||O");
                frame.append2Data("||||N||||"+liquidTypeString+"||||||||||O");
                frame.append2Data(AstmFrame.CR);

                frame.append2Data("C|1|L|"+sam.getFirstName()+" "+sam.getLastName()+"^^^^|G");
                //frame.append2Data("C|1|L|C1^C2^C3^C4^C5|G");

                frame.append2Data(AstmFrame.CR);
            }
            frame.append2Data("L|1|N");
            frame.append2Data(AstmFrame.CR);
        }

        addAndSplitDataFrame(driver, frame);

        // Last frame
        //frame = AstmFrame.newLastFrame(getInstrument(), getNextSequence(), 1);
        //addFrame(frame);
        // -------------

        // EOT frame
        frame = newEndOfTransmissionFrame(driver.getInstrument());
        driver.addFrame(frame);
        // -------------
    }
    */

}
