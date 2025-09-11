package br.com.joaobarbosa.modules.rooms;

import br.com.joaobarbosa.shared.exceptions.client.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service; // CORREÇÃO AQUI

@Service
public class RoomService {

  private final RoomRepository roomRepository;

  public RoomService(RoomRepository roomRepository) {
    this.roomRepository = roomRepository;
  }

  public Room createRoom(String name, Integer rows, Integer cols) {
    Room newRoom = Room.builder()
		    .withCols(cols)
		    .withRows(rows)
		    .withName(name)
		    .build();
    return roomRepository.save(newRoom);
  }

  public List<Room> getAllRooms() {
    return roomRepository.findAll();
  }

  public Room getRoomById(UUID id) {
    return roomRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Sala não encontrada com ID: " + id));
  }

  public Room updateRoom(
      UUID id,
      Optional<String> updatedName,
      Optional<Integer> updatedRows,
      Optional<Integer> updatedCols) {
    Room existingRoom =
        this.roomRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sala não encontrada com ID: " + id));
    // todo:Validar regra de negocio de atualização da Room

    updatedName.ifPresent(existingRoom::setName);
    updatedRows.ifPresent(existingRoom::setRows);
    updatedCols.ifPresent(existingRoom::setCols);

    return roomRepository.save(existingRoom);
  }

  public void deleteRoom(UUID id) {
    if (!roomRepository.existsById(id)) {
      throw new NotFoundException("Sala não encontrada com ID: " + id);
    }
    roomRepository.deleteById(id);
  }
}
