package b01.foc.xtree;

import java.util.ArrayList;
import java.util.Iterator;

import b01.foc.desc.FocObject;

public class FocNode {
	private FocNode   fatherNode = null;
	private FocObject focObject  = null;
	
	private ArrayList<FocNode> children = null;
	
	public FocNode(FocNode fatherNode, FocObject focObject){
		this.fatherNode = fatherNode;
		this.focObject = focObject;
		children = null;
		if(fatherNode != null){
			fatherNode.addChild(this);
		}
	}
	
	public FocNode getFatherNode(){
		return fatherNode;
	}
	
	public FocObject getFocObject(){
		return focObject;
	}
	
	public void addChild(FocNode childNode){
		if(children == null) children = new ArrayList<FocNode>();
		children.add(childNode);
	}
	
	public void removeChild(FocNode childNode){
		if(children == null) children = new ArrayList<FocNode>();
		children.remove(childNode);
	}
	
	public void scan(TreeScanner scanner){
		if(scanner != null){
			boolean goInside = scanner.beforChildren(this);
			if(goInside && children != null){
				Iterator iter = children.iterator();
				while(iter != null && iter.hasNext()){
					FocNode child = (FocNode) iter.next();
					child.scan(scanner);
				}				
			}
			scanner.afterChildren(this);			
		}
	}
}
