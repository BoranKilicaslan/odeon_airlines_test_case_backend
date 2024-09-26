package com.odeon.odeon_airlines.controller;

import com.odeon.odeon_airlines.model.Flight;
import com.odeon.odeon_airlines.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:3000") // Frontend URL'sini buraya yazın
@RequestMapping("/api/admin")
public class FlightController {

    @Autowired
    private FlightService flightService;

    // Uçuş oluşturma
    @PostMapping("/create")
    public ResponseEntity<Boolean> createFlight(@RequestBody Flight flight) {
        System.out.println(flight.getArrivalCity());
        System.out.println(flight.getDepartureCity());
        System.out.println(flight.getDepartureTime());
        System.out.println(flight.getArrivalTime());

        // Uçuş zamanlarının çakışıp çakışmadığını kontrol et
        if (!flightService.isFlightScheduleAvailable(flight)) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        // Uçuşu yaratmayı dene
        try {
            flightService.createFlight(flight);
            return new ResponseEntity<>(true, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Uçuş güncelleme
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateFlight(@PathVariable Long id, @RequestBody Flight flight) {
        try {
            Flight updatedFlight = flightService.updateFlight(id, flight);
            return new ResponseEntity<>(updatedFlight, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Uçuş güncellerken zaman çakışıyorsa, hata mesajını döndür
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace(); // Hata yığın izini konsola yazdır
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Hata durumunda
        }
    }

    // Tüm uçuşları alma
    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return flights.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(flights, HttpStatus.OK);
    }

    // Uçuş ID'sine göre uçuş alma
    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        Optional<Flight> optionalFlight = flightService.getFlightById(id);
        if (optionalFlight.isPresent()) {
            return new ResponseEntity<>(optionalFlight.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Uçuş silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Uçuş başarıyla silindi
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Uçuş bulunamadı
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Diğer hatalar
        }
    }
    // Uçuş zamanlarının doğruluğunu kontrol etme
    @GetMapping("/flights/validate")
    public ResponseEntity<Boolean> validateFlight(@RequestParam String city, @RequestParam String time) {
        // Define a DateTimeFormatter to parse the input time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // Parse the time string into a LocalDateTime object
        LocalDateTime flightTime;
        try {
            flightTime = LocalDateTime.parse(time, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Geçersiz tarih formatı. Lütfen şu formatı kullanın: yyyy-MM-dd HH:mm");
        }

        // Call the service method to validate the flight schedule
        boolean isValid = flightService.isScheduleValid(city, flightTime);
        return ResponseEntity.ok(isValid);
    }


}