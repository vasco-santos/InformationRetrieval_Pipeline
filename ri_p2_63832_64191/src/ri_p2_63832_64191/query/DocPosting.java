/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval 
 * Miguel Vicente, 63832 Vasco Santos, 64191
 */
package ri_p2_63832_64191.query;

import java.util.ArrayList;

/**
 * collect Document Posting of an indexed term.
 *
 * @author vsantos,mvicente
 */
public class DocPosting {
    
    /**
     * Score of the term in the document.
     */
    private double score;
    
    /**
     * Positions of the term in the file.
     */
    private ArrayList<Integer> positions;
    
    /**
     * Doc Posting information constructor.
     * @param score score of the term.
     * @param positions positions in the term.
     */
    public DocPosting(double score, ArrayList<Integer> positions){
        this.score = score;
        this.positions = positions;
    }

    /**
     * Get the score of the term in the file.
     * @return score.
     */
    public double getScore() {
        return score;
    }

    /**
     * Get the positions of the term in the file.
     * @return positions.
     */
    public ArrayList<Integer> getPositions() {
        return positions;
    }
}