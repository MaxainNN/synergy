package io.mkalugin.synergy.repository;

import io.mkalugin.synergy.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Contact c WHERE c.id = :id")
    void deleteById(@Param("id") Long id);
}
