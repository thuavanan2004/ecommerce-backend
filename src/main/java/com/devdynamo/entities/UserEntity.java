package com.devdynamo.entities;

import com.devdynamo.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String full_name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String phone;

    @Column
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('admin', 'customer') DEFAULT 'customer'")
    private RoleEnum role = RoleEnum.CUSTOMER;

    @Column
    @CreationTimestamp
    private Timestamp created_at;

    @Column
    @UpdateTimestamp
    private Timestamp updatedAt;
}
