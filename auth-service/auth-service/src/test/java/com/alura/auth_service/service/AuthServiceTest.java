package com.alura.auth_service.service;

import com.alura.auth_service.config.JwtConfig;
import com.alura.auth_service.dto.LoginRequest;
import com.alura.auth_service.dto.RegisterRequest;
import com.alura.auth_service.entity.UserCredential;
import com.alura.auth_service.exception.RegraDeNegocioException;
import com.alura.auth_service.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserCredentialRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtConfig jwtConfig;

    @InjectMocks
    private AuthService authService;

    private UserCredential usuario;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        usuario = new UserCredential();
        usuario.setId(1L);
        usuario.setEmail("joao@email.com");
        usuario.setSenha("hash_da_senha");
        usuario.setRole(UserCredential.Role.USER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("joao@email.com");
        loginRequest.setSenha("123456");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("joao@email.com");
        registerRequest.setSenha("123456");
    }

    // ─── Login ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve fazer login com sucesso quando credenciais são válidas")
    void deveFazerLoginComSucesso() {
        when(repository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hash_da_senha")).thenReturn(true);
        when(jwtConfig.gerarToken(any(), any(), any())).thenReturn("jwt_token");
        when(jwtConfig.getExpiration()).thenReturn(86400000L);

        var response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt_token");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não existe")
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        when(repository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("inválidos");
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha está errada")
    void deveLancarExcecaoQuandoSenhaErrada() {
        when(repository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hash_da_senha")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("inválidos");
    }

// ─── Registro ─────────────────────────────────
@Test
@DisplayName("Deve registrar usuário com sucesso")
void deveRegistrarUsuarioComSucesso() {
    when(repository.findByEmail("joao@email.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("123456")).thenReturn("hash_da_senha");
    when(repository.save(any())).thenReturn(usuario);

    assertThatNoException().isThrownBy(() -> authService.registrar(registerRequest));
    verify(repository, times(1)).save(any());
}

    @Test
    @DisplayName("Deve lançar exceção ao registrar email duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        when(repository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.registrar(registerRequest))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("cadastrado");
    }
}