package mrgcorp.PrinterQuoteService.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mrgcorp.PrinterQuoteService.enumerations.LayerHeightProfile;
import mrgcorp.PrinterQuoteService.exceptions.FileFormatException;
import mrgcorp.PrinterQuoteService.exceptions.InvalidParametersException;
import mrgcorp.PrinterQuoteService.exceptions.InvalidQuantityException;
import mrgcorp.PrinterQuoteService.exceptions.MissingParametersException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

//1. Con @NotNull se valida que no venga ni un solo dato vacio
public record QuoteRequestDTO(
        @NotNull(message = "El archivo STL es obligatorio") MultipartFile stlFile,
        @NotNull(message = "El ID del filamento es obligatorio") Long filamentId,
        @NotNull(message = "El Perfil de Altura de Capa es obligatorio") LayerHeightProfile layerProfile,
        @NotNull(message = "El porcentaje de relleno es obligatorio") Double infillPercentage,
        @NotNull(message = "La cantidad de piezas es obligatoria") Integer pieceQuantity,
        @NotNull(message = "Decidir si se necesitan soportes es obligatorio") Boolean supportsNeeded,
        @NotBlank(message = "Es necesario colocar su nombre para identificarlo en la cotizacion") String clientName,
        @NotBlank(message = "Es necesario un correo para comunicarnos con usted") String clientEmail) {
}
