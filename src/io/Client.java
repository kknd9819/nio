package io;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static long count = 0;

    public static void main(String[] args){
        try {
            System.out.println("说明: 本程序可以直接拷贝整个文件夹或整个盘符到指定服务器,并保留原路径规则,但是格式必须输入正确!");
            System.out.println("1、请输入你要拷贝的路径, 例如 F:\\example\\" );
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNext()) {
                String next = scanner.nextLine();
                System.out.println("2、请填写服务器的IP地址或域名,例如:127.0.0.1");
                String ip = scanner.nextLine();
                System.out.println("3、请输入端口号,不输入则使用默认端口:");
                String port = scanner.nextLine();
                if(port.equals("")){
                    port = "10015";
                }
                scanner.close();
                File file = new File(next);
                File[] files = file.listFiles();
                assert files != null;
                for(File f : files){
                    calcFilesCount(f);
                }
                System.out.println("总数： " + count + "个");;
                System.out.println("-------------------------------------------");
                for(File f : files){
                    getAllFile(f,ip,Integer.parseInt(port));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void getAllFile(File file,String ip,int port) throws IOException {
        if(file == null)
            return ;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files != null){
                for(File f : files) {
                    getAllFile(f,ip,port);
                }
            }
        } else {
            copyFile(file.getAbsoluteFile(),ip,port);
        }
    }

    private static void calcFilesCount(File file){
        if(file == null)
            return ;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files != null){
                for(File f : files) {
                    calcFilesCount(f);
                }
            }
        } else {
             count++;
        }
    }

    private static void copyFile(File file,String ip,int port) throws IOException {

        Socket socket = new Socket(ip,port);
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        int i;
        byte[] buf = new byte[8192];
        System.out.println("当前文件: " + file.getAbsolutePath());
        System.out.println("长度: " + file.length());
        outputStream.writeUTF(file.getAbsolutePath());
        outputStream.writeLong(file.length());
        outputStream.writeLong(count);
        long over = 0;
        while((i=inputStream.read(buf,0,buf.length)) != -1){
            outputStream.write(buf,0,i);
            outputStream.flush();
            over += i;
            System.out.print("\r进度: " + over);
        }
        outputStream.close();
        inputStream.close();
        socket.close();
        System.out.println();
        System.out.println("剩余:" +  --count + "个");
        System.out.println("-------------------------------------------");
    }
}
