/*
 * Created on Jul 25, 2005
 */
package b01.foc.property.validators;

import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class FNumLimitValidator implements FPropertyValidator{

  private boolean minActive = false; 
  private boolean maxActive = false;
  private double min = 0;
  private double max = 0;
  
  public FNumLimitValidator(double min, int max){
    this.min = min;
    this.max = max;
    minActive = true;
    maxActive = true;
  }

  public FNumLimitValidator(boolean isMin, int value){
    this.min = value;
    this.max = value;
    minActive = isMin;
    maxActive = !isMin;
  }

  public void dispose(){
    
  }
  
  public boolean validateProperty(FProperty property){
    double d = property.getDouble();
    
    if(minActive && d < min){
      property.setDouble(min);
    }else if(maxActive && d > max){
      property.setDouble(max);
    }
    return true;
  }
}
