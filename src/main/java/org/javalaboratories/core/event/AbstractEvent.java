package org.javalaboratories.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.javalaboratories.util.Generics;

import java.util.EventObject;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
public abstract class AbstractEvent<T extends EventSource> extends EventObject implements Event<T> {

    @EqualsAndHashCode.Include
    private final String eventId;

    public AbstractEvent(T source) {
        super(source);
        this.eventId = String.format("{event-id-%s}",this.getClass().getSimpleName().toLowerCase());
    }

    @ToString.Include
    public T getSource() {
        return Generics.unchecked(source);
    }
}
