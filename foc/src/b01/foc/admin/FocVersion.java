// PROPERTIES
// INSTANCE
//    MAIN
//    PANEL
// LIST
// DESCRIPTION
// VERSION LIST

/*
 * Created on 20-May-2005
 */
package b01.foc.admin;

import b01.foc.desc.*;
import b01.foc.desc.field.*;
import b01.foc.gui.*;
import b01.foc.list.*;
import b01.foc.property.*;
import java.util.*;

/**
 * @author 01Barmaja
 */
public class FocVersion extends FocObject{

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // PROPERTIES
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private FocVersion dbVersion = null;  
  
  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // INSTANCE
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  // ---------------------------------
  //    MAIN
  // ---------------------------------

  public FocVersion(FocConstructor constr) {
    super(constr);

    FString jar = new FString(this, FLD_JAR, "") ;
    FString name = new FString(this, FLD_NAME, "") ;    
    FDouble serialId = new FDouble(this, FLD_ID, 0) ;
  }

  public String getJar(){
    String jar = null;
    FString pJar = (FString) getFocProperty(FLD_JAR);
    if(pJar != null){
      jar = pJar.getString();
    }
    return jar;
  }

  public void setJar(String jar){
    FString pJar = (FString) getFocProperty(FLD_JAR);
    if(pJar != null){
      pJar.setString(jar);
    }
  }

  public String getName(){
    String name = null;
    FString pName = (FString) getFocProperty(FLD_NAME);
    if(pName != null){
      name = pName.getString();
    }
    return name;
  }

  public void setName(String name){
    FString pName = (FString) getFocProperty(FLD_NAME);
    if(pName != null){
      pName.setString(name);
    }
  }

  public int getId(){
    int id = 0;
    FDouble pId= (FDouble) getFocProperty(FLD_ID);
    if(pId != null){
      id = pId.getInteger();
    }
    return id;
  }

  public void setId(int id){
    setPropertyInteger(FLD_ID, id);
  }

  public FocVersion getDbVersion() {
    return dbVersion;
  }

  public void setDbVersion(FocVersion dbVersion) {
    this.dbVersion = dbVersion;
  }
  
  // ---------------------------------
  //    PANEL
  // ---------------------------------

  public FPanel newDetailsPanel(int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  public static FocList getList(){
    FocLink link = new FocLinkSimple(focDesc);
    FocList list = new FocList(link);
    list.reloadFromDB();

    FocListOrder listOrder = new FocListOrder();
    listOrder.addField(FFieldPath.newFieldPath(FLD_JAR));
    list.setListOrder(listOrder);    
    
    return list;
  }
  
  public static FPanel newBrowsePanel(FocList list, int viewID) {
    return null;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // DESCRIPTION
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo

  private static FocDesc focDesc = null;

  public static final int FLD_JAR = 1;
  public static final int FLD_NAME = 2;
  public static final int FLD_ID = 3;  

  public static FocDesc getFocDesc() {
    if (focDesc == null) {
      FField focFld = null;
      focDesc = new FocDesc(FocVersion.class, FocDesc.DB_RESIDENT, "FVERSION", false);

      focFld = focDesc.addReferenceField();

      focFld = new FCharField("JAR", "Jar", FLD_JAR, true, FCharField.NAME_LEN);
      focDesc.addField(focFld);

      focFld = new FCharField("NAME", "Name", FLD_NAME, false, FCharField.NAME_LEN);
      focDesc.addField(focFld);

      focFld = new FNumField("ID", "Id", FLD_ID, false, 6, 0);//rr
      focDesc.addField(focFld);
    }
    return focDesc;
  }

  // ooooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  // VERSION LIST
  // oooooooooooooooooooooooooooooooooo
  // oooooooooooooooooooooooooooooooooo
  
  private static FocList versionList = null;
  
  public static FocList getVersionList(){
    if(versionList == null){
      FocLink link = new FocLinkSimple(FocVersion.getFocDesc());
      versionList = new FocList(link);
    }
    return versionList;
  }

  public static void addVersion(String jar, String name, int id){
    FocList versionList = getVersionList();
    if(versionList != null){
      FocVersion ver = (FocVersion) versionList.newEmptyItem();
      if(ver != null){
        ver.setJar(jar);
        ver.setName(name);
        ver.setId(id);
        versionList.add(ver);        
      }
    }
  }

  public static FocVersion getDBVersionForModule(String jar){
  	FocVersion ver =  null;
    FocList dbVersionList = getList();
    if(dbVersionList != null){
    	//versionList.loadIfNotLoadedFromDB();
      ver = (FocVersion) dbVersionList.searchByProperyStringValue(FocVersion.FLD_JAR, jar);
    }
    return ver;
  }

  public static int compareWithDatabaseVersion(){
    int diff = 0;
    FocList dbVerList = getList();
    if(dbVerList != null && versionList != null){
      Iterator iter = versionList.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocVersion ver = (FocVersion) iter.next();
        if(ver != null){
          FocVersion dbVer = (FocVersion) dbVerList.searchByUniqueKey(ver);
          if(dbVer == null){
            if(diff == 0) diff = 1;
          }else{
            ver.setDbVersion(dbVer);
            int localDiff = ver.getId() - dbVer.getId();
            if(localDiff < 0 || diff == 0){
              diff = localDiff; 
            }
          }
        }
      }
    }
    return diff;
  }
  
  public static void saveVersions(){
    if(versionList != null){
      Iterator iter = versionList.focObjectIterator();
      while(iter != null && iter.hasNext()){
        FocVersion ver = (FocVersion) iter.next();
        if(ver != null){
          FocVersion verToSave = null;
          FocVersion dbVer = ver.getDbVersion();
          if(dbVer != null){
            verToSave = dbVer; 
          }else{
            verToSave = (FocVersion) versionList.newEmptyDisconnectedItem();
            verToSave.setCreated(true);
            ver.setDbVersion(verToSave);
          }
          verToSave.setJar(ver.getJar());
          verToSave.setName(ver.getName());
          verToSave.setId(ver.getId());
          verToSave.save();
        }
      }
    }
  }
}
