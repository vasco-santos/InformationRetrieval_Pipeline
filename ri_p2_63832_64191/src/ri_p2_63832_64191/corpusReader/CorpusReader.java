/**
 * Aveiro University, Department of Electronics, Telecommunications and Informatics.
 * MIECT - Information Retrieval
 *  Miguel Vicente, 63832
 *  Vasco Santos, 64191
 */
package ri_p2_63832_64191.corpusReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Corpus Reader data type, which is responsible for reading the
 * files of the collection.
 * @author vsantos,mvicente
 */
public class CorpusReader {

    List<Document> docs;
    
    /**
     * List of files in a path
     */
    List<Path> files;
    int file, filepos, bufferpos;
    Pattern findChapter = Pattern.compile("<CHAPTER ID=[\"]?(?<cid>\\d+-?\\d*)[\"]?");
    Pattern findSpeaker = Pattern.compile("<SPEAKER ID=[\"]?(?<sid>\\d+-?\\d*)[\"]?");
    Pattern findSpeakerName = Pattern.compile("NAME=\"(?<nm>.+(\n.+)?)\"");
    Pattern findLanguage = Pattern.compile("LANGUAGE=\"(?<lan>\\w+)\"");
    Path currentFile;

    boolean hasNewDoc, newChapter, newFile;
    String lastChapter;

    /**
     * Corpus Reader class.
     * Reads all files in a directory and pre-processes them.
     * @param path location of the collection to process.
     */
    public CorpusReader(String path) {
        files = new ArrayList<>();
        try (Stream<Path> lines = Files.list(Paths.get(path))) {
            lines.forEach(s -> files.add(s));
            lines.close();
        } catch (IOException ex) {
        }

        file = 0;
        newChapter = true;
        hasNewDoc = true;
        currentFile = files.get(file);
    }

    /**
     * Get the next document of the collection.
     * Parses the next document of the currently open file, if the file reaches the
     * end, it opens a new one while there are files to read
     * @return document content.
     */
    public Document nextDocument() {

        //Get the name of the file currently parsing
        String filename = currentFile.getFileName().toString().replace(".txt", "");
        
        //Variable initialization
        String cid = "", sid = "", content = "", language="", name="";

        //If there are more documents
        if (hasNewDoc) {
            
            //and a new chapter
            if (newChapter) {
                //Stream the file starting on the last saved position
                try (Stream<String> lines = Files.lines(currentFile).skip(filepos)) {
                    //Find the chapter tag
                    String tmp = lines.map(s -> count(s)).filter(s -> s.contains("CHAPTER")).findFirst().get();
                    
                    //If we find one
                    Matcher m = findChapter.matcher(tmp);
                    if (m.find()) {
                        //Current Chapter ID stored 
                        cid = m.group("cid");
                        lastChapter = cid;
                    }
                    lines.close();
                } catch (IOException ex) {
                    Logger.getLogger(CorpusReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            //We're on the same chapter as the last document
            } else {
                cid = lastChapter;
            }

            //Stream the file starting on the last saved position
            try (Stream<String> lines = Files.lines(currentFile).skip(filepos)) {
                //Find speaker tag
                Optional<String> tmp = lines.map(s -> count(s)).filter(s -> s.contains("SPEAKER")).findFirst();
                
                //if we find it
                if (tmp.isPresent()) {
                    Matcher m = findSpeaker.matcher(tmp.get());
                    if (m.find()) {
                        //current speaker id saved
                        sid = m.group("sid");
                        
                        //Find the language of the speaker (EN is default)
                        Matcher l = findLanguage.matcher(tmp.get());
                        if(l.find()){
                            language = l.group("lan");
                        } else{
                            language = "EN";
                        }
                        
                        //Find the name of the speaker
                        Matcher n = findSpeakerName.matcher(tmp.get());
                        if(n.find()){
                            name = n.group("nm");
                        }else{
                            //There is case of ONE speaker name that the regex
                            //cant capture
                            name = "NotAvailable";
                        }
                    }
                }
                lines.close();
            } catch (IOException ex) {
                Logger.getLogger(CorpusReader.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Parse the document content
            //We're using a buffered reader for line control
            try (BufferedReader reader = Files.newBufferedReader(currentFile)) {
                //Start the reader on the last saved position
                reader.skip(bufferpos);
                String s;
                while (true) {
                    //Read next line
                    s = reader.readLine();
                    
                    //If there is one
                    if (s != null) {
                        
                        //If we're not reading a tag
                        if (!findSpeaker.matcher(s).find(0) && !findChapter.matcher(s).find(0)) {
                            
                            //Increment line counter
                            filepos++;
                            
                            //Increment character position
                            bufferpos += s.length() + 1;
                            
                            //Discard paragraph tags
                            if (!s.startsWith("<P>")) {
                                
                                //Valid content line
                                content += s.toLowerCase();
                            }
                        } else {
                            //If we read a Speaker tag
                            if (findSpeaker.matcher(s).find(0)) {
                                //no more content or new chapter
                                newChapter = false;
                                break;
                            } else {
                                //If we read a Chapter tag
                                if (findChapter.matcher(s).find(0)) {
                                    //no more content but a new chapter
                                    newChapter = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        //File reached the end
                        //We change file and start new Chapter
                        newChapter = true;
                        loadNextDoc(++file);
                        reader.close();
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CorpusReader.class.getName()).log(Level.SEVERE, null, ex);
            }            
            //Return the parsed document
            return new Document(filename,cid, sid, content, name, "",language);
        }
        return null;
    }

    public int getFilepos() {
        return filepos;
    }

    /**
     * Load a new file for parsing.
     * @param nextFile file position in collection index
     */
    private void loadNextDoc(int nextFile) {
        if (nextFile < files.size()) {
            this.currentFile = files.get(nextFile);
            bufferpos = 0;
            filepos = 0;
        } else {
            hasNewDoc = false;
        }
    }

     /**
     * Auxiliary function for file streaming.
     * Counts the number of characters of the streamed line and increments
     * line index
     * @param s current line
     * @return current line
     */
    private String count(String s) {
        filepos++;
        bufferpos += s.length() + 1;
        return s;
    }
}
