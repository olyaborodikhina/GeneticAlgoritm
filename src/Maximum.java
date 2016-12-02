/**
 * Created by olyab on 02.12.2016.
 */

import java.util.Random;


public class Maximum {

    //number of genes in a genome
    public static final int GENES_COUNT = 16;

    /*
     * Starting point we're exploring the function for maximum
     * */
    public static final double START_X = 0;

    /*
     * Ending point we're exploring the function for maximum.
     * NOTE: this value is excluded
     * */
    public static final double END_X = Math.PI;

    /*
     * Maximum value for integer (binary) representation
     * of the function value on the given segment
     * */
    public static final int MAX_BINARY_VALUE = (int)Math.pow( 2 , GENES_COUNT);





    //number of individuals in population
    public static final int POPULATION_COUNT = 200;

    //likelihood (in percent) of the mutation
    public static final double MUTATION_LIKELIHOOD= .5;

    //maximum number of "generations". If solution not found after
    //this number of iterations, the program will return.
    public static final int MAX_ITERATIONS = 200;

    //array of individuals (Chromosomes)
    private Chromosome population[]=new Chromosome[POPULATION_COUNT];



    /*
     * Writes a string to the log
     * */
    public static void log(String message){
        //System.out.println( message );
    }

    /*
     * Function  we're finding maximum for
     * */
    public static double function( double x ){
        return x + Math.abs(  Math.sin( 32 * x )  ) ;
    }


    /*
     * Iterate through all chromosomes and fill their "fitness" property
     * */
    private void fillChromosomesWithFitnesses(){
        log( "***Started to create FITNESSES for all chromosomes. " );
        for ( int i=0; i<POPULATION_COUNT;++i ){
            log("Filling with fitness population number "+i);
            double currentFitness = population[i].calculateFitness();
            population[i].setFitness(  currentFitness  );
            log("Fitness: "+population[i].getFitness());

        }

        log( "***Finished to create FITNESSES for all chromosomes. " );

    }


    /*
     * Returns sum of fitnesses of all chromosomes.
     * This value is used when calculating likelihood
     * */
    private double getAllFitnessesSum(){
        double allFitnessesSum = 0;
        for ( int i=0; i<POPULATION_COUNT;++i ){
            allFitnessesSum+=population[i].getFitness();
        }
        return allFitnessesSum;
    }


    /*
     * Iterate through all chromosomes and fill their "likelihood" property
     * */
    private void fillChromosomesWithLikelihoods(){
        double allFitnessesSum = getAllFitnessesSum();
        log( "***Started to create LIKELIHOODS for all chromosomes. allFitnessesSum="+allFitnessesSum );
        double last=0;

        int i;
        for ( i=0; i<POPULATION_COUNT;++i ){

            double likelihood = last + (100* population[i].getFitness()/allFitnessesSum );
            last=likelihood;
            population[i].setLikelihood( likelihood  );
            log( "Created likelihood for chromosome number "+i+". Likelihood  value:  "+likelihood );
        }

        //setting last chromosome's likeliness to 100 by hand.
        //because sometimes it's 99.9999 and that's not good
        population[i-1].setLikelihood( 100  );

        log( "***Finished to create LIKELIHOODS for all chromosomes. " );
    }

    /*
     * Creates an initial population
     * */
    private void createInitialPopulation(){
        log("*** Started creating initial population...");
        for (int i = 0; i<POPULATION_COUNT;++i){
            log("Creating chromosome number "+i);
            population[i]=new Chromosome();
            Chromosome.fillChromosomeWithRandomGenes(population[i]);
        }
        log("*** FINISHED creating initial population...");

    }




    /*
     * Returns pairs for the crossover operations.
     * [0][0] with [0][1]
     * [1][0] with [1][1]
     * etc. etc.
     * */
    private int[][] getPairsForCrossover(){
        log("*** Started looking for pairs for crossover");

        int[][] pairs = new int[POPULATION_COUNT][2];

        for (int i = 0; i<POPULATION_COUNT;++i){
            log("Looking for pair number "+i+"...");
            double rand=getRandomFloat(0, 100);
            int firstChromosome = getChromosomeNumberForThisRand(rand);
            log("First individual... Random number: "+rand+"; corresponding chromosome:"+firstChromosome+
                    "; chromosome's fitness*100: "+population[firstChromosome].getFitness()*100);

            int secondChromosome;
            do{
                rand=getRandomFloat(0, 100);
                secondChromosome = getChromosomeNumberForThisRand(rand);

            }while (firstChromosome==secondChromosome) ;  //prevent individual's crossover with itself :)


            log("Second individual... Random number: "+rand+"; corresponding chromosome:"+secondChromosome+
                    "; chromosome's fitness*100: "+population[secondChromosome].getFitness()*100);

            pairs[i][0] = firstChromosome;
            pairs[i][1] = secondChromosome;

        }

        log("*** Finished looking for pairs for crossover");

        return pairs;
    }

    /*
     * Returns number of chromosome in population[] array
     * corrresponding to the randomly generated number rand
     * */
    private int getChromosomeNumberForThisRand(double rand){

        //looks like a little optimiztion would be great here

        int i;
        for ( i = 0; i<POPULATION_COUNT;++i){

            if (  rand<=population[i].getLikelihood() ){
                return i;
            }
        }
        return i-1; //unreachable code imho :) But without this it doesn't compile

    }


    /*
     * For testing only.
     * Check if the individuals selected for crossover are really the best :)
     * */
    private void analizePairs(int[][] pairs){
        log( "*** Started analyzing totals (for testing only)" );

        int[] totals = new int[POPULATION_COUNT];

        //fill totals array with zeroes
        for (int i = 0; i<POPULATION_COUNT;++i){
            totals[i] = 0;
        }

        //calculate how many times each individual will do the crossover
        for (int i = 0; i<POPULATION_COUNT;++i){
            for (int j = 0; j<2;++j){
                totals [	 pairs[i][j]  ] ++;
            }

        }

        //printing totals
        for (int i = 0; i<POPULATION_COUNT;++i){
            log( "Individual #"+i+"; fitness:"+population[i].getFitness()+"; number of crossovers:"+totals[i] );
        }

        log( "*** Finished analyzing totals (for testing only)" );

    }


    private  Chromosome[] performCrossoverAndMutationForThePopulationAndGetNextGeneration(  int[][] pairs ){

        Chromosome nextGeneration[]=new Chromosome[POPULATION_COUNT];

        log("*******************************");
        log("Starting performing Crossover operation For The Population...");

        //the best individual goes to the next generation in any case.
        //Please note that because of this we start the next loop from 1, not from 0
        nextGeneration[0] = findIndividualWithMaxFitness();

        for (int i = 1; i<POPULATION_COUNT;++i){
            log("** Starting crossover #"+i);
            Chromosome firstParent = population[  pairs[i][0]  ];
            Chromosome secondParent = population[  pairs[i][1]  ];
            log("First parent (#"+pairs[i][0]+")\n" + firstParent );
            log("Second parent (#"+pairs[i][1]+")\n" + secondParent );

            Chromosome result = firstParent.singleCrossover( secondParent );
            nextGeneration[i]=result;
            log( "Resulting (child) chromosome BEFORE the mutation:\n"+ nextGeneration[i]);

            nextGeneration[i]=nextGeneration[i].mutateWithGivenLikelihood();

            log( "Resulting (child) chromosome AFTER the mutation:\n"+ nextGeneration[i]);
            log("** Finished crossover #"+i);
        }

        log("Finished performing Crossover operation For The Population...");
        return nextGeneration;
    }


    /*
     * Converts x to integer (
     * vozvrashaet nomer togo kusochka, kotoryi sootvetstvuet x
     * )
     * */
    public static int xToInt(double x){
        return  (int)Math.floor(  (x-START_X)*MAX_BINARY_VALUE/(END_X-START_X) ) ;

    }


    /*
     * Converts x to integer (
     * vozvrashaet (v dvoichnom formate) nomer togo kusochka, kotoryi sootvetstvuet x
     * )
     * */
    public static String doubleToBinaryString(double x){
        String result = Integer.toBinaryString(  xToInt(  x  )   )  ;

        //adding leading zeroes so than the genome length is exactly GENES_COUNT
        for  (  int i=result.length();i<GENES_COUNT;++i )
            result="0"+result;

        return result;
    }


    /*
     * Converts binary string to int
     * */
    public static int binaryToInt( String binaryX ){
        return Integer.parseInt(  binaryX, 2 );
    }

    /*
     * Converts binary string to x from the given segment
     * */
    public static double binaryToX( String binaryX ){
        int intX=binaryToInt(binaryX);
        return START_X + intX * (END_X - START_X) / MAX_BINARY_VALUE;
    }


    /*
     * Returns random integer number between min and max ( all included :)  )
     * */
    public static int getRandomInt( int min, int max ){
        Random randomGenerator;
        randomGenerator = new Random();
        return  randomGenerator.nextInt( max+1 ) + min ;
    }

    /*
     * Returns random float number between min (included) and max ( NOT included :)  )
     * */
    public static float getRandomFloat( float min, float max ){
        return  (float) (Math.random()*max + min) ;
    }

    public Chromosome findIndividualWithMaxFitness(){
        double currMaxFitness = 0;
        Chromosome result = population[0];
        for (int i = 0; i<POPULATION_COUNT;++i){
            if ( population[i].getFitness() > currMaxFitness ){
                result = population[i];
                currMaxFitness = population[i].getFitness();
            }
        }
        return result;
    }

    public Chromosome[] getPopulation() {
        return population;
    }

    public void setPopulation(Chromosome[] population) {
        this.population = population;
    }

    public static void main( String[] args ){



        System.out.println( function( 3.093464006369881 ) );

        System.out.println( function( 3.093464006369871 ) );

        System.out.println( function( 3.093564006369881  ) );

        //exactness:  3.1415/65536 = 0.0000479


        log("main() is started");
        log("POPULATION_COUNT="+POPULATION_COUNT);
        log("GENES_COUNT="+GENES_COUNT);
        Maximum maximum = new Maximum();
        maximum.createInitialPopulation();

        long iterationsNumber = 0;

        do {
            maximum.fillChromosomesWithFitnesses();

            System.out.println( "-=-=========== Finished iteration #"+iterationsNumber  );
            System.out.println( "Best individual: "+maximum.findIndividualWithMaxFitness()  );

            maximum.fillChromosomesWithLikelihoods();
            int[][] pairs = maximum.getPairsForCrossover();
            maximum.analizePairs(pairs);
            Chromosome nextGeneration[]=new Chromosome[POPULATION_COUNT];
            nextGeneration = maximum.performCrossoverAndMutationForThePopulationAndGetNextGeneration(  pairs );
            maximum.setPopulation(nextGeneration);





        } while ( iterationsNumber++<MAX_ITERATIONS );


        System.out.println( "Result probably is 1111110000010100 or 3.093464006369881. Value=4.092993423870975"   );
        System.out.println( "If we take -1: 1111110000010011... Value=" + function  ( binaryToX( "1111110000010011" ) ));
        System.out.println( "If we take +1: 1111110000010101... Value=" + function  ( binaryToX( "1111110000010101" ) ));


    }




}

