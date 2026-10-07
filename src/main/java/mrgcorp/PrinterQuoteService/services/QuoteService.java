package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.QuoteResponseDTO;
import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;
import mrgcorp.PrinterQuoteService.exceptions.*;
import mrgcorp.PrinterQuoteService.models.Filament;
import mrgcorp.PrinterQuoteService.models.Quote;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import mrgcorp.PrinterQuoteService.repositories.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final FilamentRepository filamentRepository;

    public QuoteService(QuoteRepository quoteRepository, FilamentRepository filamentRepository){
        this.quoteRepository=quoteRepository;
        this.filamentRepository=filamentRepository;
    }
    //Generar Quote
    public QuoteResponseDTO processQuote(QuoteRequestDTO quoteRequestDTO){
        //Validamos Datos antes de crear la Quote
        validarStlFile(quoteRequestDTO.stlFile());
        validarFilamento(quoteRequestDTO.filamentId());
        validarPieceQuantity(quoteRequestDTO.pieceQuantity());
        validarInfillPercentage(quoteRequestDTO.infillPercentage());
        validarClientName(quoteRequestDTO.clientName());
        validarClientEmail(quoteRequestDTO.clientEmail());
        try {
            /*Convertir el MultipartFile (tipo de archivo de Spring) es una interfaz de Spring
            que representa el archivo todavía sin leer — vive temporalmente mientras dura la petición HTTP,
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
            //Mandamos la Quote creada en formato de un QuoteResponse
            return new QuoteResponseDTO(quoteCreada.getId(),quoteCreada.getStatus(),quoteCreada.getCreatedAt(),quoteCreada.getTimeSeconds(),quoteCreada.getWeightGrams(),quoteCreada.getTotalPrice());
        }catch (IOException e){
            throw new FileFormatException("No se pudo leer archivo STL");
        }
    }

    //Obtener Quote
    public QuoteResponseDTO getQuoteById(Long id){
        var quote = quoteRepository.findQuote(id)
                .orElseThrow(()->new ResourceNotFoundException("Quote \""+id+"\" no encontrada, verifica que el ID de la Quote sea uno valido"));
        return new QuoteResponseDTO(quote.getId(),quote.getStatus(),quote.getCreatedAt(),quote.getTimeSeconds(),quote.getWeightGrams(),quote.getTotalPrice());
    }

    //Metodos de validacion
    private void validarFilamento(Long filamentId){
        //Verificar que el id del filamento exista en la BD
        Filament filament = filamentRepository.findFilamentById(filamentId)
                .orElseThrow(()->new ResourceNotFoundException("Filamento no encontrado"));
        //Verificar que el filamento este disponible
        if(!filament.available()) throw new FilamentUnavailableException("Filamento no disponible, elige otro");
    }

    private void validarStlFile(MultipartFile stlFile){
        //1.1 Evaluar si el archivo esta vacio
        if(stlFile.isEmpty()) throw new InvalidParametersException("El archivo STL esta vacio");
        //1.2 Evaluar el tipo de archivo
        String filename = stlFile.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".stl")) {
            throw new InvalidParametersException("El archivo no es STL");
        }
        //1.3 Evaluar el contenido del archivo
        if(!contenidoStlValido(stlFile)) throw new InvalidParametersException("El archivo tiene la extension STL pero su contenido no lo es");
    }

    private void validarInfillPercentage(Double infillPercentage){
        //2.1 InfillPercentage fuera de limites
        if (infillPercentage > 100 || infillPercentage < 0)
            throw new InvalidParametersException("El porcentaje de relleno es Invalido(0 a 100)");
    }

    private void validarClientName(String clientName){
        // 2.2 Se evalúa el formato SOLO si el cliente envió un texto con contenido
        if (!clientName.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new InvalidParametersException("Nombre no válido, contiene números o caracteres especiales");
        }
        if (clientName.length() > 100) {
            throw new InvalidParametersException("El nombre tiene una longitud no válida (MAX: 100 caracteres)");
        }
    }

    private void validarClientEmail(String clientEmail){
        //2.3 Verificar que el correo sea valido
        if (!clientEmail.contains("@") || !clientEmail.endsWith(".com"))
            throw new InvalidParametersException("Correo invalido");
    }

    private void validarPieceQuantity(Integer pieceQuantity){
        //NOTA: VALOR/CONDICION TEMPORAL, se sustituira con la informacion que se obtenga del slicer
        if (pieceQuantity < 1 || pieceQuantity > 50)
            throw new InvalidQuantityException("Cantidad invalida de piezas");
    }

    //Metodos static para metodos de validacion
    private static boolean contenidoStlValido(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] encabezado = new byte[84];
            int bytesLeidos = is.read(encabezado);

            if (bytesLeidos < 5) return false;

            // 1. STL ASCII: comienza con "solid"
            String inicio = new String(encabezado, 0, 5, StandardCharsets.UTF_8);
            if ("solid".equalsIgnoreCase(inicio)) return true;

            // 2. STL binario: 80 bytes de encabezado + 4 del contador + 50 por triangulo
            if (bytesLeidos < 84) return false;

            int numeroTriangulos = ByteBuffer.wrap(encabezado, 80, 4)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .getInt();

            if (numeroTriangulos <= 0) return false;

            return file.getSize() == 84L + (long) numeroTriangulos * 50L;

        } catch (IOException e) {
            throw new FileFormatException("Error al leer el archivo");
        }
    }
}
