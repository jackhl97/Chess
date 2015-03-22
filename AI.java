public interface AI {
	public int[] getMoves(Board board, boolean white, int inputDepth, boolean check);
	public int getDepth();
}