package com._30something.lib_calc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class ParserImpl implements Parser {

    public enum CharTypes {
        NUMBER,
        VARIABLE,
        OPERATOR,
        DEFAULT,
    }

    public static class Token {
        public String string;
        public CharTypes type;

        public Token(String string, CharTypes type) {
            this.string = string;
            this.type = type;
        }
    }

    public CharTypes charType(char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) return CharTypes.VARIABLE;
        if ((c >= '0' && c <= '9') || c == '.') return CharTypes.NUMBER;
        if (c == '-' || c == '+' || c == '*' || c == '/' || c == '(' || c == ')') return CharTypes.OPERATOR;
        return CharTypes.DEFAULT;
    }

    public List<Token> tokenize(String input) {
        List<Token> tokensList = new ArrayList<>();
        Token tempToken;
        char[] chars = input.toCharArray();
        for (int currentPtr = 0; currentPtr < chars.length; ) {
            CharTypes tokenType = charType(chars[currentPtr]);
            if (tokenType == CharTypes.VARIABLE || tokenType == CharTypes.NUMBER) {
                StringBuilder tokenName = new StringBuilder();
                while (currentPtr < chars.length && charType(chars[currentPtr]) == tokenType) {
                    tokenName.append(chars[currentPtr]);
                    currentPtr++;
                }
                tempToken = new Token(tokenName.toString(), tokenType);
                tokensList.add(tempToken);
            } else {
                tempToken = new Token(Character.toString(chars[currentPtr]), tokenType);
                tokensList.add(tempToken);
                currentPtr++;
            }
        }
        return tokensList;
    }

    /**
     * Validates order of tokens in expression and constructs new tokens.
     * <strong>It translates variables like '-x' as '-1 * x'.
     * But lefts numbers like '-123' as '-123' and ignores unary plus.</strong>
     *
     * @param tokens - raw tokens
     * @return newTokens - verified tokens
     * @throws ExpressionParseException - parsing exception
     */
    public List<Token> verifyTokens(List<Token> tokens) throws ExpressionParseException {
        List<Token> newTokens = new ArrayList<>();
        Token pastToken = new Token("", CharTypes.DEFAULT);
        int leftBrackets = 0;
        boolean unaryMinus = false;
        for (Token token : tokens) {
            if (Objects.equals(token.string, " ")) {
                continue;
            }
            if (token.type == CharTypes.DEFAULT || Objects.equals(token.string, ".")) {
                throw new ExpressionParseException("Unexpected token found: '" + token.string + "'");
            }
            if (token.type == CharTypes.OPERATOR) {
                if (Objects.equals(token.string, "(")) {
                    leftBrackets++;
                    if (unaryMinus) {
                        newTokens.add(new Token("-1", CharTypes.NUMBER));
                        newTokens.add(new Token("*", CharTypes.OPERATOR));
                        unaryMinus = false;
                    } else if (Objects.equals(pastToken.string, ")") || pastToken.type == CharTypes.VARIABLE ||
                            pastToken.type == CharTypes.NUMBER) {
                        throw new ExpressionParseException(
                                "Wrong order of operators found (left bracket after right bracket or literal)");
                    }
                    newTokens.add(token);
                } else if (pastToken.type == CharTypes.DEFAULT) {
                    if (Objects.equals(token.string, "-")) {
                        unaryMinus = true;
                    } else if (!Objects.equals(token.string, "+")) {
                        throw new ExpressionParseException(
                                "Wrong order of operators found (operator in the start of expression)");
                    }
                } else if (Objects.equals(token.string, ")")) {
                    leftBrackets--;
                    if (leftBrackets < 0) {
                        throw new ExpressionParseException("Overflow with right brackets found");
                    }
                    if (pastToken.type == CharTypes.OPERATOR && !Objects.equals(pastToken.string, ")")) {
                        throw new ExpressionParseException(
                                "Wrong order of operators found (right bracket not after literal or right bracket)");
                    }
                    newTokens.add(token);
                } else {
                    if (pastToken.type == CharTypes.OPERATOR) {
                        if (!Objects.equals(pastToken.string, ")") && !Objects.equals(pastToken.string, "(")) {
                            throw new ExpressionParseException(
                                    "Wrong order of operators found (operator after operator)");
                        } else if (Objects.equals(pastToken.string, "(")) {
                            if (Objects.equals(token.string, "*") || Objects.equals(token.string, "/")) {
                                throw new ExpressionParseException(
                                        "Wrong order of operators found (wrong operator after left bracket)");
                            } else if (Objects.equals(token.string, "-")) {
                                unaryMinus = true;
                            }
                        } else {
                            newTokens.add(token);
                        }
                    } else {
                        newTokens.add(token);
                    }
                }
            } else {
                if (pastToken.type == CharTypes.NUMBER || pastToken.type == CharTypes.VARIABLE) {
                    throw new ExpressionParseException(
                            "Wrong order of operators found (literal after literal)");
                }
                if (Objects.equals(pastToken.string, ")")) {
                    throw new ExpressionParseException(
                            "Wrong order of operators found (literal after right bracket)");
                }
                if (token.type == CharTypes.NUMBER && token.string.chars().filter(c -> c == '.').count() > 1) {
                    throw new ExpressionParseException("Two dots in float number found: '" + token.string + "'");
                }
                if (unaryMinus) {
                    if (token.type == CharTypes.NUMBER) {
                        newTokens.add(new Token("-" + token.string, token.type));
                    } else {
                        newTokens.add(new Token("-1", CharTypes.NUMBER));
                        newTokens.add(new Token("*", CharTypes.OPERATOR));
                        newTokens.add(token);
                    }
                    unaryMinus = false;
                } else {
                    newTokens.add(token);
                }
            }
            pastToken = token;
        }
        if (pastToken.type != CharTypes.NUMBER && pastToken.type != CharTypes.VARIABLE &&
                !Objects.equals(pastToken.string, ")")) {
            throw new ExpressionParseException("Wrong order of operators found (operator in the end of expression)");
        }
        if (leftBrackets > 0) {
            throw new ExpressionParseException("Overflow with left brackets found");
        }
        if (newTokens.isEmpty()) {
            throw new ExpressionParseException("Expression is empty or insignificant");
        }
        return newTokens;
    }

    public List<Token> buildPolishNotation(List<Token> tokens) {
        Stack<Token> operators = new Stack<>();
        List<Token> newList = new ArrayList<>();
        for (Token token : tokens) {
            if (token.type == CharTypes.OPERATOR) {
                if (Objects.equals(token.string, "(")) {
                    operators.add(token);
                    newList.add(token);
                } else if (Objects.equals(token.string, ")")) {
                    while (!Objects.equals(operators.peek().string, "(")) {
                        newList.add(operators.peek());
                        operators.pop();
                    }
                    operators.pop();
                    newList.add(token);
                } else if (Objects.equals(token.string, "*") || Objects.equals(token.string, "/")) {
                    while (!operators.empty() && (Objects.equals(operators.peek().string, "*") ||
                            Objects.equals(operators.peek().string, "/"))) {
                        newList.add(operators.peek());
                        operators.pop();
                    }
                    operators.push(token);
                } else {
                    while (!operators.empty() && !Objects.equals(operators.peek().string, "(")) {
                        newList.add(operators.peek());
                        operators.pop();
                    }
                    operators.push(token);
                }
            } else {
                newList.add(token);
            }
        }
        while (!operators.empty()) {
            newList.add(operators.peek());
            operators.pop();
        }
        return newList;
    }

    public Expression buildExpression(List<Token> tokens) throws ExpressionParseException {
        Stack<Expression> expressions = new Stack<>();
        for (Token token : tokens) {
            if (token.type == CharTypes.OPERATOR) {
                if (Objects.equals(token.string, ")")) {
                    Expression lower_expr = expressions.peek();
                    expressions.pop();
                    expressions.push(new ParenthesisExpressionImpl(lower_expr));
                } else if (!Objects.equals(token.string, "(")) {
                    Expression right_expr = expressions.peek();
                    expressions.pop();
                    Expression left_expr = expressions.peek();
                    expressions.pop();
                    BinOpKind operation;
                    if (Objects.equals(token.string, "+")) operation = BinOpKind.ADD;
                    else if (Objects.equals(token.string, "-")) operation = BinOpKind.SUBTRACT;
                    else if (Objects.equals(token.string, "*")) operation = BinOpKind.MULTIPLY;
                    else operation = BinOpKind.DIVIDE;
                    expressions.push(new BinaryExpressionImpl(left_expr, right_expr, operation));
                }
            } else if (token.type == CharTypes.NUMBER) {
                expressions.push(new LiteralImpl(Double.parseDouble(token.string)));
            } else {
                expressions.push(new VariableImpl(token.string));
            }
        }
        if (expressions.size() > 1) {
            // In case if method 'verifiedTokens' didn't find any errors in expression
            throw new ExpressionParseException("Wrong order of operands found");
        }
        return expressions.peek();
    }

    @Override
    public Expression parseExpression(String input) throws ExpressionParseException {
        List<Token> rawTokens = tokenize(input);
        List<Token> verifiedTokens = verifyTokens(rawTokens);
        List<Token> polishNotationTokens = buildPolishNotation(verifiedTokens);
        return buildExpression(polishNotationTokens);
    }
}
