/*
 * Created on Jun 26, 2006
 */
package b01.l3.drivers.unit;

import java.util.Properties;

import b01.l3.Driver;
import b01.l3.Instrument;
import b01.l3.data.L3Message;

/**
 * @author 01Barmaja
 */
public class DriverUnit extends Driver{

  /* (non-Javadoc)
   * @see b01.l3.Driver#init(b01.l3.Instrument, java.util.Properties)
   */
  public void init(Instrument instrument, Properties props) throws Exception {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see b01.l3.Driver#isBusy()
   */
  public boolean isBusy() {
    // TODO Auto-generated method stub
    return false;
  }

  /* (non-Javadoc)
   * @see b01.l3.Driver#connect()
   */
  public void connect() throws Exception {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see b01.l3.Driver#disconnect()
   */
  public void disconnect() {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see b01.l3.Driver#send(b01.l3.data.L3Message)
   */
  public void send(L3Message message) throws Exception {
    // TODO Auto-generated method stub
    
  }

	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
		// TODO Auto-generated method stub
		
	}
}
