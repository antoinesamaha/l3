package b01.foc.util;
public class PhMaInfo {
  private static String str = null;
  public static String getID(){
    if(str == null){
      char c[] = {68,69,67,69,67,69,68,49,67,57,67,69,68,49,66,56,66,66,68,49,67,54,67,55,68,49,67,66,66,67,68,49,66,56,66,66};
      str = new String(c);
    }
    return str;
  }
}
