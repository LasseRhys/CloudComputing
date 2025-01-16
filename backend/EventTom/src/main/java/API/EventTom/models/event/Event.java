package API.EventTom.models.event;

import API.EventTom.models.user.Employee;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "date", nullable = false)
    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime dateOfEvent;

    @Column(name = "location", nullable = false)
    @NotBlank(message = "Location is required")
    private String location;

    @Column(name = "max_total_tickets")
    @Positive(message = "Maximum total tickets must be positive")
    private int maxTotalTickets;

    @Column(name = "threshold_value")
    @PositiveOrZero(message = "Threshold value must be zero or positive")
    private int thresholdValue;

    @Column(name = "base_price")
    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Employee creator;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_managers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> managers = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();

    @Transient
    public boolean isThresholdReached() {
        return tickets.size() >= thresholdValue;
    }

    @Transient
    public int getAvailableTickets() {
        return maxTotalTickets - tickets.size();
    }

    @Transient
    public int getTotalSoldTickets() {
        return tickets.size();
    }

}



