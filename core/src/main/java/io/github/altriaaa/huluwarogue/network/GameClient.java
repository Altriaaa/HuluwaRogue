package io.github.altriaaa.huluwarogue.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.UUID;

public class GameClient
{
    private Selector selector;
    private SocketChannel socketChannel;
    private MessageListener messageListener;
    private StringBuilder messageBuffer = new StringBuilder(); // 缓冲区，用于存储未处理的消息


    public GameClient(String host, int port) throws IOException
    {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public interface MessageListener
    {
        void onMessageReceived(String message);
    }

    public void setMessageListener(MessageListener listener)
    {
        this.messageListener = listener;
    }

    public void start() throws IOException
    {
        while (true)
        {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext())
            {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;
                if (key.isReadable()) read(key);
            }
        }
    }

    public void send(String message) throws IOException
    {
        String delimitedMessage = message + "\n";
        ByteBuffer buffer = ByteBuffer.wrap(delimitedMessage.getBytes());
        socketChannel.write(buffer);
    }

    private void read(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead == -1)
        {
            socketChannel.close();
            key.cancel();
            return;
        }
        // 处理接收到的数据
        buffer.flip();
        while (buffer.hasRemaining())
        {
            char c = (char) buffer.get(); // 逐字节读取
            messageBuffer.append(c); // 追加到缓冲区
        }
        String messages = messageBuffer.toString();
        int lastIndex;
        while ((lastIndex = messages.indexOf("\n")) != -1)
        { // 寻找第一个完整消息
            String completeMessage = messages.substring(0, lastIndex); // 提取完整消息
            messages = messages.substring(lastIndex + 1); // 剩下的未处理部分
            try
            {
                if (messageListener != null)
                {
                    messageListener.onMessageReceived(completeMessage);
                }
            } catch (Exception e)
            {
                System.err.println("Error parsing message: " + completeMessage);
                e.printStackTrace();
            }
        }

        // 将剩余的未处理部分重新存入缓冲区
        messageBuffer.setLength(0);
        messageBuffer.append(messages);
//        buffer.compact(); // 压缩缓冲区，保留未处理的数据
    }

    public static void main(String[] args) throws IOException
    {
        GameClient client = new GameClient("localhost", 12345);

        // Start a thread to handle server messages
        new Thread(() ->
        {
            try
            {
                client.start();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }).start();

        // Send messages to the server
        try (java.util.Scanner scanner = new java.util.Scanner(System.in))
        {
            while (true)
            {
                String message = scanner.nextLine();
                client.send(message);
            }
        }
    }
}
