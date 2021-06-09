package b01.foc;

import java.util.HashMap;
import java.util.Iterator;

public class ClassFactory<C> {
	private HashMap<String, Class<? extends C>> classMap = null;
		
	public ClassFactory(){
		classMap = new HashMap<String, Class<? extends C>>();
	}
	
	public void addClass(String className, Class<? extends C> cls){
  	if(classMap != null && cls != null){
  		classMap.put(className, cls);
  	}
  }
	
	public Class<? extends C> getClass(String code){
    return classMap != null && code != null? classMap.get(code) : null;
  }
	
  public Iterator<String> newClassNameIterator(){
  	return classMap.keySet().iterator();
  }
  
  public C newInstance(String code) throws Exception {
  	Class<? extends C> cls = getClass(code);
  	return (C) cls.newInstance(); 	
  }
}
