import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

//https://projectreactor.io/docs/core/release/reference/index.html#schedulers
public class Demo10006Concurrency2 {
	
	/**
	 * 
	 * publishOn applies in the same way as any other operator, in the middle of the subscriber chain. 
	 * It takes signals from upstream and replays them downstream while executing the callback on a worker from 
	 * the associated Scheduler. Consequently, it affects where the subsequent operators will execute
	 * **/
	
	@Test
	public void schedulerOnPublish() throws InterruptedException{
		Scheduler s = Schedulers.newParallel("parallel-scheduler", 4); 
		final Flux<String> flux = Flux
		    .range(5, 2)
		    .log()
		    .map(element -> {
		    	int newValue=10 + element;
		    	System.out.println("Map1 NewValue " + newValue+", "+Thread.currentThread().getName());
		    	return newValue;
		    })  
		    .publishOn(s)  
		    .map(element -> {
		    	System.out.println("Map2 NewValue " + element+", "+Thread.currentThread().getName());
		    	return "Value: " + element;
		    });  
		//StartCustomThread
		new Thread(
				()-> {
						flux.log()
						.subscribe(
									element->System.out.println("From consumer of subsribe(): "+element+","+Thread.currentThread().getName())
								);		
					}
				,"StartCustomThread").start();  
		Thread.sleep(5000);
	}
	/**Description:
	 * Creates a new Scheduler backed by 4 Thread
	 * The first map runs on the StartCustomThread in 30
	 * The publishOn switches the whole sequence on a Thread picked from Scheduler
	 * The second map runs on said Thread from Scheduler
	 * This StartCustomThread is the one where the subscription happens. 
	 * The print happens on the latest execution context which is the one from publishOn. 
	 * **/
	/**
	 * Sample output: Sequence of Statments after highlighted line can change 
	 * as this execution is going to happen in multiple threads...
	 *  [DEBUG] (main) Using Console logging
		[ INFO] (StartCustomThread) | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
		[ INFO] (StartCustomThread) | onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
		[ INFO] (StartCustomThread) | request(unbounded)
		[ INFO] (StartCustomThread) | request(256)
		[ INFO] (StartCustomThread) | onNext(5)
		Map1 NewValue 15, StartCustomThread-------------<<This line>>
		[ INFO] (StartCustomThread) | onNext(6)
		Map2 NewValue 15, parallel-scheduler-1
		Map1 NewValue 16, StartCustomThread
		[ INFO] (parallel-scheduler-1) | onNext(Value: 15)
		From consumer of subsribe(): Value: 15,parallel-scheduler-1
		Map2 NewValue 16, parallel-scheduler-1
		[ INFO] (parallel-scheduler-1) | onNext(Value: 16)
		From consumer of subsribe(): Value: 16,parallel-scheduler-1
		[ INFO] (StartCustomThread) | onComplete()
		[ INFO] (parallel-scheduler-1) | onComplete()
	 * 
	 * */
	
	
	/**subscribeOn applies to the subscription process, when that backward chain is constructed. 
	no matter where you place the subscribeOn in the chain, it always affects 
	the context of the source emission*/

	@Test
	public void schedulerOnSubScribe() throws InterruptedException{
		Scheduler s = Schedulers.newParallel("parallel-scheduler", 4); 
		final Flux<String> flux = Flux
		    .range(5, 2)
		    .log()
		    .map(element -> {
		    	int newValue=10 + element;
		    	System.out.println("Map1 NewValue " + newValue+", "+Thread.currentThread().getName());
		    	return newValue;
		    })  
		    .subscribeOn(s)  
		    .map(element -> {
		    	System.out.println("Map2 NewValue " + element+", "+Thread.currentThread().getName());
		    	return "Value: " + element;
		    });  
		//StartCustomThread
		new Thread(
				()-> {
						flux.log()
						.subscribe(
									element->System.out.println("From consumer of subsribe(): "+element+","+Thread.currentThread().getName())
								);		
					}
				,"StartCustomThread").start();  
		Thread.sleep(5000);
		
	}
	/** Description:
	 * 	Creates a new Scheduler backed by 4 Thread
	 *  The first map runs on one of these 4 threads
	 *  because subscribeOn switches the whole sequence right from subscription time
	 *  The second map also runs on same thread
	 *  StartCustomThread is the one where the subscription initially happens, 
	 *  but subscribeOn immediately shifts it to one of the 4 scheduler threads...
	 * */

	/**
	 * Sample Output:
	 * 	[DEBUG] (main) Using Console logging
		[ INFO] (StartCustomThread) onSubscribe(FluxMap.MapSubscriber)
		[ INFO] (StartCustomThread) request(unbounded)
		[ INFO] (parallel-scheduler-1) | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
		[ INFO] (parallel-scheduler-1) | request(unbounded)
		[ INFO] (parallel-scheduler-1) | onNext(5)
		Map1 NewValue 15, parallel-scheduler-1
		Map2 NewValue 15, parallel-scheduler-1
		[ INFO] (parallel-scheduler-1) onNext(Value: 15)
		From consumer of subsribe(): Value: 15,parallel-scheduler-1
		[ INFO] (parallel-scheduler-1) | onNext(6)
		Map1 NewValue 16, parallel-scheduler-1
		Map2 NewValue 16, parallel-scheduler-1
		[ INFO] (parallel-scheduler-1) onNext(Value: 16)
		From consumer of subsribe(): Value: 16,parallel-scheduler-1
		[ INFO] (parallel-scheduler-1) | onComplete()
		[ INFO] (parallel-scheduler-1) onComplete()
	 * 
	 * */

	@Test
	public void schedulerOnSubScribe2() throws InterruptedException{
		Scheduler s1 = Schedulers.newParallel("parallel-scheduler-Sub", 4);
		Scheduler s2 = Schedulers.newParallel("parallel-scheduler-Pub", 4);
		final Flux<String> flux = Flux
		    .range(5, 2)
		    .log()
		    .map(element -> {
		    	int newValue=10 + element;
		    	System.out.println("Map1 NewValue " + newValue+", "+Thread.currentThread().getName());
		    	return newValue;
		    })  
		    
		    .subscribeOn(s1)
		    .publishOn(s2)
		    .map(element -> {
		    	System.out.println("Map2 NewValue " + element+", "+Thread.currentThread().getName());
		    	return "Value: " + element;
		    });  
		//StartCustomThread
		new Thread(
				()-> {
						flux.log()
						.subscribe(
									element->System.out.println("From consumer of subsribe(): "+element+","+Thread.currentThread().getName())
								);		
					}
				,"StartCustomThread").start();  
		Thread.sleep(5000);
		
	}
}


//UpStream mean Producer Side
//Down Stream means Consumer side
/**
 * 
 *In Reactor, when you chain operators, you can wrap as many Flux and Mono implementations inside one another as you need. 
 *Once you subscribe, a chain of Subscriber objects is created, backward (up the chain) to the first publisher. 
 *This is effectively hidden from you. All you can see is the outer layer of Flux (or Mono) and Subscription, 
 *but these intermediate operator-specific subscribers are where the real work happens. 
 * 
 * **/
