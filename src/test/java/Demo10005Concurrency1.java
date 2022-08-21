import org.junit.Test;

import reactor.core.publisher.Mono;

//https://projectreactor.io/docs/core/release/reference/index.html#schedulers
public class Demo10005Concurrency1 {
	
	@Test
    public void normalBlockedOutterThread() {
        Mono.just("hello ")
                .log()
                .subscribe(s -> 
		          System.out.println(s +", thread "+ Thread.currentThread().getName()) 
		      );
    }
	
	/**
	 * The Mono<String> is assembled in thread main
	 * but it is subscribed to in thread Thread-0
	 * As a consequence, onNext callback actually run in Thread-0
	 * */
	@Test
	public void normalNonBlockedOutterThread() throws InterruptedException{
		  final Mono<String> mono = Mono.just("hello "); 
		  Thread t=new Thread(
				  () -> mono.log()
				  .subscribe(s -> 
		            System.out.println(s +", thread "+ Thread.currentThread().getName()) 
				)
		      );
		  t.start();
		  t.join();//makes the current thread executing, normalNonBlockedOutterThread() wait. 
		  		   // till thread t finishes its work. 
	}

}



//https://projectreactor.io/docs/core/release/reference/index.html#schedulers

/**
 *
 * Reactor, like RxJava, can be considered concurrency agnostic. That is, it does not enforce a concurrency model. 
 * Rather it leaves you, the developer, in command. However, that does not prevent the library from helping you with concurrency.
 * Obtaining a Flux or a Mono doesn’t necessarily mean it will run in a dedicated Thread. Instead, most operators continue working in 
 * the Thread on which the previous operator executed. Unless specified, the topmost operator (the source) itself runs on the Thread in which the subscribe() call was made.
 * */
