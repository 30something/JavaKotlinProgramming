package com._30something.lib_calc;

public interface BinaryExpression extends Expression {
    Expression getLeft();
    Expression getRight();
    BinOpKind getOperation();
}
