import java.time.Duration;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Demo10003_OperatorTest {

	/**
	 * map converts the Flux of one type to other 
	 * */
   @Test
   public void convertingFluxOfOneTypeToOther() {
        Flux<Integer>original=Flux.range(1, 5);
        Flux<String>converted=original.map(i -> "Num:"+i * 10);
        System.out.println("Original Flux");
        original.subscribe(element->System.out.print(element+"\t"));
        System.out.println("\nConverted Flux");
        converted.subscribe(element->System.out.print(element+"\t"));
        
    }

   /**
    * For every element of the flux from 1 to 5,
    * map generates internal Flux using Range.
    * for Outer 1
    * 	10,11
    * for Outer 2  
    * 	20,21
    * for Outer 3  
    * 	30,31
    * for Outer 4  
    * 	40,41
    * for Outer 5  
    * 	50,51
    * but this will generate a Flux<Flux<Integer>>
    * */
   @Test
   public void needOfFlatMap() {
       Flux.range(1, 5)
               .map(outerElement -> Flux.range(outerElement*10, 2))
               .subscribe(x->System.out.println(x+","));
   }
   
   /**
    * For every element of the flux from 1 to 5,
    * map generates internal Flux using Range.
    * for Outer 1
    * 	10,11
    * for Outer 2  
    * 	20,21
    * for Outer 3  
    * 	30,31
    * for Outer 4  
    * 	40,41
    * for Outer 5  
    * 	50,51
    * but this will generate a Flux<Flux<Integer>>
    * here as flatMap is used, it will flatten the content to single flux of elements
    * */
   @Test
   public void flatMap() {
        Flux.range(1, 5)
                .flatMap(outerElement -> Flux.range(outerElement*10, 2))
                .subscribe(element->System.out.print(element+","));
   }
   
   //Converts a Mono into Flux
    @Test
    public void flatMapMany() {
        Mono.just(3)
                .flatMapMany(outerElement -> Flux.range(1, outerElement))
                .subscribe(element->System.out.print(element+","));
    }
    

    //Converts a Mono into Flux    
    @Test
    public void concat() throws InterruptedException {
    	//Flux producing numbers 1 to 5
    	Flux<Integer> oneToFive = Flux.range(1, 5);
                       
    	//Flux producing numbers 6 to 10
        Flux<Integer> sixToTen = Flux.range(6, 5);

        Flux.concat(oneToFive, sixToTen)
        	.subscribe(element->System.out.print(element+","));   
    }

    //concatWith returns the flux even when called on Mono
    @Test
    public void concatWith() throws InterruptedException {
    	//Flux producing numbers 1 to 5
    	Flux<Integer> oneToFive = Flux.range(1, 5);
                       
    	//Flux producing numbers 6 to 10
        Flux<Integer> sixToTen = Flux.range(6, 5);
        
        oneToFive.concatWith(sixToTen)
        	.subscribe(element->System.out.print(element+","));    
    }
    
    /**
     * DelayElements will produce each element of flux after a delay.
     * It runs in a parallel thread hence we need to make the current thread sleep.
     * Using concat we can concatinate one or more flux. 
     * */
    @Test
    public void concatPrallelThreads() throws InterruptedException {
    	//Flux producing numbers 1 to 5
    	Flux<Integer> oneToFive = Flux.range(1, 5).delayElements(Duration.ofMillis(600));
               
    	//Flux producing numbers 6 to 10
        Flux<Integer> sixToTen = Flux.range(6, 5)
                .delayElements(Duration.ofMillis(400));
        
        Flux.concat(oneToFive, sixToTen)
                .subscribe(element->System.out.print(element+","));
        
        Thread.sleep(6000);
    }
    
    /**Un like concat, merge does not combine the publishers 
    in sequential way, it interleave the values*/
    @Test
    public void merge() throws InterruptedException  {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));
        Flux<Integer> sixToTen = Flux.range(6, 5)
                .delayElements(Duration.ofMillis(400));

        Flux.merge(oneToFive, sixToTen)
                .subscribe(element->System.out.print(element+","));

        Thread.sleep(4000);
    }
    
    /**Un like concat, mergeWith does not combine the publishers 
    in sequential way, it interleave the values.
    mergeWith returns the flux even when called on Mono
    */
    @Test
    public void mergeWith() throws InterruptedException  {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));
        Flux<Integer> sixToTen = Flux.range(6, 5)
                .delayElements(Duration.ofMillis(400));

        oneToFive.mergeWith(sixToTen)
                .subscribe(element->System.out.print(element+","));
        Thread.sleep(4000);
    }
    
    /** Combines the elements of 2 Publisher available at each place.
     *  Each combination produces an element which is zipped/combined result 
     *  of elements at that position
     * */
    @Test
    public void zip()  {
        Flux<Integer> oneToFive = Flux.range(1, 5);
        Flux<Integer> sixToTen = Flux.range(6, 5);
        Flux.zip(oneToFive, sixToTen,
                (item1, item2) -> "{"+item1 + ", " + + item2+"}")
                .subscribe(element->System.out.print(element+","));
    }
    
    /** Combines the elements of 2 Publisher available at each place.
     *  Each combination produces an element which is zipped/combined result 
     *  of elements at that position. Outcome of zipWith is a Tupple
     * */
    @Test
    public void zipWith()  {
        Flux<Integer> oneToFive = Flux.range(1, 5);
        Flux<Integer> sixToTen = Flux.range(6, 5);
        oneToFive.zipWith(sixToTen)
        	.subscribe(element->System.out.print(element+","));
    }
}
