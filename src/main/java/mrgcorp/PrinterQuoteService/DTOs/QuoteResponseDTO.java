package mrgcorp.PrinterQuoteService.DTOs;

import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;

import java.time.LocalDateTime;

public record QuoteResponseDTO(Long quoteId, QuoteStatus quoteStatus, LocalDateTime createdAt) {
}
