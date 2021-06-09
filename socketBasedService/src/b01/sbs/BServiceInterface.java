package b01.sbs;

public interface BServiceInterface {
	public boolean isOn();
	public boolean switchOn();
	public boolean switchOff();
	public String  getLaunchCommand();
	public boolean exit();
	public void    refreshLaunchStatus();
	public void    refreshSwitchStatus();
	public String  getName();
}
