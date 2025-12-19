package com.orderping.infra.store.entity;

import com.orderping.domain.store.StoreAccount;
import jakarta.persistence.*;
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

    @Column(name = "account_number_enc", nullable = false, length = 256)
    private String accountNumberEnc;

    @Column(name = "account_number_mask", nullable = false, length = 50)
    private String accountNumberMask;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public StoreAccountEntity(Long id, Long storeId, String bankCode, String accountNumberEnc, String accountNumberMask, Boolean isActive) {
        this.id = id;
        this.storeId = storeId;
        this.bankCode = bankCode;
        this.accountNumberEnc = accountNumberEnc;
        this.accountNumberMask = accountNumberMask;
        this.isActive = isActive;
    }

    @PrePersist
    protected void onCreate() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    // Domain -> Entity
    public static StoreAccountEntity from(StoreAccount storeAccount) {
        return StoreAccountEntity.builder()
                .id(storeAccount.getId())
                .storeId(storeAccount.getStoreId())
                .bankCode(storeAccount.getBankCode())
                .accountNumberEnc(storeAccount.getAccountNumberEnc())
                .accountNumberMask(storeAccount.getAccountNumberMask())
                .isActive(storeAccount.getIsActive())
                .build();
    }

    // Entity -> Domain
    public StoreAccount toDomain() {
        return StoreAccount.builder()
                .id(this.id)
                .storeId(this.storeId)
                .bankCode(this.bankCode)
                .accountNumberEnc(this.accountNumberEnc)
                .accountNumberMask(this.accountNumberMask)
                .isActive(this.isActive)
                .build();
    }
}
