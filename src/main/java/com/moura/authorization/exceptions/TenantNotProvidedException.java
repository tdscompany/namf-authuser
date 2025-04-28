package com.moura.authorization.exceptions;

public class TenantNotProvidedException extends RuntimeException {
  public TenantNotProvidedException(String message) {
    super(message);
  }
}
