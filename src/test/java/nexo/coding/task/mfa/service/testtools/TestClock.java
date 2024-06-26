package nexo.coding.task.mfa.service.testtools;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TestClock {

    private static Clock clock = Clock.systemDefaultZone();

    public static void setFixedClock(Instant instant, ZoneId zone) {
        clock = Clock.fixed(instant, zone);
    }

    public static void resetClock() {
        clock = Clock.systemDefaultZone();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}