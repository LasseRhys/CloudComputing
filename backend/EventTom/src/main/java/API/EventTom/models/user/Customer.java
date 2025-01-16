package API.EventTom.models.user;

import API.EventTom.models.event.Ticket;
import API.EventTom.models.event.Voucher;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Customer {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    @PreUpdate
    private void validateUserType() {
        if (user.getUserType() != UserType.CUSTOMER) {
            throw new IllegalStateException("User must be of type CUSTOMER");
        }
    }

    @Column(name = "customer_number", unique = true, nullable = false)
    private String customerNumber;

    @OneToMany(mappedBy = "customer")
    private Set<Voucher> vouchers = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

}