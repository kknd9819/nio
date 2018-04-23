package io;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args){
        try {
            System.out.println("1、请输入要接收文件的根路径,例如: F:\\receive\\");
            Scanner scanner = new Scanner(System.in);
            String rootPath = scanner.nextLine();
            System.out.println("2、请输入监听的端口,如不输入则使用默认的10015端口");
            String port = scanner.nextLine();
            if(port.equals("")){
                port = "10015";
            }
            scanner.close();
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            System.out.println("服务已经建立，等待客户端的连接,端口号为: " + port);
            while (true){
                Socket socket = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                String absolutePath = inputStream.readUTF();
                long len = inputStream.readLong();
                long residue = inputStream.readLong();
                System.out.println("剩余文件数量: " + residue);
                System.out.println("接收文件: " + absolutePath + "....");
                System.out.println("长度: " + len);
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
                long over = 0;
                byte[] buf = new byte[8192];
                while((i=inputStream.read(buf,0,buf.length)) != -1){
                    outputStream.write(buf,0,i);
                    outputStream.flush();
                    over += i;
                    System.out.print("\r进度: " + over);
                }
                outputStream.close();
                inputStream.close();
                socket.close();
                System.out.println("接收完毕...");
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
