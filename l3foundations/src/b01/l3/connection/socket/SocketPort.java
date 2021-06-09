package b01.l3.connection.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.comm.SerialPortEventListener;

import b01.foc.Globals;
import b01.l3.connection.SerialPortInterface;
import b01.l3.exceptions.L3SerialPortOpeningException;

public class SocketPort implements SerialPortInterface {
	private PhysicalSocket physicalSocket = null;

	public SocketPort(Properties props) throws Exception {
		setParametersFromProperties(props);
	}

	public void dispose() {
		closeConnection();
		if (physicalSocket != null) {
			physicalSocket.dispose();
			physicalSocket = null;
		}
	}
	
	public PhysicalSocket getPhysicalSocket(){
		return physicalSocket;
	}

	public boolean isSerialPortNull() {
		return physicalSocket == null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		//Not Needed for Socket Ports
		// return serialPort.getInputStream();
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		OutputStream out = null;
		if(getPhysicalSocket() != null){
			out = getPhysicalSocket().getOutputStream();
		}
		return out;
	}

	@Override
	public void setParametersFromProperties(Properties props) throws Exception {
		if (props != null) {
			int portNbtInt = -1;
			String portNbr = props.getProperty("serialPort.name");
			if (portNbr != null && portNbr.compareTo("") != 0) {
				portNbtInt = Integer.valueOf(portNbr);
			}

			physicalSocket = new PhysicalSocket(portNbtInt);
		}
	}

	@Override
	public void closeConnection() {
		if (physicalSocket != null) {
			physicalSocket.closeSocket();
			physicalSocket = null;
		}
	}

	@Override
	public void openConnection() throws L3SerialPortOpeningException {
		if (getPhysicalSocket() != null) {
			Globals.logString("Socket connecting...");
			Globals.logString("    starting to pool on port = " + getPhysicalSocket().getPort());

			boolean error = getPhysicalSocket().startPolling();
			Globals.logString("    started pooling");
			
			if (error) {
				throw new L3SerialPortOpeningException("L3X - Port " + physicalSocket.getPort() + " Error opening Socket");
			}
		} else {
			throw new L3SerialPortOpeningException("Physical Socket is Null");
		}

		Globals.logString("Socket connected successfuly");
	}
	
	@Override
	public void addEventListener(SerialPortEventListener cumulationListener) throws Exception {
		if(getPhysicalSocket() != null){
			getPhysicalSocket().addEventListener(cumulationListener);
		}
	}

	@Override
	public void removeEventListener() {
		if(getPhysicalSocket() != null){
			getPhysicalSocket().removeEventListener();
		}
	}
}
