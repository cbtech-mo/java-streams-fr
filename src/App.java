import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws Exception {
        /*
         * CREATION D'UN STREAM
         */
        
        Stream<String> stream = Stream.<String>builder().add("Hello").add("World").build();
        // Affichage d'un stream
        stream.forEach(s -> System.out.println(s));
        // IntStream, LongStream, DoubleStream
        IntStream intStream = IntStream.of(1, 20, 30, 579);
        IntStream rangeStream = IntStream.range(0, 1_000_000_000);
        // Création d'un stream à partir d'un tableau
        int[] tableau = { 1, 2, 3, 4, 5 };
        IntStream tablStream = Arrays.stream(tableau);
        // Les collections: peuvent être utilisés sous forme d'un stream
        List<String> liste = new ArrayList<>();
        liste.add("Hello");
        liste.add("World");
        Stream<String> stream2 = liste.stream();
        // Les fichiers: un fichier texte peut être parcouru sous la forme d'un stream
        //de chacune de ses lignes
        Path fichier = Paths.get("fichier.txt");
        //Stream<String> linesStream = Files.lines(fichier);
        //Un stream est également utilisé pour produire un résultat unique ou une collection. 
        //Dans le premier cas, on dit que l’on réduit, 
        //tandis que dans le second cas, on dit que l’on collecte.

        /*
         * LA REDUCTION
         */
        // La réduction consiste à obtenir un résultat unique à partir d’un stream. 
        //On peut par exemple compter le nombre d’éléments. 
        //Si le stream est composé de nombres, on peut réaliser une réduction mathématique 
        //en calculant la somme, la moyenne ou en demandant la valeur minimale ou maximale…
        long resultat = LongStream.range(0, 50).sum();
        System.out.println(resultat);

        OptionalDouble moyenne = LongStream.range(0, 50).average();
        if (moyenne.isPresent()) {
            System.out.println(moyenne.getAsDouble());
        }
        // L’API streams introduit la notion de Optional.
        // Certaines opérations de réduction peuvent ne pas être possibles.
        // Par exemple, le calcul de la moyenne n’est pas possible si le stream ne
        // contient aucun élément.
        List<String> liste3 = Arrays.asList("une chaine", "une autre chaine", "encore une chaine");
        Optional<String> chaineLaPlusLongue = liste3.stream().reduce((s1, s2) -> s1.length() > s2.length() ? s1 : s2);

        System.out.println(chaineLaPlusLongue.get()); // "encore une chaine"

        /*
         * LA COLLECTE
         */
        //La collecte permet de créer une nouvelle collection à partir d'un stream
        List<String> liste4 = Arrays.asList("une chaine", "une autre chiane");
        List<String> autreListe = liste4.stream().collect(Collectors.toList());

        class Voiture {

            private String marque;

            public Voiture(String marque) {
                this.marque = marque;
            }

            public String getMarque() {
                return marque;
            }
        }

        List<Voiture> liste5 = Arrays.asList(new Voiture("citroen"),
                new Voiture("renault"),
                new Voiture("audi"),
                new Voiture("citroen"));

        Map<String, List<Voiture>> map = liste5.stream().collect(Collectors.groupingBy(Voiture::getMarque));
        System.out.println(map.keySet());
        //On peut également créer une chaîne de caractéres en joignant les éléments d'un stream:
        List<String> list6 = Arrays.asList("un", "deux", "trois", "quatre", "cinq");
        String resultat2 = list6.stream().collect(Collectors.joining(", "));
        System.out.println(resultat2);

        /*
         * LE FILTRAGE
         */
        List<Voiture> liste7 = Arrays.asList(new Voiture("citroen"),
                new Voiture("audi"),
                new Voiture("citroen"));
        // on construit la liste des voitures qui ne sont pas de marque "citroen"
        List<Voiture> sansCitroen = liste7.stream()
                .filter(v -> !v.getMarque().equals("citroen"))
                .collect(Collectors.toList());
        /*
         * LE MAPPING
         */
        // Le mapping est une opération qui permet de transformer la nature du stream afin de passer d’un type à un autre.
        // Par exemple, si nous voulons récupérer l’ensemble des marques distinctes
        // d’une liste de Voiture, nous pouvons utiliser un mapping pour passer d’un
        // stream de Voiture à un stream de String (représentant les marques des
        // voitures).
        List<Voiture> liste8 = Arrays.asList(new Voiture("citroen"),
                new Voiture("audi"),
                new Voiture("renault"),
                new Voiture("volkswagen"),
                new Voiture("citroen"));

        // mapping du stream de voiture en stream de String
        Set<String> marques = liste8.stream()
                .map(Voiture::getMarque)
                .collect(Collectors.toSet());

        System.out.println(marques); // ["audi", "citroen", "renault", "volkswagen"]

        /*
        * LE PARALLELISME
        */
        //Afin de tirer profit des processeurs multi-cœurs et des machines multi-processeurs, les opérations sur les streams peuvent être exécutées en parallèle. À partir d’une Collection, il suffit d’appeler la méthode Collection.parallelStream ou à partir d’un Stream, il suffit d’appeler la méthode BaseStream.parallel.
        // Un stream en parallèle découpe le flux pour assigner l’exécution à différents processeurs et recombine ensuite le résultat à la fin. Cela signifie que les traitements sur le stream ne doivent pas être dépendant de l’ordre d’exécution.
        // Par exemple, si vous utilisez un stream parallèle pour afficher les 100 premiers entiers, vous constaterez que la sortie du programme est imprédictible.
        
        // affiche les 100 premiers entiers sur la console en utilisant un stream
        // parallèle.
        // Ceci n'est pas une bonne idée car l'opération d'affichage implique
        // que le stream est parcouru séquentiellement. Or un stream parallèle
        // est réparti sur plusieurs processeurs et donc l'ordre d'exécution
        // n'est pas prédictible
        IntStream.range(1, 101).parallel().forEach(System.out::println);

        // Par contre, les streams parallèles peuvent être utiles pour des réductions de
        // type somme puisque le calcul peut être réparti en sommes intermédiaires avant
        // de réaliser la somme totale.
    }
}
