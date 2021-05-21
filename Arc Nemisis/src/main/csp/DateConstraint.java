package main.csp;

import java.util.*;

/**
 * DateConstraint superclass: all date constraints will have
 * an L_VAL variable and some operation that compares it to
 * some other variable or date value.
 */
public abstract class DateConstraint {

    public final int L_VAL;
    public final String OP;
    
    private static final Set<String> LEGAL_OPS = new HashSet<>(
        Arrays.asList("==", "!=", "<", "<=", ">", ">=")
    );
    
    /**
     * DateConstraint superclass constructor employed by specific-arity
     * constructors. See subclass constructors.
     * @param lVal Integer index 
     * @param operator String representation of a comparator
     */
    public DateConstraint (int lVal, String operator) {
        if (!LEGAL_OPS.contains(operator)) {
            throw new IllegalArgumentException("Invalid constraint operator");
        }
        if (lVal < 0) {
            throw new IllegalArgumentException("Invalid variable index");
        }
        
        L_VAL = lVal;
        OP = operator;
    }
    
    /**
     * The arity of a constraint determines the number of variables
     * found within
     * @return 1 for UnaryDateConstraints, 2 for Binary
     */
    public abstract int arity ();
    
    @Override
    public String toString () {
        return L_VAL + " " + OP;
    }
    
}
