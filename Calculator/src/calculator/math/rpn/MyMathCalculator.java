package calculator.math.rpn;

import calculator.software.CustonException;

public class MyMathCalculator {
    /**
     * @param num1 Float for the first number
     * @param num2 Float for the second number
     *
     * @return A float of the number calculated
     * @throws CustonException - Invalid Math Input
     *
     * */
    protected static float calculate(float num1, float num2, String op) throws CustonException {
        //feeds operator into switch statement to return value
        switch (op){
            case "*":
                return num1 * num2;

            case "/":
                return num1 / num2;

            case "+":
                return num1 + num2;

            case "-":
                return num1 - num2;

            case "^":
                return (float) Math.pow((double) num1, (double) num2);
        }
        //if there is no operator, the throw error
        throw new CustonException("Invalid Math Input");
    }
}
