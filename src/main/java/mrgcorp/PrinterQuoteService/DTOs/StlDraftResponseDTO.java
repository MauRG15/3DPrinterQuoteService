package mrgcorp.PrinterQuoteService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StlDraftResponseDTO(@JsonProperty("draft_token") String draftToken) {
}
