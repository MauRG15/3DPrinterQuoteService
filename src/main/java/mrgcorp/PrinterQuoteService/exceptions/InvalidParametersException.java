package mrgcorp.PrinterQuoteService.exceptions;

public class InvalidParametersException extends QuoteException{
    public InvalidParametersException(String mensaje) {
        super(mensaje);
    }
}
