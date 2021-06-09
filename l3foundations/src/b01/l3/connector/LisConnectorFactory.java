package b01.l3.connector;

import java.util.HashMap;
import java.util.Iterator;

public class LisConnectorFactory {
	private HashMap<String, Class> lisConnectorMap = null;
  
  private static LisConnectorFactory lisConnectorFactory = null;
  
  public static LisConnectorFactory getInstance(){
    if(lisConnectorFactory == null) lisConnectorFactory = new LisConnectorFactory();
    return lisConnectorFactory;
  }
  
  public LisConnectorFactory(){
    lisConnectorMap = new HashMap<String, Class>();
  }
  
  public void addLisConnector(String className, Class cls){
    if(lisConnectorMap != null && cls != null){
      lisConnectorMap.put(className, cls);
    }
  }
  
  public Class getLisConnector(String code){
    return lisConnectorMap != null && code != null? lisConnectorMap.get(code) : null;
  }

  public Iterator<String> newClassNameIterator(){
  	return lisConnectorMap.keySet().iterator();
  }
}
