package com.example.demo.Model;

import com.example.demo.Repository.ProviderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String address;
    @Column
    private Date birth;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Notification> notifications;

    @Column
    @Enumerated(EnumType.STRING)
    private ProviderType provider;

    @Transient
    @JsonProperty("isPasswordNull")
    public boolean isPasswordNull() {
        return this.password == null;
    }

    @Column
    @JsonIgnore
    private String password;

    @Column
    private boolean isActive;

    @Column(nullable = false)
    private boolean isVerified;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Booking> bookings;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Token> tokens;

    @OneToOne(mappedBy = "customer")
    @JsonIgnore
    private ForgotPassword forgotPassword;

    @ManyToOne
    @JoinColumn(name = "role")
    private Role role;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.getRole().getName().toUpperCase()));
    }
    @Override
    public String getUsername() {
        return this.getPhone();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return this.isActive;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
