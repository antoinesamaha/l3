/*
 * Created on 17 fvr. 2004
 */
package b01.foc;

import b01.fab.FabModule;
import b01.foc.db.*;
import b01.foc.db.tools.DB2ASCII;
import b01.foc.fUnit.FocTestSuite;
import b01.foc.gui.*;
import b01.foc.list.*;
import b01.foc.menu.*;
import b01.foc.unit.*;
import b01.foc.desc.*;
import b01.foc.admin.*;
import b01.foc.access.*;
import b01.foc.calendar.FCalendar;
import b01.foc.cashdesk.*;
import b01.foc.property.*;
import b01.foc.desc.field.*;

import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Date;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * @author 01Barmaja
 */
public class Application {

  private DBManager dbManager = null;
  private DisplayManager dispManager = null;
  private HashMap<FocModuleInterface, FocModuleInterface> modules = null;
  private ArrayList<IFocDescDeclaration> focObjects = null;  
  private boolean objectsAlreadyDeclared = false; 
  private AccessControl defaultAccessControl = null;
  private UnitFactory unitFactory = null;
  private FocIcons focIcons = null;
  private PrintStream logFile = null;
  private boolean withDatabase = false;
  private boolean doNotCheckTables = false;
  private boolean withLogin = false;
  private int guiNavigatorType = DisplayManager.GUI_NAVIGATOR_MDI;
  private RightsByLevel rightsByLevel = null;
  private int loginStatus = LOGIN_WAITING;
  private FocUser user = null;
  private boolean withReporting = false;
  
  private AdminModule adminModule = null;
  
  private FMenu mainAppMenu = null;
  private FMenu mainFocMenu = null;  
  private FMenu mainAdminMenu = null;
  private DebugOutputInterface debugOutputInterface = null;
  private java.sql.Date systemDate = null;
  private boolean cashDeskModuleIncluded = false; 
  private boolean currencyModuleIncluded = false;
  
  private boolean disableMenus = false;
  private boolean isExiting = false;
  private String name = null;
  private boolean isDemo = false;
  private int trialPeriod = 0;
  private long timeZoneShift = -10*Globals.DAY_TIME;
  
  //BElias
  private ArrayList<IExitListener> exitListenerList = null;// il faut hash map
  //EElias
  private HashMap<String, IFocDescDeclaration> iFocDescDeclarationMap = null;
  
  private RootGarbageClass rgc = null;
  private FocTestSuite focTestSuite = null;
  
  private DB2ASCII dbAsciiConverter = null;
    
  public static final int LOGIN_WAITING = 1;
  public static final int LOGIN_VALID = 2;
  public static final int LOGIN_ADMIN = 3;  
  public static final int LOGIN_WRONG = 4;
  
  public static final String REGISTRY_PARENT_APPLICATION_NODE_NAME = "01barmaja";
  public static final String REGISTRY_APPLICATION_DIRECTORY        = "directory";
  public static final String REGISTRY_APPLICATION_ENVIRONMENT      = "lastenv";
  public static final String REGISTRY_APPLICATION_INSTALL_DATE     = "install_date";
  
  private String predefinedUserLogin = null;
  private String predefinedEncriptedPassword = null;
  
  public static void initArgs(String[] args){
  	String timeZone = Application.argumentGetValue(args, "timeZone");
  	if(timeZone != null){
  		TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
  	}
  }
  
  public Application(boolean withDatabase, boolean withLogin, boolean mdi) {
    this(withDatabase, withLogin, mdi, null);
  }
  
  public Application(boolean withDatabase, boolean withLogin, boolean mdi, String appName) {
    this(withDatabase, withLogin, mdi, appName, 0);
  }
  
  public Application(boolean withDatabase, boolean withLogin, boolean mdi, String appName, int trialPeriod) {
  	this(withDatabase, withLogin, mdi ? DisplayManager.GUI_NAVIGATOR_MDI : DisplayManager.GUI_NAVIGATOR_MONOFRAME, appName, trialPeriod);
  }

  public Application(boolean withDatabase, boolean withLogin, int guiNavigatorType, String appName, int trialPeriod) {
    this.name = appName;
    this.withDatabase = withDatabase;
    this.withLogin = withDatabase && withLogin;
    this.guiNavigatorType = guiNavigatorType;
    this.trialPeriod = trialPeriod;
  }

  public long getTimeZoneShift(){
    if(timeZoneShift < -Globals.DAY_TIME){
      Date stringComposed = Date.valueOf("1970-01-01");
      timeZoneShift = stringComposed.getTime();
    }
    return timeZoneShift;
  }
  
  private boolean isTrialVersionStillValid_WithPopupMessage(){
    java.sql.Date installationDate = getInstallationDate();
    boolean isValid = true;
    
    if( trialPeriod > 0 && installationDate != null){
      java.sql.Date currentDate = getSystemDate();
      installationDate = FCalendar.shiftDate(Calendar.getInstance(), installationDate, trialPeriod);
      
      if( currentDate.compareTo(installationDate) > 0){
      	isValid = false;
        JOptionPane.showMessageDialog(Globals.getDisplayManager().getMainFrame(),
            "You have exceeded your "+trialPeriod+" day trial Period.",
            "Trial version expired",
            JOptionPane.ERROR_MESSAGE);
      }else{
      	isValid = true;
      }
    }

    return isValid;
  }
  
  public void initDisplay(String[] args){
    try{
      dispManager = new DisplayManager(guiNavigatorType);
      dispManager.init();
      dispManager.startMonoNavigator();
      dispManager.getMainFrame().setVisible(false);
      
      if( isWithRegistry() ){
      	if(!isTrialVersionStillValid_WithPopupMessage()){
      		System.exit(0);
      	}else{
	        String directory = getDefaultAppDirectory();
	        String environment = getDefaultEnvironment();
	        String homePath = directory+"/"+environment;
	          
	        File file = new File( homePath+"/properties/config.properties");
	        if( !file.exists() ){
	          ConfigInfoWizardPanel panel = new ConfigInfoWizardPanel(new GuiConfigInfo(), ConfigInfoWizardPanel.STATE_DIRECTORY);
	          Globals.getDisplayManager().popupDialog(panel, "", true);    
	        }
      	}
      }
      focIcons = new FocIcons();
      
      dispManager.getMainFrame().setVisible(true);
      
    }catch(Exception e){
      Globals.logException(e);
    }    
  }
  
  public void initApp(String[] args){
    modules = new HashMap<FocModuleInterface, FocModuleInterface>();

    logFile = null;
    try{
    	String filePrefix = Application.argumentGetValue(args, Globals.ARG_LOG_FILE_PREFIX);
    	if(filePrefix == null) filePrefix ="";
    	logFile = (ConfigInfo.isLogFileActive() && !Globals.logFile_CheckLogDir()) ? new PrintStream(Globals.logFile_GetFileName(filePrefix, "log")) : null;
    }catch(Exception e){
    	Globals.logException(e);
    }
    exitListenerList = new ArrayList<IExitListener>();
    
  	predefinedUserLogin = Application.argumentGetValue(args, "userLogin");
  	predefinedEncriptedPassword = Application.argumentGetValue(args, "encriptedPassword");
  }
  
  public void initInternal(String[] args, boolean withDisplay){
  	if(withDisplay){
  		initDisplay(args);
  	}
  	ConfigInfo.loadFile(argumentExists(args, "unitTesting"));
  	initApp(args);
  }
  
  public void init(String[] args){
  	initInternal(args, getGuiNavigatorType() != DisplayManager.GUI_NAVIGATOR_NONE);
  }

  public void initWithoutDisplay(String[] args){
  	initInternal(args, false);
  }

  public boolean isWithRegistry(){
    return name != null;
  }
  
  private String getPreferencesRegistryStringValue(String key, String pathName){
    String value = null;
    
    if(isWithRegistry()){
    	Preferences prefs = Preferences.systemRoot();
      if ( preferencesRegistryNodeExists(prefs, pathName) ){
        prefs = prefs.node(pathName);  
        value = prefs.get(key, "");
      }
    }    
    return value;
  }
  
  public void setPreferencesRegistryStringValue(String key, String value){
    if(isWithRegistry() && value != null ){
      Preferences prefs = Preferences.systemRoot();
      prefs = prefs.node( REGISTRY_PARENT_APPLICATION_NODE_NAME+"/"+name );
      prefs.put( key, value );  
    }
  }
  
  public boolean preferencesRegistryNodeExists(Preferences prefs, String pathName){
    boolean exists = false;
    try {
    	if(isWithRegistry()){
    		exists = prefs.nodeExists(pathName);
    	}
    }catch (BackingStoreException e) {
      Globals.logException(e);
    }
    return exists;
  }
  
  public java.sql.Date getInstallationDate() {
    String date = getPreferencesRegistryStringValue(REGISTRY_APPLICATION_INSTALL_DATE, Application.REGISTRY_PARENT_APPLICATION_NODE_NAME+"/"+name);
    java.sql.Date installationDate = null;
    if( date != null && !date.equals("")){
      installationDate = java.sql.Date.valueOf(date);
    }
    return installationDate;
  }
  
  public String getDefaultEnvironment() {
    return getPreferencesRegistryStringValue(REGISTRY_APPLICATION_ENVIRONMENT, Application.REGISTRY_PARENT_APPLICATION_NODE_NAME+"/"+name);
  }

  public void setDefaultEnvironment(String defaultEnvironment) {
    setPreferencesRegistryStringValue(REGISTRY_APPLICATION_ENVIRONMENT, defaultEnvironment);
  }

  public String getDefaultAppDirectory() {
    return getPreferencesRegistryStringValue(REGISTRY_APPLICATION_DIRECTORY, Application.REGISTRY_PARENT_APPLICATION_NODE_NAME+"/"+name);
  }

  public void setDefaultAppDirectory(String defaultAppDirectory) {
    setPreferencesRegistryStringValue(REGISTRY_APPLICATION_DIRECTORY, defaultAppDirectory);
  }
  
  public void dispose(){
    if( focIcons != null ){
      focIcons = null;  
    }

    if( logFile != null ){
      logFile = null;  
    }
    
    if( modules != null ){
      modules.clear();
      modules = null;  
    }
    
    if( exitListenerList != null ){
      exitListenerList.clear();
      exitListenerList = null;   
    }
    
    if ( defaultAccessControl != null ){
      defaultAccessControl = null;
    }
    
    if ( dispManager != null ){
      dispManager = null;
    }
  }
  
  private ArrayList<IFocDescDeclaration> getFocObjectsList(){
  	if(focObjects == null){
  		focObjects = new ArrayList<IFocDescDeclaration>();
  	}
  	return focObjects;
  }
  
  public Iterator<IFocDescDeclaration> getFocDescDeclarationIterator(){
  	return getFocObjectsList().iterator();
  }

  private HashMap<String, IFocDescDeclaration> getIFocDescDeclarationMap(){
  	if(this.iFocDescDeclarationMap == null){
  		this.iFocDescDeclarationMap = new HashMap<String, IFocDescDeclaration>();
  	}
  	return this.iFocDescDeclarationMap;
  }
  
  public void putIFocDescDeclaration(String storageName, IFocDescDeclaration iFocDescDeclaration){
  	if(iFocDescDeclaration != null && storageName != null){
  		getIFocDescDeclarationMap().put(storageName, iFocDescDeclaration);
  	}
  }
  
  public void printDebug(){
  	Globals.logString("IFocDescMap : ");
  	/*Iterator<IFocDescDeclaration> mapIter = getIFocDescDeclarationMap().values().iterator();
  	while(mapIter != null && mapIter.hasNext()){
  		IFocDescDeclaration decl = mapIter.next();
  		Globals.logString(decl.getFocDesctiption().getStorageName());
  	}*/
  	
  	Globals.logString("FocDesc Array : ");
  	Iterator<IFocDescDeclaration> arrayIter = getFocDescDeclarationIterator();
  	while(arrayIter != null && arrayIter.hasNext()){
  		IFocDescDeclaration decl = arrayIter.next();
  		//if(!iFocDescDeclarationMap.containsKey(decl.getFocDesctiption().getStorageName())){
  			Globals.logString(decl.getFocDesctiption().getStorageName());	
  		//}
  		
  	}
  }
  
  public IFocDescDeclaration getIFocDescDeclarationByName(String storageName){
  	IFocDescDeclaration iFocDescDeclaration = null;
  	if(storageName != null){
  		iFocDescDeclaration = getIFocDescDeclarationMap().get(storageName);
  	}
  	return iFocDescDeclaration;
  }
  
  /*public FocDesc getFocDescByName(String name){
  	FocDesc res = null;
  	if(name != null){
  		res = getFocDescFromMap(name);
  		if(res == null){
		  	Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
		  	while(iter != null && iter.hasNext() && res == null){
		  		IFocDescDeclaration iFocDescDeclaration = iter.next();
		  		if(iFocDescDeclaration != null){
		  			FocDesc focDesc = iFocDescDeclaration.getFocDesctiption();
		  			if(focDesc != null){
		  				if(name.equals(focDesc.getStorageName())){
		  					putFocDescInMap(focDesc);
		  					res = focDesc;
		  				}
		  			}
		  		}
		  	}
  		}
  	}
  	return res;
  }*/
  
  public FocDesc getFocDescByName(String name){
  	FocDesc res = null;
  	if(name != null){
  		IFocDescDeclaration iFocDescDeclarationFromMap = getIFocDescDeclarationByName(name);
    	if(iFocDescDeclarationFromMap != null){
    		res = iFocDescDeclarationFromMap.getFocDesctiption();
    	}
  		if(res == null){
		  	Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
		  	while(iter != null && iter.hasNext() && res == null){
		  		IFocDescDeclaration iFocDescDeclaration = iter.next();
		  		if(iFocDescDeclaration != null){
		  			FocDesc focDesc = iFocDescDeclaration.getFocDesctiption();
		  			if(focDesc != null){
		  				if(name.equals(focDesc.getStorageName())){
		  					res = focDesc;
		  				}
		  			}
		  		}
		  	}
  		}
  	}
  	return res;
  }

  public static String argumentGetValue(String[] args, String key){
  	String value = null;
  	int pos = argumentGetPositionForKey(args, key);
  	if(pos >= 0){
  		String arg = args[pos];
  		if(arg.length() > key.length()+2){
  			value = arg.substring(key.length()+2);
  		}
  	}
  	return value;
  }

  public static boolean argumentExists(String[] args, String key){
  	return argumentGetPositionForKey(args, key) != -1;
  }
  
  private static int argumentGetPositionForKey(String[] args, String key){
  	int pos = 0;
  	boolean found = false;
  	if(args != null){
	  	while(pos < args.length && !found){
	  		String arg = args[pos];
	  		if(arg != null){
	  			found = arg.startsWith("-"+key) || arg.startsWith("/"+key);
	  		}
	  		if(!found){
	  			pos++;
	  		}
	  	}
  	}
  	return found ? pos : -1;
  }
  
    
  public boolean isWithReporting() {
    return withReporting;
  }
  
  public void setWithReporting(boolean withReporting) {
    this.withReporting = withReporting;
  }
  
  public void setRightsByLevel(RightsByLevel rightsByLevel){
    this.rightsByLevel = rightsByLevel;
  }

  public RightsByLevel getRightsByLevel(){
    return rightsByLevel;
  }

  public boolean isWithRightsByLevel(){
    return rightsByLevel != null && rightsByLevel.getNbOfLevels() > 0;
  }
  
  public void initDBManager(){
    if(withDatabase){
      dbManager = new DBManager();
      Date date = getSystemDate();
      if(date != null){
        Globals.logString(date.toString());
      }
    }
  }
  
  public void prepareDBForLogin(String dbFile){
    initDBManager();
    //focObjects = new ArrayList<IFocDescDeclaration>();
    
    b01.foc.db.DBModule dbModule = new b01.foc.db.DBModule();
    declareModule(dbModule);

    adminModule = new b01.foc.admin.AdminModule(withLogin);
    declareModule(adminModule);
    
    declareFocObjects();
    /*for(int i=0; i<focObjects.size(); i++){
      Class cls = focObjects.get(i);
      FocDesc focDesc = FocDesc.getFocDescriptionForDescClass(cls);
      focDesc.scanFieldsAndAddReferenceLocations();
    }*/
    
    ArrayList<IFocDescDeclaration> focObjectsList = getFocObjectsList();
    if(focObjectsList != null){
    	Collections.sort(focObjectsList, new Comparator<IFocDescDeclaration>(){
				public int compare(IFocDescDeclaration o1, IFocDescDeclaration o2) {
					return o1.getPriority() - o2.getPriority();
				}
    	});
    }
    
    Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
    while(iter != null && iter.hasNext()){
    	IFocDescDeclaration focDescDeclaration = iter.next();
    	FocDesc focDesc = focDescDeclaration.getFocDesctiption();
    	if(focDesc != null){
    		focDesc.scanFieldsAndAddReferenceLocations();
    	}
    }
    
    iter = getFocDescDeclarationIterator();
    while(iter != null && iter.hasNext()){
    	IFocDescDeclaration focDescDeclaration = iter.next();
    	FocDesc focDesc = focDescDeclaration.getFocDesctiption();
    	if(focDesc != null){
    		focDesc.afterConstructionInternal();
    	}
    }
        
    FocVersion.addVersion("FOC", "foc1.6" , 1600);
    
    if(withDatabase && !isDoNotCheckTables()){
      adminModule.checkTables();
    }
    
    if(adminModule.isNewUserTables() && dbFile != null){
      try {
        dbAsciiConverter = new DB2ASCII(dbFile, DB2ASCII.COPY_DIRECTION_ASCII_TO_DB);
      } catch (Exception e) {
        Globals.logException(e);
      } 
    }
    
    iter = getFocDescDeclarationIterator();
    while(iter != null && iter.hasNext()){
      IFocDescDeclaration focDescDeclaration = iter.next();
      FocDesc focDesc = focDescDeclaration.getFocDesctiption();
      if(focDesc != null){
        focDesc.beforeLogin();
      }
    }
  }
  
  public boolean login( ){
    return login(null);
  }
  
  public boolean login(String dbFile){
	  return login(dbFile, false);
  }
  
  public boolean login(String dbFile, boolean noDBTestingOnly){
    boolean logOk = true;
    defaultAccessControl = new AccessControl(true, true);
    
    if(noDBTestingOnly) withDatabase = false;
    prepareDBForLogin(dbFile);

    if(dbAsciiConverter != null){
    	try{
    		dbAsciiConverter.backupRestore();
    	}catch(Exception e){
    		Globals.logException(e);
    	}
    }
    
    if(withLogin && !noDBTestingOnly){
      logOk = false;
      if(getGuiNavigatorType() != DisplayManager.GUI_NAVIGATOR_NONE){
      	showLoginScreenAndWaitForUserEntry(3);
      }else{
   		  FocUser.userLoginCheck(predefinedUserLogin, predefinedEncriptedPassword);
      }
      
      if(loginStatus == LOGIN_WRONG){
        exit();
      }
      logOk = loginStatus == LOGIN_VALID && loginStatus != LOGIN_ADMIN;
    }else{
      loginStatus = LOGIN_VALID;
      logOk = true;
    }
    
    Iterator iter = modules.values().iterator();
    while(iter != null && iter.hasNext()){
      FocModuleInterface module = (FocModuleInterface) iter.next();
      if (module instanceof FocUniqueModule) {
        FocUniqueModule uniqueModule = (FocUniqueModule) module;
        uniqueModule.afterApplicationLogin();
      }
    }
    
    return logOk ;
  }

  public void showLoginScreenAndWaitForUserEntry(int maxNbOfAttempt){
    dispManager.displayLogin();
    
    int nbOfAttempt = 0;
    setLoginStatus(LOGIN_WRONG);      
    while(nbOfAttempt < maxNbOfAttempt && loginStatus == LOGIN_WRONG){
      if(nbOfAttempt > 0){
        JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
            "Failed to login.\nAttempt"+nbOfAttempt+" of "+maxNbOfAttempt,
            "01Barmaja",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null);
      }
      setLoginStatus(LOGIN_WAITING);        
      while(loginStatus == LOGIN_WAITING){
        try{
          Thread.sleep(500);
        }catch(Exception e){
          Globals.logException(e);
        }
      }
      nbOfAttempt++;        
    }
  }
  
  @SuppressWarnings("serial")
  private void menuPreparation(){
    {
      AbstractAction exitAction = new AbstractAction(){
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3977012929554167095L;

        public void actionPerformed(ActionEvent e){
          Application app = Globals.getApp();
          app.exit();
        }
      };
      
      AbstractAction aboutAction = new AbstractAction(){
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3546357331844610361L;

        public void actionPerformed(ActionEvent e){
          String text = "01Barmaja software\n\n";
          String demoVersion = "";
          if( isDemo() ){
            demoVersion = "(Demo Version)";
          }
          text = text + "  - Versions: "+demoVersion+"\n";
          
          FocList verList = FocVersion.getVersionList();
          Iterator iter = verList.focObjectIterator();
          while(iter != null && iter.hasNext()){
            FocVersion ver = (FocVersion)iter.next();
            text = text + "    + "+ver.getJar() + " : " + ver.getName() + " ("+ver.getId()+")\n";
          }
            
          JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
              text,
              "01Barmaja",
              JOptionPane.DEFAULT_OPTION,
              JOptionPane.WARNING_MESSAGE,
              null);          
        }
      };
      
      AbstractAction changePasswordAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
          getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.CHANGE_PASSWORD_VIEW_ID), "Change password", true);
        }
      };
      
      AbstractAction changeLanguageAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
          //getDisplayManager().newInternalFrame(user.newDetailsPanel(FocUser.CHANGE_LANGUAGE_VIEW_ID));/*, MultiLanguage.getString(FocLangKeys.ADMIN_CHANGE_LANGUAGE), true);*/
          getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.CHANGE_LANGUAGE_VIEW_ID), MultiLanguage.getString(FocLangKeys.ADMIN_CHANGE_LANGUAGE), true);
        }
      };      

      AbstractAction viewUserGroupInfoAction = new AbstractAction(){
        public void actionPerformed(ActionEvent e){
          //getDisplayManager().newInternalFrame(user.newDetailsPanel(FocUser.CHANGE_LANGUAGE_VIEW_ID));/*, MultiLanguage.getString(FocLangKeys.ADMIN_CHANGE_LANGUAGE), true);*/
          getDisplayManager().popupDialog(user.newDetailsPanel(FocUser.USER_GROUP_INFO_VIEW_ID), MultiLanguage.getString(FocLangKeys.ADMIN_USER), true);
        }
      };      
      
      /*AbstractAction navigationHelpAction = new AbstractAction(){
        *//**
         * Comment for <code>serialVersionUID</code>
         *//*
        private static final long serialVersionUID = 3546357331844610361L;

        public void actionPerformed(ActionEvent e){
          FocHelp.popupNavigationHelp();
        }
      };*/

      FMenuList mainMenu = new FMenuList(FocLangKeys.MENU_ADMIN_MENU);
      
      if(withLogin || MultiLanguage.isMultiLanguage()){
        FMenuList toolsMenu = new FMenuList(FocLangKeys.MENU_TOOLS);
        if(withLogin){
          FMenuItem userGroupInfoItem = new FMenuItem(FocLangKeys.MENU_USER_GROUP_INFO, viewUserGroupInfoAction);
          toolsMenu.addMenu(userGroupInfoItem);
          
          FMenuItem changePasswordItem = new FMenuItem(FocLangKeys.MENU_CHANGE_PASSWORD, changePasswordAction);
          toolsMenu.addMenu(changePasswordItem);
        }

        FMenuItem changeLanguageItem = new FMenuItem(FocLangKeys.MENU_USER_PREFERENCES, changeLanguageAction);
        toolsMenu.addMenu(changeLanguageItem);

        FMenuList memoryMenuList = new FMenuList("Memory", 'M');
        toolsMenu.addMenu(memoryMenuList);        
        
        FMenuItem garbageCollectorItem = new FMenuItem("Free unused memory", 'R', new AbstractAction(){
          public void actionPerformed(ActionEvent e) {
            String beforeMessage = Globals.logMemory("Before freeing ");
            if(rgc != null){ 
              rgc.dispose();
              rgc = null;
            }
            System.gc();
            Globals.logMemory("");
            
            System.gc();
            Globals.logMemory("");

            System.gc();
            String afterMessage = Globals.logMemory("After  freeing ");

            Globals.getDisplayManager().popupMessage(beforeMessage + "\n" + afterMessage);
          }
        });
        memoryMenuList.addMenu(garbageCollectorItem);       
        
        FMenuItem allocateGarbageItem = new FMenuItem("memory", 'A', new AbstractAction(){
          public void actionPerformed(ActionEvent e) {
            String message = Globals.logMemory("");
            Globals.getDisplayManager().popupMessage(message);
          }
        });
        memoryMenuList.addMenu(allocateGarbageItem);

        FocGroup group = Globals.getApp().getGroup();
        if(group != null && (group.allowDatabaseBackup() || group.allowDatabaseRestore())){
          FMenuList databaseList = new FMenuList("Database", 'D');
          toolsMenu.addMenu(databaseList);
          
          if(group.allowDatabaseBackup()){
	          FMenuItem backupDatabaseItem = new FMenuItem("Backup", 'B', new AbstractAction(){
	  					public void actionPerformed(ActionEvent e) {
	  						try {
		  						DBBackupFileChooser fileChooser = new DBBackupFileChooser(); 
		  						String outputFile = fileChooser.choose();
		  						
		  						if(outputFile != null){
	  	  						DB2ASCII db2Ascii = new DB2ASCII(outputFile, DB2ASCII.COPY_DIRECTION_DB_TO_ASCII);
										db2Ascii.backupRestore();
	  	  						db2Ascii.dispose();
		  						}
								} catch (Exception e1) {
									Globals.logException(e1);
								}
	  					}
	          });
	          databaseList.addMenu(backupDatabaseItem);
          }
          
          if(group.allowDatabaseRestore()){
	          FMenuItem restoreDatabaseItem = new FMenuItem("Restore", 'R', new AbstractAction(){
	  					public void actionPerformed(ActionEvent e) {
	  						try {
		  						DBBackupFileChooser fileChooser = new DBBackupFileChooser(); 
		  						String outputFile = fileChooser.choose();
		  						
		  						if(outputFile != null){
	  	  						DB2ASCII db2Ascii = new DB2ASCII(outputFile, DB2ASCII.COPY_DIRECTION_ASCII_TO_DB);
										db2Ascii.backupRestore();
	  	  						db2Ascii.dispose();
		  						}
								} catch (Exception e1) {
									Globals.logException(e1);
								}
	  					}
	          });
	          databaseList.addMenu(restoreDatabaseItem);
          }
        }
        
        mainMenu.addMenu(toolsMenu);
      }
      
      FMenuList helpMenu = new FMenuList(FocLangKeys.MENU_HELP);
      FMenuItem versionItem = new FMenuItem(FocLangKeys.MENU_ABOUT, aboutAction);
      helpMenu.addMenu(versionItem);
      
      FocHelp focHelp = FocHelp.getInstance();
      focHelp.putHelpFileUrl("Navigation", "help/foc/navigationTips.html");
      focHelp.fillHelpMenu(helpMenu);
      
      /*FMenuItem navigationHelpItem = new FMenuItem("Navigation", 'v', navigationHelpAction);
      helpMenu.addMenu(navigationHelpItem);*/
      
      mainMenu.addMenu(helpMenu);
      
      FMenuList exitMenu = new FMenuList(FocLangKeys.MENU_EXIT);      
      FMenuItem exitItem = new FMenuItem(FocLangKeys.MENU_EXIT, exitAction);      
      exitMenu.addMenu(exitItem);
      mainMenu.addMenu(exitMenu);
              
      setMainFocMenu(mainMenu);
    }
    
    if(withLogin){
      if(getMainAdminMenu() == null){
        setMainAdminMenu(adminModule.getAdminMenu());        
      }
    }
  }

  public void menuConstruction(){
    getDisplayManager().popupMenu(loginStatus);
    
    Iterator iter = modules.values().iterator();
    while(iter != null && iter.hasNext()){
      FocModuleInterface module = (FocModuleInterface) iter.next();
      if (module instanceof FocUniqueModule) {
        FocUniqueModule uniqueModule = (FocUniqueModule) module;
        uniqueModule.afterApplicationEntry();
      }
    }
  }
  
  @SuppressWarnings("serial")
  public void entry(){
  	if(getGuiNavigatorType() != DisplayManager.GUI_NAVIGATOR_NONE){
  		menuPreparation();      
      menuConstruction();      
  	}
  	
    Iterator iter = modules.values().iterator();
    while(iter != null && iter.hasNext()){
      FocModuleInterface module = (FocModuleInterface) iter.next();
      if (module instanceof FocUniqueModule) {
        FocUniqueModule uniqueModule = (FocUniqueModule) module;
        uniqueModule.afterApplicationEntry();
      }
    }
  }
  
  public void reconstructMenu(){
    getDisplayManager().reconstructMenu(loginStatus);
  } 
  
  public void setFocTestSuite(FocTestSuite focTestSuite){
    this.focTestSuite = focTestSuite;
  }
  
  public FocTestSuite getFocTestSuite(){
    return focTestSuite;
  }
 
  public boolean isUnitTesting(){
    return focTestSuite != null;
  }
  
  public void exit(boolean forceEvenIfOpenedWindows){
  	if(!isExiting){
  		isExiting = true;
	    boolean doExit = true;
	    if(!forceEvenIfOpenedWindows){
	      doExit = Globals.getApp().isWithLogin() && Globals.getApp().getLoginStatus() == Application.LOGIN_WAITING;    
	      if(!doExit){
	        doExit = Globals.getDisplayManager().allScreensClosed();
	        if(!doExit){
	          Globals.getDisplayManager().popupMessage("Cannot exit because some windows are still opened!");      
	        }
	      }
	    }
	    if(doExit){
	      //BElias
	      for (int i=0; i <exitListenerList.size(); i++){
	      	IExitListener listener = exitListenerList.get(i);
	        if (listener != null){
	          listener.replyToExit();
	        }
	      }
	      //EElias
	      DBManager dbManager = getDBManager();    
	      if(dbManager != null){
	        dbManager.exit();
	      }
	      Globals.logString("-- Application exit --");
	      if(!isUnitTesting()){
	        System.exit(0);
	      }
	    }
	    isExiting = false;
  	}
  }

  public void exit(){
    exit(false);
  }
  
  public UnitFactory getUnitFactory() {
    if (unitFactory == null) {
      unitFactory = new UnitFactory();
    }
    return unitFactory;
  }
  
  public void declareModule(FocModuleInterface module){
    modules.put(module, module);
  }

  public void declareFocObjects() {
    if(!objectsAlreadyDeclared){
      Iterator iter = modules.values().iterator();
      while(iter != null && iter.hasNext()){
        FocModuleInterface module = (FocModuleInterface) iter.next();
        module.declareFocObjects();
      }
    }
    objectsAlreadyDeclared = true;    
  }

  public DBManager getDBManager() {
    return dbManager;
  }

  public DisplayManager getDisplayManager() {
    return dispManager;
  }

  private void declaredObjectList_DeclareObject(Class classObject, boolean returnNullFocDesc) {
  	ClassFocDescDeclaration classDeclaration = null;
  	if(returnNullFocDesc){
  		classDeclaration = new ClassFocDescDeclarationReturnNull(classObject);
  	}else{
  		classDeclaration = new ClassFocDescDeclaration(classObject);
  	}
    ArrayList<IFocDescDeclaration> focObjectsArrayList = getFocObjectsList();
    if( !focObjectsArrayList.contains(classDeclaration)){
      focObjectsArrayList.add(classDeclaration);  
    }
  }

  public void declaredObjectList_DeclareObject(Class classObject) {
  	declaredObjectList_DeclareObject(classObject, false);
  }

  public void declaredObjectList_DeclareObjectForExistingInstance(Class classObject) {
  	declaredObjectList_DeclareObject(classObject, true);
  }

  public void declaredObjectList_DeclareDescription(Class classObject) {
  	declaredObjectList_DeclareObject(classObject);
  }
  
  public void declaredObjectList_DeclareDescriptionForExistingInstance(Class classObject) {
  	declaredObjectList_DeclareObjectForExistingInstance(classObject);
  }
  
  public void declaredObjectList_DeclareDescription(IFocDescDeclaration focDescDeclaration){
  	ArrayList<IFocDescDeclaration> focObjectsArrayList = getFocObjectsList();
  	focObjectsArrayList.add(focDescDeclaration);
  }

  /*private int declaredObjectList_SizeX() {
    return focObjects.size();
  }

  private Class declaredObjectList_getX(int i) {
    return (Class) focObjects.get(i);
  }*/

  /*public void adaptDataModel() {
    int i;

    dbManager.initAdaptDataModel();

    // Just go and open all descriptions so that links with maters get created
    for (i = 0; i < declaredObjectList_Size(); i++) {
      Class classObj = (Class) declaredObjectList_get(i);
      if (classObj != null) {
        FocDesc.getFocDescriptionForDescClass(classObj);
      }
    }

    for (i = 0; i < declaredObjectList_Size(); i++) {
      Class classObj = declaredObjectList_get(i);
      if (classObj != null) {
        FocDesc focDesc = FocDesc.getFocDescriptionForDescClass(classObj);

        dbManager.adaptTable(focDesc);
        dbManager.adaptTableIndexes(focDesc, false);

        // Scan the descriptions fields and store the links tables
        FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
        while (enumer.hasNext()) {
          FField ffield = enumer.nextField();
          if (ffield != null && ffield.isDBResident()) {
            FocLink link = ffield.getLink();
            FocDesc linkTablDesc = link != null ? link.getLinkTableDesc() : null;
            if (linkTablDesc != null) {
              dbManager.adaptTable(linkTablDesc);
              dbManager.adaptTableIndexes(linkTablDesc, false);
            }
          }
        }

      }
    }
    dbManager.endAdaptDataModel();
    FocVersion.saveVersions();
  }*/
  
  
  public void adaptDataModel(){
  	//BAntoineS - AUTOINCREMENT
  	try{
  	//EAntoineS - AUTOINCREMENT
	    Iterator iter1 = modules.values().iterator();
	    while(iter1 != null && iter1.hasNext()){
	      FocModuleInterface module = (FocModuleInterface) iter1.next();
	      if (module instanceof FocUniqueModule) {
	        FocUniqueModule uniqueModule = (FocUniqueModule) module;
	        uniqueModule.beforeAdaptDataModel();
	      }
	    }
	    
	    dbManager.initAdaptDataModel();
	
	    // Just go and open all descriptions so that links with maters get created
	    Iterator<IFocDescDeclaration> iter = getFocDescDeclarationIterator();
	    while(iter != null && iter.hasNext()){
	    	IFocDescDeclaration focDescDeclaration = iter.next();
	    	if(focDescDeclaration != null){
	    		focDescDeclaration.getFocDesctiption();
	    	}
	    }
	
	    //Do the adapt for the 2 FAB tables
	    //If Altered we need to redo this process after Desc reload from FAB
	    boolean oneOrMoreTableOfFabBasicTableAltered = false;
	    FocDesc fabFocDesc = getFocDescByName(FabModule.getTableDefinitionTableStorageName());
	    if(fabFocDesc != null){
	    	oneOrMoreTableOfFabBasicTableAltered = dbManager.adaptTable(fabFocDesc);
	    }
	    
	    fabFocDesc = getFocDescByName(FabModule.getFieldDefinitionTableStorageName());
	    if(fabFocDesc != null){
	    	oneOrMoreTableOfFabBasicTableAltered =  dbManager.adaptTable(fabFocDesc) || oneOrMoreTableOfFabBasicTableAltered;
	    }
	    
	    if(oneOrMoreTableOfFabBasicTableAltered){
	    	Globals.getDisplayManager().popupMessage("Adapt data model not completed. \n " +
	    			"Please relaunch application and adapt data modle another time.");
	    	exit(true);
	    }
	    
	    iter = getFocDescDeclarationIterator();
	    while(iter != null && iter.hasNext()){
	    	IFocDescDeclaration focDescDeclaration = iter.next();
	    	if(focDescDeclaration != null){
		    	FocDesc focDesc =  focDescDeclaration.getFocDesctiption();
		    	if(focDesc != null){
		        dbManager.adaptTable(focDesc);
		        dbManager.adaptTableIndexes(focDesc, false);
		        //BAntoineS - AUTOINCREMENT
		        dbManager.adaptTableSequence(focDesc);
		        //EAntoineS - AUTOINCREMENT
		
		        // Scan the descriptions fields and store the links tables
		        FocFieldEnum enumer = focDesc.newFocFieldEnum(FocFieldEnum.CAT_LIST, FocFieldEnum.LEVEL_PLAIN);
		        while (enumer.hasNext()) {
		          FField ffield = enumer.nextField();
		          if (ffield != null && ffield.isDBResident()) {
		            FocLink link = ffield.getLink();
		            FocDesc linkTablDesc = link != null ? link.getLinkTableDesc() : null;
		            if (linkTablDesc != null) {
		              dbManager.adaptTable(linkTablDesc);
		              dbManager.adaptTableIndexes(linkTablDesc, false);
		              //BAntoineS - AUTOINCREMENT
		    	        dbManager.adaptTableSequence(linkTablDesc);
		    	        //EAntoineS - AUTOINCREMENT
		            }
		          }
		        }
		    	}
	      }
	    }
	    dbManager.endAdaptDataModel();
	    FocVersion.saveVersions();
	    
	    iter1 = modules.values().iterator();
	    while(iter1 != null && iter1.hasNext()){
	      FocModuleInterface module = (FocModuleInterface) iter1.next();
	      if (module instanceof FocUniqueModule) {
	        FocUniqueModule uniqueModule = (FocUniqueModule) module;
	        uniqueModule.afterAdaptDataModel();
	      }
	    }
	  //BAntoineS - AUTOINCREMENT
  	}catch(Exception e){
  		Globals.logException(e);
  		if(Globals.getDisplayManager() != null){
  			Globals.getDisplayManager().popupMessage("Adapt data model failed! and Aborted!");
  		}
  	}
	  //EAntoineS - AUTOINCREMENT
  }
  

  /**
   * @return Returns the defaultAccessControl.
   */
  public AccessControl getDefaultAccessControl() {
    return defaultAccessControl;
  }
   
  /**
   * @return Returns the focIcons.
   */
  public FocIcons getFocIcons() {
    return focIcons;
  }
  
  /**
   * @return Returns the logFile.
   */
  public PrintStream getLogFile() {
    return logFile;
  }
  
  public boolean isWithLogin() {
    return withLogin;
  }
  
  public int getLoginStatus() {
    return loginStatus;
  }
  
  public void setLoginStatus(int loginStatus) {
    this.loginStatus = loginStatus;
  }
  
  public FMenu getMainAppMenu() {
    return mainAppMenu;
  }
  
  public void setMainAppMenu(FMenu mainAppMenu) {
    this.mainAppMenu = mainAppMenu;
    
    if(isCashDeskModuleIncluded() && mainAppMenu.isList()){
      FMenuList menuList = CashDeskModule.newMenuList();
      if(menuList != null){
        ((FMenuList) mainAppMenu).addMenu(menuList);
      }
    }
  }
  
  public FMenu getMainFocMenu() {
    return mainFocMenu;
  }
  
  public void setMainFocMenu(FMenu mainFocMenu) {
    this.mainFocMenu = mainFocMenu;
  }
  
  public FMenu getMainAdminMenu() {
    return mainAdminMenu;
  }
  
  public void setMainAdminMenu(FMenu mainAdminMenu) {
    this.mainAdminMenu = mainAdminMenu;
  }
  
  public FocUser getUser() {
    return user;
  }

  public void updateLanguage(){
    if(user != null){
      FMultipleChoice langMulti = (FMultipleChoice) user.getFocProperty(FocUserDesc.FLD_LANGUAGE);
      if(langMulti != null){
        MultiLanguage.setCurrentLanguage(langMulti.getInteger());
        reconstructMenu();
      }
    }
  }
  
  public void setUser(FocUser user) {
    this.user = user;
    updateLanguage();
    if(user != null){
      FMultipleChoice langMulti = (FMultipleChoice) user.getFocProperty(FocUser.FLD_LANGUAGE);
      if(langMulti != null){
        langMulti.addListener(new FPropertyListener(){
          public void propertyModified(FProperty prop){
            updateLanguage();            
          }

          public void dispose() {
            // TODO Auto-generated method stub
            
          }
        });
      }
    }
  }
  
  public FocGroup getGroup() {
    return user != null ? user.getGroup() : null;
  }

  public FocObject getAppGroup() {
    return user != null ? user.getAppGroup() : null;
  }
  
  public DebugOutputInterface getDebugOutputInterface() {
    return debugOutputInterface;
  }
  
  public void setDebugOutputInterface(DebugOutputInterface debugOutputInterface) {
    this.debugOutputInterface = debugOutputInterface;
  }
  
  public void debugOutput(String message){
    if(debugOutputInterface != null) debugOutputInterface.debugOutput(message);
  }
  
  public java.sql.Date getSystemDate() {
	java.sql.Date computerDate = new java.sql.Date(System.currentTimeMillis());
	
	if (getDBManager() == null){
		systemDate = computerDate;
	}else if (systemDate == null){
		systemDate = getDBManager().getCurrentDate();
	}else {
		if ( !computerDate.toString().equals(systemDate) ) {  // yyyy-mm-dd
    		systemDate = getDBManager().getCurrentDate();
		}
	}
	
    return systemDate;
  }
  
  //BElias
  public void addExitListener(IExitListener exitListener){
    if (exitListenerList == null){
      exitListenerList = new ArrayList<IExitListener>();
    }
    exitListenerList.add(exitListener);
  }
  //EElias
  
  //BElias
 /* public void RemoveExitListener(IExitListener exitListener){
    if (exitListenerList == null){
      exitListenerList = new HashMap <Integer,IExitListener>();
    }
    exitListenerList.remove(exitListener);
  }*/
  //EElias
  
  public boolean isCashDeskModuleIncluded() {
    return cashDeskModuleIncluded;
  }
  
  public void setCashDeskModuleIncluded(boolean cashDeskModuleIncluded) {
    this.cashDeskModuleIncluded = cashDeskModuleIncluded;
  }
  
  public boolean isCurrencyModuleIncluded() {
    return currencyModuleIncluded;
  }
  
  public void setCurrencyModuleIncluded(boolean currencyModuleIncluded) {
    this.currencyModuleIncluded = currencyModuleIncluded;
  }
  
  public boolean isDisableMenus() {
    return disableMenus;
  }
  
  public void setDisableMenus(boolean disableMenus) {
    this.disableMenus = disableMenus;
  }
  public boolean isDoNotCheckTables() {
    return doNotCheckTables;
  }
  public void setDoNotCheckTables(boolean doNotCheckTables) {
    this.doNotCheckTables = doNotCheckTables;
  }

	public boolean isMdi() {
		return guiNavigatorType == DisplayManager.GUI_NAVIGATOR_MDI;
	}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDemo() {
    return isDemo;
  }

  public void setDemo(boolean isDemo) {
    this.isDemo = isDemo;
  }

  public int getTrialPeriod() {
    return trialPeriod;
  }

  public void setTrialPeriod(int days) {
    this.trialPeriod = days;
  }

	public int getGuiNavigatorType() {
		return guiNavigatorType;
	}

  public AdminModule getAdminModule() {
    return adminModule;
  }
}