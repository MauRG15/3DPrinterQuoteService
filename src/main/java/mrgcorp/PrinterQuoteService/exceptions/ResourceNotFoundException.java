package mrgcorp.PrinterQuoteService.exceptions;

public class ResourceNotFoundException extends QuoteException{
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
