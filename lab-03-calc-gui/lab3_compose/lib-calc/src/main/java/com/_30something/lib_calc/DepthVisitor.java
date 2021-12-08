package com._30something.lib_calc;

/**
 * Visitor class used to count the depth of expression tree.
 * <strong>In fact counts the distance from current vertex to the farthest leaf plus 1</strong>.
 * This distance for root matches with the depth of tree.
 *
 * @author 30something
 * @version 1.0
 */

public class DepthVisitor implements ExpressionVisitor {

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) {
        Integer leftRes = (Integer) expr.getLeft().accept(this);
        Integer rightRes = (Integer) expr.getRight().accept(this);
        return Math.max(leftRes, rightRes) + 1;
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return 1;
    }

    @Override
    public Object visitParenthesis(ParenthesisExpression expr) {
        return (Integer) expr.getExpr().accept(this) + 1;
    }

    @Override
    public Object visitVariable(Variable expr) {
        return 1;
    }
}
