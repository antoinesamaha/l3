/*
 * Created on Jun 5, 2006
 */
package b01.l3;

import b01.l3.data.L3Message;

/**
 * @author 01Barmaja
 */
public interface Pool {
	public void postMessageToInstrument(String instrumentCode, L3Message message) throws Exception;
  public void postMessageToLIS(L3Message message);
	public void dispose();
	
	/*
	public void send(String code, L3Message message) throws Exception;
	public void postMessage(String code, L3Message message);
	public void dispose();
	public Class getDriver(String code);
	public void pushReceivedMessage(L3Message message);
	public void addInstrument(Instrument inst);
	public void addListener(MessageListener driverListener);
	public void removeListener(MessageListener driverListener);
	public void connectAllInstruments();
	public Instrument getInstrument(String code);
	*/
}