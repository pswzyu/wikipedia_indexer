package edu.buffalo.cse.ir.wikiindexer.indexer;

/*
 * pswzyu, 用来在indexwriter记录的时候记录一个term在id这个文章中出现的次数
 */
public class IdAndOccurance implements Comparable<Integer> {
	public int id;
	public int occ;
	public IdAndOccurance(int id, int occ)
	{
		this.id = id;
		this.occ = occ;
	}
	public int getId()
	{
		return id;
	}
	public int getOcc()
	{
		return occ;
	}
	@Override
	public int compareTo(Integer arg0) {
		return id-arg0;
	}
}
