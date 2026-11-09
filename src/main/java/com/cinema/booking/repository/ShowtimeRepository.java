package com.cinema.booking.repository;

import com.cinema.booking.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {
    // Hiện tại chỉ cần các hàm CRUD cơ bản từ JpaRepository là đủ để Controller chạy được
}