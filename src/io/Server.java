package io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args){
        try {
            System.out.println("请输入要接收文件的根路径,例如: F:\\receive\\");
            Scanner scanner = new Scanner(System.in);
            String rootPath = scanner.next();
            scanner.close();
            ServerSocket serverSocket = new ServerSocket(10015);
            System.out.println("服务已经建立，等待客户端的连接...");
            while (true){
                Socket socket = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                String absolutePath = inputStream.readUTF();
                long len = inputStream.readLong();
                System.out.println("接收文件: " + absolutePath + "....");
                System.out.println("该文件长度为: " + len);
                String fileName = absolutePath.substring(3,absolutePath.length());
                if(fileName.lastIndexOf("\\") != -1){
                    String prefix = fileName.substring(0,fileName.lastIndexOf("\\")) + "\\";
                    File tempFile = new File(rootPath + prefix);
                    if(!tempFile.exists()){
                        tempFile.mkdirs();
                    }
                }
                File file = new File(rootPath + fileName);
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                int i;
                byte[] buf = new byte[8192];
                while((i=inputStream.read(buf,0,buf.length)) != -1){
                    outputStream.write(buf,0,i);
                    outputStream.flush();
                }
                outputStream.close();
                inputStream.close();
                System.out.println("接收完毕...");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
