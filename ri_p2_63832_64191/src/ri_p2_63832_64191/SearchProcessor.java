/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval 
 * Miguel Vicente, 63832 Vasco Santos, 64191
 */
package ri_p2_63832_64191;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import ri_p2_63832_64191.documentsCollection.DocumentCollection;
import ri_p2_63832_64191.query.IndexerResults;
import ri_p2_63832_64191.query.Query;
import ri_p2_63832_64191.query.QueryCompute;
import ri_p2_63832_64191.stemmer.Stemmer;
import ri_p2_63832_64191.tokenizer.StopWords;
import ri_p2_63832_64191.tokenizer.Tokenizer;

/**
 * Pipeline Processor to process user queries.
 * @author vsantos,mvicente
 */
public class SearchProcessor {

    private final IndexerResults indexer;
    private final DocumentCollection docCollection;
    private Stemmer stm;
    private StopWords sw;
    private int ppID;

    /**
     * Constructor of the Pipeline query Processor (1).
     * @param indexerName Indexer results path.
     * @param collectionName Collection defined name for indexing.
     * @param maxMem Maximum amount of memory available.
     * @param ppID Pipeline processor identifier.
     */
    public SearchProcessor(String indexerName, String collectionName, int maxMem, int ppID) {
        this.docCollection = new DocumentCollection();
        this.docCollection.loadCollectionData(collectionName);
        this.indexer = new IndexerResults(docCollection);
        this.ppID = ppID;

        // Stop Words
        if (ppID == 1) {
            Path stopFile = Paths.get(System.getProperty("user.dir") + "/src/ri_p2_63832_64191/stopwords_en.txt");
            sw = new StopWords(stopFile);
        } // Stemmer
        else if (ppID == 2) {
            stm = new Stemmer();
        } // Stop Words and Stemmer
        else {
            Path stopFile = Paths.get(System.getProperty("user.dir") + "/src/ri_p2_63832_64191/stopwords_en.txt");
            sw = new StopWords(stopFile);
            stm = new Stemmer();
        }
    }

    /**
     * Constructor of the Pipeline query Processor (2).
     * @param indexerName Indexer results path.
     * @param collectionName Collection defined name for indexing.
     * @param maxMem Maximum amount of memory available.
     * @param ppID Pipeline processor identifier.
     * @param swPath Stop words file path.
     */
    public SearchProcessor(String indexerName, String collectionName, int maxMem, int ppID, String swPath) {
        this.docCollection = new DocumentCollection();
        this.docCollection.loadCollectionData(collectionName);
        this.indexer = new IndexerResults(this.docCollection);
        this.ppID = ppID;
        // Stop Words
        if (ppID == 1) {
            sw = new StopWords(Paths.get(swPath));
        } // Stop Words and Stemmer
        else {
            sw = new StopWords(Paths.get(swPath));
            stm = new Stemmer();
        }
    }

    /**
     * Method responsible for start the Pipeline processor.
     */
    public void start() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Type shutdown in order to stop the execution.");
            Query query = getQuery(br);
            while (!query.getContent().equals("shutdown")) {
                // Verify Query Type
                QueryCompute queryComp = new QueryCompute(docCollection.getSize(), query);
                // Set timer
                long start = System.currentTimeMillis(); 
                // Make Pre Processing
                String[] tokens = Tokenizer.tokenize(query.getContent());
                // Process query tokens
                for (String token : tokens) {
                    if (Tokenizer.validate(token)) {
                        if (ppID == 1 || ppID == 3) {
                            // verify if the token is a stop word
                            if (!sw.verify(token)) {
                                // stem token
                                if (ppID == 3) {
                                    token = stm.getStemmer(token);
                                }
                            }
                        } // stem token
                        else if (ppID == 2) {
                            token = stm.getStemmer(token);
                        }
                        // Add term to the query object
                        queryComp.addTerm(token);
                    }
                }
                // Get Top Results
                try{
                    System.out.println("Searching...");
                    docCollection.getTopResults(queryComp.computeScore(indexer.getPosting(queryComp.getTerms(), query)), 30);
                    long elapsedTime = System.currentTimeMillis() - start;
                    System.out.println("Spent time: " + elapsedTime + "ms\n\n\n");
                }catch(NullPointerException ex){
                    System.out.println("Not found");
                }
                query = getQuery(br);
            }
            br.close();
        } catch (IOException ex) {
        }
    }
    
    /**
     * Build user query.
     * @param br buffered reader to receive user inputs.
     * @return user query.
     */
    private Query getQuery(BufferedReader br){
        int qType = getQueryType(br);
        String content = getQueryContent(br);
        Query q = null;
        switch(qType){
            case 0:
            case 1:
                q = new Query(qType, content);
                break;
            case 2:
                q = new Query(qType, content, getQueryProximityValue(br));
                break;
            case 3:
                q = new Query(qType, content, getField(br), getFieldValue(br));
                break;
        }
        return q;
    }
    
    /**
     * Get Field chosen by user.
     * @param br buffered reader to receive user inputs.
     * @return selected field.
     */
    private String getField(BufferedReader br){
        String result = "";
        System.out.println("Field to search (0 - language; 1 - speakerName): ");
        try{
            String s = br.readLine();
            int n = Integer.parseInt(s);
            if(s.equals("shutdown")){
                System.exit(0);
            }
            if(n == 0){
                result = "language";
            }
            else if(n == 1){
                result = "speaker";
            }
            else{
                System.out.println("Wrong option!");
                System.exit(0);
            }
        } catch (IOException ex) { }
          catch (NumberFormatException e){
            System.out.println("Invalid Query Proximity Value, selected value of 1.");
        }
        return result;
    }
    
    /**
     * Get field value typed by user.
     * @param br buffered reader to receive user inputs.
     * @return field value.
     */
    private String getFieldValue(BufferedReader br){
        String result = "";
        System.out.println("Field value: ");
        try{
            String s = br.readLine();
            if(s.equals("shutdown")){
                System.exit(0);
            }
            result = s;
        } catch (IOException ex) { }
          catch (NumberFormatException e){
            System.out.println("Invalid Query Proximity Value, selected value of 1.");
        }
        return result;
    }
    
    /**
     * Get proximity value chose by user.
     * @param br buffered reader to receive user inputs.
     * @return proximity value.
     */
    private int getQueryProximityValue(BufferedReader br){
        int max = 1;
        System.out.println("Maximum proximity value: ");
        try{
            String s = br.readLine();
            if(s.equals("shutdown")){
                System.exit(0);
            }
            max = Integer.parseInt(s);
        } catch (IOException ex) { }
          catch (NumberFormatException e){
            System.out.println("Invalid Query Proximity Value, selected value of 1.");
        }
        return max;
    }

    /**
     * Get query type chose by user.
     * @param br buffered reader to receive user inputs.
     * @return query type.
     */
    private int getQueryType(BufferedReader br) {
        int qType = 0;
        System.out.println("Select query type (0 -> Simple, 1 -> Phrase, 2 -> Proximity, 3 -> Field):");
        try {
            String s = br.readLine();
            if(s.equals("shutdown")){
                System.exit(0);
            }
            qType = Integer.parseInt(s);
            if ((qType < 0) || (qType > 3)) {
                System.out.println("Invalid Query Type!");
                System.exit(0);
            }
        } catch (IOException ex) { }
          catch (NumberFormatException e){
            System.out.println("Invalid Query Type, selected Simple query.");
        }
        return qType;
    }
    
    /**
     * Get query content typed by user.
     * @param br buffered reader to receive user inputs.
     * @return query content.
     */
    private String getQueryContent(BufferedReader br){
        String query = "";
        try {
            System.out.println("Type your query: ");
            query = br.readLine().toLowerCase();
        } catch (IOException ex) {
        }
        return query;
    }
}
