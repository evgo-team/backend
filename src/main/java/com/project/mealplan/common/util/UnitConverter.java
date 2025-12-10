package com.project.mealplan.common.util;

import com.project.mealplan.common.enums.IngredientUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitConverter {

    // ===== BASE CONSTANTS =====
    // Volume (metric)
    private static final BigDecimal ML_PER_L   = new BigDecimal("1000");
    private static final BigDecimal ML_PER_DL  = new BigDecimal("100");
    private static final BigDecimal ML_PER_CL  = new BigDecimal("10");

    // Volume (US)
    private static final BigDecimal TSP_TO_ML   = new BigDecimal("4.92892");   // ~5 ml
    private static final BigDecimal TBS_TO_ML   = new BigDecimal("14.7868");   // ~15 ml
    private static final BigDecimal CUP_TO_ML   = new BigDecimal("240");       // US cup (approx)
    private static final BigDecimal FLOZ_TO_ML  = new BigDecimal("29.5735");
    private static final BigDecimal PINT_TO_ML  = new BigDecimal("473.176");
    private static final BigDecimal QUART_TO_ML = new BigDecimal("946.353");
    private static final BigDecimal GALLON_TO_ML = new BigDecimal("3785.41");

    // Mass
    private static final BigDecimal MG_TO_G   = new BigDecimal("0.001");
    private static final BigDecimal KG_TO_G   = new BigDecimal("1000");
    private static final BigDecimal OZ_TO_G   = new BigDecimal("28.3495");
    private static final BigDecimal LB_TO_G   = new BigDecimal("453.59237");
    private static final BigDecimal EGG_PIECE_TO_G = new BigDecimal("50"); // average weight of one egg

    /**
     * Convert quantity with unit → grams (g)
     *
     * @param quantity          số lượng user nhập
     * @param unit              đơn vị (G, ML, TSP, TBS, OZ, CUP, v.v.)
     * @param densityGramPerMl  khối lượng riêng (gram per 1 ml).
     *                          BẮT BUỘC KHÔNG ĐƯỢC NULL cho các đơn vị thể tích.
     */
    public static BigDecimal toGram(BigDecimal quantity, IngredientUnit unit, BigDecimal densityGramPerMl) {

        if (quantity == null || unit == null) return BigDecimal.ZERO;

        // validate density for all VOLUME units
        if (isVolumeUnit(unit) && (densityGramPerMl == null || densityGramPerMl.compareTo(BigDecimal.ZERO) <= 0)) {
            return BigDecimal.ZERO; // hoặc throw exception
        }

        BigDecimal result = switch (unit) {
            // ===== MASS =====
            case MG -> quantity.multiply(MG_TO_G);
            case G  -> quantity;
            case KG -> quantity.multiply(KG_TO_G);
            case OZ -> quantity.multiply(OZ_TO_G);
            case LB -> quantity.multiply(LB_TO_G);
            case EGG_PIECE -> quantity.multiply(EGG_PIECE_TO_G);

            // ===== VOLUME (METRIC) =====
            case ML -> quantity.multiply(densityGramPerMl);
            case L  -> quantity.multiply(ML_PER_L).multiply(densityGramPerMl);
            case DL -> quantity.multiply(ML_PER_DL).multiply(densityGramPerMl);
            case CL -> quantity.multiply(ML_PER_CL).multiply(densityGramPerMl);

            // ===== VOLUME (US CUSTOMARY) =====
            case TSP -> quantity.multiply(TSP_TO_ML).multiply(densityGramPerMl);
            case TBS -> quantity.multiply(TBS_TO_ML).multiply(densityGramPerMl);
            case CUP -> quantity.multiply(CUP_TO_ML).multiply(densityGramPerMl);
            case FLOZ -> quantity.multiply(FLOZ_TO_ML).multiply(densityGramPerMl);
            case PINT -> quantity.multiply(PINT_TO_ML).multiply(densityGramPerMl);
            case QUART -> quantity.multiply(QUART_TO_ML).multiply(densityGramPerMl);
            case GALLON -> quantity.multiply(GALLON_TO_ML).multiply(densityGramPerMl);
        };
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean isVolumeUnit(IngredientUnit unit) {
        return switch (unit) {
            case ML, L, DL, CL,
                 TSP, TBS, CUP,
                 FLOZ, PINT, QUART, GALLON -> true;
            default -> false;
        };
    }

    public static BigDecimal toGram(BigDecimal quantity, IngredientUnit unit) {
        if (quantity == null || unit == null) return BigDecimal.ZERO;

        BigDecimal result = switch (unit) {
            case MG -> quantity.multiply(MG_TO_G);
            case G  -> quantity;
            case KG -> quantity.multiply(KG_TO_G);
            case OZ -> quantity.multiply(OZ_TO_G);
            case LB -> quantity.multiply(LB_TO_G);
            case EGG_PIECE -> quantity.multiply(EGG_PIECE_TO_G);
            default -> BigDecimal.ZERO;
        };
        return result;
    }

    public static BigDecimal toMl(BigDecimal quantity, IngredientUnit unit) {
        if (quantity == null || unit == null) return BigDecimal.ZERO;

        BigDecimal result = switch (unit) {
            case ML -> quantity;
            case L  -> quantity.multiply(ML_PER_L);
            case DL -> quantity.multiply(ML_PER_DL);
            case CL -> quantity.multiply(ML_PER_CL);

            case TSP -> quantity.multiply(TSP_TO_ML);
            case TBS -> quantity.multiply(TBS_TO_ML);
            case CUP -> quantity.multiply(CUP_TO_ML);
            case FLOZ -> quantity.multiply(FLOZ_TO_ML);
            case PINT -> quantity.multiply(PINT_TO_ML);
            case QUART -> quantity.multiply(QUART_TO_ML);
            case GALLON -> quantity.multiply(GALLON_TO_ML);

            default -> BigDecimal.ZERO;
        };
        return result;
    }
}
