package org.enodeframework.eventing;

import org.enodeframework.common.exception.DomainEventInvalidException;
import org.enodeframework.messaging.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anruence@gmail.com
 */
public class DomainEventStream extends Message {
    private String commandId;
    private String aggregateRootTypeName;
    private String aggregateRootId;
    private int version;
    private List<IDomainEvent<?>> events;

    public DomainEventStream(String commandId, String aggregateRootId, String aggregateRootTypeName, Date timestamp, List<IDomainEvent<?>> events, Map<String, Object> items) {
        if (events == null || events.size() == 0) {
            throw new IllegalArgumentException("Parameter events cannot be null or empty.");
        }
        this.commandId = commandId;
        this.aggregateRootId = aggregateRootId;
        this.aggregateRootTypeName = aggregateRootTypeName;
        this.version = events.stream().findFirst().map(IDomainEvent::getVersion).orElse(0);
        this.timestamp = timestamp;
        this.events = events;
        this.items = items == null ? new HashMap<>() : items;
        this.id = aggregateRootId + "_" + version;
        int sequence = 1;
        for (IDomainEvent<?> event : events) {
            if (event.getVersion() != this.getVersion()) {
                throw new DomainEventInvalidException(String.format("Invalid domain event version, aggregateRootTypeName: %s aggregateRootId: %s expected version: %d, but was: %d",
                        aggregateRootTypeName,
                        aggregateRootId,
                        version,
                        event.getVersion()));
            }
            event.setCommandId(commandId);
            event.setAggregateRootTypeName(aggregateRootTypeName);
            event.setSequence(sequence++);
            event.setTimestamp(timestamp);
            event.mergeItems(items);
        }
    }

    public List<IDomainEvent<?>> getEvents() {
        return events;
    }

    public void setEvents(List<IDomainEvent<?>> events) {
        this.events = events;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getAggregateRootTypeName() {
        return aggregateRootTypeName;
    }

    public void setAggregateRootTypeName(String aggregateRootTypeName) {
        this.aggregateRootTypeName = aggregateRootTypeName;
    }

    public String getAggregateRootId() {
        return aggregateRootId;
    }

    public void setAggregateRootId(String aggregateRootId) {
        this.aggregateRootId = aggregateRootId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<IDomainEvent<?>> events() {
        return events;
    }
}
