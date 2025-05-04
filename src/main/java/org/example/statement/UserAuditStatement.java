package org.example.statement;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class UserAuditStatement {
    private final PreparedStatement insertStatement;
    private final PreparedStatement selectStatement;

    public UserAuditStatement(CqlSession session) {
        this.insertStatement = session.prepare(
                "INSERT INTO my_keyspace.user_audit (user_id, event_time, event_type) " +
                        "VALUES (?, ?, ?)"
        );
        this.selectStatement = session.prepare(
                "SELECT * FROM my_keyspace.user_audit WHERE user_id = ?"
        );
    }
}
