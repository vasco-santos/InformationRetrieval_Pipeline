/**
 * Aveiro University, Department of Electronics, Telecommunications and Informatics.
 * MIECT - Information Retrieval
 *  Miguel Vicente, 63832
 *  Vasco Santos, 64191
 */
package ri_p2_63832_64191.tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Data type used to verify if the term is a stop word.
 * If the term is a Stop word, it is ignored and not indexed.
 * @author vsantos
 */
public class StopWords {
    
    /**
     * List of stop Words.
     */
    List<String> words;
    
    /**
     * Constructor of the Stop Words data type.
     * @param path path to the stop words file.
     */
    public StopWords(Path path)
    {
        words = new ArrayList<>();
        // Get Files from directory
        try (Stream<String> lines = Files.lines(path)) {
            lines.filter(line -> line.length() > 2).forEach(s -> words.add(s));
        } catch (IOException ex) {}
    }
    
    /**
     * Verify if a token is a stop word.
     * @param token token to index.
     * @return good term for index.
     */
    public boolean verify(String token)
    {
        return words.contains(token);
    }
}
