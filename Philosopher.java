import common.BaseThread;

/**
 * Class Philosopher.
 * Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread
{
	/**
	 * Max time an action can take (in milliseconds)
	 */
	public static final long TIME_TO_WASTE = 1000;

	/**
	 * The act of eating.
	 * - Print the fact that a given phil (their TID) has started eating.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - Then print that they are done eating.
	 */
	public void eat()
	{
		try
		{
			// W: code in try block, not sure why the hints tell us to yield both before and after sleeping
			// since sleeping already allows other threads to take cpu time
			System.out.println(String.format("Philosopher %d has started eating.", this.iTID));
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE));
			yield();
			System.out.println(String.format("Philosopher %d is done eating.", this.iTID));
			
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.eat():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of thinking.
	 * - Print the fact that a given phil (their TID) has started thinking.
	 * - yield
	 * - Then sleep() for a random interval.
	 * - yield
	 * - The print that they are done thinking.
	 */
	public void think()
	{
		// W: using the try/catch block because sleep can throw an InterruptedException, which occurss when another thread interrupts this one
		try
		{
			System.out.println(String.format("Philosopher %d has started thinking.", this.iTID));
			yield();
			sleep((long)(Math.random() * TIME_TO_WASTE));
			yield();
			System.out.println(String.format("Philosopher %d is done thinking.", this.iTID));
			
		}
		catch(InterruptedException e)
		{
			System.err.println("Philosopher.think():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of talking.
	 * - Print the fact that a given phil (their TID) has started talking.
	 * - yield
	 * - Say something brilliant at random
	 * - yield
	 * - The print that they are done talking.
	 */
	public void talk()
	{
		System.out.println(String.format("Philosopher %d has started talking.", this.iTID));
		yield();
		saySomething();
		yield();
		System.out.println(String.format("Philosopher %d is done talking.", this.iTID));
	}

	/**
	 * Overrides Thread.run()
	 */
	public void run()
	{
		try 
		{
			for(int i = 0; i < DiningPhilosophers.DINING_STEPS; i++)
			{
				//TID starts at 1, so the corresponding array index is TID-1
				DiningPhilosophers.soMonitor.pickUp(getTID()-1);

				eat();

				DiningPhilosophers.soMonitor.putDown(getTID()-1);

				think();

				/*
				 * TODO:
				 * A decision is made at random whether this particular
				 * philosopher is about to speak.
				 */
				//CAMIL: Need to put a condition (a random number or something, and compare it with a number. If it is matching):
				//CAMIL: requestTalk
				//CAMIL: talk
				//CAMIL: endTalk
				//W: if there are 6 philosophers, each one has 2/6 = 1/3 chance to talk
				if(Math.random() < 2.0/DiningPhilosophers.soMonitor.numPhil)
				{
					DiningPhilosophers.soMonitor.requestTalk(this.iTID-1);
					talk();
					DiningPhilosophers.soMonitor.endTalk(this.iTID-1);
				}

				yield();
				//System.out.println(String.format("~~~~~~~~~~~P%d: Dining step %d is over.~~~~~~~~~~~", i,this.iTID));
			}
		} catch(InterruptedException e)
		{
			System.out.println(e);
		}
		
	} // run()

	/**
	 * Prints out a phrase from the array of phrases at random.
	 * Feel free to add your own phrases.
	 */
	public void saySomething()
	{
		String[] astrPhrases =
		{
			"Eh, it's not easy to be a philosopher: eat, think, talk, eat...",
			"You know, true is false and false is true if you think of it",
			"2 + 2 = 5 for extremely large values of 2...",
			"If thee cannot speak, thee must be silent",
			"My number is " + getTID() + ""
		};

		System.out.println
		(
			"Philosopher " + getTID() + " says: " +
			astrPhrases[(int)(Math.random() * astrPhrases.length)]
		);
	}
}

// EOF
