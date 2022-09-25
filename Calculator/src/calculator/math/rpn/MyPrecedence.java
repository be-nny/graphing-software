package calculator.math.rpn;

public class MyPrecedence {

    private String top;
    private String current;

    private int top_precedence;
    private int current_precedence;

    /**
     * @param currentOperator String of the current operator in the stack
     * @param topOperator String of the top operator in the stack
     * */
    protected MyPrecedence(String topOperator, String currentOperator){
        //setting the variables
        this.top = topOperator;
        this.current = currentOperator;

        this.top_precedence = getPrecedence(this.top);
        this.current_precedence = getPrecedence(this.current);
    }

    /**
     * Method for getting the precedence to the current operator.
     * @return returns 'less' , 'greater' or 'same' if regarding the current operator to the top operator.
     */
    protected String getCurrentPrecedence(){
        //logic for determining if the top operator has the higher precedence
        if(this.current_precedence < this.top_precedence){
            return "lower";
        } else if(this.current_precedence > this.top_precedence){
            return "higher";
        } else if(this.current_precedence == this.top_precedence){
            return "same";
        } else{
            return null;
        }
    }

    /**
     * Method for returning the associativity of regarding the current operator.
     * @return returns whether the current operator has a right or left associative.
     */
    protected String getAssociative(){
        //returns the associativity of the current operator
        if(this.current.equals("^")) {
            return "right";
        } else{
            return "left";
        }
    }

    private int getPrecedence(String op){
        //gets the precedence of the inputted operator
        switch (op){
            case "(":
            case ")":
                return 3;
            case "^":
                return 2;
            case "*":
            case "/":
                return 1;
            case "+":
            case "-":
                return 0;
        }
        return 0;
    }
}
