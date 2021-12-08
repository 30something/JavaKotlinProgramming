package com._30something.lib_calc;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * All tests are kept in tests.txt in root directory. One test consists of 3 lines.
 * In order not to complicate the tests with the presence of variables, please don't use them.
 * Tests format (4 lines):
 * 'Entered expression'
 * 'Expected parsed to string expression'
 * 'Computed result'
 * 'Line with one space'
 */
public class Tests {
    public static void main(String[] args) {
        try {
            int testNumber = 0;
            int successfulTests = 0;
            FileReader input = new FileReader("lab-01-expr-calc/tests.txt");
            Scanner in = new Scanner(input);
            Parser parser = new ParserImpl();
            ComputeExpressionVisitor computeVisitor = new ComputeExpressionVisitor(null);
            ToStringVisitor toStringVisitor = ToStringVisitor.INSTANCE;
            while (in.hasNextLine()) {
                boolean successfulTest = true;
                testNumber++;
                String expr = in.nextLine();
                String expectedParsedExpr = in.nextLine();
                Double expectedResult = Double.parseDouble(in.nextLine());
                in.nextLine();
                Expression parsedExpr = parser.parseExpression(expr);
                String parsedToStringExpr = (String) parsedExpr.accept(toStringVisitor);
                if (!Objects.equals(expectedParsedExpr, parsedToStringExpr)) {
                    System.out.printf("Error found in parsing of expression in test #%d%n", testNumber);
                    System.out.printf("Expected parsed expression: %s%n", expectedParsedExpr);
                    System.out.printf("Received parsed expression: %s%n", parsedToStringExpr);
                    successfulTest = false;
                }
                Double result = (Double) parsedExpr.accept(computeVisitor);
                if (!expectedResult.equals(result)) {
                    System.out.printf("Error found in computing result of expression in test #%d%n", testNumber);
                    System.out.printf("Expected result: %s%n", expectedResult);
                    System.out.printf("Received result: %s%n", result);
                    successfulTest = false;
                }
                if (successfulTest) {
                    successfulTests++;
                }
            }
            System.out.printf("Successfully checked tests: %d/%d", successfulTests, testNumber);
            input.close();
        } catch (Exception exc) {
            System.out.println("Error occurred while reading/performing tests. Message:");
            System.out.println(exc.getMessage());
        }
    }
}
