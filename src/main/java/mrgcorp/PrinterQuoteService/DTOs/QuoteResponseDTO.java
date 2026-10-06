package mrgcorp.PrinterQuoteService.DTOs;

import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;

import java.time.LocalDateTime;

public record QuoteResponseDTO(Long id, QuoteStatus status, LocalDateTime createdAt) {
}
