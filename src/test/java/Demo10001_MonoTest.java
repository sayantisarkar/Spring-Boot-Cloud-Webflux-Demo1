import org.junit.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Demo10001_MonoTest {

	 /**Log method will be used to peek into every event.       
	  * Till we subscribed nothing will be published and no event happens.
	  * No where we are mentioning that how many elements are needed,
	  * hence unbounded number of elements are requested.
	  * as it is mono hence will give only one element
	  */
    @Test
    public void firstMono() {
        Mono.just("A")
            .log()
            .subscribe();
    }
    //subscribe();
    //subscribe(actualValueFromPub -> System.out.println(">>>"+actualValueFromPub));
    /**
     *subscribe(actulValFromPub->System.out.println("From subscribe():" +actulValFromPub), //For the Actual value
                           exception->System.out.println(exception.getMessage()), //For Error
                           () -> System.out.println("Done")//For Completion
                 );
     * .subscribe(null,    // normal consumer
                           null,    // error consumer
                           null,    // complete consumer
                           subscription -> subscription.request(3)// to place the request for bounded number of elements 
                );
     * 
     * */
        
    /**
     *to do something with published method you can passing consumer 
	 */
    @Test
    public void monoWithConsumer() {
        Mono.just("A")
                .log()
                .subscribe(actualValueFromPub -> System.out.println(">>>"+actualValueFromPub));
    }
    
    
    /**Types of Callback methods. 
     * To the subscribe method we can pass many consumers.
     * for error has happened 
     * or Stream of elements completes
     * or equivalently we can use doOn method also as shown below
     * these are call back methods for the events
    */
    @Test
    public void monoWithDoOn() {
        Mono.just("A")
                .log()
                .doOnSubscribe(subs -> System.out.println("from doOnSubscribe(), Subscribed: " + subs))
                .doOnRequest(request -> System.out.println("from doOnRequest(), Request: " + request))
                .doOnSuccess(complete -> System.out.println("from doOnSuccess(), Complete: " + complete))
                .subscribe(actulValFromPub->System.out.println("From subscribe(): "+actulValFromPub));
    }
    
    
    /** Empty Mono that don't publish any values with the help of. 
     *  Empty Method.OnNext is never called here and no value is printed.
    */
    @Test
    public void emptyMono() {
        Mono.empty()
                .log()
                .subscribe(actulValFromPub->System.out.println("From subscribe(): "+actulValFromPub));
    }
    
    
    /**Types of Consumers. 
     * Passing a message to the subscribe method so that it executes when the sequence is completed.
     * This one has to be the third argument
     * second one is for errors
     * Empty Mono is like void, it does not return value, it emits the completion Single to tell you 
     * Processing is done
    */
    @Test
    public void emptyCompleteConsumerMono() {
        Mono.empty()
                .log()
                .subscribe(actulValFromPub->System.out.println("From subscribe(): "+actulValFromPub), //For the Actual value
                           exception->System.out.println(exception.getMessage()), //For Error
                           () -> System.out.println("Done")//For Completion
                 );
    }
    
    /** Mixing Overloaded form of subscribe() with doOn Call back Methods
    */
    
    @Test
    public void emptyCompleteConsumerMonoWithDoOn() {
        Mono.just("A")
                .log()
                .doOnSubscribe(subs -> System.out.println("from doOnSubscribe(), Subscribed: " + subs))
                .doOnRequest(request -> System.out.println("from doOnRequest(), Request: " + request))
                .doOnSuccess(actualValFromPub -> System.out.println("from doOnSuccess(), Complete: " + actualValFromPub))
                .doOnError(exception -> System.out.println("doOnError() callback: " + exception+"\n\n"))
                .subscribe(actualValFromPub->System.out.println("subscribe(): "+actualValFromPub), //For the Actual value
                			ex->System.out.println(ex.getMessage()), //For Error
                			() -> System.out.println("Done")//For Completion
                );
    }
    
    
    
    /**
     * RuntimeExceptions and Checked Exceptions
     * Both behave the same. i.e. in unchecked Exceptions way.
     * Mono.error is used to throw the Exceptions
     * */
    @Test
    public void errorRuntimeExceptionMono() {
        Mono.error(new RuntimeException())
                .log()
                .subscribe();
    }

    @Test
    public void errorExceptionMono() {
        Mono.error(new Exception())
                .log()
                .subscribe();
    }
    
    
    //Consumer and call back method to consume the error and rethrow it, down stream to other operations.
    @Test
    public void errorConsumerMono() {
        Mono.error(new Exception()).log()
                .subscribe(actulValFromPub->System.out.println("subscribe(): "+actulValFromPub),
                        exception -> System.out.println("Consumer Error: " + exception)
                );
    }
    
    
   //Consumer and call back method to consume the error and rethrow it, down stream to other operations.
    @Test
    public void errorDoOnErrorMono() {
        Mono.error(new Exception()).log()
                .doOnError(exception -> System.out.println("doOnError() callback: " + exception+"\n\n"))
                .subscribe(actulValFromPub->System.out.println("subscribe(): "+actulValFromPub),
                        exception -> System.out.println("Consumer for Error: " + exception)
                );
    }

    
     //Catching the exception and then returning a new Mono using onErrorResume().
    @Test
    public void errorOnErrorResumeMono() {
        Mono.error(new Exception())
        		.onErrorResume(e -> {
                    				System.out.println("Caught: " + e);
                    				return Mono.just("B");
                					}
        						)
        		.log()
                .subscribe();
    }
  
    /**catching the exception and then returning a new Mono, then we use onErrorResume
    here return value is not a Mono*/
    @Test
    public void errorOnErrorReturnMono() {
        Mono.error(new Exception())
                .onErrorReturn("B")
                .log()
                .subscribe();
    }
   
    /**
     * Verifying the redeemed value from Mono.
     * It is using verifyComplete()
     * 
     * verifyComplete() method of StepVerifier triggers the verification, 
     * expecting a completion signal as terminal event.
     * */
    @Test
	public void monoWithStepVerifier1() {
		Mono<String> mono=Mono.just("A");
		StepVerifier.create(mono.log())
		.expectNext("A")
		.verifyComplete();	
	}
    
    /**
     * Verifying the redeemed value from Mono.
     * It is using expectComplete() and verify().
     * expectError(class) method of StepVerifier expects an error of the specified type
     * */
    @Test
	public void monoWithStepVerifier2() {
		Mono<String> mono=Mono.just("A");
		StepVerifier.create(mono.log())
		.expectNext("A")
		.expectComplete().verify();	
	}
    
    /**
     * Verifying the Exception
     * */
	@Test
	public void monoWithStepVerifierWithException() {
		Mono<String> mono=Mono.error(new RuntimeException("Exception occurred"));
		StepVerifier.create(mono.log())
			.expectError(RuntimeException.class)
			.verify();
	}

}
