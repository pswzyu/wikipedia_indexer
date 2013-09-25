/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

/**
 * @author nikhillo
 * THis class is responsible for assigning a partition to a given term.
 * The static methods imply that all instances of this class should 
 * behave exactly the same. Given a term, irrespective of what instance
 * is called, the same partition number should be assigned to it.
 */
public class Partitioner {
	/**
	 * Method to get the total number of partitions
	 * THis is a pure design choice on how many partitions you need
	 * and also how they are assigned.
	 * @return: Total number of partitions
	 */
	public static int getNumPartitions() {
		return 4;
	}
	
	/**
	 * Method to fetch the partition number for the given term.
	 * The partition numbers should be assigned from 0 to N-1
	 * where N is the total number of partitions.
	 * @param term: The term to be looked up
	 * @return The assigned partition number for the given term
	 */
	public static int getPartitionNumber (String term) {
		System.out.println("getPartitionNumber:"+term);
		char start = term.charAt(0);
		if ("taivjq".indexOf(start) != -1)
			return 0;
		if ("sowyuk".indexOf(start) != -1)
			return 1;
		if ("hbcegnx".indexOf(start) != -1)
			return 2;
		if ("mfpdrlz".indexOf(start) != -1)
			return 3;
		return -1;
	}
}
