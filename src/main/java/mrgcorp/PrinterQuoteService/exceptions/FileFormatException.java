package mrgcorp.PrinterQuoteService.exceptions;

public class FileFormatException extends QuoteException{
    public FileFormatException(String mensaje) {
        super(mensaje);
    }
}
