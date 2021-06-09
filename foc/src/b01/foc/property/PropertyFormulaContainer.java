package b01.foc.property;

import b01.foc.formula.Formula;

public class PropertyFormulaContainer {

  private Formula formula = null;
  private boolean owner = true;
  private boolean propertyLockBackUp = true;
  
  public void dispose(){
    if( isOwner() && formula != null ){
      formula.dispose();
    }
    formula = null;
  }
  
  public Formula getFormula() {
    return formula;
  }
  
  public void setFormula(Formula formula) {
    this.formula = formula;
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
  
}
