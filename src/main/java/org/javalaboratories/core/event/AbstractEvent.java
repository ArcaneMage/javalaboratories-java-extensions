package org.javalaboratories.core.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public abstract class AbstractEvent<T extends EventSource> implements Event<T> {

    @EqualsAndHashCode.Include
    private final String eventId;

    @ToString.Include
    private final T source;

    public AbstractEvent(T source) {
        this.eventId = String.format("{event-id-%s}",this.getClass().getSimpleName().toLowerCase());
        this.source = source;
    }
}
