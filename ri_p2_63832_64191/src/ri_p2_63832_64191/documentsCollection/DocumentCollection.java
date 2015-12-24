/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval Miguel Vicente, 63832 Vasco
 * Santos, 64191
 */
package ri_p2_63832_64191.documentsCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import ri_p2_63832_64191.corpusReader.Document;

/**
 * Data type responsible for storing the document mappings identification. The
 * used ID will be the position of the data structure.
 * @author vsantos
 * @author mvicente
 */
public class DocumentCollection {

    /**
     * Document real identification.
     */
    private final ArrayList<Document> docIdentification;

    /**
     * Document collection data type.
     */
    public DocumentCollection() {
        docIdentification = new ArrayList<>();
    }

    /**
     * Add new document identification to the collection data structure.
     * @param d document.
     */
    public void addDocument(Document d) {
        docIdentification.add(new Document(d.getFilename(), d.getCid(), d.getSid(), d.getLanguage(), d.getSpeaker()));
    }

    /**
     * Method for getting the current size of the collection.
     * @return collection current size.
     */
    public int getSize() {
        return docIdentification.size();
    }
    
    /**
     * Get language of the document.
     * @param index position of the file.
     * @return language.
     */
    public String getLanguage(int index){
        return docIdentification.get(index).getLanguage();
    }
    
    /**
     * Get the name of the speaker.
     * @param index position of the file.
     * @return speaker name.
     */
    public String getSpeakerName(int index){
        return docIdentification.get(index).getSpeaker();
    }

    /**
     * Get the query results sorted by its score.
     * @param score Map with the obtained scores of each document.
     * @param limit limit number of results pretended.
     */
    public void getTopResults(HashMap<Integer, String> score, int limit) {

        score.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                //.forEachOrdered(null);
                .forEachOrdered((entry) -> {
                    System.out.println("ID: " + entry.getKey() + "\t"
                            + "Name: " + docIdentification.get(entry.getKey()).getDocName()
                            + "\t\tScore: " + entry.getValue());
                });
    }

    /**
     * Method for saving the status of the collection.
     * @param name filename to store.
     */
    public void saveCollectionData(String name) {
        File oFile = new File(name + "_CollectionData");
        try {
            FileOutputStream fos = new FileOutputStream(oFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            docIdentification.forEach((item) -> {
                try {
                    osw.write(item.getDocumentName() + "\n");
                } catch (IOException ex) {
                }
            });
            osw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    /**
     * Method for loading the status of the collection to the data structure.
     * @param name filename to load.
     */
    public void loadCollectionData(String name) {

        File iFile = new File(name + "_CollectionData");
        try {
            FileInputStream fis = new FileInputStream(iFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            try {
                String line = reader.readLine();
                while (line != null) {
                    String[] documentData = line.split("_");
                    Document doc = new Document(documentData[0], documentData[1], documentData[2], "", documentData[4], "", documentData[3]);
                    docIdentification.add(doc);
                    line = reader.readLine();
                }
                fis.close();
            } catch (IOException ex) {
            }
        } catch (FileNotFoundException ex) {
        }
    }
}
