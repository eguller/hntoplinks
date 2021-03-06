package utils;

import java.util.concurrent.TimeUnit;

public class StopWatch {
	long start;
	long diff = -1;
	long stop = -1;
	private StopWatch(){}
	public static StopWatch start(){
		StopWatch sw = new StopWatch();
		sw.start = System.nanoTime();
		return sw;
	}
	
	public long stop(){
		stop = System.nanoTime();
		diff = stop - start;
		return diff;
	}
	
	public long nanos(){
		return diff;
	}
	
	public long millis(){
		return TimeUnit.NANOSECONDS.toMillis(diff);
	}
	
	public long seconds(){
		return TimeUnit.NANOSECONDS.toSeconds(diff);
	}
	
	public long minutes(){
		return TimeUnit.NANOSECONDS.toMinutes(diff);
	}
	
	public double millisDbl(){
		return 0.0;
	}
	
	public double secondsDbl(){
		return 0.0;
	}
	
	public double minutesDbl(){
		return 0.0; 
	}
	
	public String milisTxt(){
		return null;
	}
	
	public String secondsTxt(){
		return null;
	}
	
	public String minutesTxt(){
		return null;
	}
	
	public String millisDblTxt(){
		return null;
	}
	
	public String secondsDblTxt(){
		return null;
	}
	
	public String minutesDblTxt(){
		return null;
	}
}
