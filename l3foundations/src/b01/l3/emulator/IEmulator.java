package b01.l3.emulator;

import b01.l3.data.L3Message;

public interface IEmulator {
  public void sendMessageWithResults(L3Message message) throws Exception;  
}
