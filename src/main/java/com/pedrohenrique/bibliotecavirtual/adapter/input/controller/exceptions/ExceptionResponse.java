package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.exceptions;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details){}
