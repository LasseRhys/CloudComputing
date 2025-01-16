package API.EventTom.models.user;

import API.EventTom.config.security.EmployeeOnly;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
public enum Roles {
    @EmployeeOnly
    EVENT_MANAGER("Event Manager"),
    @EmployeeOnly
    EVENT_CREATOR("Event Creator"),
    @EmployeeOnly
    SALES_REP("Sales Representative"),
    @EmployeeOnly
    ADMINISTRATOR("Administrator"),

    NONE("No Role");

    private final String displayName;

    Roles(String displayName) {
        this.displayName = displayName;
    }

    public String getAuthority() {
        return this.name();
    }

    public static boolean isEmployeeOnly(Roles role) {
        try {
            Field field = Roles.class.getField(role.name());
            return field.isAnnotationPresent(EmployeeOnly.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}