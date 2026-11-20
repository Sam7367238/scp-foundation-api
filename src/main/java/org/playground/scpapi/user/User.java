package org.playground.scpapi.user;

import jakarta.persistence.*;
import lombok.*;
import org.playground.scpapi.department.Department;
import org.playground.scpapi.userDepartment.UserDepartment;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "clearance_level")
    private byte clearanceLevel;

//    @Column(name = "password")
//    private String password;

    @Column(name = "created")
    private LocalDateTime created;

    @OneToMany(mappedBy = "user")
    private Set<Address> addresses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Department> departments = new LinkedHashSet<>();

    @OneToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany(mappedBy = "user")
    private Set<UserDepartment> userDepartments = new LinkedHashSet<>();
}