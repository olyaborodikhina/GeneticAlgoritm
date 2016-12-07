/**
 * Created by olyab on 02.12.2016.
 */

import java.util.Random;


public class Maximum {
    public static final int GENES_COUNT = 16;// Количество генов в геноме
    public static final double START_X = 0; //Начальная точка мы исследуем функцию для максимума
    public static final double END_X = Math.PI; //конечная точка отрезка, на котором исследуем
    public static final int MAX_BINARY_VALUE = (int)Math.pow( 2 , GENES_COUNT);
    public static final int POPULATION_COUNT = 200; //Количество особей в популяции
    public static final double MUTATION_LIKELIHOOD= .5;// Вероятность (в процентах) мутации
    public static final int MAX_ITERATIONS = 100; // Максимальное количество "поколений". Если решение не найдено, после
                                                   // Это число итераций, программа вернется.
    private Chromosome population[]=new Chromosome[POPULATION_COUNT]; //массив хромосом

    public static void log(String message){
        //System.out.println( message );
    }
    public static double function( double x ){
        return x + Math.abs(  Math.sin( 32 * x )  ) ;
    }
    /*
     * Перебрать всех хромосом и заполнить их свойство Приспособленности
     * */
    private void fillChromosomesWithFitnesses(){
        log( "***Started to create FITNESSES for all chromosomes. " );
        for ( int i=0; i<POPULATION_COUNT;++i ){
            log("Filling with fitness population number "+i);
            double currentFitness = population[i].calculateFitness();
            population[i].setFitness(  currentFitness  );
            log("Приспособленность: "+population[i].getFitness());

        }

    }
    /*
     * Возвращает сумму приспособленности всех хромосом.
      * Это значение используется при расчете вероятности
     * */
    private double getAllFitnessesSum(){
        double allFitnessesSum = 0;
        for ( int i=0; i<POPULATION_COUNT;++i ){
            allFitnessesSum+=population[i].getFitness();
        }
        return allFitnessesSum;
    }
    /*
     * Перебрать всех хромосом и заполнить их свойство "правдоподобия"
     * */
    private void fillChromosomesWithLikelihoods(){
        double allFitnessesSum = getAllFitnessesSum();
      //  log( "***Started to create LIKELIHOODS for all chromosomes. allFitnessesSum="+allFitnessesSum );
        double last=0;

        int i;
        for ( i=0; i<POPULATION_COUNT;++i ){

            double likelihood = last + (100* population[i].getFitness()/allFitnessesSum );
            last=likelihood;
            population[i].setLikelihood( likelihood );
            //log( "Created likelihood for chromosome number "+i+". Likelihood  value:  "+likelihood );
        }

        // Установка последнего хромосоме вероятность к 100 вручную.
        // Потому что иногда это 99,9999, и это не хорошо
        population[i-1].setLikelihood(100);

        //log( "***Finished to create LIKELIHOODS for all chromosomes. " );
    }

    /*
     * Создает рначальную популяцию
     * */
    private void createInitialPopulation(){
        //log("*** Started creating initial population...");
        for (int i = 0; i<POPULATION_COUNT;++i){
            //log("Creating chromosome number "+i);
            population[i]=new Chromosome();
            Chromosome.fillChromosomeWithRandomGenes(population[i]);
        }

    }

    private int[][] getPairsForCrossover(){

        int[][] pairs = new int[POPULATION_COUNT][2];

        for (int i = 0; i<POPULATION_COUNT;++i){
           // log("Looking for pair number "+i+"...");
            double rand=getRandomFloat(0, 100);
            int firstChromosome = getChromosomeNumberForThisRand(rand);
//            log("First individual... Random number: "+rand+"; corresponding chromosome:"+firstChromosome+
//                    "; chromosome's fitness*100: "+population[firstChromosome].getFitness()*100);

            int secondChromosome;
            do{
                rand=getRandomFloat(0, 100);
                secondChromosome = getChromosomeNumberForThisRand(rand);

            }while (firstChromosome==secondChromosome) ;

            pairs[i][0] = firstChromosome;
            pairs[i][1] = secondChromosome;

        }
        return pairs;
    }

    /*
     *Возвращает число хромосом в популяции [] массив *,
     * соответствующий сгенерированное случайным образом число рандов
     * */
    private int getChromosomeNumberForThisRand(double rand){

        //looks like a little optimiztion would be great here

        int i;
        for ( i = 0; i<POPULATION_COUNT;++i){

            if (  rand<=population[i].getLikelihood() ){
                return i;
            }
        }
        return i-1;

    }
    /*
     *
      * Проверяем, если лица, выбранные для кроссовера действительно лучшие
     * */
    private void analizePairs(int[][] pairs){
        log( "*** Started analyzing totals (for testing only)" );

        int[] totals = new int[POPULATION_COUNT];
        for (int i = 0; i<POPULATION_COUNT;++i){
            totals[i] = 0;
        }

        for (int i = 0; i<POPULATION_COUNT;++i){
            for (int j = 0; j<2;++j){
                totals [	 pairs[i][j]  ] ++;
            }

        }

    }


    private  Chromosome[] performCrossoverAndMutationForThePopulationAndGetNextGeneration(  int[][] pairs ){

        Chromosome nextGeneration[]=new Chromosome[POPULATION_COUNT];
        nextGeneration[0] = findIndividualWithMaxFitness();

        for (int i = 1; i<POPULATION_COUNT;++i){
            Chromosome firstParent = population[  pairs[i][0]  ];
            Chromosome secondParent = population[  pairs[i][1]  ];
            Chromosome result = firstParent.singleCrossover( secondParent );
            nextGeneration[i]=result;
            nextGeneration[i]=nextGeneration[i].mutateWithGivenLikelihood();
        }
        return nextGeneration;
    }


    /*
     * Converts x to integer (
     * vozvrashaet nomer togo kusochka, kotoryi sootvetstvuet x
     *
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
     * Returns random integer number between min and max ( all included )
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


        //log("main() is started");
        //log("POPULATION_COUNT="+POPULATION_COUNT);
       // log("GENES_COUNT="+GENES_COUNT);
        Maximum maximum = new Maximum();
        maximum.createInitialPopulation();

        long iterationsNumber = 0;

        do {
            maximum.fillChromosomesWithFitnesses();

            System.out.println( "=========== Итеррация #"+iterationsNumber  );
            System.out.println( "Лучший ген: "+maximum.findIndividualWithMaxFitness()  );
            System.out.println("fnjdgnkjgbrkjabggdfg");

            maximum.fillChromosomesWithLikelihoods();
            int[][] pairs = maximum.getPairsForCrossover();
            maximum.analizePairs(pairs);
            Chromosome nextGeneration[]=new Chromosome[POPULATION_COUNT];
            nextGeneration = maximum.performCrossoverAndMutationForThePopulationAndGetNextGeneration(  pairs );
            maximum.setPopulation(nextGeneration);
        } while ( iterationsNumber++<MAX_ITERATIONS );


       /// System.out.println( "Result probably is 1111110000010100 or 3.093464006369881. Value=4.092993423870975"   );
      //  System.out.println( "If we take -1: 1111110000010011... Value=" + function  ( binaryToX( "1111110000010011" ) ));
       // System.out.println( "If we take +1: 1111110000010101... Value=" + function  ( binaryToX( "1111110000010101" ) ));


    }




}

