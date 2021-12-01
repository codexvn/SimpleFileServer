package top.codexvn.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CustomRuntimeException extends RuntimeException {
    private final String  message;
}