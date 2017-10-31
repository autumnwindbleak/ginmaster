package gin;

import java.io.File;
import java.util.Random;

public class test {
	public static void main(String[] args) {
//		class myrun implements Runnable {
//			SourceFile sourceFile;
//	        TestRunner testRunner;
//	        Random rng;
//			public myrun(String sourceFilename) {
//				this.sourceFile = new SourceFile(sourceFilename);
//		       	this.testRunner = new TestRunner(this.sourceFile);
//		        rng = new Random(5678);
//			}
//			public void run() {
//				Patch bestPatch = new Patch(sourceFile);
//				double bestTime = testRunner.test(bestPatch, 10).executionTime;
//		        System.out.println(bestTime);
//			}
//		}
//		
//		
//		for(int i = 0; i < 5; i++) {
//			String sourceFilename = "examples/locoGP/SortBubbleDouble.java";
//			myrun r = new myrun(sourceFilename);
//			Thread t1 = new Thread(r);
//			t1.start();
//		}
		
		
		
		
		for(int i = 0; i < 10; i++) {
		String sourceFilename = "examples/locoGP/SortBubbleDouble.java";
		SourceFile sourceFile = new SourceFile(sourceFilename);
       	TestRunner testRunner = new TestRunner(sourceFile);
        Random rng = new Random(5678);
		Patch bestPatch = new Patch(sourceFile);
		double bestTime = testRunner.test(bestPatch, 10).executionTime;
        System.out.println(sourceFilename + ":\t" + bestTime);
        
        
//    	sourceFilename = "examples/locoGP/SortBubbleLoops.java";
//		sourceFile = new SourceFile(sourceFilename);
//       	testRunner = new TestRunner(sourceFile);
//        rng = new Random(5678);
//		bestPatch = new Patch(sourceFile);
//		bestTime = testRunner.test(bestPatch, 10).executionTime;
//		System.out.println(sourceFilename + ":\t" + bestTime);
	}
		
		
	}

}
