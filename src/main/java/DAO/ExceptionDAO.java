package DAO;



public class ExceptionDAO  extends RuntimeException{
    
    private static long serialVersionUID = 1L;

    public ExceptionDAO(String message){
        super(message);
    }

    public ExceptionDAO (String message,Throwable cause){
        super(message,cause);
    }
    
}
