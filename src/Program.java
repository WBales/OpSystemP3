public class Program {
    private String name;
    private int startTime, duration;

    public Program(String name, int startTime, int duration){
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getStartTime() {
        return startTime;
    }
    
    public String getName() {
        return name;
    }

    public Program deepCopy(){
        return new Program(name, startTime, duration);
    }
}
