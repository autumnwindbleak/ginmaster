package gin;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.io.Files;

/**
 * Simple local search.
 */
public class Exercise1 {
	
	
	
	class Details{
		Patch bestPatch;
		double bestTime;
		Details(Patch bestPatch, double bestTime){
			this.bestPatch = bestPatch;
			this.bestTime = bestTime;
		}
	}

    private static final int seed = 5678;
    private static final int NUM_STEPS = 500;
    private static final int WARMUP_REPS = 10;
    private static final String outputpath = "output/";
    private static final int runs = 2;
	private static final String defaultpath = "examples/locoGP/";
	private static final String[] defaultfiles = new String[] {"SortBubbleDouble.java"
//			,"SortBubbleLoops.java","SortInsertion.java", "SortCocktail.java"};
	};
    



    /**
     * Main method. Take a source code filename, instantiate a search instance and execute the search.
     * @param args A single source code filename, .java
     */
    public static void main(String[] args) {
        if (args.length == 0) {
        	for(int i = 0; i < defaultfiles.length; i ++) {
        		ArrayList<Double> besttimes = new ArrayList<Double>();
        		ArrayList<String> bestedit = new ArrayList<String>();
    			String sourceFilename = defaultpath + defaultfiles[i];
                System.out.println("\nOptimising source file: " + sourceFilename + "\n");
                
                //create folder
                String[] temp = sourceFilename.split("/");
                String name = temp[temp.length-1];
                File folder = new File(outputpath + name);
//                System.out.println(outputpath + name); 
                if(!folder.exists()) {
                	folder.mkdirs();
                }
                try {
					Files.copy(new File(sourceFilename), new File(outputpath + name + "/0.java"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
                //running
        		for(int j = 0; j < runs; j ++) {
                    Exercise1 e = new Exercise1();
                    Details d = e.search(j+1,sourceFilename);
                    //record best time and best edit
                    besttimes.add(d.bestTime);      
                    bestedit.add(d.bestPatch.toString());
                    //output optimal every times
                    d.bestPatch.writePatchedSourceToFile(outputpath + name + "/" + (j+1) + ".java");
//                    try {
//						Thread.sleep(5000);
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
        		}
        		File record = new File(outputpath + name + "/" + "record.csv");
        		try {
					FileWriter out = new FileWriter(record);
					
					for(int j = 0; j < besttimes.size(); j++) {
						out.write(besttimes.get(j) + "," + bestedit.get(j) + "\n");
					}
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}

        } else {
        	for(int i = 0; i < args.length; i ++) {
        		ArrayList<Double> besttimes = new ArrayList<Double>();
        		ArrayList<String> bestedit = new ArrayList<String>();
    			String sourceFilename = args[i];
                System.out.println("\nOptimising source file: " + sourceFilename + "\n");
                
              //create folder
                String[] temp = sourceFilename.split("/");
                String name = temp[temp.length-1];
                File folder = new File(outputpath + name);
//                System.out.println(outputpath + name); 
                if(!folder.exists()) {
                	folder.mkdirs();
                }
                try {
					Files.copy(new File(sourceFilename), new File(outputpath + name + "/0.java"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
                //running
        		for(int j = 0; j < runs; j ++) {
                    Exercise1 e = new Exercise1();
                    Details d = e.search(j+1,sourceFilename);
                    //record best time and best edit
                    besttimes.add(d.bestTime);      
                    bestedit.add(d.bestPatch.toString());
                    //output optimal every times
                    d.bestPatch.writePatchedSourceToFile(outputpath + name + "/" + (j+1) + ".java");
        		}
        		File record = new File(outputpath + name + "/" + "record.csv");
        		try {
					FileWriter out = new FileWriter(record);
					
					for(int j = 0; j < besttimes.size(); j++) {
						out.write(besttimes.get(j) + "," + bestedit.get(j) + "\n");
					}
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}

        }
    }

    /**
     * Actual LocalSearch.
     * @return
     */
    private Details search(int currentrun, String sourceFilename) {

        // start with the empty patch
        SourceFile sourceFile = new SourceFile(sourceFilename);
        TestRunner testRunner = new TestRunner(sourceFile);
        Random rng = new Random(seed);
        Patch bestPatch = new Patch(sourceFile);
        double bestTime = testRunner.test(bestPatch, WARMUP_REPS).executionTime;
        double origTime = bestTime;
        int bestStep = 0;

        System.out.println("Initial execution time: " + bestTime + " (ns) \n");

        for (int step = 1; step <= NUM_STEPS; step++) {
        	printProgress(sourceFile.getFilename(),NUM_STEPS,step,currentrun,runs);
			
//            System.out.println("Step " + step + " ");

            Patch neighbour = neighbour(bestPatch, rng);

//            System.out.print(neighbour);

            TestRunner.TestResult testResult = testRunner.test(neighbour);

            if (!testResult.patchSuccess) {
//                System.out.println("Patch invalid");
                continue;
            }

            if (!testResult.compiled) {
//                System.out.println("Failed to compile");
                continue;
            }

            if (!testResult.junitResult.wasSuccessful()) {
//                System.out.println("Failed to pass all tests");
                continue;
            }

            if (testResult.executionTime < bestTime) {
                bestPatch = neighbour;
                bestTime = testResult.executionTime;
                bestStep = step;
//                System.out.println("*** New best *** Time: " + bestTime + "(ns)");
            } else {
//                System.out.println("Time: " + testResult.executionTime);
            }

        }

        System.out.println("\nBest patch found: " + bestPatch);
        System.out.println("Found at step: " + bestStep);
        System.out.println("Best execution time: " + bestTime + " (ns) ");
        System.out.printf("Speedup (%%): %.2f \n\n", 100*((origTime - bestTime)/origTime));

//        bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");
        
        Details details = new Details(bestPatch,bestTime);
        return details;

    }


    /**
     * Generate a neighbouring patch, by either deleting a randomly chosen edit, or adding a new random edit
     * @param patch Generate a neighbour of this patch.
     * @return A neighbouring patch.
     */
    public Patch neighbour(Patch patch, Random rng) {

        Patch neighbour = patch.clone();

        if (neighbour.size() > 0 && rng.nextFloat() > 0.5) {
            neighbour.remove(rng.nextInt(neighbour.size()));
        } else {
            neighbour.addRandomEdit(rng);
        }
        return neighbour;
    }
    
    private static void printProgress(String filename, long total, long current, int currentrun, int totalruns) {
    	System.out.print("\r");
        int percent = (int) (current * 100 / total);
        System.out.print(filename + "\t" + percent + "%\tRuns: " +currentrun + "/" + totalruns);
    }


}
