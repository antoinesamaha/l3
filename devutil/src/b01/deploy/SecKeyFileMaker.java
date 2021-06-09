package b01.deploy;
/*
 * Created on 17-Jun-2005
 */

import java.io.*;

import b01.foc.Globals;
import b01.foc.util.*;

/**
 * @author 01Barmaja
 */
public class SecKeyFileMaker {
  
  private static PrintStream getPrintStream(String fileName){
    File file = new File(fileName);
    PrintStream stream = null;
    try{
      if(file.exists()){
        file.delete();
      }
      file.createNewFile();
      stream = new PrintStream(file);
    }catch(Exception e){
      e.printStackTrace();
    }
    return stream ;    
  }
  
  public static void main(String[] args){
    try{
      if(args.length != 2){
        Globals.logString("args[0] = {project dir name}, args[1] = {package}");
        Globals.logString("args[1] = {astm}, args[1] = {b01.labcomm}");
      }else{
        String dirName = args[0];        
        String packageStr = args[1];
        String packageDirStr = packageStr.replace(".","\\");        
        
        PrintStream out = getPrintStream("PhMaInfo.java");      
        PrintStream cout = getPrintStream("check.txt");
        PrintStream copyStream = getPrintStream("copy.cmd");
        
        copyStream.append("copy PhMaInfo.java c:\\01barmaja\\dev\\java\\app\\"+dirName+"\\src\\"+packageDirStr+"\\PhMaInfo.java");
        copyStream.close();
        
        out.println("package "+args[1]+";");
        out.println("public class PhMaInfo {");
        out.println("  private static String str = null;");
        out.println("  public static String getID(){");
        out.println("    if(str == null){");
        out.print("      char c[] = {");
        
        BCifer bc = new BCifer();
        String id = PCID.getUniqueID();
        ASCII ascii = new ASCII(bc.encode(id));
  
        ascii.writeAsciiCodeArray(out);
        cout.println(id);
        
        out.println("};");
        out.println("      str = new String(c);");
        out.println("    }");
        out.println("    return str;");
        out.println("  }");
        out.println("}");
        out.close();
        cout.close();
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  /*
  public static void main(String[] args){
    try{
      for(char c=0; c<250; c++){
        int i=c;
        System.out.println("c["+i+"="+c);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  */

}
