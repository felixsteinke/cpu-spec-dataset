package cpu.spec.dataset.api.exception;

import lombok.Getter;

import java.util.List;

public class NonUniqueResultException extends Exception{
    @Getter
    private final List<String> results;

    public NonUniqueResultException(List<String> results) {
        super("Non unique result of the request.");
        this.results = results;
    }
}
