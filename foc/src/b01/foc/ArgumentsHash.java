package b01.foc;

import java.util.HashMap;
import java.util.StringTokenizer;

public class ArgumentsHash {
	private HashMap<String, String> argsHash = null;
	
	public ArgumentsHash(String[] args){
		argsHash = new HashMap<String, String>();

		for(int i=0; i<args.length; i++){
			String name  = null;
			String value = null;
			if (args[i] != null && args[i].startsWith("/")){
				String fullNoSlash  = args[i].substring(1);
		    StringTokenizer tokenizer = new StringTokenizer(fullNoSlash, ":", false);
	      while(tokenizer.hasMoreTokens()){
	        if(name == null){
	        	name = tokenizer.nextToken();
	        }else if(value == null){
	        	value = tokenizer.nextToken();
	        }else{
	        	tokenizer.nextToken();
	        }
	      }
			}
    	if(value != null && name != null && name.trim().compareTo("") != 0 && value.trim().compareTo("") != 0){
    		argsHash.put(name.toUpperCase(), value);
    	}
  		name = null;
  		value = null;
		}
	}
	
	public String get(String key){
		return (String)argsHash.get(key.toUpperCase());
	}
}
