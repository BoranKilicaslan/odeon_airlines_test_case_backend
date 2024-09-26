package com.odeon.odeon_airlines.repository;

import com.odeon.odeon_airlines.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f WHERE f.departureCity = :location AND " +
            "((f.departureTime BETWEEN :startTime AND :endTime) OR " +
            "(f.arrivalTime BETWEEN :startTime AND :endTime))")
    List<Flight> findFlightsByLocationAndTime(@Param("location") String location,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    List<Flight> findByArrivalCity(String arrivalCity);

    List<Flight> findByDepartureCity(String departureCity);

    List<Flight> findByDepartureCityAndArrivalCity(String departureCity, String arrivalCity);


    @Query("SELECT f FROM Flight f WHERE f.departureCity = :city AND " +
            "f.departureTime = :flightTimePlus30")
    List<Flight> findFlightsByDepartureCityAndTimePlus30(@Param("city") String city,
                                                         @Param("flightTimePlus30") LocalDateTime flightTimePlus30);

}
