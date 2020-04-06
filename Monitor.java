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
		//All philosophers are initially thinking
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
	public void pickUp(final int piTID) throws InterruptedException
	{
		//need this var since the waiting occurs outside of the synched block
		//boolean canEat = false;
		philState[piTID] = State.HUNGRY;
		//Every one around me is eating, then I have to wait
		while(philState[piTID] == State.HUNGRY) 
		{
			synchronized (this) {
				if ((philState[(piTID +(numPhil-1)) % numPhil]!=State.EATING)
						&& (philState[piTID]==State.HUNGRY)
						&& (philState[(piTID+1)%numPhil]!=State.EATING)
						) 
				{
					philState[piTID] = State.EATING;
					//canEat = true;
				}
				else
				{
					//canEat = false;
					this.wait();
				}
			}
			
//			if(!canEat) 
//			{
//				this.wait();
//			}
			
		}
		
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		//Going back to thinking and letting everyone know
		philState[piTID] = State.THINKING;
		this.notifyAll();
	}

	/**
	 * Only one philosopher at a time is allowed to talk
	 * (while she is not eating).
	 */
	public void requestTalk(final int piTID) throws InterruptedException
	{
		//waiting if someone is already talking, talking otherwise
		if (philState[piTID] != State.EATING) {
			while (philState[piTID] != State.TALKING) {
				synchronized (this)
				{
					for (int i=0; i < philState.length; i++) 
					{
						
							if (philState[i]==State.TALKING) 
							{
								wait();
							} else
							{
								philState[piTID] = State.TALKING;
								return;
							}
						
					}
				}
			}
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		//Going back to thinking and letting everyone know
		philState[piTID] = State.THINKING;
		this.notifyAll();
	}

}

// EOF
