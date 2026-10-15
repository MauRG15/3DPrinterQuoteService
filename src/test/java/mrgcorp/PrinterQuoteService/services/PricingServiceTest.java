package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.Estimate;
import mrgcorp.PrinterQuoteService.DTOs.SliceResultResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PricingServiceTest {
    //Todos los tests lo usan sin modificar datos, puede ser CAMPO
    private PricingService pricingService = new PricingService(BigDecimal.valueOf(1.4),BigDecimal.valueOf(1.67));
    //1. Validar happy flow con valores conocidos
    @Test
    @DisplayName("Verificar que el precio se calcule correctamente")
    public void calculatePriceHappyFlow(){
        SliceResultResponseDTO sliceResult = new SliceResultResponseDTO(new Estimate(2576.55,13.608));
        BigDecimal precio = pricingService.calculatePrice(sliceResult,BigDecimal.valueOf(298));
        //assertEquals de JUnit se usa para comparar valores
        //Recibe 2 parametros, el esperado y el real, en este caso si el segundo argumento coincide devuelve 0
        assertEquals(0,precio.compareTo(BigDecimal.valueOf(106.08)));
    }

    @Test
    @DisplayName("Verificar la cantidad de decimales")
    public void validarCantidadDecimales(){
        SliceResultResponseDTO sliceResult = new SliceResultResponseDTO(new Estimate(2576.55,13.608));
        //Un precio con un numero que produzca una division periodica
        BigDecimal precio = pricingService.calculatePrice(sliceResult,BigDecimal.valueOf(333));
        //Debe de redondear a 2 decimales, scale() devuelve en un int cuantos digitos hay despues del punto decimal
        //Es una propiedad de BigDecimal
        assertEquals(2,precio.scale());
    }

    @Test
    @DisplayName("Pieza de 0 gr y 0 s")
    public void vaildarCasoBorde(){
        SliceResultResponseDTO sliceResult = new SliceResultResponseDTO(new Estimate(0.0,0.0));
        //0g y 0s
        BigDecimal precio = pricingService.calculatePrice(sliceResult,BigDecimal.valueOf(298));
        //assertEquals de JUnit se usa para comparar valores
        //Recibe 2 parametros, el esperado y el real, en este caso si el segundo argumento coincide devuelve 0
        assertEquals(0,precio.compareTo(BigDecimal.valueOf(0)));
    }
}
