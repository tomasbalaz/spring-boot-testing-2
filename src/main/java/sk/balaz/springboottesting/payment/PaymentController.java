package sk.balaz.springboottesting.payment;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{customerId}")
    public void makePayment(@PathVariable("customerId") UUID customerId,
                            @RequestBody PaymentRequest request) {
        paymentService.chargeCard(customerId, request);
    }
}
