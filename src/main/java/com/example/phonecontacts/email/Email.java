package com.example.phonecontacts.email;

import com.example.phonecontacts.contact.Contact;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "emails")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = {"email", "id"})
@ToString(of = "email")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id")
    private Contact contact;

}
