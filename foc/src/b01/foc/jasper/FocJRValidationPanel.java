/*
 * Created on Feb 23, 2006
 */
package b01.foc.jasper;

import java.awt.event.ActionEvent;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.view.JasperViewer;
import b01.foc.Globals;
import b01.foc.gui.FGButton;
import b01.foc.gui.FPanel;

/**
 * @author 01Barmaja
 */
public class FocJRValidationPanel extends FPanel{
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3257568403735786546L;
  public static final int BUTTON_PREVIEW     = 1;
  public static final int BUTTON_SAVE_AS_PDF = 2;
  public static final int BUTTON_PRINT       = 3;
  public static final int BUTTON_PRINT_ALL   = 4;
  public static final int BUTTON_EXIT        = 5;
  
  
  private FocJRReportLauncher launcher = null;
  private int clickedButton = 0;
  
  public FocJRValidationPanel(FocJRReportLauncher launcher){
  	this(launcher, false);
  }

  @SuppressWarnings("serial")
	public FocJRValidationPanel(FocJRReportLauncher launcher, boolean showPrintAllButton){
    this.launcher = launcher;
    
    FGButton previewsButton = new FGButton("Previews");
    previewsButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        JasperPrint print = getLauncher().fillReport();
        if(print != null){
          JasperViewer viewer = new JasperViewer(print, false);
          viewer.setVisible(true);
          setClickedButton(BUTTON_PREVIEW);
        }
      }
    });
    
    
    FGButton toPDF = new FGButton("Save As PDF");
    toPDF.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {      	
      	try {
          JasperPrint print = getLauncher().fillReport();
          if(print != null){
            String Output = getSelectedFilePath();
            if (Output != null){
              JasperExportManager.exportReportToPdfFile(print, Output);
              setClickedButton(BUTTON_SAVE_AS_PDF);
            }
      		}
        }catch(Exception x){
        	Globals.logException(x);
        }
      }
    });
    
    FGButton printButton = new FGButton("Print");
    printButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
      	printReport();
      	setClickedButton(BUTTON_PRINT);
      }
    });
    
    FGButton printAllButton = null;
    if(showPrintAllButton){
	    printAllButton = new FGButton("Print All");
	    printAllButton.addActionListener(new AbstractAction(){
				public void actionPerformed(ActionEvent e) {
					printReport();
					setClickedButton(BUTTON_PRINT_ALL);
				}
	    });
    }

    
    FGButton exitButton = new FGButton("Exit");
    exitButton.addActionListener(new AbstractAction(){
      public void actionPerformed(ActionEvent e) {
        Globals.getDisplayManager().goBack();
        //setClickedButton(BUTTON_EXIT);
      }
    });

    int y = 0; 
    add(previewsButton, y++, 0);
    add(toPDF, y++, 0);
    add(printButton, y++, 0);
    if(showPrintAllButton){
    	add(printAllButton, y++, 0);
    }
    add(exitButton, y++, 0);
  }
  
  public void printReport(){
    try{
      JasperPrint print = getLauncher().fillReport();
      if(print != null){
        //JasperPrintManager.printReport(print, true);  
        //BElie
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
        exporter.exportReport();
        //EElie
      }
    }catch (Exception x){
      Globals.logException(x);
    }
  }

  public String getSelectedFilePath() {
  	String Outputpath = null;
  	JFileChooser fch = new JFileChooser("C:\\");
  	ExtensionFileFilter filter = new ExtensionFileFilter("pdf", "PDF File Format");
  	fch.addChoosableFileFilter(filter);
  	fch.setFileSelectionMode( JFileChooser.FILES_ONLY );
    int result = fch.showSaveDialog( this );
    if ( result == JFileChooser.CANCEL_OPTION ){
    	return null;	
    }else{
      try{
        Outputpath = fch.getSelectedFile().toString();
  		} catch( Exception e ){
        Globals.logException(e);
    		//JOptionPane.showMessageDialog(this, "Error while saving file!", "Error", JOptionPane.ERROR_MESSAGE );
  		}
    	return Outputpath + ".pdf";
    }
  }
  
  public FocJRReportLauncher getLauncher(){
    return launcher;
  }
  
  public int getClickedButton(){
  	return clickedButton;
  }
  
  private void setClickedButton(int clickedButton){
  	this.clickedButton = clickedButton;
  }
}