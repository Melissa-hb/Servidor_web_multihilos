import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
public class App {
    public void init() throws IOException{
        ServerSocket server = new ServerSocket(8050);
        var isAlive = true;
        while(isAlive){
            System.out.println("Esperando un cliente ...");
            var socket = server.accept();
            System.out.println("Cliente conectado");
            dispachWorker(socket);
        }
    }
    public void dispachWorker(Socket socket) throws IOException {
        new Thread(
            () -> {
                try{
                    handleRequest(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
                    
        ).start();
    }

    public void handleRequest(Socket socket) throws IOException {
        var input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
        String line;
        while ((line = input.readLine()) != null && !line.isEmpty()) {
            if(line .startsWith("GET")) {
                System.out.println("Recibido: " + line.split(" ")[1].replace("/", ""));
                sendResponse(socket, line.split(" ")[1].replace("/", ""));
            }
        }
       
    }
    public void sendResponse(Socket socket, String resourse) throws IOException {

    var file = new File("");
    System.out.println("Ruta del proyecto: " + file.getAbsolutePath());
    var res = new File("resourses/" + resourse);
    System.out.println(res.exists());
    var extension = "";
    if (res.toString().contains(".")) {
        extension = resourse.split("\\.")[1].toLowerCase();
    }
    if(res.exists()) {
        if(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif")) {
            var fis = new FileInputStream(res);
            byte[] bytes = fis.readAllBytes();
            fis.close();

            var out = socket.getOutputStream();
            String contentType = "image/jpeg";
            if(extension.equals("gif")) {
                contentType = "image/gif";
            }
            out.write(("HTTP/1.1 200 OK\r\n").getBytes());
            out.write(("Content-Type: " + contentType + "\r\n").getBytes());
            out.write(("Content-Length: " + bytes.length + "\r\n").getBytes());
            out.write(("Connection: close\r\n").getBytes());
            out.write(("\r\n").getBytes());
            out.write(bytes);
            out.close();
            socket.close();
        } else {
            var fis = new FileInputStream(res);
            var br = new BufferedReader(new InputStreamReader(fis));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
            br.close();
            fis.close();

            var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: " + content.length() + "\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.write(content.toString());
            writer.close();
            socket.close();
        }
    }else{
        var res404 = new File("resourses/404.html");
        var fis = new FileInputStream(res404);
        var br = new BufferedReader(new InputStreamReader(fis));
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = br.readLine()) != null) {
            content.append(line);
        }
        br.close();
        fis.close();

        var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write("HTTP/1.1 404 Page not found\r\n");
        writer.write("Content-Type: text/html\r\n");
        writer.write("Content-Length: " + content.length() + "\r\n");
        writer.write("Connection: close\r\n");
        writer.write("\r\n");
        writer.write(content.toString());
        writer.close();
        socket.close();
    }
}

    public static void main(String[] args) throws IOException {
    
        App main = new App();
        main.init();

        // var response = "<html><body><h1>Hola a todos</h1></body></html>";
        //     writer.write("HTTP/1.1 200 OK\r\n");
        //     writer.write("Content-Type: text/html\r\n");
        //     writer.write("Content-Length: " + response.length()+"\r\n");
        //     writer.write("Connection: close\r\n");
        //     writer.write("\r\n");
        //     writer.write(response);

        //     writer.close();
    }
}
