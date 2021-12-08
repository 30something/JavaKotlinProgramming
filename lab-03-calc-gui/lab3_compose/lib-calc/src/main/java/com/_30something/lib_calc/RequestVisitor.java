package com._30something.lib_calc;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RequestVisitor implements ExpressionVisitor {

    Map<String, Double> map = new HashMap<>();
    Scanner in;

    public RequestVisitor(Scanner in) {
        this.in = in;
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) {
        expr.getLeft().accept(this);
        expr.getRight().accept(this);
        return null;
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return null;
    }

    @Override
    public Object visitParenthesis(ParenthesisExpression expr) {
        return expr.getExpr().accept(this);
    }

    @Override
    public Object visitVariable(Variable expr) {
        String varName = expr.getName();
        if (!map.containsKey(varName)) {
            boolean correctInput = false;
            while (!correctInput) {
                try {
                    System.out.printf("Enter value for '%s': ", varName);
                    map.put(varName, Double.parseDouble(in.nextLine()));
                    correctInput = true;
                } catch (Exception exc) {
                    System.out.println("Unable to convert input string to value");
                    System.out.println("Please input value again");
                }
            }
        }
        return null;
    }

    public Map<String, Double> getVariablesMap() {
        return map;
    }
}
