package DTOs;

import enumerations.LayerHeightProfile;
import org.springframework.web.multipart.MultipartFile;

public record QuoteRequestDTO(
        MultipartFile stlFile,
        Long filamentId,
        LayerHeightProfile layerProfile,
        double infillPercentage,
        int pieceQuantity,
        boolean supportsNeeded,
        String clientName,
        String clientEmail) {
}
