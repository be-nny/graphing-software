package calculator.software;

public class CustonException extends Exception{
    //throws the message in the parameter
    public CustonException(String message){
        super(message);
    }
}
