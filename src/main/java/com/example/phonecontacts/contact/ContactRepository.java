package com.example.phonecontacts.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Custom query is used to resolve the N+1 problem.
    @Query("""
            FROM Contact c
            LEFT JOIN FETCH c.emails
            LEFT JOIN FETCH c.phoneNumbers
            JOIN c.user u
            WHERE u.id = ?1
            """)
    List<Contact> findAllByUserId(Long userId);

    @Modifying
    @Query(value = """
            DELETE
            FROM Contact c
            WHERE c.name = ?1
            """)
    void deleteInBulkByName(String name);

    Optional<Contact> findByName(String name);

    boolean existsByName(String name);
}
