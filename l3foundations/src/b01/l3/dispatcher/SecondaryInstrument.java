package b01.l3.dispatcher;

import java.util.HashMap;
import java.util.Properties;

public class SecondaryInstrument {
	private String instrumentCode1 = null;
	private String instrumentCode2 = null;
	private HashMap<String, String> table = null;
	public final static String PREFIX = "sub";
	
	/*public SecondaryInstrument(Properties props, int idx){
		String prefix = PREFIX+idx+".";
		instrumentCode1 = props.getProperty(prefix+"instrumentCode1");
		instrumentCode2 = props.getProperty(prefix+"instrumentCode2");
		
		int i=1;
		table = new HashMap<String, String>();
		String lisTestCode = props.getProperty(prefix+"testCode"+i);
		while(lisTestCode != null){
			table.put(lisTestCode, lisTestCode);
			i++;
		}
	}*/
  
  public SecondaryInstrument(Properties props, int idx){
    String prefix = PREFIX+idx+".";
    instrumentCode1 = props.getProperty(prefix+"instrumentCode1");
    instrumentCode2 = props.getProperty(prefix+"instrumentCode2");
    
    int i=1;
    table = new HashMap<String, String>();
    String lisTestCode = new String();
    while(lisTestCode != null){
      lisTestCode = props.getProperty(prefix+"testCode"+i);
      if(lisTestCode != null){
        table.put(lisTestCode, lisTestCode);
      }
      i++;
    }
  }
	
	public void dispose(){
		instrumentCode1 = null;
		instrumentCode2 = null;
		if(table != null){
			table.clear();
			table = null;
		}
	}
	
	public boolean isValid(){
		return instrumentCode1 != null;
	}
	
	public String getPrimaryCode(){
		return instrumentCode1;
	}
	
	public String getSecondaryCode(){
		return instrumentCode2;
	}
	
	public boolean containsTest(String test){
		return table.containsKey(test);
	}
}
