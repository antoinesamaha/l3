package b01.ant;

/*
 * Created on 17-Jun-2005
 */

import java.io.File;
import java.io.PrintStream;

import org.apache.tools.ant.Task;

import b01.foc.util.ASCII;
import b01.foc.util.BCifer;
import b01.foc.util.PCID;

/**
 * @author 01Barmaja
 */
public class CreateSecFile {
  
	private String packageName = "";
	private String srcDir = "";	

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String sourceDir) {
		this.srcDir = sourceDir;
	}

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
		CreateSecFile c = new CreateSecFile();
		c.setSrcDir(".");
		c.setPackageName("package.name");
		
		System.out.println("Start");		

    String packageDirStr = c.getPackageName().replace(".","/");        
        
    System.out.println("FileName="+c.getSrcDir()+"/PhMaInfo.java");
    PrintStream out = getPrintStream(c.getSrcDir()+"/PhMaInfo.java");      
    
    out.println("package "+c.getPackageName()+";");
    out.println("public class PhMaInfo {");
    out.println("  private static String str = null;");
    out.println("  public static String getID(){");
    out.println("    if(str == null){");
    out.print("      char c[] = {");
    
    BCifer bc = new BCifer();
    String id = PCID.getUniqueID();
    
    ASCII ascii = new ASCII(bc.encode(id));
    ascii.writeAsciiCodeArray(out);    
    
    out.println("};");
    out.println("      str = new String(c);");
    out.println("    }");
    out.println("    return str;");
    out.println("  }");
    out.println("}");
    out.close();
		
    System.out.println("End");		
	}
	
	
}
