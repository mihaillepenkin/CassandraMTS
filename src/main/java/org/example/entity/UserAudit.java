package org.example.entity;

import  org.example.TypesOfEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;


@Setter
@Getter
@AllArgsConstructor
@Table(value = "user_audit")
public class UserAudit {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID id;

    @PrimaryKeyColumn(name = "event_time", ordinal = 0, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant eventTime;

    @Column(value = "event_type")
    private TypesOfEvent eventType;

}