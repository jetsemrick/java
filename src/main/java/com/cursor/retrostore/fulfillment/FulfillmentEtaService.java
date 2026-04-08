package com.cursor.retrostore.fulfillment;

import org.springframework.stereotype.Service;

/**
 * Computes storefront copy for estimated fulfillment time.
 */
@Service
public class FulfillmentEtaService {

    /** Hours reserved for order processing before hand-off. */
    public static final int PROCESSING_BUFFER_HOURS = 8;

    /** Carrier / delivery window within a business day. */
    public static final int COURIER_WINDOW_HOURS = 16;

    /**
     * Banner text for the home page. Intended total window: {@code PROCESSING_BUFFER_HOURS + COURIER_WINDOW_HOURS}
     * (24 hours).
     * <p>
     * <strong>Demo bug:</strong> the implementation raises the buffer to the power of the courier window
     * ({@code Math.pow}), which is nonsensical for ETA hours but easy to spot in the debugger.
     * Fix the calculation, then re-run {@code FulfillmentEtaServiceTest}.
     */
    public String homeBannerText() {
        long totalHours = (long) Math.pow(PROCESSING_BUFFER_HOURS, COURIER_WINDOW_HOURS);
        return "Fulfillment: orders ship within " + totalHours + " hours";
    }
}
