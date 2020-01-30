 /**
     * classe State
     *
  */
 public class State
 {
     private String name;

     /**
      * Constructeur d'objets de classe State
      */
     public State(String name)
     {
         // initialisation des variables d'instance
         this.name = name;
     }
     /**
      * accesseur
      */
     public String getName()
     {
         return this.name;
     }
     public String toString()
     {
         return this.name;
     }
 }
