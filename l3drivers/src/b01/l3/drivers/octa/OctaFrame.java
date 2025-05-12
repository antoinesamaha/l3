package b01.l3.drivers.octa;

import b01.foc.Globals;
import b01.l3.Instrument;
import b01.l3.drivers.astm.AstmFrame;

public class OctaFrame extends AstmFrame {

    public OctaFrame(Instrument instrument) {
        super(instrument);
    }

    @Override
    public void createDataWithFrame() throws Exception {

    }

    @Override
    public void extractDataFromFrame() throws Exception {

    }

    @Override
    public boolean extractAnswerFromBuffer(StringBuffer buffer) {
        Globals.logString("OctaDriver extractAnswerFromBuffer");
        String str = buffer.toString();
        boolean extractionDone = false;

        String start = String.valueOf(STX);
        String endEtx = String.valueOf(ETX);
        String endEot = String.valueOf(EOT);

        int sIdx = str.lastIndexOf(start);
        int eIdx = -1;
        if (sIdx >= 0) {
            eIdx = str.lastIndexOf(endEot);
            if (eIdx < 0) {
                eIdx = str.lastIndexOf(endEtx);
            }
        }
        Globals.logString("OctaDriver extractAnswerFromBuffer ["+sIdx+":"+eIdx+"]");
        if (sIdx >= 0 && eIdx > sIdx) {
            StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx + 1));
            setDataWithFrame(response);
            buffer.replace(0, eIdx + 1, "");
            extractionDone = true;
            Globals.logString("OctaDriver returning True");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Globals.logException(e);
        }

        return extractionDone;
    }

}
