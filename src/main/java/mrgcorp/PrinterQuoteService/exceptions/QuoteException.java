package mrgcorp.PrinterQuoteService.exceptions;

public abstract class QuoteException extends RuntimeException{
    public QuoteException(String mensaje){
        super(mensaje);
    }
}
