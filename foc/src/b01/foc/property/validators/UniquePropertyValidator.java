/*
 * Created on Jul 25, 2005
 */
package b01.foc.property.validators;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import b01.foc.Globals;
import b01.foc.desc.FocObject;
import b01.foc.list.FocList;
import b01.foc.property.FProperty;
import b01.foc.util.ASCII;

/**
 * @author 01Barmaja
 */
public class UniquePropertyValidator implements FPropertyValidator {

  private ArrayList<Character> specialCharactersArrayList = null;

  private final char CHAR_SPACE = ASCII.SPACE;
  private final char CHAR_DASH = ASCII.DASH;
  private final char CHAR_SLASH_RIGHT_TO_LEFT = ASCII.SLASH_RIGHT_TO_LEFT;
  private final char CHAR_SLASH_LEFT_TO_RIGHT = ASCII.SLASH_LEFT_TO_RIGHT;
  private final char CHAR_UNDER_SCORE = ASCII.UNDER_SCORE;

  public void dispose() {
    if (specialCharactersArrayList != null) {
      for (int i = 0; i < specialCharactersArrayList.size(); i++) {
        specialCharactersArrayList.clear();
      }
      specialCharactersArrayList = null;
    }
  }

  public boolean validateProperty(FProperty property) {
    newSpecialCharactersList();
    FocObject focObject = (property != null ? property.getFocObject() : null);

    if (focObject != null) {
      if (focObject.getFatherSubject() instanceof FocList) {

        FocList list = (FocList) focObject.getFatherSubject();
        Iterator iter = list.newSubjectIterator();

        int fieldID = property.getFocField().getID();

        boolean found = false;
        boolean modified = false;
        String focObjectValueString = focObject.getPropertyString(fieldID).toUpperCase();

        String focObjectValueStringModified = deleteSpecialCharactersFromString(focObjectValueString);

        while (iter != null && iter.hasNext() && !found) {
          FocObject focObjInList = (FocObject) iter.next();
          String focObjectInListValueString = focObjInList.getPropertyString(fieldID).toUpperCase();
          if (focObject != focObjInList) {
            if (focObjectValueString.equals(focObjectInListValueString)) {
              found = true;
            } else {
              focObjectInListValueString = deleteSpecialCharactersFromString(focObjectInListValueString);
              if (focObjectValueStringModified.equals(focObjectInListValueString)) {
                found = true;
                modified = true;
              }
            }
          }
        }

        if (found) {
          if (modified) {
            if (focObject.getPropertyString(fieldID).indexOf("New Item") == -1) {
              int res = JOptionPane.showConfirmDialog(null, "Value exist in other format.\n Do you want to keep it?", "Duplicated Value", JOptionPane.YES_NO_OPTION);
              if (res == JOptionPane.NO_OPTION) {
                property.setString("");
              }
            }
          } else {
            if (focObject.getPropertyString(fieldID).indexOf("New Item") == -1) {
              Globals.getDisplayManager().popupMessage("The value chosen already exists.\nPlease choose another value.");
            }
            property.setString("");
          }
        }
      }
    }
    return true;
  }

  public void newSpecialCharactersList() {
    specialCharactersArrayList = new ArrayList<Character>();
    specialCharactersArrayList.add(CHAR_DASH);
    specialCharactersArrayList.add(CHAR_SPACE);
    specialCharactersArrayList.add(CHAR_SLASH_LEFT_TO_RIGHT);
    specialCharactersArrayList.add(CHAR_SLASH_RIGHT_TO_LEFT);
    specialCharactersArrayList.add(CHAR_UNDER_SCORE);
  }

  public ArrayList<Character> getSpecialCharactersArrayList() {
    return specialCharactersArrayList;
  }

  public boolean isSpecialCharacter(char c) {
    boolean found = false;
    for (int i = 0; i < getSpecialCharactersArrayList().size(); i++) {
      char specialChar = specialCharactersArrayList.get(i);
      if (specialChar == c) {
        found = true;
      }
    }
    return found;
  }

  public String deleteSpecialCharactersFromString(String str) {
    String newStr = new String();
    if (str != null) {
      for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        if (!isSpecialCharacter(c)) {
          newStr += c;
        }
      }
    }
    return newStr;
  }

}
