package com.source.History;

/**
 *
 * @author SAMUN
 */
public class History {

    private long id;        // Unique Id for each history
    private String exp;     // Math expression
    private String ans;     // Answer of the expression

    public History() {
    }

    public History(String expression, String answer) {
        this.exp = expression;
        this.ans = answer;

        genarateID();
    }

    public History(long id, String expression, String answer) {
        this.id = id;
        this.exp = expression;
        this.ans = answer;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private void genarateID() {
        id = System.currentTimeMillis();
    }
}
