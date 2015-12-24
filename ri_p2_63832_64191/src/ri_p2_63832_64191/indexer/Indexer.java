/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval 
 * Miguel Vicente, 63832 Vasco Santos, 64191
 */
package ri_p2_63832_64191.indexer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import static ri_p2_63832_64191.indexer.Utils.findTermMap;

/**
 * Indexer component of the Pipeline Processor.
 * @author vsantos,mvicente
 */
public class Indexer {

    /**
     * Hashmap containing the term as a key and an Hashmap as value. The hashmap
     * consists on a set of documents where the term was found and the number of
     * times that it was found in each document.
     */
    private final TermPosting[] termReferences;

    /**
     * Decimal format of the numbers.
     */
    private DecimalFormat df;

    /**
     * Alphabet letters.
     */
    private final String[] alphapet = {
        "\\", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
        "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

    /**
     * Value of the current value of the root for normalization.
     */
    private double rootVal;

    /**
     * Term frequency in the current document.
     */
    private HashMap<String, Integer> termFreqOfDoc;
    
    /**
     * Term positions in the current document.
     */
    private HashMap<String, String> termPosOfDoc;
    
    /**
     * Indexer constructor that creates an Inverted Index.
     */
    public Indexer() {
        rootVal = 0;
        df = new DecimalFormat("#.00000");
        //We split the indexers in five, 4 for groups of the alphabet and 1 for numbers
        termReferences = new TermPosting[5];
        for (int i = 0; i < 5; i++) {
            termReferences[i] = new TermPosting(i, 0);
        }
        
        termFreqOfDoc = new HashMap<>();
        termPosOfDoc = new HashMap<>();
    }

    /**
     * Add new term to the Indexer. If the term was already found, update the
     * Hashmaps, otherwise add it to the Hashmaps.
     * @param term new term.
     * @param docId document identification.
     */
    public void addTerm(String term, int docId, int position) {
        int mapIndex = findTermMap(term);

        termFreqOfDoc.merge(term, 1, (a, b) -> a + b);
        termPosOfDoc.merge(term, position + ";", (a, b) -> a + b);
    }

    /**
     * Compute TF of the values of the current document.
     * @param docId document identification.
     */
    public void computeTF(int docId) {
        Map<String, String> tmp;
        tmp = termFreqOfDoc.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> computeValue(e.getValue())));
        
        termFreqOfDoc = new HashMap<>();
        HashMap<String, String> tmp2 = new HashMap<>(tmp);
        
        tmp2.entrySet().forEach((entry) -> {
            termReferences[findTermMap(entry.getKey())].compute(entry.getKey(), (k, v) -> v == null ? getNewHM(docId, entry.getValue(), termPosOfDoc.get(entry.getKey())) : updateHM(docId, entry.getValue(), v, termPosOfDoc.get(entry.getKey())));
        });
        rootVal = 0;
        termPosOfDoc = new HashMap<>();
    }

    /**
     * Compute the updated value of the document.
     * @param value new value found.
     * @return updated value.
     */
    private String computeValue(double value) {
        double val = 1 + Math.log10(value);
        rootVal += Math.pow(val, 2);
        return df.format(val);
    }

    /**
     * Add a new hashmap to the TermReferences Index.
     * @param docId document identification.
     */
    private HashMap<Integer, String> getNewHM(int docId, String value, String pos) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(docId, normalization(Double.valueOf(value)) + "_" + pos);
        return map;
    }

    /**
     * Update an existing hashmap in the TermReferences Index with a new value.
     * @param docId document identification.
     * @param hm hashmap with update
     */
    private HashMap<Integer, String> updateHM(int docId, String value, HashMap<Integer, String> hm, String pos) {
        hm.put(docId, normalization(Double.valueOf(value)) + "_" + pos);
        return hm;
    }

    /**
     * Compute normalization in the current document.
     * @param value value computed.
     * @return normalization result.
     */
    private String normalization(double value) {
        return df.format(value / Math.sqrt(rootVal));
    }

    /**
     * Write the Reference Maps to a file to free memory space.
     */
    public void freeRefMaps() {
        int i = 0;
        for (TermPosting tf : termReferences) {
            if (!tf.isEmpty()) {
                tf.storeTermRefMap(tf.getSubId());
                termReferences[i] = new TermPosting(i, tf.getSubId() + 1);
                
            }
            i++;
        }

    }

    /**
     * Join Reference Maps written in files to current in memory and write them
     * all to a file.
     */
    public void joinRefMaps() {
        freeRefMaps();

        for (String letter : alphapet) {
            int numberOfFiles = termReferences[findTermMap(letter)].getSubId();
            TermPosting tr = new TermPosting(0, 0);
            for (int i = 0; i < numberOfFiles; i++) {
                TermPosting tri = new TermPosting(findTermMap(letter), i);
                tri.loadTermRefMap(letter, i);
                tr.mergeRefMap(tri);
                tri = null;
            }
            try {
                for (int j = 0; j < numberOfFiles; j++) {
                    Files.delete(FileSystems.getDefault().getPath("termRef_" + letter + "_" + findTermMap(letter) + j));
                }
            } catch (IOException ex) {
            }
            tr.storeFinalMap(letter);
        }
    }

}
