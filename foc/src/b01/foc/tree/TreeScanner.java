package b01.foc.tree;

public interface TreeScanner<N> {
	public boolean beforChildren(N node);//The user can say if he wishes to go inside true to go inside
	public void    afterChildren(N node);
}
