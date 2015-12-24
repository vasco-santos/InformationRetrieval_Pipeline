/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval Miguel Vicente, 63832 Vasco
 * Santos, 64191
 */
package ri_p2_63832_64191.indexer;

import java.util.regex.Pattern;

/**
 * Static data type responsible for methods for determine which file to operate.
 *
 * @author vsantos
 * @author mvicente
 */
public class Utils {
    
    private static Pattern group1 = Pattern.compile("^[a-d]\\w*");
    private static Pattern group2 = Pattern.compile("[e-l]\\w*");
    private static Pattern group3 = Pattern.compile("^[m-r]\\w*");
    private static Pattern group4 = Pattern.compile("^[s-z]\\w*");
    
    /**
     * Matches the term to a specfic Hashmap, according to the first letter.
     * @param term
     * @return Hashmap index
     */
    public static int findTermMap(String term) {

        if (group1.matcher(term).matches()) {
            return 0;
        } else if (group2.matcher(term).matches()) {
            return 1;
        } else if (group3.matcher(term).matches()) {
            return 2;
        } else if (group4.matcher(term).matches()) {
            return 3;
        } else {
            return 4;
        }
    }
    
    public static char findFile(String term){
        char c = term.charAt(0);
        if(Character.isDigit(c)){
            return '\\';
        }
        else return c;
    }
}
