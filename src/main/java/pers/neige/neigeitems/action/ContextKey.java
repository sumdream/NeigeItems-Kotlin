package pers.neige.neigeitems.action;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Getter
@ToString
@AllArgsConstructor
public final class ContextKey<T> {
    private final Collection<String> names;

    public ContextKey(String... names) {
        this.names = new ArrayList<>();
        this.names.addAll(Arrays.asList(names));
    }
}
