package b01.l3;

public class L3Globals {

	public static final int VIEW_CONFIG = 0;
	public static final int VIEW_NORMAL = 1;
  //BAntoine - Permute
  public static final int VIEW_PERMUTE = 2;
  //public static final int VIEW_MONITORING_ONLY = 2;//not used
  //EAntoine - Permute
  public static final int VIEW_SAMPLES= 3;
  public static final int VIEW_SAMPLES_DONT_WAIT_FOR_ANY= 4;
  public static final int VIEW_SAMPLES_WAIT_FOR_RESULT_CONFIRMATION_ONLY= 5;
  public static final int VIEW_SAMPLES_WAIT_FOR_SENDING_ACCEPTATION_ONLY= 6;
  public static final int VIEW_SAMPLES_WAIT_FOR_BOTH= 7;
  public static final int VIEW_SAMPLES_OWNER = 8;// not used
  public static final int VIEW_SAMPLES_MONITOR = 9;// not used
  public static final int VIEW_ARCHIVE = 10;// used only in menus
  public static final int VIEW_ARCHIVED_SAMPLES = 11;// not used
  public static final int VIEW_IN_OTHER_WINDOW = 12;
  
  public static final int APPLICATION_MODE_WITHOUT_DB = 0;
	public static final int APPLICATION_MODE_WITH_DB = 1;
  public static final int APPLICATION_MODE_SAME_THREAD = 2;
  
  public static int view_ExtractRealViewId(int viewID){
    return viewID %100;
  }
  
  public static boolean view_IsViewLocked(int viewID){
    return viewID >= 100;
  }
  
  public static int view_BuildViewId(int viewID, boolean locked ){
    int view = viewID;
    if(locked){
      view += 100;
    }
    return view;
  }
}
