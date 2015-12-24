/**
 * Aveiro University, Department of Electronics, Telecommunications and Informatics.
 * MIECT - Information Retrieval
 *  Miguel Vicente, 63832
 *  Vasco Santos, 64191
 */
package ri_p2_63832_64191.tokenizer;

import java.util.regex.Pattern;

/**
 * Tokenizer Data Type that is used for tokenize the content of each document.
 * @author vsantos,mvicente
 */
public class Tokenizer {
    static Pattern tokenize = Pattern.compile("(\\b[a-z0-9]{2}\\b)|(\\W+)");
    static Pattern validate = Pattern.compile("(\\w+)");
    /**
     * Uses Regex to replace all words and numbers with length 2, non-alphanumerical characters with a whitespace
     * and splits the resulting string by whitespace.
     * @param s string to tokenize
     * @return String[] with tokens
     */
    public static String[] tokenize(String s) {
        
        return tokenize.matcher(s).replaceAll(" ").split("\\s");
    }

    /**
     * Verify if token is valid for indexing using regex
     * @param t token to validate
     * @return true or false
     */
    public static boolean validate(String t) {
        return t.length() > 2 && t.matches("(\\w+)");
    }
}
