package b01.foc.formula;

import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.property.FAttributeLocationProperty;
import b01.foc.property.FProperty;
import b01.foc.property.FPropertyListener;

public class PropertyFormulaContext extends FocAbstractFormulaContext implements FPropertyListener  {
  
  private boolean owner = true;
  private boolean propertyLockBackUp = true;
  private PropertyFormula propertyFormula = null;
  protected FProperty originProperty = null;
  
  public PropertyFormulaContext(Formula formula, FFieldPath fieldPath) {
    super(formula, fieldPath);
  }
 
  public void dispose(){
    if( isOwner() && getFormula() != null ){
      getFormula().dispose();
    }
    super.dispose();
    
    propertyFormula = null;
    originProperty = null;
  }

  public FocObject getOriginFocObject() {
    return originProperty.getFocObject();
  }
  
  public void plugListeners(String expression) {
    plugUnplugListeners(expression, true);
  }

  public void unplugListeners(String expression) {
    plugUnplugListeners(expression, false);
  }
  
  public void plugUnplugListeners(String expression, boolean plug){
    FProperty property = getSourceProperty(expression);
    if( property != null ){
      if(plug){
        compute();
        property.addListener(this);  
      }else{
        property.removeListener(this);
      }
    }
    /*if(expression != null){
      FProperty property = FAttributeLocationProperty.newFieldPathReturnProperty(false, expression, getOriginFocObject().getThisFocDesc(), getOriginFocObject());
      if(property != null){
        if(plug){
          compute();
          property.addListener(this);  
        }else{
          property.removeListener(this);
        }
      }
    }*/
  }
  
  public FProperty getSourceProperty(String expression){
    FProperty property = null;
    if( expression != null){
      property = FAttributeLocationProperty.newFieldPathReturnProperty(false, expression, getOriginFocObject().getThisFocDesc(), getOriginFocObject());
    }
    return property;
  }

  public void propertyModified(FProperty property) {
    compute();
  }
  
  public boolean isOwner() {
    return owner;
  }
  
  public void setOwner(boolean owner) {
    this.owner = owner;
  }
  
  public boolean getPropertyLockBackUp() {
    return propertyLockBackUp;
  }
  
  public void setPropertyLockBackUp(boolean propertyLockBackUp) {
    this.propertyLockBackUp = propertyLockBackUp;
  }

  public void popPropertyFormulaGuiDetailPanel(){
    if( propertyFormula != null ){
      Globals.getDisplayManager().newInternalFrame(new PropertyFormulaGuiDetailsPanel(getPropertyFormula(), 0));  
    }
  }
  
  public PropertyFormula getPropertyFormula() {
    return propertyFormula;
  }

  public void setPropertyFormula(PropertyFormula propertyFormula) {
    this.propertyFormula = propertyFormula;
  }

  public void setOriginProperty(FProperty originProperty) {
    this.originProperty = originProperty;
  }
}
