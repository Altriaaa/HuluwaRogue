package io.github.altriaaa.huluwarogue.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GameServer
{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private MessageListener messageListener;
    private StringBuilder messageBuffer = new StringBuilder();

    public GameServer(int port) throws IOException
    {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public interface MessageListener
    {
        void onClientConnected(String remoteAddr);

        void onMessageReceived(String message, String remoteAddr);

        void onClientDisconnected(String remoteAddr);
    }

    public void setMessageListener(MessageListener listener)
    {
        this.messageListener = listener;
    }

    public void start() throws IOException
    {
        while (true)
        {
            try
            {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext())
                {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) accept(key);
                    if (key.isReadable()) read(key);
                }
            } catch (IOException e)
            {
//                System.out.println("Error in server loop: " + e.getMessage());
            }
        }
    }

    private void accept(SelectionKey key) throws IOException
    {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        messageListener.onClientConnected(socketChannel.getRemoteAddress().toString());
        System.out.println("Client connected: " + socketChannel.getRemoteAddress());
    }

//    private void read(SelectionKey key) throws IOException
//    {
//        SocketChannel socketChannel = (SocketChannel) key.channel();
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        try
//        {
//            int bytesRead = socketChannel.read(buffer);
//            if (bytesRead == -1)
//            {
//                System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
//                socketChannel.close();
//                key.cancel();
//                return;
//            }
//            buffer.flip();
//            byte[] data = new byte[buffer.remaining()];
//            buffer.get(data);
//            String message = new String(data).trim();
//            System.out.println("Received from client: " + message);
//
//            // Echo the message back to the client (for testing purposes)
//            broadcastMessage("Server: " + message);
//        } catch (IOException e)
//        {
//            // 捕获异常并处理
//            System.out.println("Error reading from client: " + e.getMessage());
//            try
//            {
//                socketChannel.close();
//            } catch (IOException ex)
//            {
//                ex.printStackTrace();
//            }
//            key.cancel(); // 取消 SelectionKey
//        }
//    }

    private void read(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try
        {
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead == -1)
            {
                System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
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
                        messageListener.onMessageReceived(completeMessage, socketChannel.getRemoteAddress().toString());
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
        } catch (IOException e)
        {
            System.out.println("Error reading from client: " + e.getMessage());
            System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
            messageListener.onClientDisconnected(socketChannel.getRemoteAddress().toString());
            try
            {
                socketChannel.close();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
            key.cancel(); // 取消 SelectionKey
        }
    }

    public void broadcastMessage(String message) throws IOException
    {
        String delimitedMessage = message + "\n"; // 使用换行符作为分隔符
        ByteBuffer buffer = ByteBuffer.wrap(delimitedMessage.getBytes());
        for (SelectionKey key : selector.keys())
        {
            if (key.channel() instanceof SocketChannel && key.isValid())
            {
                SocketChannel client = (SocketChannel) key.channel();
                client.write(buffer);
                buffer.rewind();
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        GameServer server = new GameServer(12345);
        server.start();
    }
}
