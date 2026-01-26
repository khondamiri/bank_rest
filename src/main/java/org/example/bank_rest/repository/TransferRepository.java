package org.example.bank_rest.repository;

import org.example.bank_rest.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByFromCardIdOrToCardId(Long fromId, Long toId);
}
