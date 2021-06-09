package b01.sbs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class BServiceClient extends BClient implements BService{
	
	private BServiceInterface serviceInterface = null; 
	private String launcherIP = null;
	private int launcherPort = 0;
	
	public BServiceClient(BServiceInterface serviceInterface, String serverIP, int serverPort, String launcherServerIP, int launcherServerPort) {
		super(serverIP, serverPort);
		this.serviceInterface = serviceInterface;
		this.launcherIP = launcherServerIP;
		this.launcherPort = launcherServerPort;
	}

	public void dispose(){
		launcherIP = null;
		serviceInterface = null;
		super.dispose();
	}

	private static void popupMessage(String message){
	  JOptionPane.showConfirmDialog(null,
	      message,
	      "01Barmaja",
	      JOptionPane.DEFAULT_OPTION,
	      JOptionPane.INFORMATION_MESSAGE,
	      null);
	}
	
  public static void fillPopupMenu(JPopupMenu popup, final GuiServiceSelector guiSelector){
  	if(popup != null){  		
      JMenuItem toggleConnectionItem = new JMenuItem("Connect/Disconnect", null);
      toggleConnectionItem.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
        	BService serviceI = (BService) guiSelector.getSelectedService();
        	if(serviceI.isOn()){
        		serviceI.switchOff();
        	}else{
        		serviceI.switchOn();
        	}
        	//guiSelector.refreshStatus();
        }
      });
      popup.add(toggleConnectionItem);
      
      JMenuItem pingItem = new JMenuItem("Ping", null);
      pingItem.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
        	BService service = (BService) guiSelector.getSelectedService();
          if(!service.ping()){
          	popupMessage(service.getName()+" replied to Ping.");
          }else{
          	popupMessage(service.getName()+" did not reply!\nMaybe the process is stoped");
          }
        }
      });
      popup.add(pingItem);

      JMenuItem launchItem = new JMenuItem("Launch process", null);
      launchItem.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
        	BService serviceI = (BService) guiSelector.getSelectedService();
        	if(serviceI != null){
        		serviceI.launch();
        	}
        	guiSelector.refreshStatus();
        }
      });
      popup.add(launchItem);
      
      JMenuItem exitItem = new JMenuItem("Stop process", null);
      exitItem.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
        	BService service = (BService) guiSelector.getSelectedService();
        	if(service.exit()){
          	popupMessage(service.getName()+" could not exit");
          }
        	guiSelector.refreshStatus();
        }
      });
      popup.add(exitItem);

      exitItem = new JMenuItem("FORCE! Stop process", null);
      exitItem.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
        	BService service = (BService) guiSelector.getSelectedService();
          if(service.violentExit()){
          	popupMessage(service.getName()+" could not exit");
          }
          SwingUtilities.invokeLater(new Runnable(){
						public void run() {
		          guiSelector.refreshStatus();
						}
          });
        }
      });
      popup.add(exitItem);
  	}
  }

	public boolean exit() {
  	boolean error = true;
  	String ret = sendMessage(BService.SEND_EXIT);
  	if(ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0){
  		error = false;
  	}
  	serviceInterface.refreshSwitchStatus();
  	return error;
	}

	public boolean ping() {
  	boolean error = true;
  	String ret = sendMessage(BService.SEND_PING);
  	if(ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0){
  		error = false;
  	}
  	return error;
	}

	public boolean switchOff() {
  	boolean error = true;
  	String ret = sendMessage(BService.SEND_SWITCH_OFF);
  	if(ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0){
  		error = false;
  	}
  	serviceInterface.refreshSwitchStatus();
  	return error;
	}

	public boolean switchOn() {
  	boolean error = true;
  	String ret = sendMessage(BService.SEND_SWITCH_ON);
  	if(ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0){
  		error = false;
  	}
  	serviceInterface.refreshSwitchStatus();
  	return error;
	}

	public boolean violentExit() {
  	boolean error = true;
  	String ret = sendMessage(BService.SEND_VIOLENT_EXIT);
  	if(ret != null && ret.compareTo(BService.REPLY_SUCCESS) == 0){
  		error = false;
  	}
  	serviceInterface.refreshSwitchStatus();
  	return error;
	}

	public boolean isOn() {
  	serviceInterface.refreshSwitchStatus();
		return serviceInterface.isOn();
	}
	
	public String getName() {
		return serviceInterface.getName();
	}

	public boolean launch() {
		boolean error = false;
		BClient client = new BClient(launcherIP, launcherPort);
		String response = client.sendMessage(serviceInterface.getLaunchCommand());
		error = response.compareTo(BService.REPLY_SUCCESS) != 0;
		return error;
	}
}
