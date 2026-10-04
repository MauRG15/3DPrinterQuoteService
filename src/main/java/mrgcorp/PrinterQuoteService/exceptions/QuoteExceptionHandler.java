package mrgcorp.PrinterQuoteService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    // 2. Manejador para las excepciones de Spring
    // (BindException, MethodArgumentNotValidException, MaxUploadSizeExceededException)
    // Manejar excepcion en caso de un parametro faltante
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleSpringBindingExceptions(MissingServletRequestParameterException ex) {
        // Traducir el error de Spring instanciando mi excepción personalizada
        MissingParametersException customException =
                new MissingParametersException("Parámetros faltantes: " + ex.getMessage());

        // Devolvemos la respuesta usando el mensaje de mi excepción
        return ResponseEntity.badRequest().body(customException.getMessage());
    }
    // Manejar excepcion en caso de un parametro con tipo de dato incorrecto
    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleSpringBindException(BindException ex) {
        // Extraemos los errores de los campos formateados limpiamente
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        // Traducimos el error de Spring instanciando mi excepción personalizada
        InvalidParametersException customException =
                new InvalidParametersException("Parámetros faltantes: " + errorMessage);

        // Devolvemos la respuesta usando el mensaje de tu excepción
        return ResponseEntity.badRequest().body(customException.getMessage());
    }

    // Manejar excepcion en caso de que el archivo supere el limite 50MB
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleSpringMaxUploadSizeException(MaxUploadSizeExceededException ex) {
        // Traducimos el error de Spring instanciando excepción personalizada
        FileFormatException customException =
                new FileFormatException("El archivo supera el limite permitido (50 MB)");

        // Devolvemos la respuesta usando el mensaje de tu excepción
        return ResponseEntity.badRequest().body(customException.getMessage());
    }
}
