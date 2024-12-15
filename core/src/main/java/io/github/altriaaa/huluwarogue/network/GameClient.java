package io.github.altriaaa.huluwarogue.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GameClient
{
    private Selector selector;
    private SocketChannel socketChannel;

    public GameClient(String host, int port) throws IOException
    {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
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
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(buffer);
    }

    private void read(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(256);
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead == -1)
        {
            socketChannel.close();
            key.cancel();
            return;
        }
        // 处理接收到的数据
        buffer.flip();
        // 示例：打印接收到的数据
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        System.out.println(new String(data));
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
