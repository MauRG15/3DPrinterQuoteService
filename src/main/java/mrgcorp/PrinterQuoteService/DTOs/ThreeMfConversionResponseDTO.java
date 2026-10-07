package mrgcorp.PrinterQuoteService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThreeMfConversionResponseDTO(@JsonProperty("input_token") String inputToken) {
}
