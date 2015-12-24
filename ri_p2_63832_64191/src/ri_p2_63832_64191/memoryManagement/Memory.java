/**
 * Aveiro University, Department of Electronics, Telecommunications and Informatics.
 * MIECT - Information Retrieval
 *  Miguel Vicente, 63832
 *  Vasco Santos, 64191
 */
package ri_p2_63832_64191.memoryManagement;

/**
 * Data Type which gives access to the memory currently being consumed.
 * @author vsantos
 */
public class Memory {
    
    /**
     * JVM Runtime state.
     */
    private Runtime runtime;
    
    /**
     * Simple class to get memory values.
     */
    public Memory()
    {
        runtime = Runtime.getRuntime();
    }
    
    /**
     * Get the amount of memory currently being consumed.
     * @return Memory in Mbs.
     */
    public long getCurrentMemory()
    {
        return (runtime.totalMemory() - runtime.freeMemory()) / 1000000;
    }
    
    /**
     * Print the amount of memory currently being consumed.
     */
    public void printMemory(){
        System.out.println((runtime.totalMemory() - runtime.freeMemory()) / 1000000);
    }
}
