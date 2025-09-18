package br.com.joaobarbosa.controller;

import br.com.joaobarbosa.entity.Room;
import br.com.joaobarbosa.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada com ID: " + id));
    }

    public Room updateRoom(UUID id, Room updatedRoom) {
        Room existingRoom = getRoomById(id);

        existingRoom.setName(updatedRoom.getName());
        existingRoom.setCapacity(updatedRoom.getCapacity());

        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(UUID id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Sala não encontrada com ID: " + id);
        }
        roomRepository.deleteById(id);
    }
}
