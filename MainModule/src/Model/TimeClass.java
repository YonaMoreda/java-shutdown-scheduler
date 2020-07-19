package Model;

public class TimeClass {
    private int seconds;
    private int minutes;
    private int hours;



    public TimeClass(int totalSeconds) {
//        TODO:: CONTINUE FROM HERE
        this.hours = totalSeconds;
        this.minutes = totalSeconds;
        this.seconds = totalSeconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
