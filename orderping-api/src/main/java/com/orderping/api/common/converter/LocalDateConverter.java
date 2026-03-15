package com.orderping.api.common.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LocalDateConverter implements Converter<String, LocalDate> {

    private static final DateTimeFormatter COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate convert(String source) {
        try {
            if (source.contains("-")) {
                return LocalDate.parse(source, ISO);
            }
            return LocalDate.parse(source, COMPACT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "날짜 형식이 올바르지 않습니다. 'yyyyMMdd' 또는 'yyyy-MM-dd' 형식으로 입력해주세요. (예: 20260303 또는 2026-03-03)"
            );
        }
    }
}
