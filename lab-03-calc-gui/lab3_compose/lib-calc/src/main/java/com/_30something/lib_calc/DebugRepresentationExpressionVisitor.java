package com._30something.lib_calc;

public class DebugRepresentationExpressionVisitor implements ExpressionVisitor {

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) {
        String leftRes = (String) expr.getLeft().accept(this);
        String rightRes = (String) expr.getRight().accept(this);
        String operationPrefix;
        if (expr.getOperation() == BinOpKind.ADD) {
            operationPrefix = "add";
        } else if (expr.getOperation() == BinOpKind.SUBTRACT) {
            operationPrefix = "sub";
        } else if (expr.getOperation() == BinOpKind.MULTIPLY) {
            operationPrefix = "mul";
        } else {
            operationPrefix = "div";
        }
        return operationPrefix + "(" + leftRes + ", " + rightRes + ")";
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return "'" + expr.getValue() + "'";
    }

    @Override
    public Object visitParenthesis(ParenthesisExpression expr) {
        return "paran-expr(" + expr.getExpr().accept(this) + ")";
    }

    @Override
    public Object visitVariable(Variable expr) {
        return "var[" + expr.getName() + "]";
    }
}
