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
		//W: True, we are simply signaling the others
		
		//W: All philosophers are initially thinking
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
	 * Grants request (returns) to eat when both chopsticks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// ...
		//CAMIL: Have to add a check for tryEating at the beginning of this method.
		philState[piTID] = State.HUNGRY;
		//Every one around me is eating, then I have to wait
		//W: I think the method should be called after wait, since it blocks at wait and is released when a philo is done eating
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
		//W: what's the constant for? 5
		// I think the notifyall call should go here instead of in tryEating
		philState[piTID] = State.THINKING;
		//W: why are we forcing the philos next to him to eat?
		tryEating(((piTID)+(numPhil-1))%5);
		tryEating((piTID+1)%5);
	}

	/**
	 * Only one philopher at a time is allowed to talk
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(final int piTID)
	{
		//Extremely unsure about the logic but here goes:
		//separating it in 2 for loops ensures that all philosophers are checked, no matter
		//which philosopher does it first.
		//Please verify this!!!!!
		//It probably isn't right because then when you wait,
		//once the waiting is done you go back directly to the line after
		
		//W: We only talk after eating right? so why check if we eat?
		//So the wait and notifyall are already used for eating, so I think we might have to use something else.
		//Why two loops? Just check that every single philo is not talking
		if (philState[piTID] != State.EATING)
		{
			for (int i=0; i<piTID; i++) 
			{
				if (philState[i]==State.TALKING) 
				{
					try {
						wait();
					} catch (InterruptedException e) {
						System.err.println(e);
					}
				}
			}
			//W: switching to i if that's okay, when I see j I think immediately of nested loops
			for (int i=piTID; i<numPhil; i++) 
			{
				if (philState[i]==State.TALKING) 
				{
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
		//W: Why? This is letting everyone know since they all consult the state array.
		philState[piTID] = State.THINKING;
	}
	//CAMIL: I'm the one who added this method.
	public synchronized void tryEating(final int piTID) 
	{
		//W: you make the calling philo hungry before calling this, why check again?
		//W: makes sense, checking philos left and right
		if ((philState[(piTID +(numPhil-1)) % numPhil]!=State.EATING)
			&& (philState[piTID]==State.HUNGRY)
			&& (philState[(piTID+1)%numPhil]!=State.EATING)
			) 
		{
			philState[piTID] = State.EATING;
			//W: notifyAll wakes up all threads that are waiting on this object's monitor
			//so I think that should be used to let the others know that we're done eating.(should put this in putDown)
			//notify says "you may eat", not "I'm eating"
			notifyAll();
		}
	}
}

// EOF
