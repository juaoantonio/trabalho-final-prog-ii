package br.com.joaobarbosa.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.joaobarbosa.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
}