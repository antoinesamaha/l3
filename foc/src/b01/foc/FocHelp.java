/*
 * Created on 08-Feb-2005
 */
package b01.foc;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import b01.foc.gui.FPanel;
import b01.foc.gui.FValidationPanel;
import b01.foc.menu.FMenuItem;
import b01.foc.menu.FMenuList;

/**
 * @author Administrator
 */
public class FocHelp {
	private LinkedHashMap<String, String> helpFilesUrlMap = null;
  
  public FocHelp(){
    
  }
  
  public LinkedHashMap<String, String> getHelpFilesUrlMap(){
  	if(helpFilesUrlMap == null){
  		helpFilesUrlMap = new LinkedHashMap<String, String>();
  	}
  	return helpFilesUrlMap;
  }
  
  public void putHelpFileUrl(String menuText, String helpFileUrl){
  	if(helpFileUrl != null && menuText != null){
  		LinkedHashMap<String, String> helpFilesUrlMap = getHelpFilesUrlMap();
  		helpFilesUrlMap.put(menuText, helpFileUrl);
  	}
  }
  
  private String getHelpFileUrlForMenu(String menuTitle){
  	LinkedHashMap<String, String> helpFilesUrlMap = getHelpFilesUrlMap();
  	return helpFilesUrlMap.get(menuTitle);
  }
  
  public Iterator<String> getHelpMenuesIterator(){
  	LinkedHashMap<String, String> helpFilesUrlMap = getHelpFilesUrlMap();
  	return helpFilesUrlMap.keySet().iterator();
  }
  
  public char getMnemonicForMenu(String menuTitle){
  	char mnemonic = ' ';
  	if(menuTitle != null){
  		boolean similarMnemoniFoundInPreviousMenus = true;
  		int i = -1;
  		while(similarMnemoniFoundInPreviousMenus && i < menuTitle.length()){
  			i++;
	  		mnemonic = menuTitle.charAt(i);
	  		Iterator<String> iter = getHelpMenuesIterator();
	  		boolean found = false;
	  		boolean finish = false;
	  		while(iter != null && iter.hasNext() && !found && !finish){
	  			String aMenuTitle = iter.next();
	  			if(aMenuTitle.equals(menuTitle)){
	  				finish = true;
	  			}
	  			if(!finish){
		  			char aMenuMnemonic = getMnemonicForMenu(aMenuTitle);
		  			found = aMenuMnemonic == mnemonic;
	  			}
	  		}
	  		similarMnemoniFoundInPreviousMenus = found;
  		}
  	}
  	return mnemonic;
  }
  
  public FocHelpActionListener getFocHelpApplicationListenerForMenu(String menuTitle){
  	String focHelpFileUrl = getHelpFileUrlForMenu(menuTitle);
  	return new FocHelpActionListener(focHelpFileUrl);
  	
  }
  
  public void fillHelpMenu(FMenuList helpMenu){
  	Iterator<String> iter = getHelpMenuesIterator();
    while(iter != null && iter.hasNext()){
    	String menuTitle = iter.next();
    	FMenuItem menuItem = new FMenuItem(menuTitle, ' ', getFocHelpApplicationListenerForMenu(menuTitle));
    	menuItem.setMnemonic(helpMenu.getMnemonicForMenu(menuItem));
    	helpMenu.addMenu(menuItem);
    }
  }
  
  private static void popupHtmlFile(String fileUrl){
    //String url = fileUrl;/*"file:///"++ConfigInfo.getAppDirectory()+*/
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    
    try{
        JEditorPane editorPane = new JEditorPane(cl.getResource(fileUrl));
        editorPane.setEditable(false);
        /*
        jFrame1.getContentPane().add(editorPane, BorderLayout.CENTER);
        jFrame1.setSize(200, 200);
        jFrame1.setVisible(true);
        */
        //Put the editor pane in a scroll pane.
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setPreferredSize(new Dimension(700, 440));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));
        
        FPanel fPanel = new FPanel();
        //GridBagConstraints cst = null;
        fPanel.add(editorScrollPane, 0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.BOTH);
        fPanel.setFill(FPanel.FILL_BOTH);
        //fPanel.setMainPanelSising(FPanel.MAIN_PANEL_FILL_BOTH);
        
        FValidationPanel validPanel = fPanel.showValidationPanel(true);
        validPanel.setValidationType(FValidationPanel.VALIDATION_OK);        
        
        Globals.getDisplayManager().popupDialog(fPanel, "Navigation help", true);          
    } catch(IOException e){
        e.printStackTrace();
    }
  }
  
  @SuppressWarnings("serial")
	public class FocHelpActionListener extends AbstractAction{
  	private String fileUrl = null;
  	
  	public FocHelpActionListener(String focHelpFileUrl){
  		this.fileUrl = focHelpFileUrl;
  	}

		public void actionPerformed(ActionEvent e) {
			FocHelp.popupHtmlFile(fileUrl);
		}
  }
  
  private static FocHelp focHelp = null;
  public static FocHelp getInstance(){
  	if(focHelp == null){
  		focHelp = new FocHelp();
  	}
  	return focHelp;
  }
}
