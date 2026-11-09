package com.cinema.booking.controller;

import com.cinema.booking.model.Booking;
import com.cinema.booking.model.Movie;
import com.cinema.booking.model.User;
import com.cinema.booking.repository.BookingRepository;
import com.cinema.booking.repository.MovieRepository;
import com.cinema.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class BookingController {

    @Autowired private MovieRepository movieRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/booking")
    public String booking(@RequestParam(required = false) Integer movieId,
                          Model model) {
        Movie movie = null;
        if (movieId != null) {
            movie = movieRepository.findById(movieId).orElse(null);
        }
        if (movie == null) {
            movie = movieRepository.findAll().stream().findFirst().orElse(null);
        }
        if (movie == null) {
            movie = new Movie();
            movie.setTitle("Avengers: Doomsday");
            movie.setDuration(149);
            movie.setRated("T18");
            movie.setPosterUrl("");
            movie.setReleaseDate(LocalDate.of(2026, 5, 1));
        }
        model.addAttribute("movie", movie);
        return "booking";
    }

    // Endpoint lưu booking khi xác nhận thanh toán
    @PostMapping("/booking/confirm")
    public String confirmBooking(
            Authentication auth,
            @RequestParam Integer movieId,
            @RequestParam String bookingCode,
            @RequestParam String showDate,
            @RequestParam String showTime,
            @RequestParam String hallName,
            @RequestParam String seats,
            @RequestParam BigDecimal totalAmount,
            @RequestParam(required = false, defaultValue = "0") BigDecimal discountAmount,
            @RequestParam(required = false, defaultValue = "MoMo") String paymentMethod,
            Model model) {

        if (auth == null) return "redirect:/login";
        User user = userRepository.findByEmail(auth.getName()).orElse(null);
        if (user == null) return "redirect:/login";

        Booking booking = new Booking();
        booking.setUser(user);
        
        // SỬA LỖI TẠI ĐÂY: Dùng setShowtimeId thay vì setShowDate(1)
        com.cinema.booking.model.Showtime showtime = new com.cinema.booking.model.Showtime();
        showtime.setId(1); // Gán ID tạm thời là 1
        booking.setShowtime(showtime);; 
        
        booking.setBookingCode(bookingCode);
        booking.setSubtotal(totalAmount.add(discountAmount));
        booking.setDiscountAmount(discountAmount);
        booking.setTotalAmount(totalAmount);
        booking.setPaymentMethod(paymentMethod);
        booking.setStatus(Booking.BookingStatus.confirmed);
        booking.setPaymentStatus(Booking.PaymentStatus.paid);

        // Lưu thông tin hiển thị vào transient fields (không cần DB)
        booking.setMovieTitle(movieRepository.findById(movieId)
                .map(Movie::getTitle).orElse("Phim"));
        booking.setShowDate(showDate);
        booking.setShowTime(showTime);
        booking.setHallName(hallName);
        booking.setSeats(seats);

        bookingRepository.save(booking);

        model.addAttribute("booking", booking);
        return "redirect:/booking/history";
    }
}