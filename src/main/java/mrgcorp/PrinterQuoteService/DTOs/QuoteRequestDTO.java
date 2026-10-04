package mrgcorp.PrinterQuoteService.DTOs;

import jakarta.validation.constraints.NotNull;
import mrgcorp.PrinterQuoteService.enumerations.LayerHeightProfile;
import mrgcorp.PrinterQuoteService.exceptions.FileFormatException;
import mrgcorp.PrinterQuoteService.exceptions.InvalidParametersException;
import mrgcorp.PrinterQuoteService.exceptions.InvalidQuantityException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/*
    Casos de error a evaluar
    1 Parametros faltantes
    2 Parametros invalidos
    3 cantidad de piezas invalidas
    */
//1. Con @NotNull se valida que no venga ni un solo dato vacio
public record QuoteRequestDTO(
        @NotNull(message = "El archivo STL es obligatorio") MultipartFile stlFile,
        @NotNull(message = "El ID del filamento es obligatorio") Long filamentId,
        @NotNull(message = "El Perfil de Altura de Capa es obligatorio") LayerHeightProfile layerProfile,
        @NotNull(message = "El porcentaje de relleno es obligatorio") Double infillPercentage,
        @NotNull(message = "La cantidad de piezas es obligatoria") Integer pieceQuantity,
        @NotNull(message = "Decidir si se necesitan soportes es obligatorio") Boolean supportsNeeded,
        @NotNull(message = "Es necesario colocar su nombre para identificarlo en la cotizacion") String clientName,
        @NotNull(message = "Es necesario un correo para comunicarnos con usted") String clientEmail) {
    //Evaluar datos en constructorprimario/compacto
    public QuoteRequestDTO{
        //1.2 Verificar no venga vacio el archivo
        if(stlFile.isEmpty()) throw new InvalidParametersException("El archivo esta vacio");
        //1.2 Tipo de archivo incorrecto
        if(!stlValido(stlFile)) throw new InvalidParametersException("El archivo no es STL");
        //2.1 InfillPercentage fuera de limites
        if(infillPercentage>100 || infillPercentage<0) throw new InvalidParametersException("El porcentaje de relleno es Invalido(0 a 100)");
        //2.3 Verificar que el nombre del cliente este dentro del limite definido en la BD y tenga solo caracteres
        if(!clientName.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+"))throw new InvalidParametersException("Nombre no valido, contiene numeros o caracteres especiales");
        if(clientName.length()>100) throw new InvalidParametersException("El nombre tiene una longitud no valida (MAX: 100 caracteres)");
        //2.3 Verificar que el correo sea valido
        if(!clientEmail.contains("@")||!clientEmail.endsWith(".com"))throw new InvalidParametersException("Correo invalido");
        //3 Verificar la cantidad de piezas quepan en la cama
        //NOTA: VALOR/CONDICION TEMPORAL, se sustituira con la informacion que se obtenga del slicer
        if(pieceQuantity<1 || pieceQuantity>50)throw new InvalidQuantityException("Cantidad invalida de piezas");
    }

    private static boolean stlValido(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[5];
            int bytesLeidos = is.read(header);

            if (bytesLeidos < 5) return false;

            // 1. Validar si es STL ASCII (comienza con "solid")
            String inicio = new String(header, 0, 5, "UTF-8");
            if ("solid".equalsIgnoreCase(inicio)) {
                return true;
            }
            // 2. Validar si es STL Binario basándonos en su tamaño total
            // Un archivo STL binario válido debe tener al menos el encabezado (80 bytes) y el contador de triángulos (4 bytes)
            if (file.getSize() >= 84) {
                return true;
            }
        } catch (IOException e) {
            throw new FileFormatException("Error al leer el archivo");
        }
        return false;
    }
}
