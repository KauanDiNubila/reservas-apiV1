package com.example.user_service.service;

import com.example.user_service.dto.UserRequest;
import com.example.user_service.dto.UserResponse;
import com.example.user_service.entity.User;
import com.example.user_service.exception.RecursoNaoEncontradoException;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> listar() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse buscarPorId(Long id) {
        return UserResponse.de(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public User buscarEntidadePorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    @Transactional
    public UserResponse criar(UserRequest request) {
        User user = new User();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        return UserResponse.de(userRepository.save(user));
    }

    @Transactional
    public UserResponse atualizar(Long id, UserRequest request) {
        User user = buscarEntidadePorId(id);
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        return UserResponse.de(userRepository.save(user));
    }

    @Transactional
    public void remover(Long id) {
        buscarEntidadePorId(id);
        userRepository.deleteById(id);
    }
}
