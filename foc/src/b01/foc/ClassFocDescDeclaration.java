package b01.foc;

import java.lang.reflect.Method;

import b01.foc.desc.FocDesc;

public class ClassFocDescDeclaration implements IFocDescDeclaration{
	
	private Class cls = null;
	private FocDesc focDescription = null;
	
	public ClassFocDescDeclaration(Class cls){
		this.cls = cls;
	}
	
	public FocDesc getFocDesctiption() {
		if(focDescription == null){
			focDescription = ClassFocDescDeclaration.getFocDescriptionForClass(cls);
			if(focDescription != null){
				String name = focDescription.getStorageName();
				Globals.getApp().putIFocDescDeclaration(name, this);
			}
		}
		return focDescription;
		
    /*FocDesc focDesc = null;
    try {
      if (cls != null) {
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = cls.getMethod("getInstance", argsDeclare);
        }catch(NoSuchMethodException e){
        	methodGetFocDesc = cls.getMethod("getFocDesc", argsDeclare);
        }
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
    	Globals.logString("Exception while getting FocDesc for class : "+cls.getName());
      Globals.logException(e);
    }
    
    return focDesc;*/
	}
	
	public static FocDesc getFocDescriptionForClass(Class cls){
    FocDesc focDesc = null;
    try {
      if(cls.getName().contains("ResourceChargeEstimateDesc")){
        int debug = 5;
      }
      if (cls != null) {
        Class[] argsDeclare = null;
        Object[] args = null;
        Method methodGetFocDesc = null;
        try{
        	methodGetFocDesc = cls.getMethod("getInstance", argsDeclare);
        }catch(NoSuchMethodException e){
        	methodGetFocDesc = cls.getMethod("getFocDesc", argsDeclare);
        }
        if(methodGetFocDesc != null){
        	focDesc = (FocDesc) methodGetFocDesc.invoke(null, args);
        }
      }
    } catch (Exception e) {
      Globals.getDisplayManager().popupMessage("getFocDescriptionForClass");
    	Globals.logString("Exception while getting FocDesc for class : "+cls.getName());
      Globals.logException(e);
    }
    return focDesc;
	}

	public int getPriority() {
		return IFocDescDeclaration.PRIORITY_FIRST;
	}
}
