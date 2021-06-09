// FOCUS LOCK
// CURRENT EDIT

/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import b01.foc.*;
import b01.foc.admin.*;
import b01.foc.menu.*;
import b01.foc.gui.lock.*;

/**
 * @author 01Barmaja
 */
public class DisplayManager {
  private Color backgroundColor = null;  
  
  //Main frame est toujours la. desktop est null ou pas selon si on est en MDI ou pas
  private MainFrame mainFrame = null;
  private Navigator navigator = null;
  private int guiNavigatorType = GUI_NAVIGATOR_MDI;
  private ArrayList<FDialog> activeDialogList = null;

  private FocusLock focusLock = null;
  //private FocusLock currentEdit = null;
  private Font defaultFont = null;
  private Font defaultNotEditableFont = null;
  private Color disabledFieldBackground = new Color(235, 235, 235);
  private Color disabledTextColor = Color.BLACK;
  
  private int CHAR_WIDTH = 0;
  private int CHAR_HEIGHT = 0;
 
  public static final int GUI_NAVIGATOR_NONE      = 0;
  public static final int GUI_NAVIGATOR_MDI       = 1;
  public static final int GUI_NAVIGATOR_MONOFRAME = 2;
  
  public DisplayManager(int guiNavigatorType) {
    this.guiNavigatorType = guiNavigatorType;
    backgroundColor = new Color(141, 179, 255);//Bleu Barmaja
    
    //UIManager.put("Button.disabledText", Color.black);
    
    //backgroundColor = new Color(192, 194, 208);//Gris
    //backgroundColor = new Color(61, 67, 132);//Blue Calm
    //backgroundColor = new Color(77, 102, 167);//Blue water
    //backgroundColor = new Color(187, 180, 131);//Sable
    //backgroundColor = new Color(236, 233, 216);//Windows - Cream    
  }
  
  public void dispose(){
    if( mainFrame != null ){
      mainFrame.dispose();
      mainFrame = null;
    }
    
    if(navigator != null ){
      navigator = null;
    }

    if( activeDialogList != null ){
      activeDialogList.clear();
      activeDialogList = null;  
    }
  }

  public void init() {
    try{
    	
//      UIManager.LookAndFeelInfo lfi[] = UIManager.getInstalledLookAndFeels();
//      for(int i =0; i<lfi.length; i++){
//        Globals.logString("Look and feel: "+lfi[i].getName()+" ; "+lfi[i].getClassName());
//      }
      
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
    }catch (Exception e){ 
      Globals.logException(e);
    }

    mainFrame = new MainFrame();
    
    mainFrame.pack();
    mainFrame.setVisible(true);
    
    

    Font font = mainFrame.getFont();
    defaultFont = font.deriveFont(Font.BOLD, ConfigInfo.getFontSize());
    defaultNotEditableFont = font.deriveFont(Font.PLAIN, ConfigInfo.getFontSize());
        
    FontMetrics metrics = mainFrame.getFontMetrics(defaultFont);
    //int maxWidth = 0;
    //int[] wds = metrics.getWidths();
    //for(int i=0; i<wds.length; i++){
    //  maxWidth = Math.max(wds[i], maxWidth);
    //}
    CHAR_WIDTH = metrics.getWidths()[0];
    CHAR_HEIGHT = metrics.getAscent() ;//+ metrics.getDescent();
    Globals.logString("Ascent = "+metrics.getAscent()+" Descent = "+metrics.getDescent()+" Leading = "+metrics.getLeading()+" Height = "+metrics.getHeight());
    /*
    UIDefaults def = UIManager.getDefaults();
    Enumeration iter = def.keys();
    Globals.logString("Begin");
    while(iter != null && iter.hasMoreElements()){
    	Object str = iter.nextElement();
    	Globals.logString(str.toString()+"="+UIManager.get(str));
    }
    Globals.logString("End");
    */
    UIManager.put("TabbedPane.font", defaultFont);
    UIManager.put("CheckBox.disabledText", Color.black);
    UIManager.put("ComboBox.disabledBackground", disabledFieldBackground);
    UIManager.put("ComboBox.disabledForeground", disabledTextColor);
    //UIManager.put("InternalFrame.titlePaneHeight", Integer.valueOf(20));
    //UIManager.put("InternalFrame.titleButtonHeight", Integer.valueOf(20));
    //UIManager.put("InternalFrame.titleButtonWidth", Integer.valueOf(20));
    
    //Font tempFont = (Font) UIManager.get("InternalFrame.titleFont");
    //tempFont = tempFont.deriveFont(14);
    //UIManager.put("InternalFrame.titleFont", tempFont);
    //UIManager.put("InternalFrame.borderWidth", Integer.valueOf(0));
    
    activeDialogList = new ArrayList<FDialog>();
  }

  public void startMonoNavigator(){
    navigator = new MonoFrame(mainFrame);
  }
  
  public void setDefaultFontSize(int size){
    if(size != 0){
      if(defaultFont != null){
        defaultFont = defaultFont.deriveFont((float)size);
      }
      if(defaultNotEditableFont != null){
        defaultNotEditableFont = defaultNotEditableFont.deriveFont((float)size);
      }
      
      FontMetrics metrics = mainFrame.getFontMetrics(defaultFont);
      //int maxWidth = 0;
      //int[] wds = metrics.getWidths();
      //for(int i=0; i<wds.length; i++){
      //  maxWidth = Math.max(wds[i], maxWidth);
      //}
      CHAR_WIDTH = metrics.getWidths()[0];
      CHAR_HEIGHT = metrics.getAscent() ;//+ metrics.getDescent();
      
      UIManager.put("TabbedPane.font", defaultFont);
      UIManager.put("CheckBox.disabledText", Color.black);
      UIManager.put("ComboBox.disabledBackground", disabledFieldBackground);
      UIManager.put("ComboBox.disabledForeground", disabledTextColor);    
      UIManager.put("Table.font", defaultFont);
      
      getMainFrame().setFont(defaultFont);
    }
  }
  
  public int getCharWidth(){
    return CHAR_WIDTH;
  }

  public int getCharHeight(){
    return CHAR_HEIGHT;
  }

  private FDialog getActiveDialog(){
    FDialog dialog = null;
    if(activeDialogList != null && activeDialogList.size() > 0){
      dialog = (FDialog) activeDialogList.get(activeDialogList.size()-1);
    }
    return dialog;
  }
  
  private void pushActiveDialog(FDialog dialog){
    activeDialogList.add(dialog);
  }

  private boolean closeActiveDialog(){
    boolean closed = false;
    FDialog dialog = getActiveDialog();
    if(dialog != null){
      dialog.setVisible(false);
      activeDialogList.remove(dialog);
      closed = true;
    }
    return closed;
  }
  
  public void displayLogin(){ 
    FPanel loginPanel = FocUser.getLoginPanel();
    if (loginPanel != null) {
      navigator.changeView(loginPanel);      
    }
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * @return Returns the mainFrame.
   */
  public MainFrame getMainFrame() {
    return mainFrame;
  }
  
  public void refresh(){
    FDialog dialog = getActiveDialog();
    if(dialog == null){
      FPanelSequence panelSeq = navigator.getActivePanelSequence();
      if(panelSeq != null){
        JPanel mainPanel = panelSeq.getMainPanel();
        mainPanel.repaint();
      }
    }else{
      dialog.repaint();
    }
  }
  
  public void violentRefresh() {
    FDialog dialog = getActiveDialog();
    if(dialog == null){
      FPanelSequence panelSeq = navigator.getActivePanelSequence();
      if(panelSeq != null){
        panelSeq.violentRefresh();
      }
    }else{
      dialog.repaint();
    }
  }

  public FPanel getCurrentPanel() {
    FPanelSequence panelSeq = navigator.getActivePanelSequence();
    return panelSeq != null ? (FPanel)panelSeq.getMainPanel() : null;
  }

  public void changePanel(JPanel jNewPanel) {
    navigator.changeView((FPanel)jNewPanel);
  }
  
  public InternalFrame newInternalFrame(FPanel jNewPanel){
    return navigator != null ? navigator.newView((FPanel)jNewPanel) : null;
  }

  public void popupDialog(FDialog fDialog){
    if(fDialog.isModal()){
      pushActiveDialog(fDialog);
    }
    fDialog.pack();
    fDialog.setVisible(true);
  }

  public void popupDialog(FPanel panel, String title, boolean modal){
    popupDialog(panel, title, modal, 0, 0);
  }
  
  public void popupDialog(FPanel panel, String title, boolean modal, int x, int y){
    FDialog fDialog = new FDialog(mainFrame, title, modal);
    fDialog.setLocation(x, y);
    
    
/*    FPanelSequence panelSeq = fDialog.getPanelSequence();
    panelSeq.setMainPanel(panel);
    panelSeq.violentRefresh();*/
     
    fDialog.setPanel(panel);
    
    fDialog.pack();
    
    boolean modified = false;
    Dimension realDim = fDialog.getPreferredSize();  
    if(panel.getMainPanelSising() == FPanel.MAIN_PANEL_FILL_VERTICAL){
      //realDim.height = ConfigInfo.getGuiNavigatorHeight();
      realDim.height = mainFrame.getHeight();
      modified = true;
    }
    if(panel.getMainPanelSising() == FPanel.MAIN_PANEL_FILL_HORIZONTAL){
      //realDim.width = ConfigInfo.getGuiNavigatorWidth();
      realDim.width = mainFrame.getWidth();
      modified = true;
    }
    if(panel.getMainPanelSising() == FPanel.MAIN_PANEL_FILL_BOTH){
      //realDim.height = ConfigInfo.getGuiNavigatorHeight();
      //realDim.width = ConfigInfo.getGuiNavigatorWidth();
      realDim.width = mainFrame.getWidth();
      realDim.height = mainFrame.getHeight();
      modified = true;
    }
    if(modified){
      fDialog.setPreferredSize(realDim);
      //panelSeq.getMainPanel().setPreferredSize(realDim);
    }
    
    popupDialog(fDialog);
  }

  public void popupMessage(String message){
    JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
        message,
        "01Barmaja",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null);
  }
  
  public void goBack() {
    if(!closeActiveDialog()){
      goBackDontCheckDialogs();
    }
  }
  
  public void goBackDontCheckDialogs() {
    if(navigator.goBack()){
      Globals.getApp().exit();
    }
  }

  public void createNavigatorIfNeeded(){
    if(navigator == null){
    	switch(guiNavigatorType){
    	case GUI_NAVIGATOR_MDI:
        navigator = new FDesktop(mainFrame);
    		break;
    	case GUI_NAVIGATOR_MONOFRAME:
        navigator = new MonoFrame(mainFrame);
    		break;
    	}
    }
  }
  
  public void reconstructMenu(int loginStatus){
    MenuConstructor mc = null;
    
    createNavigatorIfNeeded();
    
    mc = navigator.getMainMenuConstructor();
    
    if(mc != null){
      if(loginStatus == Application.LOGIN_ADMIN){
        FMenu adminMenu = Globals.getApp().getMainAdminMenu();
        mc.addMainMenu(adminMenu);
      }else{
        FMenu appMenu = Globals.getApp().getMainAppMenu();
        mc.addMainMenu(appMenu);        
      }
  
      FMenu focMenu = Globals.getApp().getMainFocMenu();
      mc.addMainMenu(focMenu);
      mc.showMenu();
    }      
    mainFrame.setPreferredSize(new Dimension(750, 580));
    mainFrame.repaint();
  }
  
  public void popupMenu(int loginStatus){
    navigator = null;
    createNavigatorIfNeeded();
    if(!Globals.getApp().isDisableMenus()){
      reconstructMenu(loginStatus);
    }
  }
  
  public Navigator getNavigator() {
    return navigator;
  }
  
  public boolean restoreInternalFrame(InternalFrame intFrame){
    boolean successfull = false;
    
    if(intFrame != null){
      FDesktop desk = (FDesktop)Globals.getDisplayManager().getNavigator();
      JInternalFrame ifs[] = (JInternalFrame [])desk.getAllFrames();
      for(int i=0; i<ifs.length; i++){
        InternalFrame ifrm = (InternalFrame)ifs[i];
        if(ifrm == intFrame){
          try{
            ifrm.setIcon(false);
            ifrm.setSelected(true);
            ifrm.setVisible(false);
            ifrm.setVisible(true);
            successfull = true;
          }catch(Exception e){
            Globals.logException(e);
          }
        }
      }
    }
    return successfull;
  }

  public boolean allScreensClosed(){
    boolean allScreensClosed = false;
    if(guiNavigatorType != GUI_NAVIGATOR_NONE){
	    if(guiNavigatorType == GUI_NAVIGATOR_MDI && Globals.getDisplayManager().getNavigator() instanceof FDesktop){
	      FDesktop desk = (FDesktop)Globals.getDisplayManager().getNavigator();
	      JInternalFrame ifs[] = (JInternalFrame [])desk.getAllFrames();
	      allScreensClosed = ifs.length == 0; 
	    }else{
	      MonoFrame monoFrame = (MonoFrame)Globals.getDisplayManager().getNavigator();
	      FPanelSequence panelSequence = monoFrame.getActivePanelSequence();
	      allScreensClosed = panelSequence.getPanelDeepness() == 0;
	    }
    }
    return allScreensClosed;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // FOCUS LOCK
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public void setFocusLock(FocusLock focusLock) {
    this.focusLock = focusLock;
  }
  
  private boolean shouldLockFocusInternal(boolean displayMessage){
    boolean lock = false;
    if(focusLock != null){
      lock = focusLock.shouldHoldFocus(displayMessage);
    }
    return lock ; 
  }
  
  public boolean shouldLockFocus(boolean displayMessage){
    boolean lock = false;
    //ATTENTION
    
    if(Globals.getDisplayManager().shouldLockFocusInternal(false)){
      Globals.getDisplayManager().stopEditingLockFocus();
    }
   

    lock = Globals.getDisplayManager().shouldLockFocusInternal(displayMessage);
    
    if(!lock){
    	//Globals.getDisplayManager().removeLockFocusForObject(null);
    }
    return lock ; 
  }

  public void removeLockFocusForObject(Object obj){
    if(focusLock != null){
      if(focusLock.getLockObject() == obj || obj == null){
        focusLock.dispose();
        setFocusLock(null);
      }
    }
  }
  
  public void stopEditingLockFocus(){
    if(focusLock != null){
      focusLock.stopEditing();
    }
  }
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // CURRENT EDIT
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  //public FocusLock getCurrentEdit() {
  //  return currentEdit;
  //}
  /*
  public void setCurrentEdit(FocusLock currentEdit) {
    this.currentEdit = currentEdit;
  }
  
  public void stopEditingCurrentEdit(){
    if(currentEdit != null){
      currentEdit.stopEditing();
    }
  }
  */
  
  public Font getDefaultFont() {
    return defaultFont;
  }

  public Font getDefaultNotEditableFont() {
    return defaultNotEditableFont;
  }

  /**
   * @return
   */
  public Color getDisabledTextColor() {
    return disabledTextColor;
  }
  
  /**
   * @return
   */
  public Color getDisabledFieldBackground() {
    return disabledFieldBackground;
  }
}