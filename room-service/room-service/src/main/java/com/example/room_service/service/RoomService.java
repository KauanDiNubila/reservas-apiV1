package com.example.room_service.service;

import com.example.room_service.dto.RoomRequest;
import com.example.room_service.dto.RoomResponse;
import com.example.room_service.entity.Room;
import com.example.room_service.exception.RecursoNaoEncontradoException;
import com.example.room_service.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<RoomResponse> listar() {
        return roomRepository.findAll()
                .stream()
                .map(RoomResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoomResponse buscarPorId(Long id) {
        return RoomResponse.de(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Room buscarEntidadePorId(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala", id));
    }

    @Transactional
    public RoomResponse criar(RoomRequest request) {
        Room room = new Room();
        room.setNome(request.getNome());
        room.setCapacidade(request.getCapacidade());
        room.setLocalizacao(request.getLocalizacao());
        room.setAtiva(request.isAtiva());
        return RoomResponse.de(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse atualizar(Long id, RoomRequest request) {
        Room room = buscarEntidadePorId(id);
        room.setNome(request.getNome());
        room.setCapacidade(request.getCapacidade());
        room.setLocalizacao(request.getLocalizacao());
        room.setAtiva(request.isAtiva());
        return RoomResponse.de(roomRepository.save(room));
    }

    @Transactional
    public void remover(Long id) {
        buscarEntidadePorId(id);
        roomRepository.deleteById(id);
    }
}
