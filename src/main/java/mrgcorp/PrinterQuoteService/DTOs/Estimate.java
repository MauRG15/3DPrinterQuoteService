package mrgcorp.PrinterQuoteService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Estimate(@JsonProperty("time_seconds") Double timeSeconds, @JsonProperty("weight_g") Double weightGrams) {
}
