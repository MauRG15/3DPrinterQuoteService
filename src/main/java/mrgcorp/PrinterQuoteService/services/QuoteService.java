package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.QuoteResponseDTO;
import mrgcorp.PrinterQuoteService.annotations.ValidateIdQuote;
import mrgcorp.PrinterQuoteService.annotations.ValidateQuote;
import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;
import mrgcorp.PrinterQuoteService.exceptions.FileFormatException;
import mrgcorp.PrinterQuoteService.models.Quote;
import mrgcorp.PrinterQuoteService.repositories.QuoteRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class QuoteService {
    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository){
        this.quoteRepository=quoteRepository;
    }
    //Generar Quote
    @ValidateQuote
    public QuoteResponseDTO processQuote(QuoteRequestDTO quoteRequestDTO){
        try {
            /*Convertir el MultipartFile (tipo de archivo de Spring)
            es una interfaz de Spring que representa el archivo todavía sin leer —
            vive temporalmente mientras dura la petición HTTP,
            con metadatos (nombre original, tipo de contenido) además del contenido en sí.
             */
            byte[] archivo = quoteRequestDTO.stlFile().getBytes();
            //Crear objeto a almcacenar en la BD
            Quote quoteEntity = new Quote(
                    null,
                    LocalDateTime.now(),
                    archivo,
                    quoteRequestDTO.filamentId(),
                    quoteRequestDTO.layerProfile(),
                    quoteRequestDTO.infillPercentage(),
                    quoteRequestDTO.pieceQuantity(),
                    quoteRequestDTO.supportsNeeded(),
                    QuoteStatus.CREADO,
                    quoteRequestDTO.clientName(),
                    quoteRequestDTO.clientEmail());
            //Guardamos el objeto en la BD
            Quote quoteCreada = quoteRepository.save(quoteEntity);
            //Devolvemos el QuoteResponseDTO de la quote recien creada
            return quoteRepository.findQuote(quoteCreada.id());
        }catch (IOException e){
            throw new FileFormatException("No se pudo leer archivo STL");
        }
    }

    //Obtener Quote
    @ValidateIdQuote
    public QuoteResponseDTO getQuoteById(Long id){
        return quoteRepository.findQuote(id);
    }
}
