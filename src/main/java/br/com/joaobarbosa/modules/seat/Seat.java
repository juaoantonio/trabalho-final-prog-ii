package br.com.joaobarbosa.modules.seat;

import br.com.joaobarbosa.modules.room.Room;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity(name = "seats")
@Table(name = "seats")
@Getter
@Setter
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String rowLabel;

    @Column(nullable = false)
    private Integer colNumber;

    @Column(nullable = false)
    private String label;

    public Seat(Room room, String rowLabel, Integer colNumber, String label) {
        this.room = room;
        this.rowLabel = rowLabel;
        this.colNumber = colNumber;
        this.label = label;
    }
}