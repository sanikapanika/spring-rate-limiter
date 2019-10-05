package com.sanikapanika.ratelimiter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when method reaches {@link Throttling} configuration limit.
 */
@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS, reason = "Too many requests")
public class ThrottlingException extends RuntimeException {
}
