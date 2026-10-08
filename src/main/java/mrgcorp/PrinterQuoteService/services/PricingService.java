package mrgcorp.PrinterQuoteService.services;

import mrgcorp.PrinterQuoteService.DTOs.SliceResultResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {
    @Value("${pricing.profit-margin}")
    private BigDecimal profitMargin;
    @Value("${pricing.machine-cost-per-min}")
    private BigDecimal machineCostPerMin;

    public BigDecimal calculatePrice(SliceResultResponseDTO sliceResult, BigDecimal pricePerKg){
        BigDecimal weightGrams = new BigDecimal(sliceResult.estimate().weightGrams());
        BigDecimal minsWorked = new BigDecimal(sliceResult.estimate().timeSeconds())
                .divide(BigDecimal.valueOf(60),2,RoundingMode.HALF_UP);
        BigDecimal semiTotal = weightGrams
                .multiply(pricePerKg)
                .divide(BigDecimal.valueOf(1000),2, RoundingMode.HALF_UP)
                .add(machineCostPerMin.multiply(minsWorked));
        return semiTotal.multiply(profitMargin);
    }
}
