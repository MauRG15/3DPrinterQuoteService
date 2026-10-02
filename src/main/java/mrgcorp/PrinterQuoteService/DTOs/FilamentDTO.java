package mrgcorp.PrinterQuoteService.DTOs;

import java.math.BigDecimal;

public record FilamentDTO(Long id,
                          String materialType,
                          String color,
                          BigDecimal pricePerKg,
                          boolean available) {
}
