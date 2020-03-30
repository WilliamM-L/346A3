import java.util.concurrent.locks.Condition;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	int numPhil;
	//tag urself
	enum State {THINKING, EATING, TALKING, HUNGRY};
	State[] philState;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		//CAMIL: We don't necessarily need to keep track of chopsticks. See the word doc.
		numPhil = piNumberOfPhilosophers;
		philState = new State[numPhil];
		for (int i=0; i<numPhil; i++) {
			philState[i] = State.THINKING;
		}
		
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// ...
		//CAMIL: Have to add a check for tryEating at the beginning of this method.
		philState[piTID] = State.HUNGRY;
		//Every one around me is eating, then I have to wait
		tryEating(piTID);
		if (philState[piTID] != State.EATING) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println(e);
			}
		}
		
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		// ...
		philState[piTID] = State.THINKING;
		tryEating(((piTID)+(numPhil-1))%5);
		tryEating((piTID+1)%5);
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(final int piTID)
	{
		//Extremely unsure about the logic but here goes:
		//separating it in 2 for loops ensures that all philosophers are checked, no matter
		//which philosopher does it first.
		//Please verify this!!!!!
		//It probably isn't right because the when you wait,
		//once the waiting is done you go back directly to the line after
		if (philState[piTID] != State.EATING) {
			for (int i=0; i<piTID; i++) {
				if (philState[i]==State.TALKING) {
					try {
						wait();
					} catch (InterruptedException e) {
						System.err.println(e);
					}
				}
			}
			for (int j=piTID; j<numPhil; j++) {
				if (philState[j]==State.TALKING) {
					try {
						wait();
					} catch (InterruptedException e) {
						System.err.println(e);
					}
				}
			}
			philState[piTID] = State.TALKING;
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		//This needs extra logic to make sure we're letting every other thread try to talk
		philState[piTID] = State.THINKING;
	}
	//CAMIL: I'm the one who added this method.
	public synchronized void tryEating(final int piTID) {
		//
		if ((philState[(piTID +(numPhil-1)) % numPhil]!=State.EATING)
			&& (philState[piTID]==State.HUNGRY)
			&& (philState[(piTID+1)%numPhil]!=State.EATING)
			) {
			
			philState[piTID] = State.EATING;
			notifyAll();
		}
	}
}

// EOF
