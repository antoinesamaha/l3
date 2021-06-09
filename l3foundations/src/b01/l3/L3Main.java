package b01.l3;

import java.awt.Dimension;

import b01.foc.Application;
import b01.foc.ArgumentsHash;
import b01.foc.ConfigInfo;
import b01.foc.Globals;
import b01.foc.admin.FocVersion;
import b01.foc.calendar.CalendarModule;
import b01.foc.gui.DisplayManager;
import b01.foc.gui.FPanel;
import b01.foc.list.FocList;
import b01.l3.connector.LisConnector;
import b01.l3.connector.LisConnectorDesc;
import b01.l3.devTesting.DevTesting;
import b01.sbs.BService;
import b01.sbs.remoteLauncherServer.RemoteLauncherServer;

public class L3Main extends ArgumentsHash{

	private int    guiNavigatorType   = DisplayManager.GUI_NAVIGATOR_MDI;
	private String fileConnectorValue = null;
	
	private String  instrumentCode = null;
	private String  connectorCode  = null;
	private boolean executeSQL     = false;
	
	public L3Main(String[] args){
		super(args);
		/*
		Properties props = System.getProperties();
		Iterator iter = props.keySet().iterator();
		while(iter != null && iter.hasNext()){
			String key = (String) iter.next();
			System.out.println("Time out : "+key);
		}
		System.setProperty("java.io.connectiontimeout", "1");
		*/
		for(int i=0; i<args.length; i++){
			Globals.logDebug("Argument["+i+"]="+args[i]);
		}
		fileConnectorValue = get("FILECONNECTOR");
		instrumentCode = get("INSTRUMENT");
		connectorCode = get("CONNECTOR");
		String executeSQLString = get("EXECUTE_SQL");
		
		if(fileConnectorValue != null && fileConnectorValue.compareTo("") != 0){
			guiNavigatorType = DisplayManager.GUI_NAVIGATOR_MONOFRAME;
		}else if(instrumentCode != null && instrumentCode.compareTo("") != 0){
			guiNavigatorType = DisplayManager.GUI_NAVIGATOR_NONE;
		}else if(connectorCode != null && connectorCode.compareTo("") != 0){
			guiNavigatorType = DisplayManager.GUI_NAVIGATOR_NONE;
		}else if(executeSQLString != null && executeSQLString.compareTo("true") == 0){
			executeSQL = true;
			guiNavigatorType = DisplayManager.GUI_NAVIGATOR_NONE;
		}
	}
	
	private String[] addLogFilePrefixIfNeeded(String[] args){
		String logFilePrefix = null;
		if(instrumentCode != null && instrumentCode.compareTo("") != 0){
			logFilePrefix = instrumentCode;
		}else if(connectorCode != null && connectorCode.compareTo("") != 0){
			logFilePrefix = connectorCode;
		}
		if(logFilePrefix != null){
			String newArgs[] = new String[args.length+1];
			for(int i=0; i<args.length; i++){
				newArgs[i] = args[i];
			}
			newArgs[args.length] = "/"+Globals.ARG_LOG_FILE_PREFIX+":"+logFilePrefix;
			args = newArgs;
		}
		return args;
	}
	
	public Application newApplicationVersionAndModuleDeclaration(String[] args){
		args = addLogFilePrefixIfNeeded(args);
	  Application app = Globals.newApplication(true, true, guiNavigatorType, args);
	  FocVersion.addVersion("L3", "l3-1.0" , 1004); //1003
	  app.declareModule(new L3KernelModule());
	  app.declareModule(new CalendarModule());
	  return app;
	}
	
	private void stopLaunchcerHangAndStopLogConsoleActive(){
		ConfigInfo.setLogConsoleActive(true);//Useful if in the properties file it is false
		Globals.logString(RemoteLauncherServer.FINISH);//Important to tell the Launcher to exit and poll again  
		ConfigInfo.setLogConsoleActive(false);//Important because any System.out.println in the Globals.logString is hanging all
	}
	
	public void executeBackgroundTasks(){
		if(executeSQL){
			DevTesting devTesting = new DevTesting();
			devTesting.test_InsertSampleThenOneTestForThatSample1();
			
//			devTesting.test_DriverU601();
		}else if(instrumentCode != null){
			stopLaunchcerHangAndStopLogConsoleActive();
			Instrument instr = PoolKernel.getInstrumentForAnyPool(instrumentCode);
			BService service = null;
			boolean error = instr == null;
			if(error){
				Globals.logString("Instrument not found : "+instrumentCode);
				Globals.getApp().exit();
			}else{
				L3Application l3App = L3Application.getAppInstance();
				if(l3App != null){
					l3App.setBackgroundTask(L3Application.BACKGROUND_TASK_DRIVER);
					l3App.setBackgroundInstrument(instr);
				}
				service = instr.getService();
				error = service == null;
			}
			if(error){
				Globals.getApp().exit(true);
			}else{
				service.switchOn();
			}
		}else	if(connectorCode != null){
			stopLaunchcerHangAndStopLogConsoleActive();
	    FocList list = LisConnector.getFocDesc().getDefaultFocList(FocList.FORCE_RELOAD);
	    LisConnector connector = (LisConnector)list.searchByProperyStringValue(LisConnectorDesc.FLD_NAME, connectorCode);
			boolean error = connector == null;
			if(error){
				Globals.logString("Connector not found : "+connectorCode);
				Globals.getApp().exit();
			}else{
				L3Application l3App = L3Application.getAppInstance();
				if(l3App != null){
					l3App.setBackgroundTask(L3Application.BACKGROUND_TASK_CONNECTOR);
					l3App.setBackgroundConnector(connector);
				}
				BService service = connector.getService();
				error = service == null;
			}
			if(error){
				Globals.getApp().exit(true);
			}else{
				connector.updateConnected(false);
				connector.switchOn();
			}
		}
		
		if(L3ConfigInfo.getEmulationMode() && !Globals.getApp().isUnitTesting()){
			String message = "!!!!!!! EMULATION MODE !!!!!!!";
			Globals.logString(message);
			if(Globals.getDisplayManager() != null){
				Globals.getDisplayManager().popupMessage(message);
			}
		}
	}
	
	public void automaticPurgeCheckAndExecution(){
		L3Application.getAppInstance().automaticPurge();
	}
	
	public void popupTheRightPanel(){
		if(fileConnectorValue != null && fileConnectorValue.compareTo("") != 0){
	    FocList list = LisConnector.getFocDesc().getDefaultFocList(FocList.FORCE_RELOAD);
	    LisConnector connector = (LisConnector)list.searchByProperyStringValue(LisConnectorDesc.FLD_NAME, fileConnectorValue);
	    if(connector != null){
	      FPanel mainPanel = connector.newDetailsPanel(L3Globals.view_BuildViewId(L3Globals.VIEW_NORMAL, false));
	      mainPanel.setMainPanelSising(FPanel.MAIN_PANEL_NONE);
	      Globals.getDisplayManager().newInternalFrame(mainPanel);
	      Globals.getDisplayManager().getMainFrame().setPreferredSize(new Dimension(600, 400));
	      Globals.getDisplayManager().getMainFrame().pack();
	    }else{
	      Globals.getDisplayManager().popupMessage("Connector "+fileConnectorValue+" not found!");
	    }
		}
	}
}