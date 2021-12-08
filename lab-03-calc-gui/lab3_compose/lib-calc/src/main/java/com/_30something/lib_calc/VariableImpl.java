package com._30something.lib_calc;

public class VariableImpl implements Variable {

    private final String name;

    public VariableImpl(String name) {
        this.name = name;
    }

    @Override
    public Object accept(ExpressionVisitor visitor) {
        return visitor.visitVariable(this);
    }

    @Override
    public String getName() {
        return name;
    }
}
