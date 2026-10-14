package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.SliceResultResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {
    private final BigDecimal profitMargin;
    private final BigDecimal machineCostPerMin;
    //Recibe los valores de las propiedades  en el constructor
    //@Value es anotacion de Spring, asigna el valor cuando Spring construye el objeto, al colocarlo como paramaetros del constructor
    //nos permite poder crear objetos manualmente con dichos valores.
    public PricingService(@Value("${pricing.profit-price-multiplier}") BigDecimal profitMargin,
                          @Value("${pricing.machine-cost-per-min}") BigDecimal machineCostPerMin){
        this.profitMargin = profitMargin;
        this.machineCostPerMin = machineCostPerMin;
    }
    public BigDecimal calculatePrice(SliceResultResponseDTO sliceResult, BigDecimal pricePerKg){
        BigDecimal weightGrams = BigDecimal.valueOf(sliceResult.estimate().weightGrams());
        BigDecimal minsWorked = BigDecimal.valueOf(sliceResult.estimate().timeSeconds())
                .divide(BigDecimal.valueOf(60),2,RoundingMode.HALF_UP);
        BigDecimal semiTotal = weightGrams
                .multiply(pricePerKg)
                .divide(BigDecimal.valueOf(1000),2, RoundingMode.HALF_UP)
                .add(machineCostPerMin.multiply(minsWorked));
        return semiTotal.multiply(profitMargin).
                setScale(2, RoundingMode.HALF_UP);//Para que se redonde a 2 decimales y coincida con el dato en la BD DECIMAL(10,2)
    }
}
