package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.Estimate;
import mrgcorp.PrinterQuoteService.DTOs.QuoteRequestDTO;
import mrgcorp.PrinterQuoteService.DTOs.SliceResultResponseDTO;
import mrgcorp.PrinterQuoteService.enumerations.LayerHeightProfile;
import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;
import mrgcorp.PrinterQuoteService.exceptions.*;
import mrgcorp.PrinterQuoteService.models.Filament;
import mrgcorp.PrinterQuoteService.models.Quote;
import mrgcorp.PrinterQuoteService.proxies.OrcaSlicerClientProxy;
import mrgcorp.PrinterQuoteService.repositories.FilamentRepository;
import mrgcorp.PrinterQuoteService.repositories.QuoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuoteServiceTest {
    @Mock
    private QuoteRepository quoteRepository;
    @Mock
    private FilamentRepository filamentRepository;
    @Mock
    private OrcaSlicerClientProxy orcaSlicerClientProxy;
    @Mock
    private PricingService pricingService;
    @InjectMocks
    private QuoteService quoteService;

    //Creamos un MockFile para hacer pruebas
    private MultipartFile stlFile;
    private byte[] contenido;
    @BeforeEach
    void prepararArchivo() throws IOException {
        contenido = getClass().getResourceAsStream("/benchy.stl").readAllBytes();
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
    @Test
    @DisplayName("Validar Happy flow de processQuote")
    public void validarHappyFlow(){
        QuoteRequestDTO quoteRequest= new QuoteRequestDTO(
                stlFile,
                1L,
                LayerHeightProfile.STANDARD_20,
                40.0,
                1,
                false,
                "Mauricio Rojas",
                "maurg010115@gmail.com");
        Filament filamentoValido = filamentoValido();
        given(filamentRepository.findFilamentById(1L))
                .willReturn(Optional.of(filamentoValido));
        //Mock del precio y slicer
        SliceResultResponseDTO sliceResult = new SliceResultResponseDTO(new Estimate(2576.55, 13.608));

        given(orcaSlicerClientProxy.processSlicer(any(), any(), any(), anyInt(), anyBoolean(), anyDouble()))
                .willReturn(sliceResult);

        given(pricingService.calculatePrice(any(), any()))
                .willReturn(new BigDecimal("106.08"));
        //Verificar el save()
        Quote quoteValida = quoteValida();
        //Cualquier clase de Quote que se invoque con save regresara mi quote creada
        given(quoteRepository.save(any(Quote.class))).willReturn(quoteValida);
        //Act
        quoteService.processQuote(quoteRequest);
        //Assert
        ArgumentCaptor<Quote> captor = ArgumentCaptor.forClass(Quote.class);
        verify(quoteRepository, times(2)).save(captor.capture());

        Quote guardada = captor.getValue();
        assertEquals(QuoteStatus.COTIZADA, guardada.getStatus());
    }
    @Test
    @DisplayName("Validar una SlicerException")
    public void validarSlicerException(){
        //Arrange
        QuoteRequestDTO quoteRequest= new QuoteRequestDTO(
                stlFile,
                1L,
                LayerHeightProfile.STANDARD_20,
                40.0,
                1,
                false,
                "Mauricio Rojas",
                "maurg010115@gmail.com");
        Filament filamentoValido = filamentoValido();
        given(filamentRepository.findFilamentById(1L))
                .willReturn(Optional.of(filamentoValido));
        //Indicamos que el slicer fallo y arrojo excepcion
        given(orcaSlicerClientProxy.processSlicer(any(), any(), any(), anyInt(), anyBoolean(), anyDouble()))
                .willThrow(new ImportStlException("Slicer no disponible"));
        //Verificar el save()
        Quote quoteValida = quoteValida();
        //Cualquier clase de Quote que se invoque con save regresara mi quote creada
        given(quoteRepository.save(any(Quote.class))).willReturn(quoteValida);
        //Act
        quoteService.processQuote(quoteRequest);
        //Assert
        ArgumentCaptor<Quote> captor = ArgumentCaptor.forClass(Quote.class);
        verify(quoteRepository, times(2)).save(captor.capture());

        Quote guardada = captor.getValue();
        //Asegurar que no se ejecuto el calculo del precio cuando fallo el slicer
        assertNull(guardada.getTotalPrice());
        verify(pricingService, never()).calculatePrice(any(), any());
        //Asegurar que la quote se guardo pero con un status de revision manual
        assertEquals(QuoteStatus.REVISION_MANUAL, guardada.getStatus());
    }
    private Filament filamentoValido() {
        return new Filament(1L, "PLA", "Rojo", BigDecimal.valueOf(298), "GFSL95_00", true);
    }
    private Quote quoteValida(){
        return new Quote(1L, LocalDateTime.now(),contenido,1L,LayerHeightProfile.FINE_12,20,1,false, QuoteStatus.CREADO,"Mauricio Rojas","maurg010115@gmail.com");
    }
}
