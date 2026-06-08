package com.alura.api_gateway.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " nao encontrado(a) com id: " + id);
    }
}
