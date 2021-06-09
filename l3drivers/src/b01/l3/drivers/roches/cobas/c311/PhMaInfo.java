package b01.l3.drivers.roches.cobas.c311;
public class PhMaInfo implements b01.foc.util.IPhMaInfo{
  private static String str = null;
  public String getID(){
    if(str == null){
      char c[] = {68,69,67,69,67,69,68,49,67,68,67,57,68,49,67,57,66,65,68,49,67,69,66,68,68,49,67,55,67,65,68,49,67,68,67,53};
      str = new String(c);
    }
    return str;
  }
}
