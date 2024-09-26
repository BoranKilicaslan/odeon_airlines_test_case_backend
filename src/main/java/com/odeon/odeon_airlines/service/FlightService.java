package com.odeon.odeon_airlines.service;

import com.odeon.odeon_airlines.exception.FlightConflictException;
import com.odeon.odeon_airlines.exception.FlightNotFoundException;
import com.odeon.odeon_airlines.model.Flight;
import com.odeon.odeon_airlines.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flightRepository;

    public boolean createFlight(Flight flight) {
        if (!isFlightScheduleAvailable(flight)) {
            return false; // Uçuş zamanları çakışıyor
        }
        try {
            flightRepository.save(flight);
            return true; // Uçuş yaratma işlemi başarılı
        } catch (FlightConflictException | IllegalArgumentException e) {
            return false; // Uçuş yaratma işlemi başarısız
        }
    }

    public boolean isFlightScheduleAvailable(Flight flight) {
        // Hem kalkış hem de varış noktası için çakışma kontrolü
        boolean departureConflict = hasConflictingFlights(flight.getDepartureCity(), flight.getDepartureTime());
        boolean arrivalConflict = hasConflictingFlights(flight.getArrivalCity(), flight.getArrivalTime());

        return !(departureConflict || arrivalConflict); // Eğer hiç çakışma yoksa true döner
    }

    public Flight updateFlight(Long flightId, Flight updatedFlight) {
        Flight existingFlight = flightRepository.findById(flightId)
                .orElseThrow(() -> new FlightNotFoundException("Uçuş bulunamadı."));

        if (!isFlightScheduleAvailable(updatedFlight)) {
            throw new FlightConflictException("Uçuş zamanları çakışıyor."); // Çakışma varsa istisna fırlat
        }

        updatedFlight.setId(existingFlight.getId());
        return flightRepository.save(updatedFlight);
    }

    private void validateFlightSchedule(Flight flight) {
        // Hem kalkış hem de varış için çakışmaları kontrol et
        if (!isFlightScheduleAvailable(flight)) {
            throw new FlightConflictException("Uçuş zamanları çakışıyor.");
        }
    }

    private boolean hasConflictingFlights(String location, LocalDateTime flightTime) {
        List<Flight> conflictingFlights = flightRepository.findFlightsByLocationAndTime(
                location, flightTime.minusMinutes(30), flightTime.plusMinutes(30));
        return !conflictingFlights.isEmpty();
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightById(Long flightId) {
        return flightRepository.findById(flightId);
    }

    public void deleteFlight(Long flightId) {
        if (flightRepository.existsById(flightId)) {
            flightRepository.deleteById(flightId);
        } else {
            throw new FlightNotFoundException("Uçuş bulunamadı, silinemiyor.");
        }
    }

    public List<Flight> findFlightsByDepartureArrivalAndDate(String departure, String arrival, String flightDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dateTime;

        try {
            dateTime = LocalDateTime.parse(flightDate, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Geçersiz tarih formatı. Lütfen şu formatı kullanın: dd/MM/yyyy HH:mm:ss");
        }

        return flightRepository.findAll().stream()
                .filter(flight -> flight.getDepartureCity().equalsIgnoreCase(departure) &&
                        flight.getArrivalCity().equalsIgnoreCase(arrival) &&
                        flight.getDepartureTime().toLocalDate().equals(dateTime.toLocalDate()))
                .collect(Collectors.toList());
    }

    public List<Flight> findFlightsByDepartureCity(String departure) {
        return flightRepository.findByDepartureCity(departure);
    }

    public List<Flight> findFlightsByArrivalCity(String arrival) {
        return flightRepository.findByArrivalCity(arrival);
    }

    public boolean isScheduleValid(String city, LocalDateTime time) {
        List<Flight> conflictingFlights = flightRepository.findFlightsByLocationAndTime(
                city, time.minusMinutes(30), time.plusMinutes(30));
        return conflictingFlights.isEmpty();
    }


}
