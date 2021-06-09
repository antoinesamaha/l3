package b01.foc.xtree;


public interface TreeScanner {
	public boolean beforChildren(FocNode node);//The user can say if he wishes to go inside true to go inside
	public void    afterChildren(FocNode node);
}
