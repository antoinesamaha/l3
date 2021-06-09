package b01.ant.tasks;
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
public class CreateSecurityJavaFile extends Task{
  
	private String packageName = "";
	private String srcDir = "";
	private String code = "";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		log("Code:"+code);
		this.code = code;
	}
	
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
  
	public void execute(){
		log("Start");		

    String packageDirStr = getPackageName().replace(".","/");        
        
    log("FileName="+getSrcDir()+"/PhMaInfo.java");
    PrintStream out = getPrintStream(getSrcDir()+"/PhMaInfo.java");      
    
    out.println("package "+getPackageName()+";");
    out.println("public class PhMaInfo implements b01.foc.util.IPhMaInfo{");
    out.println("  private static String str = null;");
    out.println("  public String getID(){");
    out.println("    if(str == null){");
    out.print("      char c[] = {");
    
    BCifer bc = new BCifer();
    String id = code;
    if(id == null || id.compareTo("") == 0){
    	id = PCID.getUniqueID();
    }
    
    ASCII ascii = new ASCII(bc.encode(id));
    ascii.writeAsciiCodeArray(out);    
    
    out.println("};");
    out.println("      str = new String(c);");
    out.println("    }");
    out.println("    return str;");
    out.println("  }");
    out.println("}");
    out.close();
		
		log("End");		
	}
}
