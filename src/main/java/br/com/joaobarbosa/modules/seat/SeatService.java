package br.com.joaobarbosa.modules.seat;

import br.com.joaobarbosa.modules.room.Room;
import br.com.joaobarbosa.modules.room.RoomRepository;
import br.com.joaobarbosa.shared.exceptions.client.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SeatService {

    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;

    public SeatService(SeatRepository seatRepository, RoomRepository roomRepository) {
        this.seatRepository = seatRepository;
        this.roomRepository = roomRepository;
    }

    public Seat createSeat(UUID roomId, String rowLabel, Integer colNumber, String label) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Sala não encontrada com ID: " + roomId));

        // TODO: Adicionar regra de negócio para verificar se o assento já existe
        // TODO: Adicionar regra de negócio para verificar se as coordenadas (row, col) são válidas para a sala

        Seat newSeat = Seat.builder()
                .withRoom(room)
                .withRowLabel(rowLabel)
                .withColNumber(colNumber)
                .withLabel(label)
                .build();
        return seatRepository.save(newSeat);
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Seat getSeatById(UUID id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assento não encontrado com ID: " + id));
    }

    public Seat updateSeat(UUID id, Optional<UUID> updatedRoomId, Optional<String> updatedRowLabel,
                           Optional<Integer> updatedColNumber, Optional<String> updatedLabel) {
        Seat existingSeat = seatRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assento não encontrado com ID: " + id));

        updatedRoomId.ifPresent(newRoomId -> {
            Room newRoom = roomRepository.findById(newRoomId)
                    .orElseThrow(() -> new NotFoundException("Sala não encontrada com ID: " + newRoomId));
            existingSeat.setRoom(newRoom);
        });
        updatedRowLabel.ifPresent(existingSeat::setRowLabel);
        updatedColNumber.ifPresent(existingSeat::setColNumber);
        updatedLabel.ifPresent(existingSeat::setLabel);

        // TODO: Validar regras de negócio para a atualização

        return seatRepository.save(existingSeat);
    }

    public void deleteSeat(UUID id) {
        if (!seatRepository.existsById(id)) {
            throw new NotFoundException("Assento não encontrado com ID: " + id);
        }
        seatRepository.deleteById(id);
    }
}