// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// DESCRIPTION
// LIST

/*
 * Created on 20-May-2005
 */
package b01.l3;

import b01.foc.admin.FocGroup;
import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.list.*;
import b01.foc.property.*;

import java.awt.*;

/**
 * @author 01Barmaja
 */
public class FocAppGroup extends FocObject{

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PROPERTIES
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public FocAppGroup(FocConstructor constr) {
    super(constr);

    new FBoolean(this, FLD_ALLOW_SAMPLE_MODIFICATION, false);
    new FBoolean(this, FLD_ALLOW_RESULT_CONFIRMATION, false);
    new FBoolean(this, FLD_ALLOW_SENDING_TO_INSTRUMENT, false);
    new FBoolean(this, FLD_ALLOW_CONFIGURATION,false);
    new FBoolean(this, FLD_ALLOW_CONNECTION,false);
    new FBoolean(this, FLD_ALLOW_MONITORING,false);
    new FBoolean(this, FLD_ALLOW_ON_HOLD_MODIF,false);
  }

  public boolean allowSampleModification(){
    FBoolean allowSampleModification = (FBoolean) getFocProperty(FLD_ALLOW_SAMPLE_MODIFICATION);
    return allowSampleModification.getBoolean();
  }
  
  public boolean allowResultConfirmation(){
    FBoolean allowResultConfirmation = (FBoolean) getFocProperty(FLD_ALLOW_RESULT_CONFIRMATION);
    return allowResultConfirmation.getBoolean();
  }
  
  public boolean allowSendingToInstrument(){
    FBoolean allowSendingToInstrument= (FBoolean) getFocProperty(FLD_ALLOW_SENDING_TO_INSTRUMENT);
    return allowSendingToInstrument.getBoolean();
  }
  
  public boolean allowConfiguration(){
    FBoolean allowConfig= (FBoolean) getFocProperty(FLD_ALLOW_CONFIGURATION);
    return allowConfig.getBoolean();
  }
  
  public boolean allowConnection(){
    FBoolean allowConfig= (FBoolean) getFocProperty(FLD_ALLOW_CONNECTION);
    return allowConfig.getBoolean();
  }
  
  public boolean allowMonitoring(){
    FBoolean allowConfig= (FBoolean) getFocProperty(FLD_ALLOW_MONITORING);
    return allowConfig.getBoolean();
  }
  
  public boolean allowOnHold(){
    FBoolean allowConfig= (FBoolean) getFocProperty(FLD_ALLOW_ON_HOLD_MODIF);
    return allowConfig.getBoolean();
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    FPanel panel = new FPanel();
    Component comp = getGuiComponent(FLD_ALLOW_SAMPLE_MODIFICATION);
    if(viewID == FocGroup.VIEW_READ_ONLY) comp.setEnabled(false);
    panel.add(comp, 0, 0);
    comp = getGuiComponent(FLD_ALLOW_RESULT_CONFIRMATION);
    if(viewID == FocGroup.VIEW_READ_ONLY) comp.setEnabled(false);
    panel.add(comp, 0, 1);
    comp = getGuiComponent(FLD_ALLOW_SENDING_TO_INSTRUMENT);
    if(viewID == FocGroup.VIEW_READ_ONLY) comp.setEnabled(false);
    panel.add(comp, 0, 2);
    comp = getGuiComponent(FLD_ALLOW_CONFIGURATION);
    if(viewID == FocGroup.VIEW_READ_ONLY) comp.setEnabled(false);
    panel.add(comp, 0, 3);
    comp = getGuiComponent(FLD_ALLOW_CONNECTION);
    if(viewID == FocGroup.VIEW_READ_ONLY) comp.setEnabled(false);
    panel.add(comp, 0, 4);
    comp = getGuiComponent(FLD_ALLOW_MONITORING);
    if(viewID == FocGroup.VIEW_READ_ONLY) comp.setEnabled(false);
    panel.add(comp, 0, 5);
    comp = getGuiComponent(FLD_ALLOW_ON_HOLD_MODIF);
    if(viewID == FocGroup.VIEW_READ_ONLY) comp.setEnabled(false);
    panel.add(comp, 0, 6);
    return panel;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  public static FPanel newBrowsePanel(FocList list, int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_ALLOW_SAMPLE_MODIFICATION   = 1;
  public static final int FLD_ALLOW_RESULT_CONFIRMATION   = 2;
  public static final int FLD_ALLOW_SENDING_TO_INSTRUMENT = 3;
  public static final int FLD_ALLOW_CONFIGURATION         = 4;
  public static final int FLD_ALLOW_CONNECTION            = 5;
  public static final int FLD_ALLOW_MONITORING            = 6;
  public static final int FLD_ALLOW_ON_HOLD_MODIF         = 7;
  
  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(FocAppGroup.class, FocDesc.DB_RESIDENT, "APP_GROUP", false);

      focFld = focDesc.addReferenceField();

      focFld = new FBoolField("ALLOW_SAMPLE_MODIFICATION", "Allow sample modification", FLD_ALLOW_SAMPLE_MODIFICATION, false);
      focDesc.addField(focFld);
      
      focFld = new FBoolField("ALLOW_RESULT_CONFIRMATION", "Allow result confirmation", FLD_ALLOW_RESULT_CONFIRMATION, false);
      focDesc.addField(focFld);
      
      focFld = new FBoolField("ALLOW_SENDING_TO_INSTRUMENT", "Allow sending to instrument", FLD_ALLOW_SENDING_TO_INSTRUMENT, false);
      focDesc.addField(focFld);
      
      focFld = new FBoolField("ALLOW_CONFIGURATION", "Allow configuration", FLD_ALLOW_CONFIGURATION, false);
      focDesc.addField(focFld);

      focFld = new FBoolField("ALLOW_CONNECTION", "Allow Connection", FLD_ALLOW_CONNECTION, false);
      focDesc.addField(focFld);

      focFld = new FBoolField("ALLOW_MONITORING", "Allow Monitoring", FLD_ALLOW_MONITORING, false);
      focDesc.addField(focFld);

      focFld = new FBoolField("ALLOW_ON_HOLD_MODIF", "Allow On Hold Modification", FLD_ALLOW_ON_HOLD_MODIF, false);
      focDesc.addField(focFld);
    }
    return focDesc;
  }
}
