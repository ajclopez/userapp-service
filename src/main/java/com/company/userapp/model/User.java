package com.company.userapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity(name = "app_user")
@Table(uniqueConstraints = { @UniqueConstraint(name = "unique_email_constraint", columnNames = { "email" }) })
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant modified;

    @Column(name = "last_login", nullable = false)
    @JsonProperty("last_login")
    private Instant lastLogin;

    @Column(nullable = false)
    @JsonProperty("isactive")
    private Boolean active;

    @Column(nullable = false)
    private String token;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Phone> phones;

}
