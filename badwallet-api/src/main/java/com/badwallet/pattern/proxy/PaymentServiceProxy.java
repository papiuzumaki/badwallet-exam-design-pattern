package com.badwallet.pattern.proxy;

import com.badwallet.dto.FactureDto;
import com.badwallet.dto.PaymentRequestDto;
import com.badwallet.dto.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

// Proxy Pattern : point d'accès centralisé vers le payment-service externe
@Component
@RequiredArgsConstructor
public class PaymentServiceProxy {

    private final RestTemplate restTemplate;

    @Value("${payment.service.url:http://localhost:8081}")
    private String paymentServiceUrl;

    public List<FactureDto> getFacturesMoisCourant(String walletCode) {
        String url = paymentServiceUrl + "/api/factures/" + walletCode + "/current";
        return restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<FactureDto>>() {}).getBody();
    }

    public List<FactureDto> getFacturesMoisCourantByUnite(String walletCode, String unite) {
        String url = paymentServiceUrl + "/api/factures/" + walletCode + "/current?unite=" + unite;
        return restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<FactureDto>>() {}).getBody();
    }

    public List<FactureDto> getFacturesByPeriode(String walletCode, LocalDate debut, LocalDate fin) {
        String url = paymentServiceUrl + "/api/factures/" + walletCode
                + "/periode?debut=" + debut + "&fin=" + fin;
        return restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<FactureDto>>() {}).getBody();
    }

    public PaymentResponseDto payCurrentMonthBill(PaymentRequestDto request) {
        String url = paymentServiceUrl + "/api/factures/pay/current";
        return restTemplate.postForObject(url, request, PaymentResponseDto.class);
    }

    public PaymentResponseDto paySpecificFactures(PaymentRequestDto request) {
        String url = paymentServiceUrl + "/api/factures/pay/specific";
        return restTemplate.postForObject(url, request, PaymentResponseDto.class);
    }
}
