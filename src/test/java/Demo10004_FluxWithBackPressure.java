import java.util.Random;

import org.junit.Test;
import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class Demo10004_FluxWithBackPressure {
	
	/**
	 * To work with back pressure we need to use the request() method on the subscription object.
	 * 4th parameter of the subscribe method is consumer that will do 
	 * something at time of subscription.It is used to request initial number of elements.
	 *
	 * Notice onComplete was not called as this Flux produces 5 elements
	 * where as we requested 3 and out of total on 3 are getting consumed
	 * 
	 * */
    @Test
    public void fluxRequest() {
        Flux.range(112, 10)
                .log()
                .subscribe(null,    // normal consumer
                           null,    // error consumer
                           null,    // complete consumer
                           subscription -> subscription.request(3)// to place the request for bounded number of elements 
                );
    }
    
    /**It can be seen that during the subscribe() operation, BaseSubscriber instance has been passed.
     * This has 2 methods:hookOnSubscribe(), and hookOnNext().
     * Initially, first three elements will be requested by hookOnSubscribe().
     * Then hookOnNext() will be executed for every onNext().
     * If the count has reached 3 then it will request the random number of elements to process.
     * Instead of requesting random number of elements, a logic can be written to determine the system state and then later 
     */

	@Test
	public void fluxCustomSubscriber() {
		Flux.range(1, 10).log().subscribe(new BaseSubscriber<Integer>() {
			int elementsToProcess = 3;
			int counter = 0;
			//Initial Elements
			public void hookOnSubscribe(Subscription subscription) {
				System.out.println("Subscribed!");
				request(elementsToProcess);
			}
			//to fetch the next elements after first fetch
			public void hookOnNext(Integer value) {
				System.out.println("From hookOnNext");
				counter++;
				if (counter == elementsToProcess) {
					counter = 0;
					Random r = new Random();
					//sd/d/sdsd
					elementsToProcess = r.ints(1, 4).findFirst().getAsInt();
					request(elementsToProcess);
				}
			}
		});
	}
	
	/** this method will call the next method with same number of elements
	every time until the Flux completes*/
	@Test
	public void fluxLimitRate() {
		Flux.range(1, 5).log().limitRate(3).subscribe();
	}
}
