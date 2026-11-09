package com.cinema.booking.controller;

import com.cinema.booking.model.Movie;
import com.cinema.booking.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/movies") // ← prefix /admin/movies cho tất cả
public class AdminMovieController {

    @Autowired
    private MovieRepository movieRepository;

    // Danh sách
    @GetMapping
    public String listMovies(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        return "admin/movie/list";
    }

    // Form thêm
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("movie", new Movie());
        return "admin/movie/form";
    }

    // Form sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable @NonNull Integer id, Model model) {
        model.addAttribute("movie", movieRepository.findById(id).orElse(new Movie()));
        return "admin/movie/form";
    }

    // Lưu (thêm + sửa)
    @PostMapping("/save")
    public String saveMovie(@ModelAttribute @NonNull Movie movie) {
        movieRepository.save(movie);
        return "redirect:/admin/movies";
    }

    // Xóa
    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable @NonNull Integer id) {
        movieRepository.deleteById(id);
        return "redirect:/admin/movies";
    }

    // Tìm kiếm
    @GetMapping("/search")
    public String searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            Model model) {

        List<Movie> movies = movieRepository.findAll().stream()
                .filter(m -> title == null || title.isEmpty() ||
                        m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(m -> status == null || status.isEmpty() ||
                        m.getStatus().name().equals(status))
                .collect(Collectors.toList());

        model.addAttribute("movies", movies);
        return "admin/movie/list";
    }
}