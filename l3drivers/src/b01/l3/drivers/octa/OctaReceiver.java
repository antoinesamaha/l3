package b01.l3.drivers.octa;

import b01.l3.L3Frame;
import b01.l3.connection.L3SerialPortListener;
import b01.l3.drivers.astm.AstmDriver;

public class OctaReceiver implements L3SerialPortListener {

    private OctaDriver driver;

    public OctaReceiver(OctaDriver driver) {

    }

    public void dispose() {
        driver = null;
    }

    @Override
    public void received(L3Frame frame) {

    }

}
