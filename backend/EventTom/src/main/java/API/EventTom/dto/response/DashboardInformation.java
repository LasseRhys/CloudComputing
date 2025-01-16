package API.EventTom.dto.response;

public record DashboardInformation(
        VoucherDashboardDTO voucherDashboard,
        EventDashboardDTO eventDashboard,
        NextEventDTO nextEvent
) {}