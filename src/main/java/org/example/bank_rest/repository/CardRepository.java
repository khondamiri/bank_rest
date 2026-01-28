package org.example.bank_rest.repository;

import org.example.bank_rest.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumberEnc(String cardNumberEnc);
    Page<Card> findAllByUserId(Long id, Pageable pageable);
    Page<Card> findByUserIdAndCardLast4Containing(Long id, String last4Fragment, Pageable pageable);
    Page<Card> findAllByUserUsername(String username, Pageable pageable);
}
