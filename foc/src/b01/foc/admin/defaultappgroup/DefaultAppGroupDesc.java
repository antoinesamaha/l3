package b01.foc.admin.defaultappgroup;

import b01.foc.desc.FocDesc;

public class DefaultAppGroupDesc extends FocDesc {
	
	public DefaultAppGroupDesc(){
		super(DefaultAppGroup.class, false, "DEFAULT_APP_GROUP", false);
	}
	
  //ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // SINGLE INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;
  
  public static FocDesc getInstance() {
    if (focDesc==null){
      focDesc = new /*XXX*/DefaultAppGroupDesc();
    }
    return focDesc;
  } 

}
