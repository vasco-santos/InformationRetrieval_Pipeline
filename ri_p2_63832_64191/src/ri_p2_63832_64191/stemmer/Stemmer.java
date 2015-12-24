/**
 * Aveiro University, Department of Electronics, Telecommunications and Informatics.
 * MIECT - Information Retrieval
 *  Miguel Vicente, 63832
 *  Vasco Santos, 64191
 */
package ri_p2_63832_64191.stemmer;

import org.tartarus.snowball.*;

/**
 * Data type for using the Stemmer Porter of Tartarus Snowball.
 * The JAR File of the implementation is included.
 * @author vsantos
 */
public class Stemmer {
    
    /**
     * Stemmer algorithm.
     */
    private final String algorithm;

    /**
     * Stemmer Constructor for using the English Stemmer.
     */
    public Stemmer() {
        algorithm = "englishStemmer";
    }
    
    /**
     * Get term Stemmer value.
     * @param token to try stemmer.
     * @return new term or the previous term.
     */
    public String getStemmer(String token)
    {
        Class stemClass;
        try {
            stemClass = Class.forName("ri_p2_63832_64191.stemmer." + algorithm);
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
            stemmer.setCurrent(token);
            if (stemmer.stem()){
                return stemmer.getCurrent();
            }
                
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {}
        return token;
    }
}
