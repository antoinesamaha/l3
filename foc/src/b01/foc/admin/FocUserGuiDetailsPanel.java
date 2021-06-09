package b01.foc.admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import b01.foc.Application;
import b01.foc.ConfigInfo;
import b01.foc.ConfigInfoWizardPanel;
import b01.foc.FocKeys;
import b01.foc.FocLangKeys;
import b01.foc.Globals;
import b01.foc.GuiConfigInfo;
import b01.foc.MultiLanguage;
import b01.foc.desc.FocObject;
import b01.foc.event.FValidationListener;
import b01.foc.gui.FGButton;
import b01.foc.gui.FGComboBox;
import b01.foc.gui.FGPasswordField;
import b01.foc.gui.FGTextField;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.property.FProperty;

@SuppressWarnings("serial")
public class FocUserGuiDetailsPanel extends FPanel{
  
  private FGPasswordField confirmPass = null;
  private FGPasswordField compPass = null;
  private FGPasswordField oldPass = null;
  private FGTextField nameComp = null;
  
  FocUser user = null;
  
  public FocUserGuiDetailsPanel(FocObject object, int viewID){
    user = (FocUser)object;
    switch(viewID){
    case FocUser.LOGIN_VIEW_ID:
    case FocUser.SET_PASSWORD_VIEW_ID:
    case FocUser.CHANGE_PASSWORD_VIEW_ID:
      newDetailsPanelLoginAndPassword(viewID);
      break;      
    case FocUser.CHANGE_LANGUAGE_VIEW_ID:
    case FocUser.USER_GROUP_INFO_VIEW_ID:
      newDetailsPanelLanguage(viewID);      
      break;      
    }
  }
  
  public void dispose(){
    user = null;
    confirmPass = null;
    compPass = null;
    oldPass = null;
    nameComp = null; 
  }
  
  public void newDetailsPanelLanguage(int viewID) {
    FPanel panel = new FPanel();
    if(viewID != FocUser.USER_GROUP_INFO_VIEW_ID){
      panel.setTitle(MultiLanguage.getString(FocLangKeys.ADMIN_USER_PREFERENCES));
    }
    
    FGTextField txtComp = (FGTextField) user.getGuiComponent(FocUserDesc.FLD_NAME);
    panel.add(MultiLanguage.getString(FocLangKeys.ADMIN_NAME), txtComp, 0, 0);
    
    if(MultiLanguage.isMultiLanguage()){
      FGComboBox multiChoice = (FGComboBox) user.getGuiComponent(FocUserDesc.FLD_LANGUAGE);
      panel.add(MultiLanguage.getString(FocLangKeys.ADMIN_LANGUAGE), multiChoice, 0, 1);
      if(viewID == FocUser.USER_GROUP_INFO_VIEW_ID){
        multiChoice.setEnabled(false);
      }
    }

    Component comp = (Component) user.getGuiComponent(FocUserDesc.FLD_FONT_SIZE);
    panel.add(MultiLanguage.getString(FocLangKeys.ADMIN_TEXT_SIZE), comp, 0, 2);

    FValidationPanel validPanel = panel.showValidationPanel(true);
    user.getMasterObject();
    user.getFatherSubject();
    user.forceControler(true);
    validPanel.addSubject(user);
    
    validPanel.setValidationListener(new FValidationListener(){

      public boolean proceedValidation(FValidationPanel panel) {
        return true;
      }

      public boolean proceedCancelation(FValidationPanel panel) {
        return true;
      }

      public void postValidation(FValidationPanel panel) {
        Globals.getDisplayManager().setDefaultFontSize(Globals.getApp().getUser().getFontSize());
      }

      public void postCancelation(FValidationPanel panel) {
      }
      
    });

    if(viewID == FocUser.USER_GROUP_INFO_VIEW_ID){
      FocGroup group = user.getGroup();
      FPanel groupPanel = group.newDetailsPanel(FocGroup.VIEW_READ_ONLY);
      panel.add(groupPanel, 0, 3, 2, 1);
      groupPanel.setBorder(MultiLanguage.getString(FocLangKeys.ADMIN_GROUP));
      
      validPanel.setValidationType(FValidationPanel.VALIDATION_OK);
    }else{
      user.forceControler(true);
      validPanel.addSubject(user);
    }
    add(panel, 0, 0);
  }
  
  public void newDetailsPanelLoginAndPassword(int viewID) {
    //FPanel wrapperPanel = new FPanel("", FPanel.FILL_NONE);
    setOpaque(false);
    FPanel panel = null;
    String passwordLabel = "Password";
    int line = 0;
    
    //Creating panels and changing labels
    //-----------------------------------
    if(viewID == FocUser.LOGIN_VIEW_ID){
      //panel = new FLoginPanel();
      panel = new FPanel();
      panel.setOpaque(false);
      //panel.setTitle("Login");
    }else if(viewID == FocUser.SET_PASSWORD_VIEW_ID){
      panel = new FPanel();
      passwordLabel = "New password";
    }else if(viewID == FocUser.CHANGE_PASSWORD_VIEW_ID){
      panel = new FPanel();
      passwordLabel = "New password";
      panel.setTitle("Change password");
    }
    
    FGPasswordField passGuiSample = (FGPasswordField) user.getGuiComponent(FocUserDesc.FLD_PASSWORD);    
    nameComp = (FGTextField) user.getGuiComponent(FocUserDesc.FLD_NAME);
    //panel.add("Name", nameComp, 0, line++);
    
    float fontSize = 17f;
    JLabel label = panel.add("Name", nameComp, 0, line++);
    Font font = label.getFont();
    label.setFont(font.deriveFont(fontSize));
    
    
    //Creating panels and changing labels
    //-----------------------------------
    if(viewID == FocUser.CHANGE_PASSWORD_VIEW_ID){
      oldPass = clonePasswordField(passGuiSample);
      panel.add("Password", oldPass, 0, line++);
    }else{
      oldPass = null;
    }
    
    compPass = clonePasswordField(passGuiSample);
    compPass.setName("PASSWORD");
    //panel.add(passwordLabel, compPass, 0, line++);
    
    label = panel.add(passwordLabel, compPass, 0, line++);
    font = label.getFont();
    label.setFont(font.deriveFont(fontSize));

    AbstractAction validateAction = null;
    AbstractAction exitAction = new AbstractAction(){
      /**
       * Comment for <code>serialVersionUID</code>
       */
      private static final long serialVersionUID = 3904677171992933430L;

      public void actionPerformed(ActionEvent e){
        if(oldPass != null){
          Globals.getDisplayManager().goBack();
        }else{
          Globals.getApp().exit();
        }
      }
    };      
    
    if(viewID == FocUser.SET_PASSWORD_VIEW_ID || viewID == FocUser.CHANGE_PASSWORD_VIEW_ID){
      confirmPass = clonePasswordField(passGuiSample);
      panel.add("Confirm password", confirmPass, 0, line++);

      validateAction = new AbstractAction(){
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3905244524186777654L;

        public void actionPerformed(ActionEvent e){
          String typedName = String.valueOf(nameComp.getText());
          String typedPassword = String.valueOf(compPass.getEncryptedPassword());
          String typedConfirmation = String.valueOf(confirmPass.getEncryptedPassword());
          boolean proceed = true;
          
          if(oldPass != null){
            proceed = String.valueOf(oldPass.getEncryptedPassword()).compareTo(user.getPassword()) == 0;
          }

          if(!proceed){
            int dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                "Wrong password please try again or refer to ADMIN to reset password.",
                "01Barmaja",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null);
          }else{
            if(typedPassword.compareTo(typedConfirmation) == 0){
              FProperty prop = user.getFocProperty(FocUserDesc.FLD_PASSWORD);
              prop.setString(typedPassword);
              user.save();
              Globals.getDisplayManager().goBack();
            }else{
              int dialogRet = JOptionPane.showConfirmDialog(Globals.getDisplayManager().getMainFrame(),
                  "password confirmation error please try again.",
                  "01Barmaja",
                  JOptionPane.DEFAULT_OPTION,
                  JOptionPane.WARNING_MESSAGE,
                  null);
            }
          }
        }
      };
    }    
    
    if(viewID == FocUser.LOGIN_VIEW_ID){
      validateAction = new AbstractAction(){
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3258129137569707320L;

        public void actionPerformed(ActionEvent e){
          String typedName = String.valueOf(nameComp.getText());
          String encriptedPassword = compPass.getEncryptedPassword();
          Globals.logDetail("PASSWORD:<"+encriptedPassword+">");
          FocUser.userLoginCheck(typedName, encriptedPassword);
          if(Globals.getApp().getLoginStatus() != Application.LOGIN_WRONG){
            Globals.getDisplayManager().setDefaultFontSize(Globals.getApp().getUser().getFontSize());
          }
        }
      };
    }

    FPanel buttonsPanel = new FPanel();
    buttonsPanel.setFill(FPanel.FILL_NONE);
    FGButton bLogin = new FGButton("Validate");
    bLogin.addActionListener(validateAction);
    bLogin.setName(FValidationPanel.BUTTON_VALIDATE);
    //Belie
    if(ConfigInfo.isUnitDevMode()){
      bLogin.setToolTipText(FValidationPanel.BUTTON_VALIDATE);  
    }
    //Eelie
    
    FGButton bExit = new FGButton("Exit");
    bExit.addActionListener(exitAction);
    bExit.setName(FValidationPanel.BUTTON_CANCEL);
        
    buttonsPanel.add(bLogin, 0, 0);
    buttonsPanel.add(bExit, 1, 0);      
    
    panel.getActionMap().put("login", validateAction);
    panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(FocKeys.getValidateStroke(), "login");
    
    panel.getActionMap().put("exit", exitAction);
    panel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(FocKeys.getCancelStroke(), "exit");
    
    panel.add(buttonsPanel, 0, line++, 2 ,1);

    /*
    if(viewID == DEFAULT_VIEW_ID){
      panel.showValidationPanel(true);
    }
    */
    setCurrentDefaultFocusComponent(nameComp);
    panel.setFill(FPanel.FILL_NONE);

    setBackground(new Color(100, 100, 255, 0));
    
    panel.setBackground(getBackground());
    
    FPanel environmentPanel = new FPanel("", FPanel.FILL_NONE);
    environmentPanel.setBackground(getBackground());
    String environment = Globals.getApp().getDefaultEnvironment();
  
    FGTextField currentEnv = new FGTextField();
    currentEnv.setEditable(false);
    currentEnv.setPreferredSize(new Dimension(170, 20));
    if(environment != null ){
      currentEnv.setText(environment.substring(GuiConfigInfo.ENVIRONMENT_PREFIX.length()));  
    }
    label = environmentPanel.add("Current Environment", currentEnv, 0, 0);
    font = label.getFont();
    label.setFont(font.deriveFont(fontSize));
    
    FGButton switchButton = new FGButton("Switch Environment");
    environmentPanel.add(switchButton, 1, 1);
    switchButton.setEnabled(environment != null);
    switchButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        ConfigInfoWizardPanel panel = new ConfigInfoWizardPanel(new GuiConfigInfo(), ConfigInfoWizardPanel.STATE_DIRECTORY);
        panel.setWithRestart(true);
        Globals.getDisplayManager().popupDialog(panel, "", true);    
      }
    });
    
    //panel.add(environmentPanel, 1, line);
    add(panel, 0, 0);
    add(environmentPanel, 0, 1);
  }
  
  private FGPasswordField clonePasswordField(FGPasswordField refPass){
    FGPasswordField pass = new FGPasswordField();
    pass.setCapital(refPass.getCapital());
    pass.setColumns(refPass.getColumns());
    pass.setColumnsLimit(refPass.getColumnsLimit());
    return pass;
  }
}
