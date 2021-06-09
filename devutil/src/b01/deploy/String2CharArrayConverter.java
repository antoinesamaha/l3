package b01.deploy;
/*
 * Created on 17-Jun-2005
 */

import java.io.*;

import b01.foc.util.*;

/**
 * @author 01Barmaja
 */
public class String2CharArrayConverter {
  public static void main(String[] args){
    try{
      File fileToSend = new File("in.txt");
      FileReader in = new FileReader(fileToSend);
      int iChar = 0;
      StringBuffer strBuffer = new StringBuffer();      
      do{
        iChar = in.read();
        if(iChar == '\r' || iChar == '\n' || iChar<0){
          if(strBuffer.length() > 0){
            ASCII ascii = new ASCII(new String(strBuffer));
            ascii.writeAsciiCodeArray(System.out);
          }
        }else{
          strBuffer.append((char)iChar);              
        }
      }while(iChar >=0);      
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
