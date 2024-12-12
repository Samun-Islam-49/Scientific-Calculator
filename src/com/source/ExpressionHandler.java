package com.source;

import static com.source.LayoutContoller.BRACES;
import static com.source.LayoutContoller.OPERATORS;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

/**
 *
 * @author SAMUN
 */
// This File is responsible for Handling Mathematical Expressions
public class ExpressionHandler {

    private static ExpressionHandler instance;

    private final TreeMap<Character, Integer> pMap;
    private BigDecimal lastAns = null;

    private boolean neg;    //for determinig negative value
    private String expDB;

    // Private Constractor to ensure only one instance exists
    private ExpressionHandler() {

        // Map for calculating precedence of operators
        pMap = new TreeMap<>();
        pMap.put('+', 1);
        pMap.put('-', 1);
        pMap.put('*', 2);
        pMap.put('/', 2);
        pMap.put('%', 2);
        pMap.put('^', 3);
        pMap.put('!', 3);
        pMap.put('(', 0);
        pMap.put(')', 0);
    }

    // Using only one instance at a time
    public static ExpressionHandler getInstance() {
        if (instance == null) {
            instance = new ExpressionHandler();
        }

        return instance;
    }

    public void setLastAns(BigDecimal lastAns) {
        this.lastAns = lastAns;
    }

    public BigDecimal getLastAns() {
        return lastAns;
    }

    // For handling Complex Mathamatical Expression
    private void HandleComplexTerms(String mathTerm, List<String> infix) throws Exception {

//        System.out.println("Complex Math term -> " + mathTerm);

        // Storing Negative value
        if (neg) {
            mathTerm = "-" + mathTerm;
            neg = false;
        }

        // Iratatively calculating [] blocks
        while (mathTerm.contains("[")) {

            //Finding index of first brace
            int fb = mathTerm.indexOf('[');

            //Finding index of last brace
            int i = fb + 1;
            int x = 1;  //To find appropriet block regardless inner blocks
            while (mathTerm.charAt(i) != ']' || x != 1) {
                if (mathTerm.charAt(i) == '[') {
                    x++;
                } else if (mathTerm.charAt(i) == ']') {
                    x--;
                }
                i++;
            }

            int lb = i; // Last brace index

            // Separating left and right term from the main
            String fp = mathTerm.substring(0, fb);
            String lp = mathTerm.substring(lb + 1);

//            System.out.println("FP -> " + fp);
//            System.out.println("LP -> " + lp);
            // Calculating the inner expression
            String main = mathTerm.substring(fb + 1, lb);
//            System.out.println("MAIN -> " + main);
            BigDecimal res = calculate(main);

            // Checking the math function
            String func;
            if (fp.length() >= 3) {  // its a 3 char function
                func = fp.substring(fp.length() - 3);
                res = calc3CharFunc(func, res); // calculating function
            } else {    // its a 2 char function
                func = fp.substring(fp.length() - 2);
                res = calc2CharFunc(func, res); // calculating function
            }
//            System.out.println("RES -> " + res.toEngineeringString());

            //Separating the math function
            fp = fp.substring(0, fp.length() - func.length());

//            System.out.println("FP -> " + fp);
            // Checking if the first has an operator at last
            if (!fp.isEmpty() && !isOperator(fp.charAt(fp.length() - 1))) {
                // if not operator then insert (*) multiplication
                fp += "*";
            }

            mathTerm = fp + res.toPlainString() + lp;   // merging with parent math term
//            System.out.println("MATH-Term -> " + mathTerm);
        }

        // Handles if the expression has pie in it
        if (mathTerm.contains("π")) {
            while (mathTerm.contains("π")) {
                int ind = mathTerm.indexOf('π');

                String rep = String.valueOf(Math.PI);

                if (ind != 0 && !isOperator(mathTerm.charAt(ind - 1))) {
                    rep = "*" + rep;
                }

                if (ind != mathTerm.length() - 1 && !isOperator(mathTerm.charAt(ind + 1))) {
                    rep = rep + "*";
                }

                mathTerm = mathTerm.replaceFirst("π", rep);
            }
        }

        if (isContainingOperators(mathTerm)) {
            BigDecimal res = calculate(mathTerm);
            infix.add(res.toString());
        } else {
            infix.add(mathTerm);
        }
    }

    // Converting String Expresion to Infix List
    public List<String> getInfix(String expression) throws Exception {
        String exp = expression;

        // Resultant List
        List<String> infix = new ArrayList<>();

        String mathTerm;

        // Traversing String Expression
        for (int i = 0; i < exp.length(); i++) {

            //Reading Math Terms from Expression
            if (!isOperator(exp.charAt(i)) && !BRACES.contains("" + exp.charAt(i))) {

                int j = i;
                while (!isOperator(exp.charAt(j)) && !BRACES.contains("" + exp.charAt(j))) {

                    // Checking if there is any third braces blocks which indicates math functions
                    if (exp.charAt(j) == '[') {
                        j++;
                        int x = 1;  //To find appropriet block regardless inner blocks
                        while (exp.charAt(j) != ']' || x != 1) {
                            if (exp.charAt(j) == '[') {
                                x++;
                            } else if (exp.charAt(j) == ']') {
                                x--;
                            }
                            j++;
                        }
                    } else {
                        j++;
                    }

                    if (j == exp.length()) {
                        break;
                    }
                }

                mathTerm = exp.substring(i, j);

                i = j - 1;

                HandleComplexTerms(mathTerm, infix);

                // Usuall operator handling , merging and determinig negative value
            } else if (isOperator(exp.charAt(i)) && exp.charAt(i) != '!') {

                int j = i;

//                System.out.println("main op --> " + exp.charAt(j));
                if (j != 0 && exp.charAt(j - 1) != '(') {     //Checks if the operator is used at first or immidiately after (
                    char mainOP = exp.charAt(i);    // Main Opertor
                    infix.add(String.valueOf(mainOP));

                    j++;
                }

                int n = 1;

                // Traversing Operators and Merging + and -
                while (isOperator(exp.charAt(j)) && exp.charAt(j) != '!') {

//                    System.out.println(exp.charAt(j));
                    switch (exp.charAt(j)) {
                        case '+':
                            n *= 1;
                            break;
                        case '-':
                            n *= -1;
                            break;
                        default:
                            throw new ArithmeticException("Expression Error!");
                    }

                    j++;
                }

                i = j - 1;
                neg = (n == -1);

            } else {    //Checking automatic multiplication, example 10(5+6)(2^2)
                char x = exp.charAt(i);

                // Checking if its suitable to insert * before '('
                if (x == '(' && i != 0 && !isOperator(exp.charAt(i - 1)) && exp.charAt(i - 1) != '(' && !infix.get(infix.size() - 1).equals("*")) {
                    infix.add("*");
                }

                infix.add(x + "");

                // Checking if its suitable to insert * after ')'
                if (x == ')' && i != exp.length() - 1 && !isOperator(exp.charAt(i + 1)) && exp.charAt(i + 1) != ')') {
                    infix.add("*");
                }
            }

        }

        return infix;
    }

    // Converting Infix to Postfix List
    public List<String> getPostfix(List<String> infix) throws Exception {
        List<String> postfix = new ArrayList<>();

        Stack<Character> sym = new Stack<>();
        sym.push('(');
        infix.add(")");

        for (String s : infix) {

            if (s.length() == 1 && isOperator(s.charAt(0))) {
                while (pMap.get(sym.peek()) >= pMap.get(s.charAt(0))) {
                    postfix.add(String.valueOf(sym.peek()));
                    sym.pop();
                }
                sym.push(s.charAt(0));
            } else if (s.charAt(0) == '(') {
                sym.push(s.charAt(0));
            } else if (s.charAt(0) == ')') {
                while (sym.peek() != '(') {
                    postfix.add(String.valueOf(sym.peek()));
                    sym.pop();
                }
                sym.pop();
            } else {
                postfix.add(s);
            }

        }

        return postfix;
    }

    // This functions calculates the expression
    public BigDecimal calculate(String exp) throws Exception {
        List<String> infix = getInfix(exp);
//        System.out.println("Infix : ");
//        printList(infix);

        List<String> postfix = getPostfix(infix);
//        System.out.println("Postfix : ");
//        printList(postfix);

        Stack<BigDecimal> res = new Stack<>();

        // PostFix Calculation Algorithm
        for (String s : postfix) {
            if (s.length() == 1 && isOperator(s.charAt(0))) {
                BigDecimal x, y = BigDecimal.ZERO, z = BigDecimal.ZERO;
                x = res.pop();

                if (s.charAt(0) != '!') {
                    y = res.pop();
                }

                switch (s.charAt(0)) {
                    case '+':
                        z = y.add(x);
                        break;

                    case '-':
                        z = y.subtract(x);
                        break;

                    case '*':
                        z = y.multiply(x);
                        break;

                    case '/':
                        z = y.divide(x, MathContext.DECIMAL128);
                        break;

                    case '^':
                        z = y.pow(x.toBigIntegerExact().intValueExact());
                        break;

                    case '%':
                        z = new BigDecimal(y.toBigIntegerExact().mod(x.toBigIntegerExact()));
                        break;

                    case '!':
                        z = calcFactorial(x);
                        break;
                }

                res.push(z);
            } else {
                res.push(new BigDecimal(s));
            }
        }

        BigDecimal result = res.pop();

//        System.out.println(result);

        return result;
    }

    // Checks if a char is operator
    private boolean isOperator(char x) {
        return OPERATORS.contains(x + "");
    }

    // Checks if a expression has operators
    private boolean isContainingOperators(String exp) {

        for (int i = 0; i < exp.length(); i++) {
            char x = exp.charAt(i);

            if (isOperator(x)) {

                if (x == '-' && i == 0) {
                    continue;
                }

                return true;
            }
        }

        return false;
    }

    // Calculating 3 char math functions
    BigDecimal calc3CharFunc(String func, BigDecimal val) throws ArithmeticException {
//        System.out.println("VAL -> " + val.toEngineeringString());
        BigDecimal ret = BigDecimal.ZERO;

        if (func.equals("log")) {
            ret = BigDecimal.valueOf(Math.log10(val.doubleValue()));
            return ret;
        }

        val = val.multiply(BigDecimal.valueOf(Math.PI));
        val = val.divide(BigDecimal.valueOf(180), MathContext.DECIMAL128);

//        System.out.println("VAL -> " + val.toEngineeringString());
        switch (func) {
            case "sin":
                ret = BigDecimal.valueOf(Math.sin(val.doubleValue()));
                break;

            case "cos":
                ret = BigDecimal.valueOf(Math.cos(val.doubleValue()));
                break;

            case "tan":
                ret = BigDecimal.valueOf(Math.tan(val.doubleValue()));
                break;
        }

//        System.out.println("SIN -> " + ret.toPlainString());
        ret = ret.setScale(12, RoundingMode.HALF_EVEN).stripTrailingZeros();
//        System.out.println("SIN -> " + ret.toPlainString());
        
        if(func.equals("tan") && ret.compareTo(BigDecimal.valueOf(16331239353195370L)) == 0) {
            System.out.println(true);
            throw new ArithmeticException("Infinity");
        }
            
            

        return ret;
    }

    // Calculating 2 char math functions
    BigDecimal calc2CharFunc(String func, BigDecimal val) {
        BigDecimal ret = null;

        if (func.equals("ln")) {
            ret = BigDecimal.valueOf(Math.log(val.doubleValue()));
            return ret;
        }

        return ret;
    }

    /**
     * This function calculates factorial of given parameter.
     *
     * @param x the value which factorial needs to be calculated.
     * @return factorial result
     */
    private BigDecimal calcFactorial(BigDecimal x) {
        BigDecimal res = BigDecimal.ONE;
        BigDecimal i = BigDecimal.ONE;

        while (i.compareTo(x) != 1) {
            res = res.multiply(i);
            i = i.add(BigDecimal.ONE);
        }

        return res;
    }

    public String formatDecimal(BigDecimal decimal) {
        decimal = decimal.setScale(15, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros();
        String ret = decimal.toPlainString();

        if (ret.length() > 25) {
            DecimalFormat df = new DecimalFormat("##0.#################E0");
            ret = df.format(decimal);
        }

        return ret;
    }

    public String getFormatedResult(String exp) throws Exception {
        return formatDecimal(calculate(exp));
    }

    private void printList(List<String> lst) {
        System.out.print("List -> ");
        for (String s : lst) {
            System.out.print(s + " ");
        }

        System.out.println();
    }

    public String getExpresionDB() {
        return expDB;
    }

    public void setExpressionDB(String expDB) {
        this.expDB = expDB;
    }
}
