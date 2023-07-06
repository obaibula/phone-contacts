package com.example.phonecontacts.contact;

import com.example.phonecontacts.email.Email;
import com.example.phonecontacts.phonenumber.PhoneNumber;
import com.example.phonecontacts.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
// because name is unique
@EqualsAndHashCode(of = "name")
@ToString(exclude = "user")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "contact",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Email> emails = new HashSet<>();

    @OneToMany(mappedBy = "contact",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<PhoneNumber> phoneNumbers = new HashSet<>();

    public void addEmail(Email email) {
        emails.add(email);
        email.setContact(this);
    }

    public void removeEmail(Email email) {
        emails.remove(email);
        email.setContact(null);
    }

    public void addPhoneNumber(PhoneNumber phoneNumber){
        phoneNumbers.add(phoneNumber);
        phoneNumber.setContact(this);
    }

    public void removePhoneNumber(PhoneNumber phoneNumber){
        phoneNumbers.remove(phoneNumber);
        phoneNumber.setContact(null);
    }
}
