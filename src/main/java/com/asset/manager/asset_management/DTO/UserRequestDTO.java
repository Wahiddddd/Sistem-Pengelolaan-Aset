package com.asset.manager.asset_management.DTO;

import com.asset.manager.asset_management.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "Username wajib diisi")
    private String username;

    @NotBlank(message = "Password wajib diisi")
    private String password;

    @NotNull(message = "Role wajib diisi")
    private UserRole role;

    @NotBlank(message = "NIK wajib diisi")
    private String nik;
}
