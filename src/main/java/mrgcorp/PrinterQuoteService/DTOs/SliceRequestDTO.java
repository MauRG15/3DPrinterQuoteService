package mrgcorp.PrinterQuoteService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record SliceRequestDTO(
        @JsonProperty("input_token") String inputToken,
        @JsonProperty("machine_id") String machineId,
        @JsonProperty("process_id") String processId,
        @JsonProperty("filament_settings_ids") List<String> filamentSettingsIds,
        @JsonProperty("auto_center") boolean autoCenter,
        @JsonProperty("process_overrides") Map<String, String> processOverrides,
        int copies) {
}
