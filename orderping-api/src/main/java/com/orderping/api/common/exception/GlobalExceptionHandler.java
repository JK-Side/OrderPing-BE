package com.orderping.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.orderping.api.common.dto.ErrorResponse;
import com.orderping.domain.exception.BadRequestException;
import com.orderping.domain.exception.ForbiddenException;
import com.orderping.domain.exception.NotFoundException;
import com.orderping.domain.exception.OutOfStockException;
import com.orderping.domain.exception.UnauthorizedException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED.value(),
            "UNAUTHORIZED",
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            "FORBIDDEN",
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "BAD_REQUEST",
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStockException(OutOfStockException e) {
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.CONFLICT.value(),
            "OUT_OF_STOCK",
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.debug("Resource not found: {}", e.getResourcePath());
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            "요청한 리소스를 찾을 수 없습니다."
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestCookieException(MissingRequestCookieException e) {
        log.debug("Missing cookie: {}", e.getCookieName());
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.BAD_REQUEST.value(),
            "BAD_REQUEST",
            "필수 쿠키가 누락되었습니다: " + e.getCookieName()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_SERVER_ERROR",
            "서버 내부 오류가 발생했습니다."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
