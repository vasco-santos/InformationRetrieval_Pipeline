/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval 
 * Miguel Vicente, 63832 Vasco Santos, 64191
 */
package ri_p2_63832_64191;

import java.nio.file.Path;
import java.nio.file.Paths;

import ri_p2_63832_64191.corpusReader.Document;
import ri_p2_63832_64191.corpusReader.CorpusReader;
import ri_p2_63832_64191.documentsCollection.DocumentCollection;
import ri_p2_63832_64191.indexer.Indexer;
import ri_p2_63832_64191.memoryManagement.Memory;
import ri_p2_63832_64191.stemmer.Stemmer;
import ri_p2_63832_64191.tokenizer.StopWords;
import ri_p2_63832_64191.tokenizer.Tokenizer;

/**
 * Pipeline Processor to process the documents of the collection.
 *
 * @author vsantos,mvicente
 */
public class Processor {

    /**
     * Corpus Reader reference.
     */
    private final CorpusReader cr;
    
    /**
     * Indexer reference.
     */
    private final Indexer indexer;
    
    /**
     * Document collection reference.
     */
    private DocumentCollection docCollection;
    
    /**
     * Memory reference.
     */
    private final Memory memory;
    
    /**
     * Stemmer reference.
     */
    private Stemmer stm;
    
    /**
     * StopWords reference.
     */
    private StopWords sw;
    
    /**
     * Maximum available memory.
     */
    private final int maxMem;
    
    /**
     * Pipeline Processor identifier.
     */
    private int ppID;
    
    /**
     * Constructor of the Pipeline Processor.
     * @param dir path for the collection of documents.
     * @param maxMem max memory available.
     * @param ppID pipeline processor identification.
     */
    public Processor(String dir, int maxMem, int ppID) {
        //cr = new CorpusReader(dir);
        cr = new CorpusReader(dir);
        indexer = new Indexer();
        docCollection = new DocumentCollection();
        memory = new Memory();
        this.maxMem = maxMem;
        this.ppID = ppID;

        // Stop Words
        if (ppID == 1) {
            Path stopFile = Paths.get(System.getProperty("user.dir") + "/src/ri_p2_63832_64191/stopwords_en.txt");
            sw = new StopWords(stopFile);
        }
        // Stemmer
        else if(ppID == 2){
            stm = new Stemmer(); 
        }
        // Stop Words and Stemmer
        else{
            Path stopFile = Paths.get(System.getProperty("user.dir") + "/src/ri_p2_63832_64191/stopwords_en.txt");
            sw = new StopWords(stopFile);
            stm = new Stemmer(); 
        }
    }
    
    /**
     * Constructor of the Pipeline Processor.
     * @param dir path for the collection of documents.
     * @param maxMem max memory available.
     * @param ppID pipeline processor identification.
     * @param swPath path for the stop words file.
     */
    public Processor (String dir, int maxMem, int ppID, String swPath){
        cr = new CorpusReader(dir);
        indexer = new Indexer();
        docCollection = new DocumentCollection();
        memory = new Memory();
        this.maxMem = maxMem;
        this.ppID = ppID;
        // Stop Words
        if (ppID == 1) {
            sw = new StopWords(Paths.get(swPath));
        }
        else{
            sw = new StopWords(Paths.get(swPath));
            stm = new Stemmer(); 
        }
    }
    

    /**
     * Method responsible for start the Pipeline processor.
     */
    public void start() {
        int docId = 0;
        boolean newDoc = false;
        Document doc;
        int position;
        System.out.println("Processing...");   

        while ((doc = cr.nextDocument()) != null) {
            if (doc.valid()) {
                position=0;
                for (String token : Tokenizer.tokenize(doc.getContent())) {
                    if (Tokenizer.validate(token)) {
                        newDoc = true;
                        if(ppID == 0){
                            indexer.addTerm(token, docId,position++);
                        }
                        else if(ppID == 1 || ppID == 3){
                            //verify if it is in the sw file
                            if(!sw.verify(token)){
                                //stem
                                if(ppID == 3){
                                    token = stm.getStemmer(token);
                                }
                                indexer.addTerm(token, docId,position++);
                            }
                        }
                        else{
                            token = stm.getStemmer(token);
                            indexer.addTerm(token, docId,position++);
                        }
                    }
                    //check for memory usage and free indexer if necessary
                    if (memory.getCurrentMemory() >= (maxMem*0.85)) {
                        System.out.println(docId);
                        System.out.println("Memory usage is high! Saving Indexer current state...");
                        indexer.freeRefMaps();
                        System.gc();
                        System.out.println("Processing...");
                    }    
                }
                if (newDoc) {
                    docCollection.addDocument(doc);
                    indexer.computeTF(docId);
                    //System.out.println("DOC-ID: " + docId + "\t" + doc.getFilename());
                    docId++;
                    newDoc = false;
                }
            }
        }
        // Save State of the Indexer
        indexer.freeRefMaps();
        docCollection.saveCollectionData("doc");
        docCollection = new DocumentCollection();
        System.gc();
        indexer.joinRefMaps();
    }
}
