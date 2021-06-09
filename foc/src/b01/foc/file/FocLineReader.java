/*
 * Created on Oct 24, 2005
 */
package b01.foc.file;

import java.util.StringTokenizer;

/**
 * @author 01Barmaja
 */
public abstract class FocLineReader {
	
	public abstract void readToken(String token, int pos);//This function can be called from the readLine function
	
  private char fieldSeparator[] = null;
  private String tokenDelimiters = null;
  
  public FocLineReader(){
  	fieldSeparator = new char[1];
  	fieldSeparator[0] = ',';
  }

  public FocLineReader(char fieldDelimiter){
  	fieldSeparator = new char[1];
  	fieldSeparator[0] = fieldDelimiter;
  }

  public FocLineReader(char fieldDelimiter1, char fieldDelimiter2){
  	fieldSeparator = new char[2];
  	fieldSeparator[0] = fieldDelimiter1;
  	fieldSeparator[1] = fieldDelimiter2;
  }

  public void dispose(){
  	tokenDelimiters = null;
  }
  
  private String getTokenDelimiters(){
  	if(tokenDelimiters == null){
  		tokenDelimiters = String.valueOf(fieldSeparator)+"\"";
  	}
  	return tokenDelimiters;
  }

  public String getTokenAt(StringBuffer buff, int at){
    String token = null;
    StringTokenizer tok = new StringTokenizer(buff.toString(), getTokenDelimiters(), false);

    int count = 1;
    while(tok.hasMoreTokens() && count <= at){
      String str = tok.nextToken();
      if(count == at){
        token = str;
      }
      count++;
    }
    return token;
  }  

  public void scanTokens(StringBuffer buff){
    StringTokenizer tok = new StringTokenizer(buff.toString(), getTokenDelimiters(), true);      
    int pos = 0;
    while(tok.hasMoreTokens()){    	
      String  str = tok.nextToken();
      boolean callReadToken = true;
      
      for(int i=0; i<fieldSeparator.length; i++){
      	if(str.compareTo(String.valueOf(fieldSeparator[i])) == 0){
      		pos++;
      		callReadToken = false;
      	}
      }

    	if(str.compareTo("\"") == 0){
    		callReadToken = false;
      }

      if(callReadToken){
        readToken(str, pos);
      }
    }
  }
  
	public char getFieldSeparator() {
		return fieldSeparator[0];
	}
	
	public void setFieldSeparator(char fieldSeparator) {
		this.fieldSeparator[0] = fieldSeparator;
	}
}