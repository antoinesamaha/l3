/*
 * Created on 16-Jun-2005
 */
package b01.foc.util;

import java.io.*;

import b01.foc.Globals;

/**
 * @author 01Barmaja
 */
public class ASCII {
  private String str = null;
  
  public final static char NUL =  0;
  public final static char SOH =  1;
  public final static char STX =  2;
  public final static char ETX =  3;
  public final static char EOT =  4;
  public final static char ENQ =  5;
  public final static char ACK =  6;
  public final static char BEL =  7;
  public final static char BS  =  8;
  public final static char TAB =  9;
  public final static char LF  = 10;
  public final static char VT  = 11;
  public final static char FF  = 12;
  public final static char CR  = 13;
  public final static char SO  = 14;
  public final static char SI  = 15;
  public final static char DLE = 16;
  public final static char DC1 = 17;
  public final static char DC2 = 18;
  public final static char DC3 = 19;
  public final static char DC4 = 20;
  public final static char NACK = 21;
  public final static char SYN = 22;
  public final static char ETB = 23;
  public final static char CAN = 24;
  public final static char EN  = 25;
  public final static char SUB = 26;
  public final static char ESC = 27;
  public final static char FS  = 28;
  public final static char GS  = 29;
  public final static char RS  = 30;
  public final static char US  = 31;
  public final static char SPACE = 32;
  
  public final static char DASH                 = 45;
  public final static char SLASH_RIGHT_TO_LEFT  = 47;
  public final static char SLASH_LEFT_TO_RIGHT  = 92;
  public final static char UNDER_SCORE          = 95;
  
  public final static char TILDA  = 126;//~
  public final static char SQUARE = 178;
  public final static char CUBE   = 179;
  
  
  
  private static final String[] ASCII33 = {"NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", 
  	"TAB", "LF", "VT", "FF", "CR", "SO", "SI", "DLE", "DC1", "DC2", "DC3", "DC4", "NACK", "SYN", "ETB", "CAN", 
  	"EN", "SUB", "ESC", "FS", "GS", "RS", "US", "."};
  
  public ASCII(String str){
    this.str = str;
  }
  
  public void getASCIIArray(){
    for(int i=0 ;i< str.length(); i++){
      Globals.logString("c["+i+"]="+str.charAt(i)+":"+(int)str.charAt(i));
    }
    Globals.logString("char c[] = new char["+str.length()+"];");    
    for(int i=0 ;i< str.length(); i++){
      Globals.logString("c["+i+"]="+(int)str.charAt(i)+";");
    }
    
    System.out.print("char c[] = new {");
    writeAsciiCodeArray(System.out);
    Globals.logString("};");
    
    Globals.logString("String string = new String(c);");    
  }

  public void writeAsciiCodeArray(PrintStream out){
    for(int i=0 ; i<str.length(); i++){
      if(i > 0){
        out.print(",");
      }
      out.print((int)str.charAt(i));
    }
  }

  public static String convertNonCharactersToDescriptions(String str){
    StringBuffer newStr = new StringBuffer();
    
    for(int i=0; i<str.length(); i++){
      char c = str.charAt(i);
      if(c < 33){
        newStr.append("["+ASCII33[c]+"]");
        if(c == 10){
          newStr.append(c);
        }
      }else{
        newStr.append(c);        
      }
    }
    return newStr.toString();
  }
}
