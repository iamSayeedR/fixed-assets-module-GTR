package com.example.accounts.exception;

public class AccountInactiveException extends RuntimeException {

    public AccountInactiveException(String accountNumber) {
        super(String.format("Account %s is inactive or frozen", accountNumber));
    }
}
