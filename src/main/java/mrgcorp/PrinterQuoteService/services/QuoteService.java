package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.QuoteResponseDTO;
import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;
import mrgcorp.PrinterQuoteService.exceptions.FileFormatException;
import mrgcorp.PrinterQuoteService.exceptions.ResourceNotFoundException;
import mrgcorp.PrinterQuoteService.models.Quote;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import mrgcorp.PrinterQuoteService.repositories.QuoteRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuoteService {
    private final FilamentRepository filamentRepository;
    private final QuoteRepository quoteRepository;

    public QuoteService(FilamentRepository filamentRepository,
                        QuoteRepository quoteRepository){
        this.filamentRepository=filamentRepository;
        this.quoteRepository=quoteRepository;
    }
    //Generar Quote
    public QuoteResponseDTO processQuote(QuoteRequestDTO quote){
        try {
            /*Convertir el MultipartFile (tipo de archivo de Spring)
            es una interfaz de Spring que representa el archivo todavía sin leer —
            vive temporalmente mientras dura la petición HTTP,
            con metadatos (nombre original, tipo de contenido) además del contenido en sí.
             */
            byte[] archivo = quote.stlFile().getBytes();
            //Verificar que el id del filamento exista en la BD
            List<Long> ids = filamentRepository.findFilamentById(quote.filamentId());
            if(ids.isEmpty()) throw new ResourceNotFoundException();
            //Crear objeto a almcacenar en la BD
            Quote quoteEntity = new Quote(
                    null,
                    LocalDateTime.now(),
                    archivo,
                    quote.filamentId(),
                    quote.layerProfile(),
                    quote.infillPercentage(),
                    quote.pieceQuantity(),
                    quote.supportsNeeded(),
                    QuoteStatus.CREADO,
                    quote.clientName(),
                    quote.clientEmail());
            //Guardamos el objeto en la BD
            Quote quoteCreada = quoteRepository.save(quoteEntity);
            //Devolvemos el QuoteResponseDTO de la quote recien creada
            return quoteRepository.findQuote(quoteCreada.id());
        }catch (IOException e){
            throw new FileFormatException();
        }
    }

    //Obtener Quote
    public QuoteResponseDTO getQuoteById(Long id){
        return quoteRepository.findQuote(id);
    }
}
