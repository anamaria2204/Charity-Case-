package Problema_7.client;

import Problema_7.controller.ServiceException;
import Problema_7.model.domain.CharityCase;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON;


public class NewCaseClient {
    RestClient restClient = RestClient.builder().
            requestInterceptor(new CustomRestClientInterceptor()).
            build();

    public static final String URL = "http://localhost:8080/api/charity-cases";

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) {
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public CharityCase[] getAll() {
        return execute(() -> restClient.get().uri(URL).retrieve().body(CharityCase[].class));
    }

    public CharityCase getById(int id) {
        return execute(() -> restClient.get().uri(String.format("%s/%s", URL, id)).retrieve().body(CharityCase.class));
    }

    public CharityCase create(CharityCase charityCase) {
        return execute(() -> restClient.post().uri(URL).contentType(APPLICATION_JSON).body(charityCase).retrieve().body(CharityCase.class));
    }

    public void delete(int id){
        execute(() -> restClient.delete().uri(String.format("%s/%s", URL, id)).retrieve().toBodilessEntity());
    }
    public class CustomRestClientInterceptor
            implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            System.out.println("Sending a " + request.getMethod() + " request to " + request.getURI() + " and body [" + new String(body) + "]");
            ClientHttpResponse response = null;
            try {
                response = execution.execute(request, body);
                System.out.println("Got response code " + response.getStatusCode());
            } catch (IOException ex) {
                System.err.println("Eroare executie " + ex);
            }
            return response;
        }
    }
}