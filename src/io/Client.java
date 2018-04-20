package io;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args){
        try {
            System.out.println("说明: 本程序可以直接拷贝整个文件夹或整个盘符到指定服务器,并保留原路径规则,但是格式必须输入正确!");
            System.out.println("请输入你要拷贝的路径, 例如 F:\\example\\" );
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                String next = scanner.next();
                System.out.println("请填写服务器的IP地址或域名,例如:127.0.0.1");
                String ip = scanner.next();
                scanner.close();
                File file = new File(next);
                File[] files = file.listFiles();
                assert files != null;
                System.out.println("文件数量:" +  files.length);
                for(File f : files){
                    getAllFile(f,ip);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void getAllFile(File file,String ip) throws IOException {
        if(file == null)
            return ;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files != null){
                for(File f : files) {
                    getAllFile(f,ip);
                }
            }
        } else {
            copyFile(file.getAbsoluteFile(),ip);
        }
    }

    private static void copyFile(File file,String ip) throws IOException {

        Socket socket = new Socket(ip,10015);
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        int i;
        byte[] buf = new byte[8192];
        System.out.println("文件长度: " + file.length());
        System.out.println("正在传输: " + file.getAbsolutePath());
        outputStream.writeUTF(file.getAbsolutePath());
        outputStream.writeLong(file.length());
        while((i=inputStream.read(buf,0,buf.length)) != -1){
            outputStream.write(buf,0,i);
            outputStream.flush();
        }
        outputStream.close();
        inputStream.close();
        socket.close();
        System.out.println("传输完毕...");
        System.out.println();
    }
}
