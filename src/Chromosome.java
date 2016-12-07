/**
 * Created by olyab on 02.12.2016.
 */
//package GABorodikhina;

//import Maximum;

public class Chromosome {

    //массив генов
    private String genome=new String();

    //приспособленность особи
    private double fitness;

    //вероятность мутации
    private double likelihood;

    //setters and getters
    public double getFitness() {
        return fitness;
    }
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    public String getGenome() {
        return genome;
    }
    public void setGenome(String genome) {
        this.genome = genome;
    }
    public double getLikelihood() {
        return likelihood;
    }
    public void setLikelihood(double likelihood) {
        this.likelihood = likelihood;
    }




    /*
     *Calculates fitness for THIS chromosome and returns it.
     * */
    public double  calculateFitness(){

        Maximum.log( "Calculating fitness for genome "+genome  );
        double x = Maximum.binaryToX( genome );
        Maximum.log( "Genome converted to double: "+x  );
        double funcValue = Maximum.function(   x   );
        Maximum.log( "Function value "+ funcValue  );
        return  funcValue ;
    }

    protected Object clone()  {
        Chromosome resultChromosome = new Chromosome() ;
        resultChromosome.setFitness(  this.getFitness() );
        resultChromosome.setLikelihood(  this.getLikelihood() );
        resultChromosome.setGenome(genome);

        return resultChromosome;
    }



    public Chromosome mutateWithGivenLikelihood(){
        Maximum.log( "Starting mutateWithGivenLikelihood().... Maximum.MUTATION_LIKELIHOOD=="+Maximum.MUTATION_LIKELIHOOD+"; Maximum.GENES_COUNT= "+Maximum.GENES_COUNT);
        Maximum.log( " Genome before the mutation "+genome);
        Chromosome result =  (Chromosome ) this.clone();
        StringBuffer resultGenome = new StringBuffer(genome);

        for (int i=0;i<Maximum.GENES_COUNT;++i){
            double randomPercent = Maximum.getRandomFloat(0,100);
            if ( randomPercent < Maximum.MUTATION_LIKELIHOOD ){
                char oldValue =  genome.charAt(i);
                char newValue= 	oldValue=='1' ? '0' : '1';
                Maximum.log( "Performing mutation for gene #"+i
                        +". ( randomPercent =="+randomPercent
                        +" ). Old value:"+oldValue +"; New value:"+newValue );

                resultGenome.setCharAt( i , newValue);


            }
        }
        result.setGenome(  resultGenome.toString() );
        Maximum.log( " Genome of the mutated chromosome "+result.getGenome());
        Maximum.log( "Finished mutateWithGivenLikelihood()...." );
        return result;
    }


    /*
     * String representation of the object
     * */
    public String toString(){

        StringBuffer result = new StringBuffer();

        result.append(  "Genome: "+genome +"\n" ) ;
        result.append(  "Double value: "+ Maximum.binaryToX(genome ) +"\n" ) ;
        result.append( "Fitness:" + fitness+"\n" );
        result.append( "Likelihood:" + likelihood+"\n" );


        return result.toString();


    }


    /*
     * Returns number of gene AFTER which comes the crossover line
     * e.g. 101
     * Position after first "1" is 0
     * Position after "0" is 1 etc.
     * */
    private  int getRandomCrossoverLine(){
        int line = Maximum.getRandomInt(0, Maximum.GENES_COUNT - 2);  //-2 because we dn't need the position after the last gene
        Maximum.log("Generated random CrossoverLine at position "+line);
        return line;
    }


			/*
			   This crossover gives birth to 2 children
			*/

    public Chromosome[] doubleCrossover(  Chromosome chromosome  ){

        Maximum.log( "Starting DOUBLE crossover operation..." );
        Maximum.log( "THIS chromo:"+this );
        Maximum.log( "ARG chromo:"+chromosome );



        int crossoverline=getRandomCrossoverLine();
        Chromosome[] result = new Chromosome[2];
        result[0]=new Chromosome();
        result[1]=new Chromosome();

        StringBuffer resultGenome0 = new StringBuffer();
        StringBuffer resultGenome1 = new StringBuffer();



        for (int i=0;i<Maximum.GENES_COUNT;++i){

            //so that we can setCharAt() to this position
            resultGenome0.append(' ');
            resultGenome1.append(' ');


            if ( i<=crossoverline){
                resultGenome0.setCharAt(i , this.getGenome().charAt(i));
                resultGenome1.setCharAt(i , chromosome.getGenome().charAt(i));
            }

            else {
                resultGenome0.setCharAt(i , chromosome.getGenome().charAt(i));
                resultGenome1.setCharAt(i , this.getGenome().charAt(i));
            }

        }

        result[0].setGenome(  resultGenome0.toString() );
        result[1].setGenome(  resultGenome1.toString() );

        Maximum.log( "RESULTING chromo #0:\n"+result[0] );
        Maximum.log( "RESULTING chromo #1:\n"+result[1] );
        Maximum.log( "DOUBLE crossover operation is finished..." );

        return result;

    }

    /*
       This crossover gives birth to 1 child.
        To perform that, it calls doubleCrossover() and then
         randomly selects one of the 2 children.
    */
    public Chromosome singleCrossover(  Chromosome chromosome  ){
        Maximum.log( "Starting SINGLE crossover operation...Calling DOUBLE crossover first...." );
        Chromosome[] children = doubleCrossover(  chromosome  ) ;
        Maximum.log( "Selecting ONE of the 2 children returned by DOUBLE crossover ...." );
        int childNumber = Maximum.getRandomInt(0, 1);
        Maximum.log( "Selected child #"+childNumber +", here it is:\n"+children[childNumber] );
        Maximum.log( "SINGLE crossover operation is finished" );
        return children[childNumber];
    }


    /*
     * Fills a chromosome with random genes.
     * */
    public static void fillChromosomeWithRandomGenes( Chromosome chromosome ){
        Maximum.log("Filling chromosome with random genes....");

        StringBuffer resultGenome=new StringBuffer();

        for (int i=0;i<Maximum.GENES_COUNT;++i){

            //so that we can set smth to that position
            resultGenome.append(' ');

            resultGenome.setCharAt(i,  Maximum.getRandomInt(0, 1)==0?'0':'1'  );
            Maximum.log("Gene number:"+i+"; value:"+resultGenome.charAt(i));

        }

        chromosome.setGenome(  resultGenome.toString() );
        Maximum.log("I'm done filling chromosome with random genes.. Resulting genome: "+chromosome.getGenome());

    }
    /*
     * main method :) :) :) :) :) :)
     * For testing only
     * */
    public static void main(String[] args) throws Exception{

        Chromosome c = new Chromosome();
        c.setGenome(  "0000000000000000"  );


        Chromosome c2 = new Chromosome();
        c2.setGenome(  "1111111111111111"  );


        c.singleCrossover(c2);


        Chromosome c3 = new Chromosome();
        Chromosome.fillChromosomeWithRandomGenes(c3);



	/*
	        Chromosome  c1 = new Chromosome();
			c1.getGenes()[0]=0;
			c1.getGenes()[1]=1;
			c1.getGenes()[2]=2;
			c1.getGenes()[3]=3;

	        Chromosome  c2 = new Chromosome();
			c2.getGenes()[0]=100;
			c2.getGenes()[1]=10;
			c2.getGenes()[2]=20;
			c2.getGenes()[3]=30;

			c1.singleCrossover( c2 );

			Diofant.log("ddddddddddddddddddd");

	                                c2.setFitness(0.5f);
	                                c2.setLikelihood(0.3f);
			Chromosome c3= (Chromosome ) c2.clone();
			Diofant.log(""+c2);
			Diofant.log(""+c3);
			Diofant.log(c3.equals(c2)+"");

	*/

    }

}




