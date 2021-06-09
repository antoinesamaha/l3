package b01.foc.property.validators;

import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;

/**
 * @author 01Barmaja
 */
public class CodeNamePropertyValidator implements FPropertyValidator{
  
  private int FLD_CODE = 0;
  private int FLD_NAME = 0;
  
  public CodeNamePropertyValidator( int FLD_CODE, int FLD_NAME ){
    this.FLD_CODE = FLD_CODE;
    this.FLD_NAME = FLD_NAME;
  }
  
  
  public void dispose(){
    
  }
  
  public boolean validateProperty(FProperty property){
    
    FocObject focObj = (property != null ? property.getFocObject() : null);
    if(focObj != null ){
      String code = focObj.getPropertyString(FLD_CODE);
      String nodeName = focObj.getPropertyString(FLD_NAME);
      if( code != null && nodeName != null && !nodeName.equalsIgnoreCase("root") && (code.equals("") || code.startsWith("New Item"))){
        if( focObj.getFatherSubject() instanceof FocList ){
          FocList list = (FocList)focObj.getFatherSubject();
          FocObject resultFocObj = list.searchByProperyStringValue(FLD_CODE, nodeName);
          int key = 0;
          while( resultFocObj != null ){
            resultFocObj = list.searchByProperyStringValue(FLD_CODE, nodeName+"("+(++key)+")");
          }
          String keyString = key == 0 ? "" : "("+key+")";
          focObj.setPropertyString(FLD_CODE, nodeName+keyString);
        }
       
      }
    }
    
    return true;
  }
}
