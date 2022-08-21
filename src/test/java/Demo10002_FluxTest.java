
import java.time.Duration;
import java.util.Arrays;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class Demo10002_FluxTest {

	/**
	 * Log method will be used to peek into every event.
	 * Till we subscribed nothing will be published and no event happens.
	 * we are not mentioning how many elements are neede.
	 * hence unbounded number of elements are requested.
	 * as it is flux hence will give more than one element
	 * */
    @Test
    public void firstFlux() {
        Flux.just("A", "B", "C")
                .log()
                .subscribe();
    }
    
    /**
     * Flux from Iterable.
     * But we cannot use the just() method as in the previous example 
     * as it will pass all the list to the subscriber at once.
     * so we have to use the Method "fromIterable" as shown in the current example
     * */
    @Test
    public void fluxFromIterable() {
        Flux.fromIterable(Arrays.asList("A", "B", "C"))
                .log()
                .subscribe();
    }
    
    /**
     * creating flux from Range of integers
     * range(10, 5) first is the start value, second parameter 
     * is total number of elements published
     * */
    @Test
    public void fluxFromRange() {
        Flux.range(10, 5)
                .log()
                .subscribe();
    }
    
    
    
    /**
     * creating flux start from 0 and incrementing them after specific time intervals using 
     * "interval" Method.
     
     * In this example, every second a new value will be published, starting from 0
     * But this if executed without a Thread Sleep, then nothing will be printed as 
     * "interval" Method runs in a separate thread where as the current method 
     * "fluxFromInterval" /test method runs in the main test thread.
     * 
     * main thread terminates before letting the backend thread of "interval" Method to do its task.
     * Hence main thread has to be put to sleep to see the values created by interval.
     
     * Notice that onComplete was not called, as this block will never completes.
     * Only reason why the interval method stopped is because main thread is terminated.
     * 
     * but in server environment, interval method will produce infinite values 
     * to limit this we can use take method to take a first few values, as shown below in : fluxFromInterval2()
     * */
    @Test
    public void fluxFromInterval() throws Exception {
    	System.out.println("Thread: "+Thread.currentThread().getName());
        Flux.interval(Duration.ofSeconds(1))
                .log()
                .subscribe(x->System.out.println("\n"+x+", Thread: "+Thread.currentThread().getName()));
        Thread.sleep(5000);
    }
    
    /**Note: Take does not work as back pressure
     * take method just cancels the subscription once it takes the specified elements
     * and cancels the subscription*/
    
    @Test
    public void fluxFromInterval2() throws Exception {
    	System.out.println("Thread: "+Thread.currentThread().getName());
        Flux.interval(Duration.ofSeconds(1))
                .log()
                .take(2)
                .subscribe(x->System.out.println(x+"Thread: "+Thread.currentThread().getName()));
        Thread.sleep(5000);
    }
   
    
	/**
	 * expectNext - Expect the next element received to be equal to the given value.
	 * verifyComplete - Trigger the verification, expecting a completion signal 
	 * as terminal event.*/ 

	@Test
	public void fluxContent() {
		Flux<String> flux= Flux.just("A","B","C");
		StepVerifier.create(flux.log())
		.expectNext("A")
		.expectNext("B")
		.expectNext("C")
		.verifyComplete();
	}
	
	/**expectNextCount-Expect to received count elements, starting from the previousexpectation or onSubscribe.*/
	@Test
	public void fluxContentCount() {
		
		Flux<String> flux= Flux.just("A","B","C");
		StepVerifier.create(flux.log())
		.expectNextCount(3)
		.verifyComplete();
	}
	
	/**
	 * expectError -Expect an error of the specified type.
	 * verify -Verify the signals received by this subscriber.
	 * */
	@Test
	public void testFluxContentAndException() {
		 
		Flux<String> flux= Flux
				.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Exception Occurred")));
		StepVerifier.create(flux.log())
		.expectNext("A")
		.expectNext("B")
		.expectNext("C")
		.expectError(RuntimeException.class)
		.verify();
	}
	
	/**
	 * expectNextCount-is used to specify expected count of elements to be received from publisher, 
	 * starting from the previous expectation or onSubscribe.
	 * */
	@Test
	public void testFluxContentAndExceptionWithDoOnError() {
		Flux<String> flux= Flux.just("A","B","C").log()
							 .concatWith(Flux.error(new RuntimeException("Some Error")))
							 .doOnError(exception->System.out.println("CallBack doOnError()"+exception));	
	StepVerifier.create(flux)
					.expectNext("A")
					.expectNextCount(2)
					.expectError()
					.verify();
	}
	
	/** Testing with onErrorReturn()
	 * expectError -Expect an error of the specified type.
	 * verifyComplete -Verify the other signals and complete signal received by this subscriber.
	 * */
	@Test
	public void testFluxContentAndExceptionWithOnErrorReturn() {
		Flux<String> flux= Flux.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Some Error")))
				.onErrorReturn("default value");
		StepVerifier.create(flux.log())
					.expectNextCount(2)
					.expectNext("C")
					.expectNext("default value")
					.verifyComplete();
	}
	
	
	/** Testing with onErrorResume()
	 * expectError -Expect an error of the specified type.
	 * verifyComplete -Verify the other signals and complete signal received by this subscriber.
	 * */
	@Test
	public void testFluxContentAndExceptionWithOnErrorResume() {
		Flux<String> flux= Flux.just("A","B","C")
				.concatWith(Flux.error(new RuntimeException("Some Error")))
				.onErrorResume(exception->Flux.just("D","E"));
		StepVerifier.create(flux.log())
					.expectNextCount(3)
					.expectNext("D")
					.expectNext("E")
					.verifyComplete();
	}
    
}
