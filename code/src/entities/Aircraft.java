package entities;

public class Aircraft implements Comparable<Aircraft> {
    public String aircraftID;
    public String aircraftType;
    public long earliestUsableTime;
    public long latestUsableTime;
    public String airport;
    public int seatNum;
    public  int inSeatNum;

    /** 用来作为排序判断的时间，对于机场可用飞机，该时间为原计划起飞时间，对于还未抵达该机场的飞机，为计划抵达时间 */
    public long plannedTime = -1;

    /** 飞机就绪时间 */
    public long readyTime = -1;
    /** 飞机当前正在执行的航班任务 */
    public Flight flight = null;

    @Override
    public int compareTo(Aircraft o) {
        if (this.plannedTime > o.plannedTime) {
            return 1;
        }
        if (this.plannedTime == o.plannedTime) {
            return 0;
        }
        return -1;
    }
}
