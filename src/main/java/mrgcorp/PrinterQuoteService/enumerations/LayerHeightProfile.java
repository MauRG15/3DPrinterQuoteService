package mrgcorp.PrinterQuoteService.enumerations;

public enum LayerHeightProfile {
    EXTRA_FINE_08(0.08,"GP001"),
    HIGH_QUALITY_08(0.08,"GP099"),
    FINE_12(0.12,"GP002"),
    HIGH_QUALITY_12(0.12,"GP103"),
    HIGH_QUALITY_16(0.16,"GP107"),
    OPTIMAL_16(0.16,"GP003"),
    STANDARD_20(0.20,"GP004"),
    STRENGTH_20(0.20,"GP013"),
    DRAFT_24(0.24,"GP005"),
    EXTRA_DRAFT_28(0.28,"GP006");

    private final double layerHeight;
    private final String idProfile;
    LayerHeightProfile(double layerHeight, String idPrintProfile){
        this.layerHeight = layerHeight;
        this.idProfile = idPrintProfile;
    }
    public double getLayerHeight() {
        return layerHeight;
    }

    public String getIdProfile() {
        return idProfile;
    }
}
