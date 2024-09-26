package com.odeon.odeon_airlines.exception;

public class FlightConflictException extends RuntimeException {
  public FlightConflictException(String message) {
    super(message);
  }
}