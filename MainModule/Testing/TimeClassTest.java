import static org.junit.jupiter.api.Assertions.assertEquals;

import Model.TimeClass;
import org.junit.jupiter.api.Test;

public class TimeClassTest{

    @Test
    public void totalSecondsToTimeConverter() {
        TimeClass timeClass = new TimeClass(235);

        assertEquals(0, timeClass.getHours(), "Hours is incorrect");
        assertEquals(3, timeClass.getMinutes(), "Minutes is incorrect");
        assertEquals(55, timeClass.getSeconds(), "Seconds is incorrect");

        TimeClass secondTimeClass = new TimeClass(60);

        assertEquals(0, secondTimeClass.getHours(), "Hours is incorrect");
        assertEquals(1, secondTimeClass.getMinutes(), "Minutes is incorrect");
        assertEquals(0, secondTimeClass.getSeconds(), "Seconds is incorrect");
    }
}

