package springdatajpa.datajpa_1.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class BaseTimeEntity {

    @CreatedDate //auditing
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate //auditing
    private LocalDateTime lastModifiedDate;

}
