/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval 
 * Miguel Vicente, 63832 Vasco Santos, 64191
 */
package ri_p2_63832_64191.query;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ri_p2_63832_64191.documentsCollection.DocumentCollection;
import static ri_p2_63832_64191.indexer.Utils.findFile;
import ri_p2_63832_64191.memoryManagement.Memory;

/**
 * Data type for searching and getting indexer results for querys.
 * @author vsantos
 */
public class IndexerResults {

    /**
     * Hashmap containing the term as a key and an Hashmap as value. The hashmap
     * consists on a set of documents where the term was found and the number of
     * times that it was found in each document.
     */
    private final Memory memory;

    /**
     * Document collection reference.
     */
    private final DocumentCollection dc;
    
    /**
     * Attribute used for format float values.
     */
    private final DecimalFormat df;

    private int counter;

    /**
     * Indexer constructor that creates an Inverted Index.
     */
    public IndexerResults(DocumentCollection dc) {

        this.memory = new Memory();
        this.dc = dc;
        df = new DecimalFormat("#.00000");
    }

    /**
     * Get posting of the valid documents where the terms were found.
     * @param terms terms list.
     * @param q query mande.
     * @return posting.
     */
    public HashMap<String, HashMap<Integer, String>> getPosting(List<String> terms, Query q) {

        HashMap<String, HashMap<Integer, String>> posting = new HashMap<>();
        switch (q.getType()) {
            case 0:
                posting = getNormalQueryPosting(terms);
                break;
            case 1:
                posting = getPhraseQueryPosting(terms);
                break;
            case 2:
                posting = getProximityQueryPosting(terms, q.getProximity());
                break;
            case 3:
                posting = getFieldQueryPosting(terms, q.getFieldKey(), q.getFieldValue());
                break;
            default:
                break;
        }
        return posting;
    }

    /**
     * Simple query method.
     * @param terms query terms.
     * @return posting.
     */
    private HashMap<String, HashMap<Integer, String>> getNormalQueryPosting(List<String> terms) {

        HashMap<String, HashMap<Integer, String>> posting = new HashMap<>();
        terms.forEach((term) -> {
            char l = findFile(term);
            HashMap<Integer, String> tmp = new HashMap<>();
            String line = getTermLine (l, term);
            if (!line.equals("")){
                String s1 = line.split (" - ")[1];
                String[] s2 = s1.split(", ");
                for (String s: s2){
                    String[] s3 = s.split("=");
                    tmp.put(Integer.parseInt(s3[0].trim()), s3[1].split("_")[0].trim());
                }
            }
            if (tmp != null) {
                posting.put(term, tmp);
            }
        });
        return posting;
    }

    /**
     * Phrase query method.
     * @param terms query terms.
     * @return posting.
     */
    private HashMap<String, HashMap<Integer, String>> getPhraseQueryPosting(List<String> terms) {

        HashMap<String, HashMap<Integer, String>> posting = new HashMap<>();
        Map<Integer, ArrayList<DocPosting>> ocurrences = documentsOccurrence(terms);
        
        System.out.println(ocurrences.size());
        if (!ocurrences.isEmpty()) {
            ArrayList<Integer> docIDs = getDocIDsByDistance(ocurrences, 1, false);
            System.out.println(docIDs.size());
            if (!docIDs.isEmpty()) {
                posting = getPostingByDistance(terms, docIDs, ocurrences);
            } else {
                System.out.println("No documents found");
            }
        }
        return posting;
    }

    /**
     * Proximity query method.
     * @param terms query terms.
     * @param maxDistance maximum distance to search.
     * @return posting.
     */
    private HashMap<String, HashMap<Integer, String>> getProximityQueryPosting(List<String> terms, int maxDistance) {

        HashMap<String, HashMap<Integer, String>> posting = new HashMap<>();
        Map<Integer, ArrayList<DocPosting>> ocurrences = documentsOccurrence(terms);

        if (!ocurrences.isEmpty()) {
            ArrayList<Integer> docIDs = getDocIDsByDistance(ocurrences, maxDistance, true);
            if (!docIDs.isEmpty()) {
                posting = getPostingByDistance(terms, docIDs, ocurrences);
            } else {
                System.out.println("No documents found");
            }
        }
        return posting;
    }

    /**
     * Field based query method.
     * @param terms query terms.
     * @param fieldKey field key.
     * @param fieldValue field value.
     * @return posting.
     */
    private HashMap<String, HashMap<Integer, String>> getFieldQueryPosting(List<String> terms, String fieldKey, String fieldValue) {

        HashMap<String, HashMap<Integer, String>> posting = new HashMap<>();
        Map<Integer, ArrayList<DocPosting>> ocurrences = documentsOccurrence(terms);

        if (!ocurrences.isEmpty()) {
            ArrayList<Integer> docIDs = getDocIDsByField(ocurrences, fieldKey, fieldValue);
            if (!docIDs.isEmpty()) {
                posting = getPostingByDistance(terms, docIDs, ocurrences);
            } else {
                System.out.println("No documents found");
            }
        }
        return posting;
    }

    /**
     * Get the documents where the searching terms were found.
     * @param terms query terms.
     * @return documents information.
     */
    private Map<Integer, ArrayList<DocPosting>> documentsOccurrence(List<String> terms) {
        Map<Integer, ArrayList<DocPosting>> results = new HashMap<>();

        counter = 0;
        terms.forEach((String term) -> {
            char l = findFile(term);
            String line = getTermLine (l, term);
            HashMap<Integer, ArrayList<Integer>> tmp = new HashMap<>();
            HashMap<Integer, Double> tmp2 = new HashMap<>();
            
            if(!line.equals("")){
                String s1 = line.split (" - ")[1];
                for (String s: s1.split(", ")){
                    ArrayList<Integer> termPositions = new ArrayList<>();
                    String[] s3 = s.split("=");
                    for(String pos: s3[1].split("_")[1].split(";")){
                        termPositions.add(Integer.parseInt(pos));
                    }
                    tmp.put(Integer.parseInt(s3[0]), termPositions);
                    tmp2.put(Integer.parseInt(s3[0]), Double.valueOf(s3[1].split("_")[0]));
                }
                // First term found
                if (counter == 0){
                    counter++;
                    tmp.entrySet().stream().forEach((e) ->{
                        ArrayList<DocPosting> termsPositions = new ArrayList<>();
                        DocPosting dp = new DocPosting(tmp2.get(e.getKey()), e.getValue());
                        termsPositions.add(dp);
                        results.put(e.getKey(), termsPositions);
                    });
                }
                // Update for new term.
                else{
                    counter++;
                    tmp.entrySet().stream().forEach((e) -> {
                        ArrayList<DocPosting> aux;
                        if ((aux = results.get(e.getKey())) != null) {
                            // Only add the document if the previous document was also in this document.
                            if (aux.size() == counter - 1) {
                                DocPosting dp = new DocPosting(tmp2.get(e.getKey()), e.getValue());
                                aux.add(dp);
                                results.put(e.getKey(), aux);
                            }
                        }
                    });
                }
            }
        });
        
        /**
         * Filter the results to guarantee if all the terms were found in all the documents.
         */
        Map<Integer, ArrayList<DocPosting>> occurrances;
        occurrances = results.entrySet().stream()
                .filter(e -> e.getValue().size() == counter)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return occurrances;
    }
    
    /**
     * Get the term line indexed.
     * @param f file identifier.
     * @param term term to find.
     * @return indexed line.
     */
    public String getTermLine(char f, String term) {
        HashMap<Integer, String> hm;
        String s = "";
        Path file = Paths.get("termRef_" + String.valueOf(f));

        try (Stream<String> lines = Files.lines(file)) {
            Optional<String> tmp = lines.filter(line -> line.startsWith(term)).findFirst();
            if(tmp.isPresent()){
                s = tmp.get();
            }
        } catch (IOException ex) {}
        return s;
    }

    /**
     * Get posting documents verifying if their distance is valid.
     * @param ocurrences term occurrences.
     * @param distance maximum distance to search.
     * @param proximity if true, proximity search else phrase search. 
     * @return 
     */
    private ArrayList<Integer> getDocIDsByDistance(Map<Integer, ArrayList<DocPosting>> ocurrences, int distance, boolean proximity) {

        ArrayList<Integer> docIDs = new ArrayList<>();
        ocurrences.entrySet().stream().forEach(e -> {
            ArrayList<DocPosting> terms = e.getValue();
            int counter = 0;
            // Verify terms distance.
            for (int i = 0; i < terms.size() - 1; i++) {
                ArrayList<Integer> term1 = terms.get(i).getPositions();
                ArrayList<Integer> term2 = terms.get(i + 1).getPositions();
                boolean found = false;
                for (Integer pos : term1) {
                    for (Integer pos2 : term2) {
                        if ((Math.abs(pos2 - pos) <= distance) && (proximity || (pos2 - pos > 0))) {
                            found = true;
                            break;
                        }
                        if(found){
                            break;
                        }
                    }
                }
                if (found == true) {
                    counter++;
                }
            }
            // Verify the maximum distance between all terms of the query.
            if (counter == terms.size() - 1) {
                docIDs.add(e.getKey());
            }
            counter = 0;
        });
        return docIDs;
    }

    /**
     * Get documents identifiers filtered by valid fields.
     * @param ocurrences term occurrences.
     * @param field field to search.
     * @param value field value to verify.
     * @return 
     */
    private ArrayList<Integer> getDocIDsByField(Map<Integer, ArrayList<DocPosting>> ocurrences, String field, String value) {

        ArrayList<Integer> docIDs = new ArrayList<>();
        ocurrences.entrySet().stream().forEach(e -> {
            switch (field) {
                case "speaker":
                    if (value.equals(dc.getSpeakerName(e.getKey()))) {
                        docIDs.add(e.getKey());
                    }
                    break;
                case "language":
                    if (value.equals(dc.getLanguage(e.getKey()))) {
                        docIDs.add(e.getKey());
                    }
                    break;
            }
        });
        return docIDs;
    }

    /**
     * Get valid documents filtering by maximum distance.
     * @param terms query terms.
     * @param docIDs
     * @param initPosting
     * @return 
     */
    private HashMap<String, HashMap<Integer, String>> getPostingByDistance(List<String> terms, ArrayList<Integer> docIDs, Map<Integer, ArrayList<DocPosting>> initPosting) {

        HashMap<String, HashMap<Integer, String>> posting = new HashMap<>();
        counter = 0;
        terms.forEach((term) ->{
            HashMap<Integer, String> tmp = new HashMap<>();
            initPosting.entrySet().forEach((entry)->{
                if (docIDs.contains(entry.getKey())) {
                    tmp.merge(entry.getKey(), String.valueOf(entry.getValue().get(counter).getScore()), (a, b) -> (df.format(Double.valueOf(a) + Double.valueOf(b))));
                }
            });
            if (tmp != null) {
                posting.put(term, tmp);
            }
            counter++;
        });
        return posting;
    }
}