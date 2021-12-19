package com._30something.lib_calc;

import java.util.Map;

public class ComputeExpressionVisitor implements ExpressionVisitor {

    Map<String, Double> map;

    public ComputeExpressionVisitor(Map<String, Double> map) {
        this.map = map;
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) {
        BinOpKind operation = expr.getOperation();
        Double leftRes = (Double) expr.getLeft().accept(this);
        Double rightRes = (Double) expr.getRight().accept(this);
        switch (operation) {
            case ADD -> {
                return leftRes + rightRes;
            }
            case SUBTRACT -> {
                return leftRes - rightRes;
            }
            case MULTIPLY -> {
                return leftRes * rightRes;
            }
            case DIVIDE -> {
                if (rightRes == 0) throw new ArithmeticException("Division by zero found");
                return leftRes / rightRes;
            }
            case DEFAULT -> {
                return 0;
            }
        }
        return null;
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return expr.getValue();
    }

    @Override
    public Object visitParenthesis(ParenthesisExpression expr) {
        return expr.getExpr().accept(this);
    }

    @Override
    public Object visitVariable(Variable expr) {
        return map.get(expr.getName());
    }
}
