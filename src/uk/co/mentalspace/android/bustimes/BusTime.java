package uk.co.mentalspace.android.bustimes;

public class BusTime {

	private String busNo = null;
	private String dest = null;
	private String estArrival = null;
	
	public BusTime(String busNumber, String destination, String estArrivalTime) {
		busNo = busNumber;
		dest = destination;
		estArrival = estArrivalTime;
	}
	
	public String getBusNumber() {
		return busNo;
	}
	
	public String getDestination() {
		return dest;
	}
	
	public String getEstimatedArrivalTime() {
		return estArrival;
	}
}
