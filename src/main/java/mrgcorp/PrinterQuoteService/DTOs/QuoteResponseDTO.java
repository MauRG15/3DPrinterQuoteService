package mrgcorp.PrinterQuoteService.DTOs;

import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record QuoteResponseDTO(
        Long id,
        QuoteStatus status,
        LocalDateTime createdAt,
        Double timeSeconds,
        Double weightGrams,
        BigDecimal totalPrice) {
}
