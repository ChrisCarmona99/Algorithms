package main.csp;

import java.time.LocalDate;

/**
 * UnaryDateConstraints are those in which one variable
 * is being compared by some operator, specified by an
 * int L_VAL (for the corresponding variable 
 * / meeting index) and LocalDate R_VAL, such as:
 * 0 == 2019-1-3
 *   OR
 * 3 <= 2019-11-9
 */
public class UnaryDateConstraint extends DateConstraint {

    public final LocalDate R_VAL;
    
    /**
     * Constructs a new UnaryDateConstraint of the format:
     *   lVal operator rVal
     * where lVal is an integer index, rVal is a LocalDate object,
     * and operator is any of the binary comparators:
     *   "==", "!=", "<", "<=", ">", ">="
     * @param lVal Integer index of a variable
     * @param operator String representation of a comparator
     * @param rVal LocalDate object compared to the lVal variable
     */
    public UnaryDateConstraint (int lVal, String operator, LocalDate rVal) {
        super(lVal, operator);
        R_VAL = rVal;
    }
    
    @Override
    public String toString () {
        return super.toString() + " " + R_VAL;
    }
    
    /**
     * The arity of a constraint determines the number of variables
     * found within
     * @return 1 for UnaryDateConstraints, 2 for Binary
     */
    @Override
    public int arity () {
        return 1;
    }
    
}
