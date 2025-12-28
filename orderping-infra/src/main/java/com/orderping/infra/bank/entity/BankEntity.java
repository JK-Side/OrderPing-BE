package com.orderping.infra.bank.entity;

import com.orderping.domain.bank.Bank;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "banks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankEntity {

    @Id
    @Column(name = "bank_code", length = 10)
    private String code;

    @Column(name = "bank_name", nullable = false, length = 50)
    private String name;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public Bank toDomain() {
        return Bank.builder()
                .code(this.code)
                .name(this.name)
                .isActive(this.isActive)
                .build();
    }
}
