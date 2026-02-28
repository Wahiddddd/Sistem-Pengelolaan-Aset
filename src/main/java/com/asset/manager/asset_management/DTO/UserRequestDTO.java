package com.asset.manager.asset_management.DTO;

import com.asset.manager.asset_management.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    @NotBlank(message = "Username wajib diisi")
    private String Username;

    @NotBlank(message = "Password wajib diisi")
    private String Password;

    @NotBlank(message = "ROLE_ADMIN atau ROLE_TEKNISI ?")
    private UserRole Role;
}
