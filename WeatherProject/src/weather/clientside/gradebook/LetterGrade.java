/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package weather.clientside.gradebook;

/**
 * This class takes the score earned and total score possible and calculates
 * the letter grade earned. 
 * Letter grades are:
 *      A+ - 100%
 *      A  - 94% - 99%
 *      A- - 90% - 93%
 *      B+ - 88% - 89%
 *      B  - 84% - 87%
 *      B- - 80% - 83%
 *      C+ - 78% - 79%
 *      C  - 74% - 77%
 *      C- - 70% - 73%
 *      D+ - 68% - 69%
 *      D  - 64% - 67%
 *      D- - 60% - 63%
 *      F  - 00% - 59%
 * 
 * @author Nikita Maizet
 */
public class LetterGrade implements Comparable
{
    int pointsEarned;
    int pointsPossible;
    String grade;
    
    /**
     * Constructor sets fields using provided parameters then calls the
     * calculateLetterGrade method to determine and set letter grade.
     * 
     * @param scoreEarned
     * @param totalScorePossible 
     */
    LetterGrade(int pointsEarned, int pointsPossible){
        this.pointsEarned = pointsEarned;
        this.pointsPossible = pointsPossible;
        
        calculateLetterGrade();
    }
    
    /**
     * Calculates letter grade using score based on scale described in class
     * comments.
     */
    public void calculateLetterGrade()
    {
        
    }
    
    /**
     * Returns letter grade.
     * 
     * @return String 
     */
    public String getLetterGrade()
    {
        return null;
    }

    /**
     * Compares according to scale described in class comments.
     * 
     * @param o
     * @return The letter grade according to scale described in class comments.
     */
    @Override
    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
