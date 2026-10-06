package mrgcorp.PrinterQuoteService.exceptions;

public class InvalidQuantityException extends QuoteException{
    public InvalidQuantityException(String mensaje) {
        super(mensaje);
    }
}
