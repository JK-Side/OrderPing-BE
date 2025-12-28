package com.orderping.infra.store.entity;

import com.orderping.domain.store.StoreAccount;
import com.orderping.infra.crypto.EncryptConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_account_id")
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "bank_code", nullable = false, length = 10)
    private String bankCode;

    @Column(name = "account_holder", nullable = false, length = 50)
    private String accountHolder;

    @Column(name = "account_number_enc", nullable = false, length = 256)
    @Convert(converter = EncryptConverter.class)
    private String accountNumberEnc;

    @Column(name = "account_number_mask", nullable = false, length = 50)
    private String accountNumberMask;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public StoreAccountEntity(Long id, Long storeId, String bankCode, String accountHolder, String accountNumberEnc,
        String accountNumberMask, Boolean isActive) {
        this.id = id;
        this.storeId = storeId;
        this.bankCode = bankCode;
        this.accountHolder = accountHolder;
        this.accountNumberEnc = accountNumberEnc;
        this.accountNumberMask = accountNumberMask;
        this.isActive = isActive;
    }

    // Domain -> Entity
    public static StoreAccountEntity from(StoreAccount storeAccount) {
        return StoreAccountEntity.builder()
            .id(storeAccount.getId())
            .storeId(storeAccount.getStoreId())
            .bankCode(storeAccount.getBankCode())
            .accountHolder(storeAccount.getAccountHolder())
            .accountNumberEnc(storeAccount.getAccountNumberEnc())
            .accountNumberMask(storeAccount.getAccountNumberMask())
            .isActive(storeAccount.getIsActive())
            .build();
    }

    @PrePersist
    protected void onCreate() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    // Entity -> Domain
    public StoreAccount toDomain() {
        return StoreAccount.builder()
            .id(this.id)
            .storeId(this.storeId)
            .bankCode(this.bankCode)
            .accountHolder(this.accountHolder)
            .accountNumberEnc(this.accountNumberEnc)
            .accountNumberMask(this.accountNumberMask)
            .isActive(this.isActive)
            .build();
    }
}
