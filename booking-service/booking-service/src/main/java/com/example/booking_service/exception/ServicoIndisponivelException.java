package com.example.booking_service.exception;

public class ServicoIndisponivelException extends RuntimeException{

    public ServicoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
