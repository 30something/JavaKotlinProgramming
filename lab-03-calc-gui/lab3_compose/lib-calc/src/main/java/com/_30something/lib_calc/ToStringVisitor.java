package com._30something.lib_calc;

public class ToStringVisitor implements ExpressionVisitor {

    public static final ToStringVisitor INSTANCE = new ToStringVisitor();

    private ToStringVisitor() {}

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) {
        String leftRes = (String) expr.getLeft().accept(this);
        String rightRes = (String) expr.getRight().accept(this);
        String operation;
        if (expr.getOperation() == BinOpKind.ADD) {
            operation = "+";
        } else if (expr.getOperation() == BinOpKind.SUBTRACT) {
            operation = "-";
        } else if (expr.getOperation() == BinOpKind.MULTIPLY) {
            operation = "*";
        } else {
            operation = "/";
        }
        return leftRes + " " + operation + " " + rightRes;
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return Double.toString(expr.getValue());
    }

    @Override
    public Object visitParenthesis(ParenthesisExpression expr) {
        return "(" + expr.getExpr().accept(this) + ")";
    }

    @Override
    public Object visitVariable(Variable expr) {
        return expr.getName();
    }
}
