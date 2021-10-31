package andrasborsos;

public class StockData {

    private String ticker;
    private String name;
    private double priceNow = -1.00;
    private double changePercent = 0.00;
    private double fiftyDayAvg = -1.00;
    private double volumeTenDays = -1.00;
    private double volumePreviousDay = -1.00;

    public String getTicker() {
        return ticker;
    }
    public String getName() {
        return name;
    }
    public double getPriceNow() {
        return priceNow;
    }
    public double getChangePercent() {
        return changePercent;
    }
    public double getFiftyDayAvg() {
        return fiftyDayAvg;
    }
    public double getVolumeTenDays() {
        return volumeTenDays;
    }
    public double getVolumePreviousDay() {
        return volumePreviousDay;
    }

    public void setTicker(String ticker){
        this.ticker=ticker;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setPriceNow(double priceNow){
        this.priceNow=priceNow;
    }
    public void setChangePercent(double changePercent){
        this.changePercent=changePercent;
    }
    public void setFiftyDayAvg (double fiftyDayAvg){
        this.fiftyDayAvg=fiftyDayAvg;
    }
    public void setVolumeTenDays(double volumeTenDays){
        this.volumeTenDays=volumeTenDays;
    }
    public void setVolumePreviousDay(double volumePreviousDay){
        this.volumePreviousDay=volumePreviousDay;
    }
}
