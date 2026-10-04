package mrgcorp.PrinterQuoteService.exceptions;

public class MissingParametersException extends QuoteException{
    public MissingParametersException(String mensaje) {
        super(mensaje);
    }
}
