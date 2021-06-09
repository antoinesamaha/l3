// GET SET
// ADMIN
// MENU
// FIND CHILD
// GUI COMPONENT SET VALUE
// INTERNAL FRAME
// TABLE
// TABBED PANE
// BUTTON
// LOGIN - EXIT
// ASSERT
// LOG

package b01.foc.fUnit;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import junit.framework.TestCase;

import b01.foc.Globals;
import b01.foc.IExitListener;
import b01.foc.admin.FocUser;
import b01.foc.db.lock.LockManager;
import b01.foc.desc.FocDesc;
import b01.foc.gui.DisplayManager;
import b01.foc.gui.FDesktop;
import b01.foc.gui.FGButton;
import b01.foc.gui.FGCheckBox;
import b01.foc.gui.FGComboBox;
import b01.foc.gui.FGDateField;
import b01.foc.gui.FGNumField;
import b01.foc.gui.FGObjectComboBox;
import b01.foc.gui.FGTextField;
import b01.foc.gui.FListPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.gui.InternalFrame;
import b01.foc.gui.Navigator;
import b01.foc.gui.table.FAbstractTableModel;
import b01.foc.gui.table.FTable;
import b01.foc.gui.table.FTableModel;

public class FocTestCase extends TestCase implements IExitListener {
  private String userName = null;
  private boolean endOfTest = false;
  private Container defaultRootContainer = null;
  private FocDesc defaultFocDesc = null;
  private FocTestSuite focTestSuite = null;
  public static final int SLEEP_SCALE = 2000;
  public static final int DEFAULT_NUMBER_OF_ATTEMPTS = 10;
  private Class klass = null;
  private boolean failure = true;
  
  public FocTestCase(String functionName) {
    super(functionName);
  }
  
  public FocTestCase() {
  }
  
  public FocTestCase(FocTestSuite testSuite, String functionName) {
    super(functionName);
    this.focTestSuite = testSuite;
  }
  
  public FocTestCase(FocTestSuite testSuite, String name, String userName, Class klass) {
    this(testSuite, name);
    this.userName = userName;
    this.klass = klass;
  }
  
  public void sleep(double val) {
    try {
      Thread.sleep((long) (val * SLEEP_SCALE));
    } catch (Exception e) {
      Globals.logException(e);
    }
  }
  
  public FocTestSuite getFocTestSuite() {
    return focTestSuite;
  }
  
  protected void setUp() throws Exception {
    logBeginTest(getName());
    logSuiteName(getFocTestSuite().getName());
  }
  
  protected void tearDown() throws Exception {
    logStatus(!failure);
    logEndTest(getName());
  }
  
  // #######################################
  // #######################################
  // GET SET
  // #######################################
  // #######################################
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public Container getDefaultRootContainer() {
    if (defaultRootContainer == null) {
      defaultRootContainer = getMainFrame();
    }
    return defaultRootContainer;
  }
  
  public void setDefaultRootContainer(Container container) {
    this.defaultRootContainer = container;
  }
  
  public FocDesc getDefaultFocDesc() {
    return defaultFocDesc;
  }
  
  public void setDefaultFocDesc(FocDesc defaultFocDesc) {
    this.defaultFocDesc = defaultFocDesc;
  }
  
  public JFrame getMainFrame() {
    DisplayManager display = null;
    JFrame mainFrame = null;
    while (mainFrame == null) {
      display = b01.foc.Globals.getDisplayManager();
      if (display != null) {
        mainFrame = display.getMainFrame();
      }
      if (mainFrame == null) {
        sleep(0.5);
      }
    }
    return mainFrame;
  }

  // #######################################
  // #######################################
  // ADMIN
  // #######################################
  // #######################################

  public void unlockAllLockableObjects(){
    LockManager lockManager = new LockManager();
    lockManager.unlockAllObjectsForAllDescriptions();
    lockManager.dispose();
    //lockManager = null;
  }
  
  // #######################################
  // #######################################
  // MENU
  // #######################################
  // #######################################
  
  public JMenuBar getMenuBar(int nbrOfAttempts) {
    JMenuBar menuBar = null;
    for (int i = 0; i < nbrOfAttempts; i++) {
      DisplayManager display = b01.foc.Globals.getDisplayManager();
      
      if (display != null) {
        Navigator navigator = display.getNavigator();
        Globals.logString(" Navigator:" + navigator.getClass().getName());
        if (navigator != null && navigator instanceof FDesktop) {
          FDesktop desktop = (FDesktop) navigator;
          menuBar = desktop.getMenuBar();
          break;
        }
      }
      
      if (menuBar == null) {
        sleep(0.5);
      }
    }
    
    focAssertNotNull("Menu Bar found ? ", menuBar);
    
    return menuBar;
  }
  
  public void menu_Click(String menuItemName) {
    sleep(1);
    logBeginGuiAction("JMenuItem", menuItemName, "Click");
    JMenuBar menuBar = getMenuBar(DEFAULT_NUMBER_OF_ATTEMPTS);
    JMenuItem menu = (JMenuItem) getChildNamed(menuBar, "MENU", menuItemName, DEFAULT_NUMBER_OF_ATTEMPTS);
    // Since there is AssertNotNull in function getChildNamed
    // focAssertNotNull("Menu Found ?", menu);
    menu.doClick();
    logEndGuiAction();
  }
  
  public void menu_ClickByPath(String[] path) {
    boolean found = false;
    sleep(1);
    logBeginGuiAction("JMenuItem", path[path.length - 1], "Click");
    JMenuBar menuBar = getMenuBar(DEFAULT_NUMBER_OF_ATTEMPTS);
    for (int i = 0; i < menuBar.getMenuCount() && !found; i++) {
      if (menuBar.getMenu(i).getText().equals(path[0])) {
        JMenuItem curMenuItem = menuBar.getMenu(i);
        int x = 1;
        
        while (!found && curMenuItem != null && x <= path.length) {
          if (curMenuItem instanceof JMenu) {
            JMenu curMenu = (JMenu) curMenuItem;
            JMenuItem nextMenu = null;
            
            for (int j = 0; j < curMenu.getItemCount() && nextMenu == null; j++) {
              JMenuItem tempMenuItem = curMenu.getItem(j);
              if (tempMenuItem.getText().equals(path[x])) {
                nextMenu = tempMenuItem;
              }
            }
            
            curMenuItem = nextMenu;
            x++;
          } else if (x == path.length) {
            curMenuItem.doClick();
            found = true;
          } else {
            curMenuItem = null;
          }
        }
      }
    }
    focAssertTrue("Menu found and clicked", found);
    logEndGuiAction();
  }
  
  /*
   * public void menu_ClickByPath(String[] path) { sleep(1);
   * logBeginGuiAction("JMenuItem", path[path.length - 1], "Click"); JMenuBar
   * menuBar = getMenuBar(DEFAULT_NUMBER_OF_ATTEMPTS); for (int i = 0; i <
   * menuBar.getMenuCount(); i++) { if
   * (menuBar.getMenu(i).getText().equals(path[0])) { for (int y = 0; y <
   * menuBar.getMenu(i).getItemCount(); y++) { if
   * (menuBar.getMenu(i).getItem(y).getText().equals(path[1])) { if
   * (menuBar.getMenu(i).getItem(y) instanceof JMenuItem && path.length == 2) {
   * JMenuItem jmi = (JMenuItem) menuBar.getMenu(i).getItem(y); jmi.doClick();
   * logEndGuiAction(); } else { JMenu jm = (JMenu)
   * menuBar.getMenu(i).getItem(y); for (int x = 2; x < path.length; x++) { for
   * (int j = 0; j < jm.getItemCount(); j++) { if
   * (jm.getItem(j).getText().equals(path[x])) {
   * System.out.println(jm.getItem(j).getText()); if (x == (path.length - 1)) {
   * jm.getItem(j).doClick(); logEndGuiAction(); break; } jm = (JMenu)
   * jm.getItem(j); } } } } } } } } }
   */
  // #######################################
  // #######################################
  // FIND CHILD
  // #######################################
  // #######################################
   private static int recursiveLevelToDelete = 1;
  private static Component getChildNamed_Recursive(Component parent, String name, boolean includingNonVisible) {
    Component ret = null;
    recursiveLevelToDelete++;
    if (includingNonVisible || parent.isVisible()) {
      if (name.equals(parent.getName())) {
        ret = parent;
      } else {
        Component[] children = (parent instanceof JMenu) ? ((JMenu) parent).getMenuComponents() : ((Container) parent).getComponents();
        for (int i = 0; i < children.length && ret == null; i++) {
          String debugString = ""; for(int xx=0; xx<recursiveLevelToDelete;xx++) debugString+=" ";
          //Globals.logString(debugString+"comp:"+children[i].getName());

          Component child = getChildNamed_Recursive(children[i], name, includingNonVisible);
          if (child != null) {
            ret = child;
          }
        }
      }
    }
    recursiveLevelToDelete--;
    return ret;
  }
  
  public Component getChildNamed(Component parent, String name, int nbrAttempts, boolean includeNotVisible) {
    Component ret = null;
    
    if (parent == null)
      parent = getDefaultRootContainer();
    
    for (int a = 0; a < nbrAttempts; a++) {
      ret = getChildNamed_Recursive(parent, name, includeNotVisible);
      
      if (ret != null)
        break;
      sleep(0.5);
    }
    
    focAssertNotNull("Gui Component '" + name+ "' Found", ret);
    
    return ret;
  }
  
  private String composeName(String table, String field){
  	String str = null;
  	if(table != null && table.length() > 0){
  		str = table + "." + field;
  	}else{
  		str = field;
  	}
  	return str;
  }
  
  public Component getChildNamed(Component parent, String name, int nbrAttempts) {
    return getChildNamed(parent, name, nbrAttempts, false);
  }
  
  public Component getChildNamed(Component parent, String name) {
    return getChildNamed(parent, name, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(Container parent, String table, String field, int nbrAttempts) {
    return getChildNamed(parent, composeName(table, field), nbrAttempts);
  }
  
  public Component getChildNamed(Container parent, String table, String field) {
    return getChildNamed(parent, composeName(table, field), DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(String table, String field, int nbrAttempts) {
    return getChildNamed((Container) null, composeName(table, field), nbrAttempts);
  }
  
  public Component getChildNamed(String table, String field) {
    return getChildNamed((Container) null, composeName(table, field), DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(String name, int nbrAttempts, boolean includeNotVisible) {
    return getChildNamed((Container) null, name, nbrAttempts, includeNotVisible);
  }
  
  public Component getChildNamed(String name, int nbrAttempts) {
    return getChildNamed((Container) null, name, nbrAttempts);
  }
  
  public Component getChildNamed(String name) {
    return getChildNamed((Container) null, name, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public Component getChildNamed(String name, boolean includeNotVisible) {
    return getChildNamed((Container) null, name, DEFAULT_NUMBER_OF_ATTEMPTS, includeNotVisible);
  }
  
  // #######################################
  // #######################################
  // GET CHILD INDEXED - for modal dialog box
  // #######################################
  // #######################################
  
  static int counter;
  
  public static Component getChildIndexed(Component parent, String klass, int index) {
    counter = 0;
    if (parent instanceof Window) {
      Component[] children = ((Window) parent).getOwnedWindows();
      for (int i = 0; i < children.length; ++i) {
        if (children[i] instanceof Window && !((Window) children[i]).isActive()) {
          continue;
        }
        Component child = getChildIndexedInternal(children[i], klass, index);
        if (child != null) {
          return child;
        }
      }
    }
    return null;
  }
  
  private static Component getChildIndexedInternal(Component parent, String klass, int index) {
    if (parent.getClass().toString().endsWith(klass)) {
      if (counter == index) {
        return parent;
      }
      ++counter;
    }
    if (parent instanceof Container) {
      Component[] children = (parent instanceof JMenu) ? ((JMenu) parent).getMenuComponents() : ((Container) parent).getComponents();
      for (int i = 0; i < children.length; i++) {
        Component child = getChildIndexedInternal(children[i], klass, index);
        if (child != null) {
          return child;
        }
      }
    }
    return null;
  }
  
  // #######################################
  // #######################################
  // GUI COMPONENT SET VALUE
  // #######################################
  // #######################################
  
  public void preventAlteringIfDisabled(Component comp) {
    if (!comp.isEnabled()) {
      comp.setFocusable(false);
    }
  }
  
  private void requestFocus(Component comp){
  	boolean focused = comp.hasFocus();
  	int attempts = 0;
  	focused=true;
  	while(!focused && attempts < DEFAULT_NUMBER_OF_ATTEMPTS){
  		focused = comp.requestFocusInWindow();
  		Globals.logString("focused = "+focused);
  		if(!focused){
  			sleep(5);
  		}
  		attempts++;
  	}
  	
  	focAssertTrue("RequestFocuse for ", focused);
  }
  
  public void guiComponent_SetValue(Container rootContainer, String table, String field, Object value, int maxAttempts) {
    Component comp = (Component) getChildNamed(rootContainer, table, field, maxAttempts);
    preventAlteringIfDisabled(comp);
    
    // focAssertNotNull("Gui component found ? ", comp);
    if (comp instanceof FGTextField || comp instanceof FGDateField || comp instanceof FGNumField) {
      //logBeginGuiAction(comp.getClass().getSimpleName(), comp.getName(), "setText '" + value + "'"); //rr
      logBeginGuiAction(comp.getClass().getSimpleName(), comp.getName(), "setText" + value);
      JTextField textField = (JTextField) comp;
      requestFocus(comp);
      textField.setText("" + value);
      textField.postActionEvent();
      logEndGuiAction();
    } else if (comp instanceof FGObjectComboBox) {
      //logBeginGuiAction("FGObjectComboBox", comp.getName(), "Select '" + value + "'");//rr
      logBeginGuiAction("FGObjectComboBox", comp.getName(), "Select " + value);
      FGObjectComboBox combo = (FGObjectComboBox) comp;
      requestFocus(comp);
      combo.setSelectedItem(value);
      logEndGuiAction();
    } else if (comp instanceof FGComboBox) {
      //logBeginGuiAction("FGComboBox", comp.getName(), "Select '" + value + "'");//rr
      logBeginGuiAction("FGComboBox", comp.getName(), "Select " + value);
      FGComboBox combo = (FGComboBox) comp;
      requestFocus(comp);
      combo.setSelectedItem(value);
      logEndGuiAction();
    } else if (comp instanceof FGCheckBox) {
      logBeginGuiAction("FGCheckBox", comp.getName(), "Selected");
      FGCheckBox chkbox = (FGCheckBox) comp;
      requestFocus(comp);
      chkbox.setSelected((Boolean) value);
      logEndGuiAction();
    }
  }
  
  public Component getComponent(String compName) {
    return getChildNamed(compName, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  /*
   * public void guiComponent_SetValue(String compName, Object val) { Component
   * comp = getComponent(compName);
   * 
   * if (comp != null) { if (comp instanceof FGTextField) { FGTextField
   * textField = (FGTextField) comp; textField.setText(""+val);
   * textField.postActionEvent(); } else if (comp instanceof FGObjectComboBox) {
   * FGObjectComboBox combo = (FGObjectComboBox) comp;
   * combo.setSelectedItem(val); } else if (comp instanceof FGComboBox) {
   * FGComboBox combo = (FGComboBox) comp; combo.setSelectedItem(val); }else if
   * (comp instanceof FGCheckBox){ FGCheckBox chkbox = (FGCheckBox) comp;
   * chkbox.setSelected((Boolean) val); } sleep(1); } }
   * 
   * /*public void guiComponent_SetValue(String compName, Object val) {
   * 
   * Component comp = getComponent(compName); if (comp instanceof JCheckBox) {
   * JCheckBox chkbox = (JCheckBox) comp; chkbox.setSelected((Boolean) val); }
   * else if (comp instanceof JComboBox) { JComboBox cb = (JComboBox) comp;
   * cb.setSelectedItem(val); } }
   */

  public void guiComponent_SetValue(String table, String field, Object value, int maxAttempts) {
    guiComponent_SetValue(null, table, field, value, maxAttempts);
  }
  
  public void guiComponent_SetValue(Container rootContainer, String table, String field, Object value) {
    guiComponent_SetValue(rootContainer, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void guiComponent_SetValue(String table, String field, Object value) {
    guiComponent_SetValue(null, table, field, value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  public void guiComponent_SetValue(int fieldId, Object value) {
    assertNotNull("Default FocDesc not NULL ?", getDefaultFocDesc());
    guiComponent_SetValue(getDefaultFocDesc().getStorageName(), getDefaultFocDesc().getFieldByID(fieldId).getName(), value, DEFAULT_NUMBER_OF_ATTEMPTS);
  }
  
  protected String convertDateInGuiTypingFormat(Date date){
  	String stringDate = "";
  	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  	stringDate = dateFormat.format(date);
  	return stringDate;
  }

  protected String getCurrentDateInStringFormat(){
  	return convertDateInGuiTypingFormat(Globals.getApp().getSystemDate());
  }

  // #######################################
  // #######################################
  // INTERNAL FRAME
  // #######################################
  // #######################################
  
  public InternalFrame internalFrame_Activate(String name) throws Exception{
    InternalFrame internalFrame = (InternalFrame) getChildNamed(name + ".INTFRAME", DEFAULT_NUMBER_OF_ATTEMPTS);
    internalFrame.setIcon(false);
    internalFrame.setSelected(true);
    internalFrame.setVisible(false);
    internalFrame.setVisible(true);
    Globals.getApp().getDisplayManager().restoreInternalFrame(internalFrame);
    return internalFrame ;
  }

  public void internalFrame_Iconify(String name, boolean iconify) throws Exception{
    InternalFrame internalFrame = (InternalFrame) getChildNamed(name + ".INTFRAME", DEFAULT_NUMBER_OF_ATTEMPTS);
    internalFrame.setIcon(iconify);
  }

  // #######################################
  // #######################################
  // TABLE
  // #######################################
  // #######################################
  
  public FTableModel table_GetTableModel() {
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    FTableModel model = (FTableModel) fTable.getModel();
    return model;
  }
  
  public void table_SetValue(int row, int columnId, Object value) {
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    FAbstractTableModel model = (FAbstractTableModel) fTable.getModel();
    int col = model.getTableView().getColumnIndexForId(columnId);
    logBeginGuiAction("Table cell", "("+row +" , "+col+")","writing : "+ value.toString());
    model.setValueAt(value, row, col);
    logEndGuiAction();
  }
  
  public String table_GetValue(int row, int columnId){
    logBeginGuiAction("JTable", "Table", "GetValue");
    FTable fTable = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    FTableModel model = (FTableModel) fTable.getModel();
    int col = model.getTableView().getColumnIndexForId(columnId);
    String value = (String)model.getValueAt(row, col);
    logEndGuiAction();
    return value;
  }

  public int table_ClickInsertButtonAndWaitForNewLine() {
    FTableModel model = table_GetTableModel();
    int rowCount0 = model.getRowCount();
    button_Click(FListPanel.BUTTON_INSERT);
    int rowCount1 = model.getRowCount();
    for (int att = 0; att < 5 && rowCount0 == rowCount1; att++) {
      sleep(1);
      rowCount1 = model.getRowCount();
    }
    focAssertEquals("Insertion on new line", rowCount0 + 1, rowCount1);
    return rowCount1 - 1;
  }

  public void table_ClickInsertButton() {
    button_Click(FListPanel.BUTTON_INSERT);
  }
  
  private int table_FindAndSelectRowByColumn(FTable fTable, int col, Object cellValue, boolean doAssert) {
    int index = -1;
    boolean found = false;
    for (int i = 0; i < fTable.getRowCount() && !found; i++) {
      Object realCellValue = (Object) fTable.getValueAt(i, col);
      if (realCellValue.equals(cellValue)) {
        fTable.changeSelection(i, col, false, false);
        index = i;
        found = true;
      }
    }
    if (doAssert) {
      focAssertTrue("Finding Cell with value '" + cellValue + "' at row: " + index, found);
    }
    return index;
  }
  
  public int table_FindAndSelectRow(int columnId, Object cellValue) {
    FTable fTable = getTable();
    int col = fTable.getTableView().getColumnIndexForId(columnId);
    return table_FindAndSelectRowByColumn(fTable, col, cellValue, true);
  }

  public int table_FindAndSelectRow(Object cellValue) {
    return table_FindAndSelectRow(cellValue, true);
  }
  
  public int table_FindAndSelectRow(Object cellValue, boolean doAssert) {
    int index = -1;
    boolean found = false;
    FTable fTable = getTable();
    for (int j = 0; j < fTable.getColumnCount() && !found; j++) {
      index = table_FindAndSelectRowByColumn(fTable, j, cellValue, false);
      found = index >= 0;
    }
    if(doAssert){
      focAssertTrue("Finding Cell with value '" + cellValue + "' at row:" + index, found);
    }
    return index;
  }
  
  public FTable getTable() {
    FTable table = (FTable) getChildNamed(getDefaultFocDesc().getStorageName() + ".TABLE", DEFAULT_NUMBER_OF_ATTEMPTS);
    // focAssertNotNull("Table found ? ", table);
    return table;
  }
  
  // #######################################
  // #######################################
  // TABBED PANE
  // #######################################
  // #######################################
  
  public void Click_TabbedPane(String paneTitle) {
    Component comp = getChildNamed(paneTitle, DEFAULT_NUMBER_OF_ATTEMPTS, true);
    
    ArrayList<Component> tabbedArray = new ArrayList<Component>();
    if (comp != null) {
      Component root = getDefaultRootContainer();
      
      Component parent = comp;
      while (parent != root && parent != null) {
        tabbedArray.add(parent);
        parent = parent.getParent();
      }
      
      for (int i = (tabbedArray.size() - 1); i > 0; i--) {
        Component c = tabbedArray.get(i);
        if (c instanceof JTabbedPane) {
          JTabbedPane tp = (JTabbedPane) c;
          requestFocus(tp);          
          tp.setSelectedComponent(tabbedArray.get(i - 1));
          sleep(1);
        }
      }
    }
  }
  
  // #######################################
  // #######################################
  // BUTTON
  // #######################################
  // #######################################
  
  public void button_Click(Container rootContainer, String storageName, String buttonSuffix, int maxAttempts) {
    logBeginGuiAction("JButton", buttonSuffix, "Click");
    AbstractButton button = (AbstractButton) getChildNamed(rootContainer, storageName, buttonSuffix, maxAttempts);
    preventAlteringIfDisabled(button);
    final AbstractButton usedButton = button;
    
    focAssertTrue("Focused :" + usedButton.getName(), usedButton.requestFocusInWindow());
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        usedButton.doClick();
      }
    });
    logEndGuiAction();
  }
  
  public void button_Click(String buttonSuffix, int maxAttempts) {
    assertNotNull("Default FocDesc NULL", getDefaultFocDesc());
    button_Click(null, getDefaultFocDesc().getStorageName(), buttonSuffix, maxAttempts);
  }
  
  public void button_Click(String buttonSuffix) {
    button_Click(buttonSuffix, DEFAULT_NUMBER_OF_ATTEMPTS);
  }

  public void button_ClickValidateCancel(Container rootContainer, String preButtonName, String buttonName) {
    sleep(0.5);
    logBeginGuiAction("JButton", preButtonName + " " + buttonName, "Click");
    FGButton validButton = null;
    if (rootContainer != null) {
      validButton = (FGButton) getChildNamed(rootContainer, buttonName);
    } else {
      // validButton = (FGButton) getChildNamed(rootContainer,
      // preButtonName+".FVALIDATIONPANEL.VALIDATE");
      // to be removed later ( for finding hidden buttons )
      validButton = (FGButton) getChildNamed(preButtonName + ".FVALIDATIONPANEL.VALIDATE", true);
    }
    
    preventAlteringIfDisabled(validButton);
    validButton = (FGButton) validButton;
    requestFocus(validButton);
    validButton.doClick();
    logEndGuiAction();
  }
  
  public void button_ClickValidate(Container rootContainer, String preButtonName) {
  	button_ClickValidateCancel(rootContainer, preButtonName, FValidationPanel.BUTTON_VALIDATE);
  }
  
  public void button_ClickValidate(String preButtonName) {
    button_ClickValidate(null, preButtonName);
  }

  public void button_ClickCancel(Container rootContainer, String preButtonName) {
  	button_ClickValidateCancel(rootContainer, preButtonName, FValidationPanel.BUTTON_CANCEL);
  }
  
  public void button_ClickCancel(String preButtonName) {
    button_ClickCancel(null, preButtonName);
  }

  public void button_ClickWithModalDialogBox(final FocDesc desc, final String buttonName) {
    sleep(1);
    button_ClickIfModalDialogBoxShows("Yes", new Runnable() {
      public void run() {
        setDefaultFocDesc(desc);
        button_Click(buttonName);
      }
    });
  }
 
  public boolean button_ClickIfModalDialogBoxShows(String buttonText, Runnable runnable){
    
    SwingUtilities.invokeLater(runnable);
    
    JButton button = null;
    for (int i = 0; button == null; i++) {
      sleep(0.2);
      button = (JButton) getChildIndexed(getMainFrame(), "JButton", 0);
      if(button != null){
      int buttonIndex = 0;
        while(!button.getText().equals(buttonText)){
          sleep(0.2);
          button = (JButton) getChildIndexed(getMainFrame(), "JButton", buttonIndex++);
          assertTrue(buttonIndex < 5);
        }
      }  
      assertTrue(i < 10);
    }
    boolean isJOptionPane = false;
    boolean isJDialog = false;
    if(button != null){
      Component comp = (Component)button;
      while ( comp != null){
        comp = comp.getParent();
        if( comp instanceof JOptionPane){
          isJOptionPane = true;
        }else if ( comp instanceof JDialog ){
          isJDialog = true;
        }
      }
    }
    if( isJOptionPane && isJDialog ){
      sleep(0.5);
      button.requestFocusInWindow();
      sleep(0.5);
      button.doClick();
      return true;
    }else{
      return false;
    }
  }
  
  
  // #######################################
  // #######################################
  // ASSERT
  // #######################################
  // #######################################
  
  public void focAssertNotSame(String message, Object obj1, Object obj2) {
    failure = true;
    logBeginAssert(message);
    assertNotSame(message, obj1, obj2);
    failure = false;
    logEndAssert();
  }
  
  public void focAssertEquals(String message, Object obj1, Object obj2) {
    failure = true;
    logBeginAssert(message);
    assertEquals(message, obj1, obj2);
    failure = false;
    logEndAssert();
  }
  
  public void focAssertTrue(String message, boolean arg1) {
    failure = true;
    logBeginAssert(message);
    assertTrue(message, arg1);
    failure = false;
    logEndAssert();
  }
  
  public void focAssertNotNull(String message, Object obj) {
    failure = true;
    logBeginAssert(message);
    assertNotNull(message, obj);
    failure = false;
    logEndAssert();
  }
  
  // #######################################
  // #######################################
  // LOGIN - EXIT
  // #######################################
  // #######################################
  
  public void testLogin() throws Exception {
    typeUserLogin(userName);
    Method meth = klass.getMethod("main", new Class[] { String[].class });
    meth.invoke(this, new Object[] { new String[] {"/unitTesting"} });
    Globals.getApp().setFocTestSuite(getFocTestSuite());
  }
  
  public void testExit() throws Exception {
    Globals.getApp().addExitListener(this);
    menu_Click("Exit");
    blockUntilExit();
  }

  public void testBlockUntilExit() throws Exception {
    blockUntilExit();
  }

  public void typeUserLogin(final String userName) {
    setUserName(userName);
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        sleep(5);/* When database connection is very slow this is usefull */
        JFrame mainFrame = getMainFrame();
        logBeginGuiAction("TextField", "Login", "Insert " + userName);
        guiComponent_SetValue(mainFrame, FocUser.getFocDesc().getStorageName(), FocUser.FLDNAME_NAME, getUserName(), 3*DEFAULT_NUMBER_OF_ATTEMPTS);
        logEndGuiAction();
        button_ClickValidate(mainFrame, "");
      }
    });
  }
  
  public boolean isEndOfTest() {
    return endOfTest;
  }
  
  public void setEndOfTest(boolean endOfTest) {
    this.endOfTest = endOfTest;
    // sleep(2000);//Should be > blockUntilExit
  }
  
  public void blockUntilExit() {
    while (!isEndOfTest()) {
      sleep(0.1);// Should be < setEndOfTest(true);
    }
  }
  
  public void replyToExit() {
    setEndOfTest(true);
  }
  
  // #######################################
  // #######################################
  // LOG
  // #######################################
  // #######################################
  
  public void logBeginAssert(String message) {
    focTestSuite.getLogFile().logBeginAssert(message);
  }
  
  public void logEndAssert() {
    focTestSuite.getLogFile().logEndAssert();
  }
  
  public void logBeginStep(String message) {
    focTestSuite.getLogFile().logBeginStep(message);
  }
  
  public void logEndStep() {
    focTestSuite.getLogFile().logEndStep();
  }
  
  public void logBeginGuiAction(String component, String componentName, String action) {
    focTestSuite.getLogFile().logBeginGuiAction(component, componentName, action);
  }
  
  public void logEndGuiAction() {
    focTestSuite.getLogFile().logEndGuiAction();
  }
  
  public void logStatus(boolean success) {
    focTestSuite.getLogFile().logStatus(success);
  }
  
  public void logBeginTest(String testName) {
    focTestSuite.getLogFile().logBeginTest(testName);
  }
  
  public void logEndTest(String testName) {
    focTestSuite.getLogFile().logEndTest(testName);
  }
  
  public void logSuiteName(String suiteName) {
    focTestSuite.getLogFile().logSuiteName(suiteName);
  }
}