package com.badwallet.controller;

import com.badwallet.dto.FactureDto;
import com.badwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// Proxy API : expose les factures du payment-service via badwallet-api
@RestController
@RequestMapping("/api/external/factures")
@RequiredArgsConstructor
public class ExternalFactureController {

    private final WalletService walletService;

    // 2.2 & 2.3 Factures du mois courant (avec filtre optionnel par unité)
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<List<FactureDto>> getCurrentFactures(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {
        if (unite != null) {
            return ResponseEntity.ok(walletService.getFacturesMoisCourantByUnite(walletCode, unite));
        }
        return ResponseEntity.ok(walletService.getFacturesMoisCourant(walletCode));
    }

    // 2.4 Factures sur une période
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<List<FactureDto>> getFacturesByPeriode(
            @PathVariable String walletCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(walletService.getFacturesByPeriode(walletCode, debut, fin));
    }
}
