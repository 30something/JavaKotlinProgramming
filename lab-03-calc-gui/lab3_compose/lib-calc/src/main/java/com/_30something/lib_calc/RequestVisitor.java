package com._30something.lib_calc;

import java.util.HashSet;

public class RequestVisitor implements ExpressionVisitor {

    HashSet<String> literals = new HashSet<>();

    public RequestVisitor() {}

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
        literals.add(expr.getName());
        return null;
    }

    public HashSet<String> getVariablesSet() {
        return literals;
    }
}
