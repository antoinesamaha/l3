package b01.foc.fUnit;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

public class LogFile extends LogResultFileAbstract{

  public LogFile(String name){
    try{
        /*fileWriter = new FileWriter(name+".txt");
        fileWriter.close();*/
        fileWriter = new FileWriter(name+".txt");
        out = new PrintWriter(fileWriter, true);
        out.println("---------------------------------------------------");
        out.println("--- Automated test started by 01Barmaja FocUnit");
        out.println("--- Suite: "+name);
        //DateFormat format = DateFormat.getDateTimeInstance();
        //format.setTimeZone(TimeZone.getDefault());
        //out.println("---Default At "+format.format(new Date()));
        //TimeZone zone = new TimeZone();
        
        /*String a[] = TimeZone.getAvailableIDs();
        for(int i = 0; i < a.length; i++){
          System.out.println(a[i]);
        }*/
        /*TimeZone z = TimeZone.getTimeZone("Asia/Beirut");
        System.out.println(z.getID());
        System.out.println(z.getRawOffset());*/
        
        out.println("--- At "+new Date());
        out.println("---------------------------------------------------");
        out.println("");
        out.println("");        
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public void dispose(){
    try{
      out.println("");
      out.println("-- End of suite --");
      out.flush();
      fileWriter.close();  
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
