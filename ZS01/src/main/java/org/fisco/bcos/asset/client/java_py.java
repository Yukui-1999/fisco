package org.fisco.bcos.asset.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class java_py {
    public static String callpy2(String txtfile){
        String res="";
        String venv="/Users/dingzhengyao/venv/bin/python3";
        String pythonfile="/Users/dingzhengyao/fisco/ZS01/src/main/java/org/fisco/bcos/asset/client/txt_csv.py";
        try {
            String[] args1 = new String[] {venv ,pythonfile ,txtfile };
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;

            while ((line = in.readLine()) != null) {
                System.out.println("这里是Java——py里："+line);
                res=line;
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return res;

    }
    public static int callpy(String csvfile){
        String res="";
        String venv="/Users/dingzhengyao/venv/bin/python3";
        String pythonfile="/Users/dingzhengyao/fisco/ZS01/src/main/java/org/fisco/bcos/asset/client/newtest.py";

        try {
            String[] args1 = new String[] {venv ,pythonfile ,csvfile };
            Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;

            while ((line = in.readLine()) != null) {
                System.out.println(line);
                res=line;
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(res);
    }
    public static void main(String[] args) {
        String res="";
        String venv="/Users/dingzhengyao/venv/bin/python3";
        String pythonfile="/Users/dingzhengyao/fisco/ZS01/src/main/java/org/fisco/bcos/asset/client/transfer_txt_file_server.py";
        HashMap<String,String> map=new HashMap<>();

            try {
                String[] args1 = new String[] {venv ,pythonfile };
                Process proc = Runtime.getRuntime().exec(args1);// 执行py文件
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line = null;

                map.put("name","0");
                map.put("size","0");
                map.put("id","0");
                String[] arr={"name","size","id"};

                int i=0;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    map.put(arr[i],line);
                    i++;
                }
                in.close();
                proc.waitFor();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        System.out.println(map);
    }
}
