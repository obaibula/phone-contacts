package com.example.phonecontacts.phonenumber;

import com.example.phonecontacts.contact.Contact;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phone_numbers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "phoneNumber"})
@ToString(of = "phoneNumber")
public class PhoneNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    public PhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
