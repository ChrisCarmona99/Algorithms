package main.csp;

/**
 * BinaryDateConstraints are those in which two variables
 * are being compared by some operator, specified by an
 * int L_VAL and R_VAL for the corresponding variable / meeting
 * indexes, such as:
 * 0 == 1
 *   OR
 * 3 <= 5
 */
public class BinaryDateConstraint extends DateConstraint {

    public final int R_VAL;
    
    /**
     * Constructs a new BinaryDateConstraint of the format:
     *   lVal operator rVal
     * where lVal and rVal are integer variable indexes, and operator
     * is any of the binary comparators:
     *   "==", "!=", "<", "<=", ">", ">="
     * @param lVal Integer index of a variable
     * @param operator String representation of a comparator
     * @param rVal Integer index of a variable
     */
    public BinaryDateConstraint (int lVal, String operator, int rVal) {
        super(lVal, operator);
        if (rVal < 0 || lVal == rVal) {
            throw new IllegalArgumentException("Invalid variable index");
        }
        
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
        return 2;
    }
    
}
