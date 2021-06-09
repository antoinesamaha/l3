/*
 * Created on May 7, 2006
 */
package b01.l3;

import java.util.Properties;

import b01.l3.data.L3Message;
import b01.foc.list.FocList;

/**
 * @author 01Barmaja
 */
public interface IDriver {
  public void    init(Instrument instrument, Properties props) throws Exception;
  public void    dispose();
  public boolean isBusy(); 
  public boolean reserve();
  public void    release();
  
  public void    connect() throws Exception;
  public void    disconnect();
  
  public void    send(L3Message message) throws Exception;
  public void    addListener(MessageListener driverListener);
  public void    removeListener(MessageListener driverListener);
  public void    completeListOfAvailableTests(FocList testList) throws Exception;
  public boolean isResendAllPendingTests();
  public void    makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message);
}
