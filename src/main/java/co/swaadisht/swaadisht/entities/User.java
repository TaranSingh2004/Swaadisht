package co.swaadisht.swaadisht.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String userId;

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
    private boolean enabled=false;

    private boolean emailVerified=false;
    private boolean phoneVerified = false;

    @Enumerated(value = EnumType.STRING)
    private Providers provider=Providers.SELF;

    private String providerId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roleList = new ArrayList<>();

    private String emailToken;

    private String role;

}