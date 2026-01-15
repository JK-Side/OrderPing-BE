package com.orderping.infra.store.entity;

import com.orderping.domain.enums.TableStatus;
import com.orderping.domain.store.StoreTable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "store_tables")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "table_num", nullable = false)
    private Integer tableNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TableStatus status;

    @Column(name = "qr_image_url", length = 512)
    private String qrImageUrl;

    @Builder
    public StoreTableEntity(Long id, Long storeId, Integer tableNum, TableStatus status, String qrImageUrl) {
        this.id = id;
        this.storeId = storeId;
        this.tableNum = tableNum;
        this.status = status;
        this.qrImageUrl = qrImageUrl;
    }

    // Domain -> Entity
    public static StoreTableEntity from(StoreTable storeTable) {
        return StoreTableEntity.builder()
            .id(storeTable.getId())
            .storeId(storeTable.getStoreId())
            .tableNum(storeTable.getTableNum())
            .status(storeTable.getStatus())
            .qrImageUrl(storeTable.getQrImageUrl())
            .build();
    }

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = TableStatus.EMPTY;
        }
    }

    // Entity -> Domain
    public StoreTable toDomain() {
        return StoreTable.builder()
            .id(this.id)
            .storeId(this.storeId)
            .tableNum(this.tableNum)
            .status(this.status)
            .qrImageUrl(this.qrImageUrl)
            .build();
    }
}
