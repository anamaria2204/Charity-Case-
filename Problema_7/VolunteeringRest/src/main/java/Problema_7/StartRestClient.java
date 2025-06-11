package Problema_7;

import Problema_7.client.NewCaseClient;
import Problema_7.model.domain.CharityCase;
import org.springframework.web.client.RestClientException;


public class StartRestClient {
    private static final NewCaseClient newCaseClient = new NewCaseClient();

    public static void main(String[] args){
        CharityCase caseUpdated = null;
        CharityCase newCase = new CharityCase( "School supply for children", 1600.0);
        try{
            System.out.println("Adding a new charity case" + newCase);
            CharityCase createdCase = newCaseClient.create(newCase);
            System.out.println("Created charity case: " + createdCase);
            System.out.println("\nPrinting all charity cases ...");
            show(() -> {
                CharityCase[] res = newCaseClient.getAll();
                for (CharityCase c : res) {
                    System.out.println(c.getId() + ": " + c.getCase_name());
                }
            });
        }
        catch (Exception ex) {
            System.out.println("Exception ... " + ex.getMessage());
        }

        System.out.println("\nInfo for charity case with id=12");
        show(() -> System.out.println(newCaseClient.getById(12)));

        System.out.println("\nDeleting charity case with id=15");
        show(() -> newCaseClient.delete(15));
    }

    private static void show(Runnable task){
        try {
            task.run();
        } catch (RestClientException e) {
            System.out.println("Service exception" + e);
        }
    }
}
