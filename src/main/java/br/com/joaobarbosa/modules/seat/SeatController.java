package br.com.joaobarbosa.modules.seat;

import br.com.joaobarbosa.modules.seat.dto.CreateSeatRequest;
import br.com.joaobarbosa.modules.seat.dto.UpdateSeatRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestBody @Valid CreateSeatRequest createSeatRequest) {
        Seat createdSeat = seatService.createSeat(
                createSeatRequest.roomId(),
                createSeatRequest.rowLabel(),
                createSeatRequest.colNumber(),
                createSeatRequest.label()
        );
        return new ResponseEntity<>(createdSeat, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        List<Seat> seats = seatService.getAllSeats();
        return new ResponseEntity<>(seats, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seat> getSeatById(@PathVariable UUID id) {
        Seat seat = seatService.getSeatById(id);
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seat> updateSeat(@PathVariable UUID id, @RequestBody @Valid UpdateSeatRequest seatRequest) {
        Seat seat = seatService.updateSeat(
                id,
                Optional.ofNullable(seatRequest.roomId()),
                Optional.ofNullable(seatRequest.rowLabel()),
                Optional.ofNullable(seatRequest.colNumber()),
                Optional.ofNullable(seatRequest.label())
        );
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(@PathVariable UUID id) {
        seatService.deleteSeat(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}