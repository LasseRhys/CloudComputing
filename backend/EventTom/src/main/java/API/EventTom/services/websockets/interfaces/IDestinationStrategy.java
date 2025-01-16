package API.EventTom.services.websockets.interfaces;

public interface IDestinationStrategy {
    String buildDestination(String type, Object... params);
}