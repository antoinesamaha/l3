/*
 * Created on Dec 2, 2005
 */
package b01.foc.list;

import b01.foc.desc.FocObject;
import b01.foc.desc.field.FFieldPath;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class ListSumOperation implements ListOperation{
  protected FFieldPath fieldPath = null; 
  protected FProperty sumProp = null;
  protected double sum = 0;
  
  public ListSumOperation(FFieldPath fieldPath, FProperty sumProp){
    this.fieldPath = fieldPath; 
    this.sumProp = sumProp;
  }
  
  public void dispose(){
    if(fieldPath != null){
      fieldPath.dispose();
      fieldPath = null;
    }
    if(sumProp != null){
      sumProp.dispose();
      sumProp = null;
    }
  }
  
  public void reset(){
    sum = 0;  
  }
  
  public void treatObject(FocObject obj){
    FProperty prop = fieldPath.getPropertyFromObject(obj);
    if(prop != null){
      sum += prop.getDouble();
    }
  }
  
  public void sendResult(){
    sumProp.setDouble(sum);
  }
}
