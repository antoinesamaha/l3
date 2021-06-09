package b01.foc.gui.tree;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

public abstract class FAbstractTreeModel implements TreeModel{
  private ArrayList<TreeModelListener> vector = new ArrayList<TreeModelListener>();

  public void addTreeModelListener( TreeModelListener listener ) {
     if ( listener != null && !vector.contains( listener ) ) {
        vector.add( listener );
     }
  }

  public void removeTreeModelListener( TreeModelListener listener ) {
     if ( listener != null ) {
        vector.remove( listener );
     }
  }

  public void fireTreeNodesChanged( TreeModelEvent e ) {
     Iterator listeners = (Iterator) vector.iterator();
     while ( listeners.hasNext()) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeNodesChanged( e );
     }
  }

  public void fireTreeNodesInserted( TreeModelEvent e ) {
     Iterator listeners = vector.iterator();
     while ( listeners.hasNext() ) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeNodesInserted( e );
     }
  }

  public void fireTreeNodesRemoved( TreeModelEvent e ) {
     Iterator listeners = vector.iterator();
     while ( listeners.hasNext() ) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeNodesRemoved( e );
     }
  }

  public void fireTreeStructureChanged( TreeModelEvent e ) {
     Iterator listeners = vector.iterator();
     while ( listeners.hasNext() ) {
        TreeModelListener listener = (TreeModelListener)listeners.next();
        listener.treeStructureChanged( e );
     }
  }
}
