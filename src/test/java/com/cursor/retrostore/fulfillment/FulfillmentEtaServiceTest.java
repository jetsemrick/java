package com.cursor.retrostore.fulfillment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fails until {@link FulfillmentEtaService#homeBannerText()} computes the promised 24-hour window correctly.
 * Ideal for debugger: set a breakpoint on {@code totalHours} and compare {@link Math#pow} vs adding the two hour constants.
 */
class FulfillmentEtaServiceTest {

    private final FulfillmentEtaService service = new FulfillmentEtaService();

    @Test
    void homeBannerText_reflectsTwentyFourHourFulfillmentWindow() {
        assertThat(service.homeBannerText())
                .isEqualTo("Fulfillment: orders ship within 24 hours");
    }
}
