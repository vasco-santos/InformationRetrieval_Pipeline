/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ri_p2_63832_64191.corpusReader;

/**
 *
 * @author vicente
 */
public class Document {
    private String cid;
    private String sid;
    private String content;
    private String filename;
    private String speaker;
    private String date;
    private String language;
    
    
    
    public Document(String filename, String cid, String sid, String content) {
        this.filename = filename;
        this.cid = cid;
        this.sid = sid;
        this.content = content;
    }
    
    public Document(String filename, String cid, String sid, String content, int startPos, int endPos) {
        this.filename = filename;
        this.cid = cid;
        this.sid = sid;
        this.content = content;
      
    }

    public Document( String filename,String cid, String sid, String content, String speaker, String date, String language) {
        this.cid = cid;
        this.sid = sid;
        this.content = content;
        this.filename = filename;
        this.speaker = speaker;
        this.date = date;
        this.language = language;
      
    }
    
    public Document(String filename, String cid, String sid, String language, String speaker){
        this.cid = cid;
        this.sid = sid;
        this.filename = filename;
        this.speaker = speaker;
        this.date = date;
        this.language = language;
    }
    
    
    public String getDocumentName(){
        return this.filename+"_"+cid+"_"+sid+"_"+language+"_"+speaker;
    }

    public String getFilename() {
        return filename;
    }
    
    public String getDocName(){
        return this.filename+"_"+cid+"_"+sid;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
    
    
    public boolean valid(){
        return !this.cid.equals("") && !this.sid.equals("") && !this.content.equals("");
    }
    
}
