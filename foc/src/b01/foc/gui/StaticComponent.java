/*
 * Created on Nov 28, 2005
 */
package b01.foc.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;

import b01.foc.Globals;
import java.awt.*;

/**
 * @author 01Barmaja
 */
public class StaticComponent {
  private static void setEnabled(Component comp, boolean b, boolean setBackgroundColor){
    if(b){
      comp.setFont(Globals.getDisplayManager().getDefaultFont());
      if(setBackgroundColor) comp.setBackground(Color.WHITE);
    }else{
      comp.setFont(Globals.getDisplayManager().getDefaultNotEditableFont());
      if(setBackgroundColor) comp.setBackground(new Color(235, 235, 235));
    }
  }

  public static void setEnabled(Component comp, boolean b){
    setEnabled(comp, b, true);
  }
  
  public static void setEnabledNoBackground(Component comp, boolean b){
    setEnabled(comp, b, false);
  }
  
  public static void cleanComponent(Component baseComponent){
    try{
      cleanComponent(baseComponent, 0);
    }
    catch ( Exception e ){
      Globals.logException(e);
    }
  }
  
  public static void setAllComponentsEnable(Component baseComponent, boolean enable, boolean recursive){
  	if(baseComponent != null){
  		if(baseComponent instanceof FValidationPanel || baseComponent instanceof JLabel){
  			
  		}else if(baseComponent instanceof JPanel || baseComponent instanceof JTabbedPane || baseComponent instanceof JSplitPane){
  			if(recursive){
	  			Container container = (Container) baseComponent;
		    	Component comps[] = container.getComponents();
		    	for(int i = 0; i < comps.length; i++){
		    		Component component = comps[i];
		    		if(component != null){
		    			setAllComponentsEnable(component, enable, recursive);
		    		}
		    	}
  			}
  		}else{
  			baseComponent.setEnabled(enable);
  			setEnabled(baseComponent, enable);
  		}
  	}
  }
  
  /*
  * The "depth" parameter was being used for text output debugging.
  * But isn't essential now.  I'll keep it anyways, as it avoids
  * calling the garbage collector every recursion.
  */
  protected static void cleanComponent(Component baseComponent, int depth){
    if (baseComponent == null){ // recursion terminating clause
      return ;
    }
 
    Container cont;
    Component[] childComponents;
    int numChildren;
 
    if(baseComponent instanceof FPanel){
      FPanel panel = (FPanel) baseComponent;
      panel.dispose();
    }else if(baseComponent instanceof FGButton){
      FGButton fComp = (FGButton) baseComponent;
      fComp.dispose();
    }else if(baseComponent instanceof FGToggleButton){
      FGToggleButton fComp = (FGToggleButton) baseComponent;
      fComp.dispose();
    }else if(baseComponent instanceof FGAbstractComboBox){
      FGAbstractComboBox fComp = (FGAbstractComboBox) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGCheckBox){
      FGCheckBox fComp = (FGCheckBox) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGFormattedTextField){
      FGFormattedTextField fComp = (FGFormattedTextField) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGLabel){
      FGLabel fComp = (FGLabel) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGTextField){
      FGTextField fComp = (FGTextField) baseComponent;
      fComp.dispose();
    }else if(baseComponent instanceof FGPasswordField){
      FGPasswordField fComp = (FGPasswordField) baseComponent;
      fComp.dispose();      
    }
    
    // clean up component containers
    if(baseComponent instanceof Container){
      // now clean up container instance variables
      if(baseComponent instanceof RootPaneContainer){ // Swing specialised container
        cont = (Container)baseComponent;
        numChildren = cont.getComponentCount();
        childComponents = cont.getComponents();
        for(int i = 0;i < numChildren;i++){
          // remove each component from the current container
          // each child component may be a container itself
          cleanComponent(childComponents[i], depth + 1);
          ((RootPaneContainer)cont).getContentPane().remove(childComponents[i]);
        }
        ((RootPaneContainer)cont).getContentPane().setLayout(null);
      }else{ // General Swing, and AWT, Containers
        cont = (Container)baseComponent;
        numChildren = cont.getComponentCount();
 
        childComponents = cont.getComponents();
        for(int i = 0;i < numChildren;i++){  //for(int i = 0;i < numChildren;i++)
          // remove each component from the current container
          // each child component may be a container itself
          cleanComponent(childComponents[i], depth + 1);
          cont.remove(childComponents[i]);
        }
        cont.setLayout(null);
      }      
    } // if component is also a container
  }
}
