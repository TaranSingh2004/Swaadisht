package co.swaadisht.swaadisht.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserForm {

    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Min 3 Characters is required")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min=8, max=12, message = "Invalid Phone Number")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Min 6 characters is required")
    private String password;

    private String confirmPassword;

}
