package co.swaadisht.swaadisht.forms;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserForm {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String confirmPassword;

}
