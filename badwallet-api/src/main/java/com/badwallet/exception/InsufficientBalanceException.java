package com.badwallet.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(double required, double available) {
        super(String.format("Solde insuffisant. Requis : %.2f XOF | Disponible : %.2f XOF", required, available));
    }
}
