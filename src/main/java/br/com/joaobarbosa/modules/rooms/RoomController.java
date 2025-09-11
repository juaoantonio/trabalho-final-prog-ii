package br.com.joaobarbosa.modules.rooms;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.joaobarbosa.modules.rooms.dto.UpdateRoomRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.joaobarbosa.modules.rooms.dto.CreateRoomRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rooms")
public class RoomController {

  private final RoomService roomService;

  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  @PostMapping
  public ResponseEntity<Room> createRoom(@RequestBody @Valid CreateRoomRequest createRoomRequest) {
    Room createdRoom =
        roomService.createRoom(createRoomRequest.name(), createRoomRequest.rows(), createRoomRequest.cols());
    return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Room>> getAllRooms() {
    List<Room> rooms = roomService.getAllRooms();
    return new ResponseEntity<>(rooms, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Room> getRoomById(@PathVariable UUID id) {
    Room room = roomService.getRoomById(id);
    return new ResponseEntity<>(room, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Room> updateRoom(
      @PathVariable UUID id, @RequestBody @Valid UpdateRoomRequest roomRequest) {

    Room room =
        roomService.updateRoom(
            id,
            Optional.ofNullable(roomRequest.name()),
            Optional.ofNullable(roomRequest.rows()),
            Optional.ofNullable(roomRequest.cols()));

    return new ResponseEntity<>(room, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
    roomService.deleteRoom(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
