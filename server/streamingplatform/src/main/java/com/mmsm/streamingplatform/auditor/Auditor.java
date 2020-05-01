package com.mmsm.streamingplatform.auditor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Embeddable
@EntityListeners(AuditingEntityListener.class)
public class Auditor {

    @CreatedBy
    @Column(name = "created_by_id")
    private String createdById;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by_id")
    private String lastModifiedById;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    public static Auditor of() {
        return new Auditor();
    }
}
