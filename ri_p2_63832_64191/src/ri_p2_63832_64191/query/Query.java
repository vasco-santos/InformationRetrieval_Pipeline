/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval 
 * Miguel Vicente, 63832 Vasco Santos, 64191
 */
package ri_p2_63832_64191.query;

/**
 * Query object that represents the user query.
 * @author vsantos,mvicente
 */
public class Query {
    
    /**
     * Type of the query.
     */
    private int type;
    
    /**
     * Content of the query.
     */
    private String content;
    
    /**
     * Proximity search value.
     */
    private int proximity;
    
    /**
     * Field key of the query..
     */
    private String fieldKey;
    
    /**
     * Field value of the query.
     */
    private String fieldValue;
    
    /**
     * Constructor of the Query object (Type 1).
     * @param type query type.
     * @param content query content.
     */
    public Query(int type, String content){
        this.type = type;
        this.content = content;
        this.proximity = -1;
        this.fieldKey = "";
        this.fieldValue = "";
    }
    
    /**
     * Constructor of the Query object (Type 2).
     * @param type query type.
     * @param content query content.
     * @param proximity query proximity value.
     */
    public Query(int type, String content, int proximity){
        this.type = type;
        this.content = content;
        this.proximity = proximity;
        this.fieldKey = "";
        this.fieldValue = "";
    }
    
    /**
     * Constructor of the Query object (Type 3).
     * @param type query type.
     * @param content query content.
     * @param fieldKey query field key.
     * @param fieldValue query field value.
     */
    public Query(int type, String content, String fieldKey, String fieldValue){
        this.type = type;
        this.content = content;
        this.proximity = -1;
        this.fieldKey = fieldKey;
        this.fieldValue = fieldValue;
    }

    /**
     * Get query type.
     * @return type.
     */
    public int getType() {
        return type;
    }

    /**
     * Get query content.
     * @return content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get query proximity value.
     * @return proximity.
     */
    public int getProximity() {
        return proximity;
    }

    /**
     * Get query field key.
     * @return field key.
     */
    public String getFieldKey() {
        return fieldKey;
    }

    /**
     * Get query field value.
     * @return value.
     */
    public String getFieldValue() {
        return fieldValue;
    }
}