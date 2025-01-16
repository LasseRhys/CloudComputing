package API.EventTom.models.user;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Roles name;

    public static Role from(Roles role) {
        Role newRole = new Role();
        newRole.setName(role);
        return newRole;
    }

    public String getAuthority() {
        return "ROLE_" + name.getAuthority();
    }
}