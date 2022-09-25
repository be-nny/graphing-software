package calculator.math.rpn;
import calculator.software.CustonException;
public class MyFunctionCalculator {
    /**
     * @param function A string function
     * @param number A float of the number
     *
     * @return A float of the number calculated
     * @throws CustonException - Function Input Error
     * */
    protected static float calculate(String function, float number) throws CustonException {
        //parameter is fed through the switch statement to get the function
        switch (function){
            case "ln":
                return (float) Math.log(number);
            case "sin":
                return (float) Math.sin(number);
            case "cos":
                return (float) Math.cos(number);
            case "tan":
                return (float) Math.tan(number);
            case "cosec":
                return (float) (1 / Math.sin(number));
            case "cot":
                return (float) (1 / Math.tan(number));
            case "sec":
                return (float) (1 / Math.cos(number));
            case "mod":
                return (float) Math.abs(number);
            case "sqrt":
                return (float) Math.sqrt(number);
            case "sinh":
                return (float) Math.sinh(number);
            case "cosh":
                return (float) Math.cosh(number);
            case "tanh":
                return (float) Math.tanh(number);
            case "arcsin":
                return (float) Math.asin(number);
            case "arccos":
                return (float) Math.acos(number);
            case "arctan":
                return (float) Math.atan(number);
        }

        //throws exception if the function isnt defined
        throw new CustonException("Function Input Error");
    }
}
