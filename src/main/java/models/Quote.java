package models;

import enumerations.LayerHeightProfile;
import enumerations.QuoteStatus;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record Quote(
        @Id Long id,
        LocalDateTime createdAt,
        byte[] stlFile,
        Long filamentId,
        LayerHeightProfile layerProfile,
        double infillPercentage,
        int pieceQuantity,
        boolean supportsNeeded,
        QuoteStatus status,
        String clientName,
        String clientEmail
) {
}
