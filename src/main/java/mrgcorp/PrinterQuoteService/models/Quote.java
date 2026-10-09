package mrgcorp.PrinterQuoteService.models;

import mrgcorp.PrinterQuoteService.enumerations.LayerHeightProfile;
import mrgcorp.PrinterQuoteService.enumerations.QuoteStatus;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Quote {
    @Id
    private Long id;
    private LocalDateTime createdAt;
    private byte[] stlFile;
    private Long filamentId;
    private LayerHeightProfile layerProfile;
    private double infillPercentage;
    private int pieceQuantity;
    private boolean supportsNeeded;
    private QuoteStatus status;
    private String clientName;
    private String clientEmail;
    private Double weightGrams;
    private Double timeSeconds;
    private BigDecimal totalPrice;
    public Quote(
            Long id,
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
    ){
        this.id = id;
        this.createdAt = createdAt;
        this.stlFile = stlFile;
        this.filamentId = filamentId;
        this.infillPercentage = infillPercentage;
        this.layerProfile = layerProfile;
        this.pieceQuantity = pieceQuantity;
        this.supportsNeeded = supportsNeeded;
        this.status = status;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public byte[] getStlFile() {
        return stlFile;
    }

    public Long getFilamentId() {
        return filamentId;
    }

    public LayerHeightProfile getLayerProfile() {
        return layerProfile;
    }

    public double getInfillPercentage() {
        return infillPercentage;
    }

    public int getPieceQuantity() {
        return pieceQuantity;
    }

    public boolean isSupportsNeeded() {
        return supportsNeeded;
    }

    public void setStatus(QuoteStatus status) {
        this.status = status;
    }

    public QuoteStatus getStatus() {
        return status;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public Double getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(Double weightGrams) {
        this.weightGrams = weightGrams;
    }

    public Double getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(Double timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
