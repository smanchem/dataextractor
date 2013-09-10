package datax;

public class SystemState {
	int time;
	String event, component, effect, phase, user;
	
	public SystemState() {
		time = 0;
		event = null;
		component = null;
		effect = null;
		phase = null;
	}
	
	public SystemState(String time, String component, String effect, String phase, String event, String user) {
		this.time = Integer.parseInt(time);
		this.event = event;
		this.component = component;
		this.effect = effect;
		this.phase = phase;
		this.user = user;
	}
}
