package com.pedrohenrique.bibliotecavirtual.adapter.input.controller.exceptions;

import java.util.Date;

public record ExcpetionResponse(Date timestamp, String message, String details){}
