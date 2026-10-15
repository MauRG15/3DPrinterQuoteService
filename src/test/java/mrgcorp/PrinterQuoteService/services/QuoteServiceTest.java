package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.enumerations.LayerHeightProfile;
import mrgcorp.PrinterQuoteService.exceptions.FilamentUnavailableException;
import mrgcorp.PrinterQuoteService.exceptions.InvalidParametersException;
import mrgcorp.PrinterQuoteService.exceptions.InvalidQuantityException;
import mrgcorp.PrinterQuoteService.exceptions.ResourceNotFoundException;
import mrgcorp.PrinterQuoteService.models.Filament;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import mrgcorp.PrinterQuoteService.repositories.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceTest {
    @Mock
    private QuoteRepository quoteRepository;
    @Mock
    private FilamentRepository filamentRepository;
    @InjectMocks
    private QuoteService quoteService;

    //Creamos un MockFile para hacer pruebas
    private MultipartFile stlFile;
    @BeforeEach
    void prepararArchivo() throws IOException {
        byte[] contenido = getClass().getResourceAsStream("/benchy.stl").readAllBytes();
        stlFile = new MockMultipartFile("stlFile", "benchy.stl", "application/octet-stream", contenido);
    }
    @Test
    @DisplayName("Validar existencia de filamento en la BD")
    public void validarFilamento(){
        //Creamos una peticion con un filamento que haga fallar al metodo como se tenia previsto
        QuoteRequestDTO quoteRequest= new QuoteRequestDTO(
                stlFile,
                99L,
                LayerHeightProfile.FINE_12,
                20.0,
                1,
                false,
                "Mauricio Rojas",
                "maurg010115@gmail.com");
        //Establecemos el punto de evaluacion, el metodo a evaluar
        given(filamentRepository.findFilamentById(99L)).willReturn(Optional.empty());
        //Validamos que lance una excepcion al no encontrar el id del filamento
        assertThrows(ResourceNotFoundException.class,
                ()->quoteService.processQuote(quoteRequest));
    }
    @Test
    @DisplayName("Validar que seleccionar un filamento no disponible arroja excepcion")
    public void validarFilamentoDisponible(){
        //Creamos una peticion con un filamento que haga fallar al metodo como se tenia previsto
        QuoteRequestDTO quoteRequest= new QuoteRequestDTO(
                stlFile,
                3L,
                LayerHeightProfile.FINE_12,
                20.0,
                1,
                false,
                "Mauricio Rojas",
                "maurg010115@gmail.com");
        //Establecemos el punto de evaluacion, el metodo a evaluar
        //Creamos un filamento no disponible
        Filament filament = new Filament(3L,"PLA","ROJO", BigDecimal.valueOf(298),"GFSL95_00",false);
        given(filamentRepository.findFilamentById(3L)).willReturn(Optional.of(filament));
        //Validamos que lance una excepcion al no encontrar el id del filamento
        assertThrows(FilamentUnavailableException.class,
                ()->quoteService.processQuote(quoteRequest));
    }
    @Test
    @DisplayName("Validar una cantidad de piezas fuera de rango")
    public void validarCantidadPiezas(){
        //Creamos una peticion con un filamento que haga fallar al metodo como se tenia previsto
        QuoteRequestDTO quoteRequest= new QuoteRequestDTO(
                stlFile,
                1L,
                LayerHeightProfile.FINE_12,
                20.0,
                51,//Cantidad de piezas invalida
                false,
                "Mauricio Rojas",
                "maurg010115@gmail.com");
        Filament filamentoValido = filamentoValido();
        given(filamentRepository.findFilamentById(1L))
                .willReturn(Optional.of(filamentoValido));
        assertThrows(InvalidQuantityException.class,
                ()->quoteService.processQuote(quoteRequest));
    }
    @Test
    @DisplayName("Validar porcentaje de relleno valido")
    public void validarRelleno(){
        //Creamos una peticion con un filamento que haga fallar al metodo como se tenia previsto
        QuoteRequestDTO quoteRequest= new QuoteRequestDTO(
                stlFile,
                1L,
                LayerHeightProfile.FINE_12,
                101.0,//Relleno invalido
                1,
                false,
                "Mauricio Rojas",
                "maurg010115@gmail.com");
        Filament filamentoValido = filamentoValido();
        given(filamentRepository.findFilamentById(1L))
                .willReturn(Optional.of(filamentoValido));
        assertThrows(InvalidParametersException.class,
                ()->quoteService.processQuote(quoteRequest));
    }
    @Test
    @DisplayName("Validar correo valido")
    public void validarCorreo(){
        //Creamos una peticion con un filamento que haga fallar al metodo como se tenia previsto
        QuoteRequestDTO quoteRequest= new QuoteRequestDTO(
                stlFile,
                1L,
                LayerHeightProfile.FINE_12,
                20.0,
                1,
                false,
                "Mauricio Rojas",
                "maurg010115gmail.com");//Correo invalido
        Filament filamentoValido = filamentoValido();
        given(filamentRepository.findFilamentById(1L))
                .willReturn(Optional.of(filamentoValido));
        assertThrows(InvalidParametersException.class,
                ()->quoteService.processQuote(quoteRequest));
    }
    private Filament filamentoValido() {
        return new Filament(1L, "PLA", "Rojo", BigDecimal.valueOf(298), "GFSL95_00", true);
    }
}
