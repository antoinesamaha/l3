/*
 * Created on 14 fvr. 2004
 */
package b01.foc.gui;

import b01.foc.*;
import b01.foc.property.*;
import b01.foc.desc.*;
import b01.foc.desc.field.FBoolField;
import b01.foc.desc.field.FField;
import b01.foc.event.*;

import java.util.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FPanel extends JPanel /*implements Cloneable*/{

  private GridBagConstraints c = null;
  private GridBagConstraints cBackup = null;
  private boolean isDisposing = false;
  
  private int fill = FILL_BOTH;//This is used as the fill mode of the panel when inserting it in the parent.
  
  public static final int FILL_NONE = GridBagConstraints.NONE;
  public static final int FILL_HORIZONTAL = GridBagConstraints.HORIZONTAL;
  public static final int FILL_VERTICAL = GridBagConstraints.VERTICAL;
  public static final int FILL_BOTH = GridBagConstraints.BOTH;

  private int mainPanelSising = MAIN_PANEL_NONE;

  public static final int MAIN_PANEL_NONE            = FILL_NONE;
  public static final int MAIN_PANEL_FILL_VERTICAL   = FILL_VERTICAL;
  public static final int MAIN_PANEL_FILL_HORIZONTAL = FILL_HORIZONTAL;
  public static final int MAIN_PANEL_FILL_BOTH       = FILL_BOTH;
  
  /*
   * In the FPanle I have a border layout that allows me to insert a
   * SaveAndExitPanel on the buttom. And in the central part I have a Panel with
   */
  private JPanel centralPanel = null;
  private FPanel footerPanel = null;
  private FPanel eastPanel = null;

  private ArrayList<FocListener> listeners = null;

  JComponent currentDefaultFocusComponent = null;
  
  private String frameTitle = null;
  private BufferedImage backgroundImage = null;
  
  private static final double DEFAULT_WEIGHT_Y = 0.99;
  
  private void init() {
    this.setLayout(new GridBagLayout());
    
    centralPanel = new JPanel(){
      public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(backgroundImage != null) g.drawImage(backgroundImage, 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight(), this);
      }
    };
    
    centralPanel.setLayout(new GridBagLayout());

    c = new GridBagConstraints();
    cBackup = new GridBagConstraints();

    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets.bottom = 1;
    c.insets.top = 1;
    c.insets.left = 1;
    c.insets.right = 1;
    c.weightx = 0.99;
    c.weighty = DEFAULT_WEIGHT_Y;
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.CENTER;
    
    c.gridx = 0;
    c.gridy = 1;
    this.add(centralPanel, c);
    //this.add(centralPanel, BorderLayout.CENTER);
    centralPanel.setBackground(Globals.getDisplayManager().getBackgroundColor());    
    setBackground(Globals.getDisplayManager().getBackgroundColor());
  }

  private void initIfNeeded() {
    if(centralPanel == null){
      init();
    }
  }
    
  public FPanel() {
    super();
    initIfNeeded();  
  }

  public FPanel(String frameTitle, int frameSizing, int panelFill) {
    this();
    //this.frameTitle = frameTitle;
    setFrameTitle(frameTitle);
    mainPanelSising = frameSizing;
    fill = panelFill;  
  }

  public FPanel(String frameTitle, int fill) {
    this();
    //this.frameTitle = frameTitle;
    setFrameTitle(frameTitle);
    mainPanelSising = fill;
    this.fill = fill;  
  }
  
  public void dispose(){
    
    if(!isDisposing){
      isDisposing = true;
      StaticComponent.cleanComponent(this);
  
      c = null;
      cBackup = null;
  
      centralPanel = null;
      footerPanel = null;
      eastPanel = null;
  
      listeners = null;
      
      currentDefaultFocusComponent = null;      
      frameTitle = null;
      //BELie
      /*if(focObj != null && revisionButton != null){
        focObj.removeRelatedGuiComponent(revisionButton);  
      }*/
      //focObj = null;
      //EElie
      isDisposing = false;
      backgroundImage = null;
    }
  }

  /*@Override
  protected Object clone() throws CloneNotSupportedException {
    FPanel panel = (FPanel) super.clone();
    panel.init();

    panel.footerPanel = null;
    panel.eastPanel = null;
    panel.listeners = null;
    panel.currentDefaultFocusComponent = null;
    
    return panel;
  }*/

  private JPanel getCentralPanel(){
    initIfNeeded();
    return centralPanel;
  }

  private GridBagConstraints getC(){
    initIfNeeded();
    return c;
  }

  private GridBagConstraints getCBackup(){
    initIfNeeded();
    return cBackup;
  }
  
  public void setBackgroundImage(BufferedImage image){
  	backgroundImage = image;
  }
  
//  public void paintComponent(Graphics g){
//    super.paintComponent(g);
//    if(backgroundImage != null) g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
//  }
  
  public String getPanelName(){
  	return "ANY_PANEL";
  }

  @Override
  public void setName(String name) {
    super.setName(name);
    if(ConfigInfo.isUnitDevMode()){
      setToolTipText(getName());
    }
  }

  private static int getFillDefaultValueForComponent(Component comp){
    int fill = GridBagConstraints.BOTH;

    if(FPanel.class.isInstance(comp)){
      FPanel panel = (FPanel) comp;
      fill = panel.getFill();
    }else if(FGComboBox.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }else if(FGButton.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }else if(FGToggleButton.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }else if(FGTextField.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }else if(FGPasswordField.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }else if(FGNumField.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }else if(FGDateField.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }else if(FGLabel.class.isInstance(comp)){
      fill = GridBagConstraints.NONE;
    }
    
    return fill;
  }
  
  public void setBorder(){
    setBorder(new BevelBorder(BevelBorder.LOWERED));
  }

  public void setBorder(String title){
    TitledBorder border = new TitledBorder(new BevelBorder(BevelBorder.LOWERED), title);
    //border.setTitleColor(getBackground());
    setBorder(border);
  }

  public void add(Component jComp, int x, int y, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill) {
    if (jComp != null && getCentralPanel() != null ) {
      GridBagConstraints c       = getC();
      GridBagConstraints cBackup = getCBackup();
      
      c.gridx = x;
      c.gridy = y;

      cBackup.gridwidth = c.gridwidth;
      cBackup.gridheight = c.gridheight;
      cBackup.anchor = c.anchor;
      cBackup.fill = c.fill;
      cBackup.weightx = weightx;
      cBackup.weighty = weighty;

      c.gridwidth = gridwidth;
      c.gridheight = gridheight;
      c.anchor = anchor;
      c.fill = fill;

      if(c.fill == GridBagConstraints.NONE){
        c.weightx = 0;
        c.weighty = 0;
      }else{
        if(c.weightx == 0 ){
          c.weightx = 0.99;
        }
        if(c.weighty == 0 ){
          c.weighty = 0.99;
        }
      }

      /*
      if(FPanel.class.isInstance(jComp)){
        FPanel panel = (FPanel) jComp;
        c.fill = panel.getFill();
      }
      */
      
      getCentralPanel().add(jComp, c);

      c.gridwidth = cBackup.gridwidth;
      c.gridheight = cBackup.gridheight;
      c.anchor = cBackup.anchor;
      c.fill = cBackup.fill;
      c.weightx = cBackup.weightx;
      c.weighty = cBackup.weighty;
    }
  }

  public void setBackground(Color c){
    setCentralPanelBackground(c);
    super.setBackground(c);
  }

  public void setCentralPanelBackground(Color c){
  	if(getCentralPanel() != null) getCentralPanel().setBackground(c);
  }
  
  public Component add(Component jComp) {
    add(jComp, 0, 0, 1, 1, getC().weightx, getC().weighty, getC().anchor, getC().fill);
    return jComp;
  }
  
  public void add(Component jComp, int x, int y, int anchor) {
    add(jComp, x, y, 1, 1, getC().weightx, getC().weighty, anchor, getFillDefaultValueForComponent(jComp));
  }
  
  public void add(Component jComp, int x, int y, int gridwidth, int gridheight, int anchor, int fill) {
    add(jComp, x, y, gridwidth, gridheight, 0, 0, anchor, fill);
  }
  
  public void add(Component jComp, int x, int y, int w, int h) {
    add(jComp, x, y, w, h, getC().anchor, getFillDefaultValueForComponent(jComp));
  }

  public void add(Component jComp, int x, int y) {
    add(jComp, x, y, 1, 1);
  }

  //Label
  //-----

  public FGLabel addLabel(String label, int x, int y, int width, int height) {
    FGLabel jLabel = new FGLabel(label);
    jLabel.setFont(Globals.getDisplayManager().getDefaultFont());
    add(jLabel, x, y, width, height, GridBagConstraints.NORTHEAST, getFillDefaultValueForComponent(jLabel));
    return jLabel;
  }

  public FGLabel addLabel(String label, int x, int y) {
  	return addLabel(label, x, y, 1, 1);
  }

  public FGLabel addLabel(FocObject focObj, int fieldId, int x, int y, int width, int height) {
  	String label = focObj.getThisFocDesc().getFieldByID(fieldId).getTitle();  	
    return addLabel(label, x, y, width, height);
  }

  //Field
  //-----

  public Component addField(FocObject focObj, int fieldId, int x, int y) {
  	Component jComp = focObj.getGuiComponent(fieldId);
  	addField(jComp, x, y, 1, 1, GridBagConstraints.NONE);
  	return jComp;
  }
  
  public void addField(Component jComp, int x, int y, int width, int height, int fill) {
    add(jComp, x, y, width, height, GridBagConstraints.NORTHWEST, fill);
  }

  public void addField(Component jComp, int x, int y, int fill) {
  	addField(jComp, x, y, 1, 1, fill);
  }

  public Component addField(FocObject focObj, int fieldId, int x, int y, int width, int height, int fill) {
  	Component jComp = focObj.getGuiComponent(fieldId);
  	addField(jComp, x, y, width, height, fill);
  	return jComp;
  }
  
  //Label + Field
  //-------------
  public JLabel add(String label, Component jComp, int x, int y, int fill) {
  	FGLabel jLabel = addLabel(label, x, y);
  	addField(jComp, x+1, y, fill);
    return jLabel;
  }
  
  public JLabel add(String label, Component jComp, int x, int y) {
    return add(label, jComp, x, y, GridBagConstraints.NONE);
  }

  private String getFieldTitleIfNotCheckBox(FocObject focObj, int fieldId){
  	String title = "";
  	FField field = focObj != null ? focObj.getThisFocDesc().getFieldByID(fieldId) : null;
  	if(field != null && !(field instanceof FBoolField)){
  		title = field.getTitle();
  	}
  	return title;
  }
  
  public Component add(FocObject focObj, int fieldId, int x, int y) {
  	Component comp  = focObj.getGuiComponent(fieldId);
  	String    label = getFieldTitleIfNotCheckBox(focObj, fieldId);
  	add(label, comp, x, y);
    return comp;
  }

  public Component add(FocObject focObj, int fieldId, int x, int y, int width, int height) {
  	Component comp = addField(focObj, fieldId, x+1, y, width, height, GridBagConstraints.NONE);
  	if(!(comp instanceof FGCheckBox)){
  		addLabel(focObj, fieldId, x, y, 1, 1);	
  	}
  	return comp;
  }

  public Component add(FocObject focObj, int fieldId, int x, int y, int width, int height, int fill) {
  	Component comp = addField(focObj, fieldId, x+1, y, width, height, fill);
  	if(!(comp instanceof FGCheckBox)){
  		addLabel(focObj, fieldId, x  , y, 1, 1);	
  	}
  	return comp;
  }

  public JLabel add(String label, FGObjectField objComp, int x, int y) {
    JLabel jLabel = new JLabel(label);
    jLabel.setFont(Globals.getDisplayManager().getDefaultFont());
    add(jLabel, x, y, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
    add(objComp, x + 1, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    FObject fObj = (FObject) objComp.getObjectProperty();

    JButton select = new JButton(Globals.getIcons().getSelectIcon());
    select.addActionListener(new SelectButtonListener(this, fObj));
    add(select, x + 2, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);

    JButton edit = new JButton(Globals.getIcons().getEditIcon());
    edit.addActionListener(new EditButtonListener(this, fObj));
    add(edit, x + 3, y, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
    
    return jLabel;
  }

  public FValidationPanel showValidationPanel(boolean show) {
    return showValidationPanel(show, true);
  }  
  
  public FValidationPanel showValidationPanel(boolean show, boolean normalButtonsPositioning) {
    if (footerPanel == null && show) {
      FValidationPanel validPanel = new FValidationPanel(normalButtonsPositioning, getFrameTitle());
      setFooterPanel(validPanel, show);
    }else if(footerPanel != null){
      footerPanel.setVisible(show);
    }

    return (FValidationPanel)footerPanel;
  }

  public FIFooterPanel getValidationPanel() {
    return (FIFooterPanel) footerPanel;
  }

  public FPanel getFooterPanel() {
    return footerPanel;
  }

  public void setFooterPanel(FPanel fooPanel) {
    setFooterPanel(fooPanel, true);
  }

  public void setFooterPanel(FPanel fooPanel, boolean show) {
    if(fooPanel != null){
      if(footerPanel != null) this.remove(footerPanel);
      footerPanel = fooPanel;
      
      footerPanel.setFill(FPanel.FILL_NONE);
      getC().gridx = 0;
      getC().gridy = 2;
      getC().fill = GridBagConstraints.NONE;
      getC().anchor = GridBagConstraints.NORTH;
      getC().weighty = 0.01;
      this.add(footerPanel, getC());
      getC().weighty = DEFAULT_WEIGHT_Y;
      
      footerPanel.setVisible(show);
    }
  }

  public FPanel showEastPanel(boolean show) {
    if (eastPanel == null && show) {
      eastPanel = new FPanel();
      getC().gridx = 1;
      getC().gridy = 1;
      getC().fill = GridBagConstraints.NONE;
      this.add(eastPanel, getC());
      
      //this.add(eastPanel, BorderLayout.EAST);
    }
    if (eastPanel != null) {
      eastPanel.setVisible(show);
    }
    return eastPanel;
  }

  public FPanel getEastPanel() {
    return eastPanel;
  }
  
  public void setTitle(String title) {
    if(title != null){
      FPanel titlePanel = new FPanel();
      FGLabel text = new FGLabel(title);
  
      double oldx = getC().weightx;
      double oldy = getC().weighty;
      getC().weightx = 0.99;
      getC().weighty = 0.99;
  
      titlePanel.add(text, 0, 0, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE);
      getC().weightx = oldx;
      getC().weighty = oldy;
  
      getC().gridx = 0;
      getC().gridy = 0;
      getC().weightx = 0.05;
      getC().weighty = 0.05;
      getC().fill = GridBagConstraints.NONE;
      getC().anchor = GridBagConstraints.SOUTH;
      this.add(titlePanel, getC());
    }
  }

  public class EditButtonListener extends AbstractAction {
    FPanel fPanel = null;
    FObject fObj = null;

    public EditButtonListener(FPanel fPanel, FObject fObj) {
      this.fPanel = fPanel;
      this.fObj = fObj;
    }

    public void actionPerformed(ActionEvent e) {
      try {
        if (fPanel != null) {
          FocObject fObject = (FocObject) ((FObject) fObj).getObject();
          fObject.popup();
        }
      } catch (Exception e1) {
        Globals.logException(e1);
      }
    }
  }

  public class SelectButtonListener extends AbstractAction {
    FPanel fPanel = null;
    FProperty fObj = null;

    public SelectButtonListener(FPanel fPanel, FProperty fObj) {
      this.fPanel = fPanel;
      this.fObj = fObj;
    }

    public void actionPerformed(ActionEvent e) {
      try {
        if (fPanel != null) {
          ((FObject) fObj).popupSelectionPanel();
          /*
           * FocAbstract focObj = getfocDescription().newClassInstance();
           * focObj.addListener((FocListener)getThis()); FPanel focObjPanel =
           * focObj.newDetailsPanel();
           * Globals.getDisplayManager().changePanel(focObjPanel);
           */
        }
      } catch (Exception e1) {
        Globals.logException(e1);
      }
    }
  }

  public void addFocListener(FocListener listener) {
    if (listeners == null) {
      listeners = new ArrayList<FocListener>(3);
    }
    listeners.add(listener);
  }

  public void removeFocListener(FocListener listener) {
    if (listeners != null) {
      for (int i = 0; i < listeners.size(); i++) {
        FocListener curListener = (FocListener) listeners.get(i);
        if (curListener == listener) {
          listeners.remove(i);
          break;
        }
      }
    }
  }

  public void notifyListeners(FocEvent event) {
    if (listeners != null) {
      for (int i = 0; i < listeners.size(); i++) {
        FocListener listener = (FocListener) listeners.get(i);
        if (listener != null) {
          listener.focActionPerformed(event);
        }
      }
    }
  }
  
  /**
   * @return Returns the currentDefaultFocusComponent.
   */
  public JComponent getCurrentDefaultFocusComponent() {
    return currentDefaultFocusComponent;
  }
  
  /**
   * @param currentDefaultFocusComponent The currentDefaultFocusComponent to set.
   */
  public void setCurrentDefaultFocusComponent(JComponent currentDefaultFocusComponent) {
    this.currentDefaultFocusComponent = currentDefaultFocusComponent;
  }
  
  public void refreshFocus(){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        if(currentDefaultFocusComponent != null){
          currentDefaultFocusComponent.requestFocusInWindow();
        }
      }
    });
  }
    
  public String getFrameTitle() {
    return frameTitle != null ? frameTitle : "";
  }
  
  public void setFrameTitle(String frameTitle) {
    this.frameTitle = frameTitle;
    setName(frameTitle+".FPanel");
    
  }
  
  public int getFill() {
  	return fill;
  }
  
  public void setFill(int fill) {
    this.fill = fill;
  }
  
  public int getMainPanelSising() {
    return mainPanelSising;
  }
  
  public void setMainPanelSising(int mainPanelSising) {
    this.mainPanelSising = mainPanelSising;
  }
  
  public void cleanCentralPanel(){
    if( centralPanel != null ){
      centralPanel.removeAll();  
    }
  }
  
  public void addToCentralPanel(Component comp){
    getCentralPanel().add(comp);
  }
}