package com._30something.lib_calc;

public interface ExpressionVisitor {
    Object visitBinaryExpression(BinaryExpression expr);
    Object visitLiteral(Literal expr);
    Object visitParenthesis(ParenthesisExpression expr);
    Object visitVariable(Variable expr);
}
