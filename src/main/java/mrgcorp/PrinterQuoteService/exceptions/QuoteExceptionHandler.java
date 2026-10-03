package mrgcorp.PrinterQuoteService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//Clase que se encarga de capturar las excepciones lanzadas en mi app spring
@RestControllerAdvice
public class QuoteExceptionHandler{
    @ExceptionHandler(FileFormatException.class)
    public ResponseEntity<String> exceptionFileFormatHandler(FileFormatException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(InvalidParametersException.class)
    public ResponseEntity<String> exceptionInvalidParametersHandler(InvalidParametersException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<String> exceptionInvalidQuantityHandler(InvalidQuantityException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(MissingParametersException.class)
    public ResponseEntity<String> exceptionMissinParametersHandler(MissingParametersException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> exceptionResourceNotFoundHandler(ResourceNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    @ExceptionHandler(QuoteException.class)
    public ResponseEntity<String> exceptionQuoteHandler(QuoteException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
