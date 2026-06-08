package com.alura.api_gateway.exception;

public class ServicoIndisponivelException extends RuntimeException {
    public ServicoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
