package lesson_6.networking.one;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Handler {
    private static int clientCounter = 0;//нумерация клиентов для их идентификации/ счетчик статика, новый клиент с новым номером
    private int clientNumber;//по номеру
    private Socket socket;//поле мы получаем из сервера, который создает объект Handler на каждого подключившегося клиента
    private DataOutputStream out;//потоки ввода вывода
    private DataInputStream in;
    private Thread handlerThread;
    private Server server;//ссылка на сервер

    //когда сервер создает обработчика
    public Handler(Socket socket, Server server) { //ссылка на сервер, для отправки сообщений
        try {
            this.server = server;//передается ссылка на самого себя/что бы были сообщения
            this.socket = socket;//передается серверам
            this.in = new DataInputStream(socket.getInputStream());//из socket - достаем потоки вв и выв
            this.out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Handler created");
            this.clientNumber = ++clientCounter;//обновляем номер клиента
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //метод обработки сообщений
    public void handle() {
        handlerThread = new Thread(() -> { //запуск в отдельном потоке
            while (!Thread.currentThread().isInterrupted() && socket.isConnected()) {
                //слушает входящее сообщение от своего клиента
                try {
                    String message = in.readUTF();
                    message = "client #" + this.clientNumber + ": " + message;//форматирование сообщения
                    server.broadcast(message);// у сервера вызываем метод для отправки сообщения всем
                    System.out.printf("Client #%d: %s\n", this.clientNumber, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        handlerThread.start();//запуск потока
    }

    //метод, который сервер у каждого Handler- вызывает у обработчика
    public void send(String msg) {
        try {
            out.writeUTF(msg);//запись сообщения и отправка его в сеть
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Thread getHandlerThread() {
        return handlerThread;
    }
}