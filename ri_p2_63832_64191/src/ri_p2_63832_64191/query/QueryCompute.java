/**
 * Aveiro University, Department of Electronics, Telecommunications and Informatics.
 * MIECT - Information Retrieval
 *  Miguel Vicente, 63832
 *  Vasco Santos, 64191
 */
package ri_p2_63832_64191.query;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data tyoe for storing the current results of a query.
 * @author vsantos
 */
public class QueryCompute {
    
    /**
     * Term frequence in the query.
     */
    private final LinkedHashMap<String, Integer> termFreq;
    
    /**
     * Attribute used for format float values.
     */
    private final DecimalFormat df;
    
    /**
     * Size of the Document collection.
     */    
    private final int collectionSize;
    
    /**
     * Value of the root used to compute normalization.
     */
    private double rootVal;
    
    /**
     * User query.
     */
    private Query query;
    
    /**
     * Query data type.
     * @param n number of documents of the collection.
     * @param query user query.
     */
    public QueryCompute(int n, Query query){
        df = new DecimalFormat("#.00000");
        termFreq = new LinkedHashMap<>();
        collectionSize = n;
        rootVal = 0.0;
        this.query = query;
    }
    
    /**
     * Add a new term to the the term frequence map.
     * @param term term to add.
     */
    public void addTerm (String term){
        termFreq.merge(term, 1, (a, b) -> a + b);
    }
    
    /**
     * Get a list of the terms of the query.
     * @return list of terms.
     */
    public List<String> getTerms(){
        return termFreq.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * Compute score of the query, by computing the terms weight and normalization.
     * @param posting of each term of the query.
     * @return score of each document where the terms of the query are.
     */
    public HashMap<Integer, String> computeScore(HashMap<String, HashMap<Integer, String>> posting){
                
        // Compute term Weight
        HashMap<String, Double> termWeight = new HashMap<>();
        termFreq.entrySet().parallelStream()
                .forEach((entry) ->{
                    double weight = (1 + Math.log10(entry.getValue())) * computeIDF(posting.get(entry.getKey()).size());
                    rootVal += Math.pow(weight, 2);
                    termWeight.put(entry.getKey(), weight);
                });
        
        // Normalize weight
        termWeight.replaceAll((k, v) -> normalization(v));
        rootVal = 0.0;
        
        // Compute Score
        HashMap<Integer, String> score = new HashMap<>();
        posting.entrySet().stream()
                .forEach((entry)->{
                    entry.getValue().entrySet().stream().forEach((e) ->{
                       score.merge(e.getKey(), df.format(
                               ((termWeight.get(entry.getKey()) * Double.valueOf(e.getValue())))),
                               (a, b) -> (df.format(Double.valueOf(a) + Double.valueOf(b))));
                    });
                });
        return score;
    }
    
    /**
     * Compute IDF of a term.
     * @param size size of the posting of the term.
     * @return IDF value.
     */
    private double computeIDF(int size) {
        return Double.valueOf(df.format(Math.log10((double)collectionSize / size)));
    }
    
    /**
     * Compute the normalization of the value.
     * @param value value to normalize.
     * @return normalization value.
     */
    private double normalization(double value) {
        return Double.valueOf(df.format(value / Math.sqrt(rootVal)));
    }
}