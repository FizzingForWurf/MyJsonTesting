package itrans.myjsontesting;

public class Buses {

    private int busNumber;
    private int nextBusTime;

    public Buses(){

    }

    public Buses(int busNumber,  int nextBusTime){
        this.busNumber = busNumber;
        this.nextBusTime = nextBusTime;
    }

    public int getBusNumber(){
        return busNumber;
    }

    public void setBusNumber(int busNumber){
        this.busNumber = busNumber;
    }

    public int getNextBusTime() {
        return nextBusTime;
    }

    public void setNextBusTime(int nextBusTime) {
        this.nextBusTime = nextBusTime;
    }

    @Override
    public String toString(){
        return "Bus no. " + busNumber + "\n" +
                "Bus arrival timing " + nextBusTime + "\n";

    }
}
