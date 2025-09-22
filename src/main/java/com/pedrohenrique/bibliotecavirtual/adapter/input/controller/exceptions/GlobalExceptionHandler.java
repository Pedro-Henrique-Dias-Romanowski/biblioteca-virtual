package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.exceptions;

import com.pedrohenrique.bibliotecavirtual.domain.exceptions.BusinessException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.DataBaseException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.ResourceNotFoundException;
import com.pedrohenrique.bibliotecavirtual.domain.exceptions.livro.LivroNaoEcontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExcpetionResponse> handleException(Exception ex, HttpServletRequest request){
        ExcpetionResponse response = new ExcpetionResponse(
                new Date(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExcpetionResponse> handleBusinessException(Exception ex, HttpServletRequest request){
        ExcpetionResponse response = new ExcpetionResponse(
                new Date(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
