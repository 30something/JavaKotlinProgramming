package com._30something.lib_calc;

public interface Expression {
    Object accept(ExpressionVisitor visitor);
}
