package SchroldOthello;

import java.util.TimerTask;

public class InterruptTask extends TimerTask {

	@Override
	public void run() {
		Board1d.printComment("(****>timeup)");
		Board1d.timeUP = true;
  		Board1d.timer.cancel();
	}

}
