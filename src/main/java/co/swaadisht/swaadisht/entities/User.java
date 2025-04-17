package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private int id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

//    @Getter(value = AccessLevel.NONE)
//    @Setter
    private String password;

    private String profilePic;

    private String phoneNumber;

//    @Getter(value = AccessLevel.NONE)
    private Boolean enabled;

    private boolean emailVerified=false;
    private boolean phoneVerified = false;

    @Enumerated(value = EnumType.STRING)
    private Providers provider=Providers.SELF;

    private String providerId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roleList = new ArrayList<>();

    private String emailToken;

    private String role;

    private String resetToken;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<OrderAddress> addresses = new ArrayList<>();

}