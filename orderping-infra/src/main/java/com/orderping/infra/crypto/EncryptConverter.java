package com.orderping.infra.crypto;

import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@Component
public class EncryptConverter implements AttributeConverter<String, String> {

    private final AesEncryptor aesEncryptor;

    public EncryptConverter(AesEncryptor aesEncryptor) {
        this.aesEncryptor = aesEncryptor;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return aesEncryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return aesEncryptor.decrypt(dbData);
    }
}
