package test.collective;

import io.collective.SimpleAgedCache;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static junit.framework.TestCase.*;

public class SimpleAgedCacheTest {
    SimpleAgedCache empty = new SimpleAgedCache();
    SimpleAgedCache nonempty = new SimpleAgedCache();

    @Before
    public void before() {
        nonempty.put("aKey", "aValue", 2000);
        nonempty.put("anotherKey", "anotherValue", 4000);
    }

    @Test
    public void isEmpty() {
        assertTrue(empty.isEmpty());
        assertFalse(nonempty.isEmpty());
    }

    @Test
    public void size() {
        assertEquals(0, empty.size());
        assertEquals(2, nonempty.size());
    }

    @Test
    public void get() {
        assertNull(empty.get("aKey"));
        assertEquals("aValue", nonempty.get("aKey"));
        assertEquals("anotherValue", nonempty.get("anotherKey"));
    }

    @Test
    public void getExpired() {
        // create some test clock
        TestClock clock = new TestClock();

        // create class being tested with test clock
        SimpleAgedCache expired = new SimpleAgedCache(clock);

        // adding two entries
        expired.put("aKey", "aValue", 2000);
        expired.put("anotherKey", "anotherValue", 4000);

        // set clock to 3 seconds ahead
        clock.offset(Duration.ofMillis(3000));

        assertEquals(1, expired.size());
        assertEquals("anotherValue", expired.get("anotherKey"));
    }

    static class TestClock extends Clock {
        Duration offset = Duration.ZERO;

        @Override
        public ZoneId getZone() {
            return Clock.systemDefaultZone().getZone();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.offset(Clock.system(zone), offset);
        }

        @Override
        public Instant instant() {
            return Clock.offset(Clock.systemDefaultZone(), offset).instant();
        }

        public void offset(Duration offset) {
            this.offset = offset;
        }
    }
}