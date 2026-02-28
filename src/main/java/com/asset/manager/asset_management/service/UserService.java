package com.asset.manager.asset_management.service;

import com.asset.manager.asset_management.DTO.UserRequestDTO;
import com.asset.manager.asset_management.DTO.UserResponseDTO;
import com.asset.manager.asset_management.entity.User;
import com.asset.manager.asset_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Siap untuk JWT nanti

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE USER (ADMIN ONLY)

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {

        // Cek duplicate username
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Cek duplicate NIK
        if (userRepository.findByNik(dto.getNik()).isPresent()) {
            throw new RuntimeException("NIK already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setNik(dto.getNik());

        // Encrypt password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setRole(dto.getRole());
        user.setFailedAttempts(0);
        user.setDeleted(false);

        User saved = userRepository.save(user);

        return mapToResponseDTO(saved);
    }

    // GET USER BY ID

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponseDTO(user);
    }

    // GET ALL USERS (PAGINATION)

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    // UPDATE USER

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cek duplicate username (kecuali dirinya sendiri)
        Optional<User> existing = userRepository.findByUsername(dto.getUsername());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new RuntimeException("Username already used");
        }

        user.setUsername(dto.getUsername());
        user.setNik(dto.getNik());
        user.setRole(dto.getRole());

        // Kalau password diubah
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return mapToResponseDTO(userRepository.save(user));
    }

    // SOFT DELETE USER

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user); // Karena sudah pakai @SQLDelete
    }

    // HANDLE FAILED LOGIN (UNTUK JWT NANTI)

    @Transactional
    public void increaseFailedAttempts(String username) {

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);

        if (attempts >= 3) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
        }

        userRepository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(String username) {

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFailedAttempts(0);
        user.setLockedUntil(null);

        userRepository.save(user);
    }

    // CHECK ACCOUNT LOCKED

    public void checkIfLocked(User user) {

        if (user.getLockedUntil() != null &&
                user.getLockedUntil().isAfter(LocalDateTime.now())) {

            throw new RuntimeException("Account is locked. Try again later.");
        }
    }

    // HELPER MAPPER

    private UserResponseDTO mapToResponseDTO(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().name());
    }
}
