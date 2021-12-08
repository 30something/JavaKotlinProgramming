package com._30something.lib_calc;

public interface Parser {
    /**
     * Parses expression from the string.
     * If the string doesn't represent a valid expression, then throws ExpressionParseException.
     *
     * @param input the input string.
     * @return parsed expression tree.
     */
    Expression parseExpression(String input) throws ExpressionParseException;
}
