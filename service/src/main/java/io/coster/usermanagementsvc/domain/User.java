package io.coster.usermanagementsvc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    private String emailAddr;

    private String firstName;
    private String lastName;
    private String password;

    @Basic
    private LocalDateTime registered;

    @Basic
    private LocalDateTime lastActive;

}
