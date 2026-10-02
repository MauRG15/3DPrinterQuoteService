package models;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

public record Filament(
    @Id Long id,
    String materialType,
    String color,
    BigDecimal pricePerKg,
    String filamentSettingsId,
    boolean available){
}
