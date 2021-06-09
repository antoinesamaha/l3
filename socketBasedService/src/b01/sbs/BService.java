package b01.sbs;

public interface BService {
	public final static String SEND_PING         = "PING";
	public final static String SEND_SWITCH_ON    = "ON";
	public final static String SEND_SWITCH_OFF   = "OFF";
	public final static String SEND_EXIT         = "EXIT";
	public final static String SEND_VIOLENT_EXIT = "VIOLENT_EXIT";
	
	public final static String REPLY_SUCCESS     = "SUCCESS";
	public final static String REPLY_FAILED      = "FAIL";
	
	public String  getName();
	public boolean isOn();
	public boolean switchOn();
	public boolean switchOff();
	public boolean exit();
	public boolean violentExit();
	public boolean ping();
	public boolean launch();
	public void    dispose();
}
