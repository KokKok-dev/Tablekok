package com.tablekok.user_service.user.presentation.advice;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tablekok.exception.GlobalExceptionHandler;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler extends GlobalExceptionHandler {

}





