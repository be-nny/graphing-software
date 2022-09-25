package calculator.math.rpn;

import calculator.gui.RenderPanel;
import calculator.software.CustonException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyMathCompiler {

    //the resolution is used in an inverse sense
    public static int resolution = RenderPanel.width/5;
    private String equation;
    private static Pattern OperatorPattern = Pattern.compile("[+|\\*|\\-|/|(|)|^]");
    private static Pattern DecimalPattern = Pattern.compile("(\\p{Digit}+[.]\\p{Digit}+)|(\\p{Digit}+)");
    private static Pattern FunctionPattern = Pattern.compile("arctan|arccos|arcsin|sin[h]?|cos[h]?|tan[h]?|cot|[co]?sec|ln|e|pi|x|mod|sqrt");

    /**
     * @param equation The string equation to be parsed
     * @throws CustonException
     **/
    public MyMathCompiler(String equation) throws CustonException {
        this.equation = equation;
    }

    public ArrayList<String> getRPN() throws CustonException {
        //returns the rpn
        return createRPN(createTokens(this.equation));
    }

    /**
     * @return int[] of the coordinates relative to the graph canvas
     *
     * @param start Starting coordinate on the graph
     * @param origin_offset Offset from the origin of the graph on screen
     * @param scale_factor How much to zoom in or out, scales the graph accordingly
     * @param rpn ArrayList of the RPN tokens
     *
     * @apiNote
     *     The smaller the scale-factor is the further out you will zoom.
     *     The start and ending coordinates are that of the graph, not the canvas, so (0,0) is the center of the graph.
     *
     * @throws CustonException Invalid Mathematical Input
     * */
    public synchronized int[][] getCoordinates(float start, int[] origin_offset, double scale_factor, ArrayList<String> rpn) throws CustonException {
        start = (float) (start * scale_factor);
        double end = (RenderPanel.width * scale_factor) + start;
        double difference = Math.floor(end - start);
        double increase = (resolution / (difference / Math.pow(scale_factor, 2)));

        ArrayList<Integer> xCoordinates = new ArrayList<Integer>();
        ArrayList<Integer> yCoordinates = new ArrayList<Integer>();

        int[] x_coords;
        int[] y_coords;

        //go through the visible scale range and use the x val and calculate the value
        for(double x = start; x < end; x += increase){
            try{
                int x_val = (int) ((x / scale_factor) + origin_offset[0]);
                int y = (int) ((-calculate(x, rpn) / scale_factor) + origin_offset[1]);

                if(y > RenderPanel.height){
                    y = RenderPanel.height;
                } else if (y < 0){
                    y = -4;
                }

                xCoordinates.add(x_val);
                yCoordinates.add(y);

            }catch (ArithmeticException | CustonException e){
                throw new CustonException("Invalid Mathematical Input");
            }
        }

        x_coords = xCoordinates.stream().mapToInt(i -> i).toArray();
        y_coords = yCoordinates.stream().mapToInt(i -> i).toArray();

        return new int[][]{x_coords, y_coords};
    }

    private float calculate(double x_value, ArrayList<String> rpn) throws CustonException {
        //setting up the queues and stacks
        Queue<String> equationQueue = new LinkedList<String>();
        Stack<String> calculateQueue = new Stack<String>();
        Stack<String> operatorStack = new Stack<String>();

        equationQueue.addAll(rpn);

        Matcher intMatcher;
        Matcher funcMatcher;
        Matcher opMatcher;

        //go through the tokens and apply all the operations to the inputted number with the RPN form
        while(equationQueue.size() > 0) {
            String token = equationQueue.poll();
            intMatcher = DecimalPattern.matcher(token);
            funcMatcher = FunctionPattern.matcher(token);
            opMatcher = OperatorPattern.matcher(token);

            //checking the token to check determine which order to apply the operators to the token
            if(funcMatcher.find() && !token.equals("x")){
                operatorStack.push(token);
            } if(intMatcher.find()){
                calculateQueue.add(token);
            } if (token.equals("x")) {
                calculateQueue.add(String.valueOf(x_value));
            } if(opMatcher.find() && token.length() == 1){
                operatorStack.push(token);
            } if(!operatorStack.empty() && operatorStack.peek().matches(funcMatcher.pattern().pattern()) && !calculateQueue.empty()){
                String func = operatorStack.pop();
                float num = Float.parseFloat(calculateQueue.pop());
                calculateQueue.push(String.valueOf(MyFunctionCalculator.calculate(func, num)));
            } if(calculateQueue.size() >= 2 && !operatorStack.empty() && !funcMatcher.find()) {
                float num2 = Float.valueOf(calculateQueue.pop());
                float num1 = Float.valueOf(calculateQueue.pop());
                String op = operatorStack.pop();
                calculateQueue.push(String.valueOf(MyMathCalculator.calculate(num1, num2, op)));
            }
        }

        try{
            //return the float number output
            return Float.parseFloat(calculateQueue.pop());

        } catch (NullPointerException | EmptyStackException e){
            //through an error if no number is calculated
            throw new CustonException("Missing Expression");
        }
    }

    /**
     * Creates the tokens to be parsed into the RPN creator
     * @param equation String of the inputted equation
     * */
    private ArrayList<String> createTokens(String equation) throws CustonException {
        equation.trim();
        equation.toLowerCase();

        Matcher matcher;
        String[] list = new String[equation.length()];

        //matching numbers including decimals
        matcher = DecimalPattern.matcher(equation);
        while (matcher.find()){
            try{
                //matching the special numbers such as e and pi
                if(matcher.group().equals("e")){
                    list[matcher.start()] = String.valueOf(Math.E);
                } else if(matcher.group().equals("pi")){
                    list[matcher.start()] = String.valueOf(Math.PI);
                } else{
                    if(list[matcher.start()].isEmpty()){
                        list[matcher.start()] = matcher.group();
                    }
                }
            }catch (NullPointerException n){
                list[matcher.start()] = matcher.group();
            }
        }

        //matching operators
        matcher = OperatorPattern.matcher(equation);
        while (matcher.find()){
            try{
                if(list[matcher.start()].isEmpty()){
                    list[matcher.start()] = matcher.group();
                }
            }catch (NullPointerException n){
                list[matcher.start()] = matcher.group();
            }
        }

        //matching functions
        matcher = FunctionPattern.matcher(equation);
        while (matcher.find()){
            try{
                if(list[matcher.start()].isEmpty()){
                    list[matcher.start()] = matcher.group();
                }
            }catch (NullPointerException n){
                list[matcher.start()] = matcher.group();
            }
        }

        ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(list));
        tokens.removeAll(Collections.singleton(null));

        //adding * to if there is a negative grad
        try{
            if(tokens.get(0).equals("-")){
                tokens.set(1, tokens.get(0) + tokens.get(1));
                tokens.add("*");
                tokens.remove(0);
                tokens.removeAll(Collections.singleton(null));
            }
            ArrayList<String> temp = new ArrayList<String>();
            int count = 0;
            for(String s: tokens){
                try{
                    String next = tokens.get(count + 1);
                    if(next.equals("x") && s.matches(DecimalPattern.pattern())){
                        temp.add(s);
                        temp.add("*");
                    } else{
                        temp.add(s);
                    }
                } catch (IndexOutOfBoundsException n){
                    temp.add(s);
                }
                count += 1;
            }
            tokens = temp;
            System.out.println(temp);
            return tokens;
        } catch (IndexOutOfBoundsException n){
            throw new CustonException("Invalid Mathematical Input");
        }
    }

    /**
     * Creates the RPN with the tokenized equation
     * @return ArrayList containing the new equation
     * @throws CustonException 'Bracket Error'
     * @apiNote doesnt throw any mathematical errors
     * */
    public synchronized ArrayList<String> createRPN(ArrayList<String> tokens) throws CustonException {
        //setting variables
        Stack<String> OperatorStack = new Stack<>();
        Queue<String> Output = new LinkedList<>();
        Stack<String> FuncStack = new Stack<>();

        //go through all the tokens, apply the logic acordingly
        for(String token: tokens) {
            Matcher intMatch = DecimalPattern.matcher(token);
            Matcher funcMatch = FunctionPattern.matcher(token);
            Matcher opMatch = OperatorPattern.matcher(token);

            //checking for numbers and / or the letter x
            if (intMatch.find() || token.equals("x")) {
                Output.add(token);
            } else if(token.equals("e")){
                Output.add(String.valueOf(Math.E));
            } else if(token.equals("pi")){
                Output.add(String.valueOf(Math.PI));
            } else if (funcMatch.find() && !token.equals("x")) {
                FuncStack.add(token);
                //matching brackets
            } else if (token.equals("(")) {
                OperatorStack.push(token);
            } else if (token.equals(")")) {
                try{
                    while (!OperatorStack.peek().equals("(")) {
                        Output.add(OperatorStack.pop());
                    }
                    OperatorStack.pop();
                    //adding the previous function to the output
                    if(!FuncStack.isEmpty()){
                        Output.add(FuncStack.pop());
                    }
                } catch (EmptyStackException e){
                    throw new CustonException("Bracket Error");
                }
            } else if (opMatch.find() && OperatorStack.empty() || OperatorStack.peek().equals("(")) {
                OperatorStack.push(token);

            } else if (new MyPrecedence(OperatorStack.peek(), token).getCurrentPrecedence().equals("higher") || new MyPrecedence(OperatorStack.peek(), token).getCurrentPrecedence().equals("same") && new MyPrecedence(OperatorStack.peek(), token).getAssociative().equals("right")) {
                OperatorStack.push(token);

            } else if (new MyPrecedence(OperatorStack.peek(), token).getCurrentPrecedence().equals("lower") || new MyPrecedence(OperatorStack.peek(), token).getCurrentPrecedence().equals("same") && new MyPrecedence(OperatorStack.peek(), token).getAssociative().equals("left")) {
                // if ( is found pop the vals
                try {
                    while (!OperatorStack.peek().equals("(") && new MyPrecedence(OperatorStack.peek(), token).getCurrentPrecedence().equals("lower") || new MyPrecedence(OperatorStack.peek(), token).getCurrentPrecedence().equals("same") && new MyPrecedence(OperatorStack.peek(), token).getAssociative().equals("left")) {
                        Output.add(OperatorStack.pop());
                    }
                    OperatorStack.push(token);

                } catch (EmptyStackException e) {
                    OperatorStack.push(token);
                }
            }
        }

        //pop any remaining operators
        while(!OperatorStack.empty()){
            Output.add(OperatorStack.pop());
        }

        //find any errors that filter through
        if(Output.containsAll(Collections.singleton("(")) || Output.containsAll(Collections.singleton(")"))){
            throw new CustonException("Bracket Error");
        } else {
            //return the list
            ArrayList<String> list = new ArrayList(Output);
            return list;
        }
    }
}