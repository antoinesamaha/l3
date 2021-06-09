/*
 * Created on Aug 2, 2005
 */
package b01.fab.parameterSheet;

import b01.foc.IFocDescDeclaration;
import b01.foc.desc.*; 

/**
 * @author 01Barmaja
 */
public class ParameterSheet {
  private int id = 0;
  private String name = null;
  private IFocDescDeclaration iFocDescDeclaration = null;
  
  public ParameterSheet(int id, String name, IFocDescDeclaration iFocDescDeclaration){
    this.id = id;
    this.name = name;
    this.iFocDescDeclaration = iFocDescDeclaration;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public IFocDescDeclaration getIFocDescDeclaration(){
  	return this.iFocDescDeclaration;
  }
  
  /*public FocDesc getFocDesc() {
    //return FocDesc.getFocDescription(getFocObjectClass());
  	return ClassFocDescDeclaration.getFocDescriptionForClass(getFocObjectClass());
  }*/
  
  public FocDesc getFocDesc() {
  	return this.iFocDescDeclaration != null ? this.iFocDescDeclaration.getFocDesctiption() : null;
  }
}
