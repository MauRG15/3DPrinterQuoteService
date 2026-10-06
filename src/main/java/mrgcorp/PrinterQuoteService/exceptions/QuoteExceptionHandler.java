package mrgcorp.PrinterQuoteService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


//Clase que se encarga de capturar las excepciones lanzadas en mi app spring
@RestControllerAdvice
public class QuoteExceptionHandler{
    //1. Manejador para excepciones personalizadas
    @ExceptionHandler({
            FileFormatException.class,
            InvalidParametersException.class,
            InvalidQuantityException.class})
    public ResponseEntity<String> exceptionHandler(QuoteException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> exceptionResourceNotFoundHandler(ResourceNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    // 2. Manejador para las excepciones de Spring
    // (BindException, MethodArgumentNotValidException, MaxUploadSizeExceededException)
    // Manejar excepcion en caso de un parametro con tipo de dato incorrecto
    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleSpringBindException(BindException ex) {
        var error = ex.getFieldError();
        if (error == null) {
            return ResponseEntity.badRequest().body("Parámetros de entrada incorrectos");
        }
        String mensaje;
        // Si el error fue por tipo de dato
        if (error.isBindingFailure()) {
            mensaje = String.format("Valor inválido '%s' para el campo '%s'",
                    error.getRejectedValue(), error.getField());
        }
        // Si el error fue porque faltó un parámetro obligatorios (disparado por @NotNull / @NotBlank)
        else {
            mensaje = error.getDefaultMessage(); // Obtiene el mensaje de @NotNull
        }
        return ResponseEntity.badRequest().body(mensaje);
    }

    // Manejar excepcion en caso de que el archivo supere el limite 50MB
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleSpringMaxUploadSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(413)).body("El archivo supera el limite permitido (100 MB)");
    }

    //Manejar errores de fallo de conversion de tipos
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String nombreParametro = ex.getName();
        Object valorRecibido = ex.getValue();
        String tipoRequerido = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "número";

        String mensaje = String.format("El parámetro '%s' debe ser de tipo %s. Valor recibido: '%s'",
                nombreParametro, tipoRequerido, valorRecibido);

        return ResponseEntity.badRequest().body(mensaje);
    }
}
