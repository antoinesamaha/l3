package b01.l3.connector;

import b01.foc.list.FocList;
import b01.l3.data.L3Sample;

public interface L3IConnector {
  public void    dispose();
	
  public boolean connect() throws Exception;
  public boolean disconnect() throws Exception;
  
  public void    setLisConnector(LisConnector lisConnector);

  public void    postSampleToLis(L3Sample sample) throws Exception;
  public void    postMessagesToLis(FocList instrMessageList) throws Exception;
}
