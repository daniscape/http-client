package no.kristiania.http;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private String responseBody;
    private HttpMessage responseMessage;

    // Constructor - det som kalles når vi sier new
    public HttpClient(final String hostname, int port, final String requestTarget) throws IOException {
        // Connect til serven
        Socket socket = new Socket(hostname, port);

        // HTTP Request consists of request line + 0 or more request headers
        //  request line consists of "verb" (GET, POST, PUT) request target ("/echo", "/echo?status=404"), protocol (HTTP/1.1)
        HttpMessage requestMessage = new HttpMessage("GET " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.write(socket);

        // The first line in the response is called status line or response line
        // response line consists of protocol ("HTTP/1.1") status code (200, 404, 401, 500) and status message
        responseMessage = HttpMessage.read(socket);
        responseBody = responseMessage.readBody(socket);
    }

    public HttpClient(String hostname, int port, String requestTarget, String method, QueryString form) throws IOException {
        Socket socket = new Socket(hostname, port);

        String requestBody = form.getQueryString();

        HttpMessage requestMessage = new HttpMessage(method + " " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostname);
        requestMessage.setHeader("Content-Length", String.valueOf(requestBody.length()));
        requestMessage.write(socket);
        socket.getOutputStream().write(requestBody.getBytes());

        responseMessage = HttpMessage.read(socket);
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404&Content-Type=text%2Fhtml&body=Hello+world");
        System.out.println(client.getResponseBody());
    }

    public int getStatusCode() {
        String[] responseLineParts = responseMessage.getStartLine().split(" ");
        return Integer.parseInt(responseLineParts[1]);
    }

    public String getResponseHeader(String headerName) {
        // Implementation of HttpMessage.getHeader is left as an exercise to the reader
        return responseMessage.getHeader(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}
