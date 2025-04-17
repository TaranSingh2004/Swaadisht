package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.ShippingCalculatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {
    private final ShippingCalculatorService shippingService;

    public ShippingController(ShippingCalculatorService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<?> calculateShippingCharge(
            @RequestParam String originPincode,
            @RequestParam String destinationPincode) {

        try {
            double charge = shippingService.calculateShippingCharge(originPincode, destinationPincode);
            double distanceKm = shippingService.getLastCalculatedDistance() / 1000;

            return ResponseEntity.ok(Map.of(
                    "origin", originPincode,
                    "destination", destinationPincode,
                    "distanceKm", distanceKm,
                    "shippingCharge", charge,
                    "currency", "INR"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to calculate shipping charge",
                            "details", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }
}