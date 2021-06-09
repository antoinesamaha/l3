package b01.foc.formula;

import b01.foc.desc.FocDesc;
import b01.foc.desc.FocObject;
import b01.foc.desc.field.FField;
import b01.foc.desc.field.FFieldPath;
import b01.foc.property.FAttributeLocationProperty;
import b01.foc.property.FProperty;


public abstract class FocAbstractFormulaContext extends AbstractFormulaContext {
	
	public abstract FocObject getOriginFocObject();
	private FFieldPath outputFieldPath = null;
	
	public FocAbstractFormulaContext(Formula formula, FFieldPath outputFieldPath){
		super(formula);
		setOutputFieldPath(outputFieldPath);
	}
	
	public void dispose(){
		super.dispose();
		outputFieldPath = null;
	}
	
	private void setOutputFieldPath(FFieldPath outputFieldPath){
		this.outputFieldPath = outputFieldPath;
	}
	
	public FFieldPath getOutputFieldPath(){
		return this.outputFieldPath;
	}
	
  public FocObject getOutputOriginFocObject(){
    return getOriginFocObject();
  }
  
	public boolean isFieldPathValid(FFieldPath fieldPath){
		boolean valid = false;
		if(fieldPath != null){
			valid = true;
			for(int i = 0; i < fieldPath.size() && valid; i++){
				int at = fieldPath.get(i);
				valid = at != FField.NO_FIELD_ID;
			}
		}
		return valid;
	}
	
	/*public Object evaluateExpression(String expression){
		Object object = null;
		if(expression != null){
			FocObject originFocObject = getOriginFocObject();
			if(originFocObject != null){
				FocDesc focDesc = originFocObject.getThisFocDesc();
				FFieldPath fieldPath = FAttributeLocationProperty.newFieldPath(false, expression, focDesc);
				
				if(fieldPath != null && isFieldPathValid(fieldPath)){
					FProperty property = fieldPath.getPropertyFromObject(originFocObject);
					if(property != null){
						object = property.getObject();
					}
				}else{
					//it is not a legal path (fieldName.fieldName.fieldName) so get the value from a hashMap or something else
				}
			}
		}
		return object;
	}*/
	
	public Object evaluateExpression(String expression){
		Object object = null;
		if(expression != null){
			FocObject originFocObject = getOriginFocObject();
			if(originFocObject != null){
				FocDesc focDesc = originFocObject.getThisFocDesc();
				FProperty property = FAttributeLocationProperty.newFieldPathReturnProperty(false, expression, focDesc, getOriginFocObject());
				if(property != null){
					object = property.getObject();
				}else{
					//it is not a legal path (fieldName.fieldName.fieldName) so get the value from a hashMap or something else
				}
			}
		}
		return object;
	}
	
	public void commitValueToOutput(Object value){
		FFieldPath outputFieldPath = getOutputFieldPath();
		if(outputFieldPath != null){
			FocObject originFocObject = getOutputOriginFocObject(); 
			if(originFocObject != null){
				FProperty property = outputFieldPath.getPropertyFromObject(originFocObject);
				if(property != null){
					property.setObject(value);
				}
			}
		}
	}
}
