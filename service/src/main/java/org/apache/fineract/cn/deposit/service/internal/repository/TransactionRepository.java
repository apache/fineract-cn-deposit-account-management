package org.apache.fineract.cn.deposit.service.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByIdentifier(final String identifier);

}

