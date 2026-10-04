package mrgcorp.PrinterQuoteService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

//Clase que se encarga de capturar las excepciones lanzadas en mi app spring
@RestControllerAdvice
public class QuoteExceptionHandler{
    //1. Manejador para excepciones personalizadas
    @ExceptionHandler({
            FileFormatException.class,
            InvalidParametersException.class,
            InvalidQuantityException.class,
            MissingParametersException.class})
    public ResponseEntity<String> exceptionHandler(QuoteException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> exceptionResourceNotFoundHandler(ResourceNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 2. Manejador para las excepciones de Spring (BindException y MethodArgumentNotValidException)
    // Manejar excepcion en caso de un parametro faltante
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleSpringBindingExceptions(BindException ex) {
        // Extraemos los errores de los campos formateados limpiamente
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        // Traducimos el error de Spring instanciando tu excepción personalizada
        MissingParametersException customException =
                new MissingParametersException("Parámetros faltantes: " + errorMessage);

        // Devolvemos la respuesta usando el mensaje de tu excepción
        return ResponseEntity.badRequest().body(customException.getMessage());
    }
    // Manejar excepcion en caso de un parametro con tipo de dato incorrecto
    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleSpringBindException(BindException ex) {
        // Extraemos los errores de los campos formateados limpiamente
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        // Traducimos el error de Spring instanciando tu excepción personalizada
        InvalidParametersException customException =
                new InvalidParametersException("Parámetros faltantes: " + errorMessage);

        // Devolvemos la respuesta usando el mensaje de tu excepción
        return ResponseEntity.badRequest().body(customException.getMessage());
    }
}
