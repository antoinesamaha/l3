package b01.foc.formula;

import b01.foc.desc.FocConstructor;
import b01.foc.desc.FocFieldEnum;
import b01.foc.desc.FocObject;
import b01.foc.property.FProperty;

public class PropertyFormula extends FocObject {

  public PropertyFormula(FocConstructor constr) {
    super(constr);
    newFocProperties();
  }
  
  public String getFieldName(){
    return getPropertyString(PropertyFormulaDesc.FLD_FIELD_NAME);
  }
  
  public void setFieldName( String fieldName ){
    setPropertyString(PropertyFormulaDesc.FLD_FIELD_NAME, fieldName);
  }
  
  public String getExpression(){
    return getPropertyString(PropertyFormulaDesc.FLD_EXPRESSION);
  }
  
  public void setExpression( String expression ){
    setPropertyString(PropertyFormulaDesc.FLD_EXPRESSION, expression);
  }
  
  public Formula getFormula(){
    /*Formula formula = null;
    FocObject foreinObject = getForeignObject();
    if( foreinObject != null ){
      FocDesc desc = foreinObject.getThisFocDesc();
      FField field = desc.getFieldByName(getFieldName());
      if( field != null ){
        formula = new Formula(desc, getExpression());
        //formula.setCurrentFocObject(foreinObject);
        formula.setFormulaFocObject(this);
      }
    }*/
    
    return new Formula(getExpression());
  }
  
  public FProperty getForeignObjectPropertyIfNotEmpty(){
    FProperty foundProp = null;
    PropertyFormulaDesc desc = (PropertyFormulaDesc)getThisFocDesc();
    for( int i = 0; i < desc.getForeignObjectFieldCount() && foundProp == null; i++ ){
      int fieldID = PropertyFormulaDesc.FLD_FIRST_OBJECT+i;
      FocObject foreignObject = getPropertyObject(fieldID);
      if(foreignObject != null){
        foundProp = getFocProperty(fieldID);
      }
    }
    return foundProp;
  }
  
  public void updateFatherFormulaProperties(){
    FocObject foreignObject = getForeignObject();
    if( foreignObject != null ){
      FocFieldEnum iter = new FocFieldEnum(foreignObject.getThisFocDesc(), foreignObject, FocFieldEnum.CAT_ALL, FocFieldEnum.LEVEL_PLAIN);
      while(iter != null && iter.hasNext()){
        iter.next();
        FProperty prop = (FProperty) iter.getProperty();
        if( prop.getFocField().getDBName().equals(getFieldName()) ){
          if(prop.isWithFormula()){
            String expression = getExpression(); 
            
            if(expression.equals("")){
              prop.removeFormula();
              setDeleted(true);
            }else{
              prop.changeFormula(expression);  
            }
            
            break;  
          }
        }
      }
    }
  }
  
  public FocObject getForeignObject(){
    FocObject foreinObject = null;
    FProperty prop = getForeignObjectPropertyIfNotEmpty();
    if( prop != null ){
      foreinObject = getPropertyObject(prop.getFocField().getID());
    }
    return foreinObject; 
  }
  
  public void dispose(){
    super.dispose();
  }
}
