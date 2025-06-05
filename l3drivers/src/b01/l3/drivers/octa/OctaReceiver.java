package b01.l3.drivers.octa;

import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.list.FocListElement;
import b01.foc.list.FocListIterator;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.data.*;
import b01.l3.drivers.astm.AstmFrame;

import java.util.Iterator;

public class OctaReceiver implements L3SerialPortListener {

    private OctaDriver driver;

    private static final int[] PROGRAM_CODE = {2, 1};
    private static final int[] SAMPLE_NUMBER = {3, 4};
    private static final int[] PATIENT_ID = {7, 15};
    private static final int[] PATIENT_NAME = {22, 30};
    private static final int[] PATIENT_DOB = {52, 8};
    private static final int[] PATIENT_SEX = {60, 1};

    private static final int[] SAMPLE_DATE = {84, 8};
    private static final int[] CONCENTRATION = {92, 5};
    private static final int[] CONCENTRATION_UNIT = {97, 8};
    private static final int[] FRACTION_1_NAME = {268, 10};
    private static final int[] FRACTION_1_PERCENT = {368, 5};

    private static final int[] INQUIRY__PATIENT_ID = {3, 15};

    /*
        1STX11ASCII 02h
        2Program code21Letter from A to Z, 0 to 9 (see index 1 )
        3Sample number34Aligned right e.g. 0001
        4Patient ID code715Alphanumeric character, aligned left and spaces
        5Patient Name2230Alphanumeric character., aligned left and spaces
        6Date of Birth528DDMMYYYY
        7Sex601M or F
        8Age in years613Aligned right e.g. 015
        9Department6420Alphanumeric character., aligned left and spaces
        10Semple date848Format DDMMYYYY
        11Concentration (e.g. total protéine)925Not fixed with a decimal separator “.” (2Eh)
        12Measurement unit of concentration978Alphanumeric character., aligned left and spaces
                        13Free field 110530Alphanumeric character., aligned left and spaces
                        14Free field 213530Alphanumeric character., aligned left and spaces
                        15Free field 316530Alphanumeric character., aligned left and spaces
                        16Free field 419530Alphanumeric character., aligned left and spaces
                        17Free field 522530Alphanumeric character., aligned left and spaces
                        18Operator ID2553Alphanumeric character. e.g. JON
                        19Date of Analysis2588Format DDMMYYYY
                        20Number of fractions (max. 10)2662Aligned right e.g. 06
                        21Fraction 1 name26810Alphanumeric character., aligned left and spaces
                        22Fraction 2 name27810Alphanumeric character., aligned left and spaces
                        23Fraction 3 name28810Alphanumeric character., aligned left and spaces
                        24Fraction 4 name29810Alphanumeric character., aligned left and spaces
                        25Fraction 5 name30810Alphanumeric character., aligned left and spaces
                        26Fraction 6 name31810Alphanumeric character., aligned left and spaces
                        27Fraction 7 name32810Alphanumeric character., aligned left and spaces
                        28Fraction 8 name33810Alphanumeric character., aligned left and spaces
                        29Fraction 9 name34810Alphanumeric character., aligned left and spaces
                        30Fraction 10 name35810Alphanumeric character., aligned left and spaces
                        31Fraction 1 % value3685Not fixed with a decimal separator “.” (2Eh)
                        32Fraction 2 % value3735Not fixed with a decimal separator “.” (2Eh)
                        33Fraction 3 % value3785Not fixed with a decimal separator “.” (2Eh)
                        34Fraction 4 % value3835Not fixed with a decimal separator “.” (2Eh)
                        35Fraction 5 % value3885Not fixed with a decimal separator “.” (2Eh)
                        36Fraction 6 % value3935Not fixed with a decimal separator “.” (2Eh)
                        37Fraction 7 % value3985Not fixed with a decimal separator “.” (2Eh)
                        38Fraction 8 % value4035Not fixed with a decimal separator “.” (2Eh)
                        39Fraction 9 % value4085Not fixed with a decimal separator “.” (2Eh)
                        40Fraction 10 % value4135Not fixed with a decimal separator “.” (2Eh)
                        41Fraction 1 conc. value4185Not fixed with a decimal separator “.” (2Eh)
                        42Fraction 2 conc. value4235Not fixed with a decimal separator “.” (2Eh)
                        43Fraction 3 conc. value4285Not fixed with a decimal separator “.” (2Eh)
                        44Fraction 4 conc. value4335Not fixed with a decimal separator “.” (2Eh)
                        45Fraction 5 conc. value4385Not fixed with a decimal separator “.” (2Eh)
                        46Fraction 6 conc. value4435Not fixed with a decimal separator “.” (2Eh)
                        47Fraction 7 conc. value4485Not fixed with a decimal separator “.” (2Eh)
                        48Fraction 8 conc. value4535Not fixed with a decimal separator “.” (2Eh)
                        849Fraction 9 conc. value4585Not fixed with a decimal separator “.” (2Eh)
                        50Fraction 10 conc. value4635Not fixed with a decimal separator “.” (2Eh)
                        51Peak 1 name46810Alphanumeric character., aligned left and spaces
                        52Peak 2 name47810Alphanumeric character., aligned left and spaces
                        53Peak 3 name48810Alphanumeric character., aligned left and spaces
                        54Peak 4 name49810Alphanumeric character., aligned left and spaces
                        55Peak 1 % value5085Not fixed with a decimal separator “.” (2Eh)
                        56Peak 2 % value5135Not fixed with a decimal separator “.” (2Eh)
                        57Peak 3 % value5185Not fixed with a decimal separator “.” (2Eh)
                        58Peak 4 % value5235Not fixed with a decimal separator “.” (2Eh)
                        59Peak 1 conc. value5285Not fixed with a decimal separator “.” (2Eh)
                        60Peak 2 conc. value5335Not fixed with a decimal separator “.” (2Eh)
                        61Peak 3 conc. value5385Not fixed with a decimal separator “.” (2Eh)
                        62Peak 4 conc. value5435Not fixed with a decimal separator “.” (2Eh)
                        63Pathological Flag54810 = Normal, 1 = Pathological
                        64Ratio 1 (e.g. A/G for the proteins)5495Not fixed with a decimal separator “.” (2Eh)
                        65Ratio 25545Not fixed with a decimal separator “.” (2Eh)
                        66Comment559230Alphanumeric character., aligned left and spaces
                        67Reference pattern flag78910 = Normal pattern, 1 = Reference patte
     */

    public OctaReceiver(OctaDriver driver) {
        this.driver = driver;
    }

    public void dispose() {
        driver = null;
    }

    private void extractDataFromFrame(OctaFrame frame) {
        // This method should parse the frame and extract the relevant information
        // based on the defined indices.
        // For now, we will just log the received frame.
        Globals.logString("OctaReceiver analysing frame: " + frame);
        if (       frame.getDataWithFrame().length() > 10
                && frame.getDataWithFrame().charAt(0) == AstmFrame.STX
                && (   frame.getDataWithFrame().charAt(frame.getDataWithFrame().length() - 1) == AstmFrame.ETX
                    || frame.getDataWithFrame().charAt(frame.getDataWithFrame().length() - 1) == AstmFrame.EOT)
        ) {
            StringBuffer data = new StringBuffer(frame.getDataWithFrame().substring(1, frame.getDataWithFrame().length() - 2));
            frame.setData(data);

            if (data.length() > 700) {
                frame.setType(AstmFrame.FRAME_TYPE_RESULT);
            } else if (data.length() == 15) {
                frame.setType(AstmFrame.FRAME_TYPE_INFORMATION_INQUIRY);
            } else if (data.length() == 3) {
                if (data.charAt(0) == AstmFrame.ACK) {
                    frame.setType(AstmFrame.FRAME_TYPE_ACK);
                }else if (data.charAt(0) == AstmFrame.NACK) {
                    frame.setType(AstmFrame.FRAME_TYPE_NACK);
                }
            }

        } else if (frame.getDataWithFrame().length() == 3 && frame.getDataWithFrame().charAt(0) == AstmFrame.STX) {

        }
    }

    private String readString(StringBuffer data, int[] indexes) {
        int start = indexes[0] - 2; // Convert to 0-based index and remove STX
        if (data.length() >= start + indexes[1] - 1) {
            return data.substring(start, start + indexes[1] -1).trim();
        } else {
            return "";
        }
    }

    private double readDouble(StringBuffer data, int[] indexes) {
        String stringValue = readString(data, indexes);
        double value = 0;
        try {
            value = Double.valueOf(stringValue);
        } catch (Exception e) {
            Globals.logException(e);
        }

        return value;
    }

    protected boolean treatResultFrame(OctaFrame frame) {
        boolean error = false;
        StringBuffer data = frame.getData();

        String programCode = readString(data, PROGRAM_CODE);
        String sampleNumber = readString(data, SAMPLE_NUMBER);
        String patientId = readString(data, PATIENT_ID);
        String patientName = readString(data, PATIENT_NAME);
        String patientDob = readString(data, PATIENT_DOB);
        String patientSex = readString(data, PATIENT_SEX);

        String sampleDate = readString(data, SAMPLE_DATE);
        double concentration = readDouble(data, CONCENTRATION);
        String concentrationUnit = readString(data, CONCENTRATION_UNIT);
        double fraction1Percent = readDouble(data, FRACTION_1_PERCENT);
        String fraction1Name = readString(data, FRACTION_1_NAME);

        String[] parts = patientName.split(" ");
        String firstName = patientName;
        String lastName = patientName;
        String middleName = "";
        if (parts.length > 0) {
            firstName = parts[0];
            lastName = parts[parts.length - 1];
        }
        if (parts.length > 2) {
            middleName = parts[1];
        }

        L3Message message = new L3Message();

        L3Sample sample = new L3Sample(patientId);//Since the sample number is very small, we can use the patient ID as a unique identifier
        sample.setFirstName(firstName);
        sample.setLastName(lastName);
        sample.setMiddleInitial(middleName);
        message.addSample(sample);

        //sample.setDateAndTime();
        L3Test test = sample.addTest();
        test.setLabel(fraction1Name);
        test.setValue(fraction1Percent); // Example value
        test.setUnitLabel("%");
        message.addSample(sample);
        driver.notifyListeners(message);

        return error;
    }

    public void respondToInquiryIfNecessary(OctaFrame frame) {
        boolean error = false;

        final Instrument instrument = driver.getInstrument();
        StringBuffer data = frame.getData();
        String sampleId = readString(data, INQUIRY__PATIENT_ID);

        Globals.logString("Calling sendASampleAnsweringInquiry : "+sampleId);
        if (instrument != null && !driver.reserve()) {
            try {
                OctaFrameCreator creator = new OctaFrameCreator();
                driver.getL3SerialPort().send(creator.buildAckFrame());

                String orderFrame = creator.buildOrderFrame(instrument, sampleId);
                driver.getL3SerialPort().send(orderFrame);

                L3Sample sample = new L3Sample(sampleId);
                sample.load();
                sample.getTestList().iterate(new FocListIterator() {
                    @Override
                    public boolean treatElement(FocListElement element, FocObject focObj) {
                        L3Test test = (L3Test) focObj;
                        if (test.getInstrument().getReference() == instrument.getReference()) {
                            test.updateStatus(L3TestDesc.TEST_STATUS_ANALYSING);
                        }
                        return false;
                    }
                });

                driver.release();
            } catch (Exception e) {
                Globals.logString("Exception while answering inquiry");
                Globals.logException(e);
            }
        }

    }

    @Override
    public void received(L3Frame l3Frame) {
        OctaFrame frame = (OctaFrame) l3Frame;

        extractDataFromFrame(frame);
        if (frame.getType() == OctaFrame.FRAME_TYPE_RESULT) {
            boolean error = treatResultFrame((OctaFrame) frame);
            String answer = OctaFrame.ACK_FRAME;
            if (error) {
                Globals.logString("Prepare NACK answer");
                answer = OctaFrame.NACK_FRAME;
            }

            try {
                driver.send(answer);
            } catch (Exception e) {
                Globals.logString("Exception while Sending answer");
                Globals.logException(e);
            }
        } else if (frame.getType() == OctaFrame.FRAME_TYPE_INFORMATION_INQUIRY) {
            Globals.logString("Received Information Inquiry Frame");
            respondToInquiryIfNecessary(frame);
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_ACK) {
            Globals.logString("Received ACK Frame");
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_NACK) {
            Globals.logString("Received NACK Frame");
        } else {
            Globals.logString("Received Unknown Frame Type: " + frame.getType());
        }

    }

}
