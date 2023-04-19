package org.fisco.bcos.asset.client;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;
import org.fisco.bcos.asset.contract.ZS01;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple4;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ZSClient {

  static Logger logger = LoggerFactory.getLogger(ZSClient.class);

  private BcosSDK bcosSDK;
  private Client client;
  private CryptoKeyPair cryptoKeyPair;

  public void initialize() throws Exception {
    @SuppressWarnings("resource")
    ApplicationContext context =
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    bcosSDK = context.getBean(BcosSDK.class);
    client = bcosSDK.getClient(1);
    cryptoKeyPair = client.getCryptoSuite().createKeyPair();
    client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
    logger.debug("create client for group1, account address is " + cryptoKeyPair.getAddress());
  }

  public void deployAssetAndRecordAddr() {

    try {
      ZS01 asset = ZS01.deploy(client, cryptoKeyPair);
      System.out.println(
          " deploy Asset success, contract address is " + asset.getContractAddress());

      recordAssetAddr(asset.getContractAddress());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
    }
  }

  public void recordAssetAddr(String address) throws FileNotFoundException, IOException {
    Properties prop = new Properties();
    prop.setProperty("address", address);
    final Resource contractResource = new ClassPathResource("contract.properties");
    FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
    prop.store(fileOutputStream, "contract address");
  }

  public String loadAssetAddr() throws Exception {
    // load Asset contact address from contract.properties
    Properties prop = new Properties();
    final Resource contractResource = new ClassPathResource("contract.properties");
    prop.load(contractResource.getInputStream());

    String contractAddress = prop.getProperty("address");
    if (contractAddress == null || contractAddress.trim().equals("")) {
      throw new Exception(" load Asset contract address failed, please deploy it first. ");
    }
    logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
    return contractAddress;
  }

  public void queryAssetAmount(String id_time) {
    try {
      String contractAddress = loadAssetAddr();
      ZS01 asset = ZS01.load(contractAddress, client, cryptoKeyPair);
      Tuple4<BigInteger,String,String,BigInteger> result = asset.select(id_time);
      if (result.getValue1().compareTo(new BigInteger("0")) == 0) {
        System.out.printf(" %s, total_hash %s ,part_hash %s result %s \n", id_time, result.getValue2(),result.getValue3(),result.getValue4());
      } else {
        System.out.printf(" %s is not exist ret %d\n", id_time,result.getValue1());
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

      System.out.printf(" query id_time failed, error message is %s\n", e.getMessage());
    }
  }

  public void registerAssetAccount(String id,String time, String filepath) {
//public void registerAssetAccount(String id, String time, String filepath, BigInteger result) {
    try {
      File f=new File(filepath);
      String id_time=id+"_"+time;
      System.out.println(id_time);
      String contractAddress = loadAssetAddr();
      String total_hash=Getsha256.toHex(Getsha256.hashV2(filepath));
      System.out.println("total_hash is "+total_hash);

      String partfilepath=Split.readAndSplitFile(f.getName(),f.getParentFile()+"/",3);
      System.out.println("partfilepath is "+partfilepath);
      String part_hash=Getsha256.toHex(Getsha256.hashV2(partfilepath));
      System.out.println("part_hash is "+part_hash);

      BigInteger result=BigInteger.valueOf(java_py.callpy(filepath));
      System.out.println("result is "+result);
      file entry=new file(id,time,total_hash,filepath,result.intValue());
      machine ma=new machine(id,result.toString());
      FileDao dao=new FileDao();
      MachineDao madao=new MachineDao();
      ZS01 table = ZS01.load(contractAddress, client, cryptoKeyPair);
      TransactionReceipt receipt = table.register(id_time, total_hash,part_hash,result);
      List<ZS01.RegisterdataEventEventResponse> response = table.getRegisterdataEventEvents(receipt);
      if (!response.isEmpty()) {
        if (response.get(0).ret.compareTo(new BigInteger("0")) == 0) {
          System.out.printf(
                  " register asset account success  \n");
          if(dao.regist(entry)){
            System.out.printf("save to mysql success  %s\n",entry.toString());
          }

          if(madao.regist(ma)){
            System.out.printf("add new machine success  %s\n",ma.toString());
          }

        } else {
          System.out.printf(
              " register asset account failed, ret code is %s \n", response.get(0).ret.toString());
        }
      } else {
        System.out.println(" event log not found, maybe transaction not exec. ");
        System.out.println(response);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();

      logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
      System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
    }
  }

//  public static void Usage() {
//    System.out.println(" Usage:");
//    System.out.println(
//        "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.AssetClient deploy");
//    System.out.println(
//        "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.AssetClient query account");
//    System.out.println(
//        "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.AssetClient register account value");
//    System.out.println(
//        "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.client.AssetClient transfer from_account to_account amount");
//    System.exit(0);
//  }

  static class Serverclient implements Runnable {
    ZSClient client = new ZSClient(); //创建区块链操作对象

    ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    @Override
    public void run() {
      try {
        client.initialize();
        client.deployAssetAndRecordAddr();//部署
      }catch (Exception exception){
      }

      System.out.println("master等待client连接...");
      // 创建ServerSocket
      ServerSocket serverSocket;
      {
        try {
          serverSocket = new ServerSocket(5001);
          while (true) {
            Socket socket = serverSocket.accept();
            System.out.println(
                    "Accepted a client: " + socket.getInetAddress().toString() + ":" + socket.getPort()
                            + " local port: " + socket.getLocalPort());

            // 就创建一个线程，与之通讯(单独写一个方法)

            newCachedThreadPool.execute(new Runnable(){
              public void run() { // 我们重写
                // 可以和客户端通讯
                try {
                  handler(socket);
                }catch (Exception exception){
                }
              }
            });
          }
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    }
    public  void handler(Socket clientSocket) throws Exception {


          try {
            String receiveTxtFileName = null;
            dataInputStream = new DataInputStream(
                    clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(
                    clientSocket.getOutputStream());
            // Here we call receiveFile define new for that
            // file
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH");
            LocalDateTime now = LocalDateTime.now();
            String timesplit="1";
            int minute=now.getMinute();
            if(minute>=12&&minute<24)
              timesplit="2";
            if(minute>=24&&minute<36)
              timesplit="3";
            if(minute>=36&&minute<48)
              timesplit="4";
            if(minute>=48&&minute<60)
              timesplit="5";

            String thisTime = dtf.format(now)+timesplit;

            String currentDirectory = System.getProperty("user.dir");
            // 新建一个指定名称的文件，将接受到的数据读取到这个目录下
            String id;
            id=dataInputStream.readUTF();
            receiveTxtFileName = currentDirectory + "/SensorData/"+id+"/" + thisTime + ".txt";
            File file = new File(receiveTxtFileName);
            if (!file.getParentFile().exists()) {
              file.getParentFile().mkdirs();
            }
            receiveFile(id,thisTime,receiveTxtFileName,file,clientSocket);
          }catch (IOException exception){
            exception.printStackTrace();
          }


    }
    public  void receiveFile(String id ,String time,String fileName,File file,Socket socket) throws Exception {
      try {
        file.createNewFile();
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        //接收客户端消息
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message;
        while ((message = br.readLine()) != null) {
          System.out.println("接收到客户端消息：" + message);
          //将消息写入文件(重要)
          bw.write(message + "\n");
          bw.flush();
        }
//        int bytes = 0;
//        FileOutputStream fileOutputStream
//                = new FileOutputStream(fileName);
//
//        long size = dataInputStream.readLong(); // read file size
//        byte[] buffer = new byte[4 * 1024];
//        while (size > 0
//                && (bytes = dataInputStream.read(
//                buffer, 0,
//                (int)Math.min(buffer.length, size)))
//                != -1) {
//          // Here we write the file using write method
//          fileOutputStream.write(buffer, 0, bytes);
//          size -= bytes; // read upto file size
//        }
        // Here we received file
        System.out.println("File is Received");
        String csvfile=java_py.callpy2(fileName);

        File file_csv = new File(csvfile);
        while (true){
          if(file_csv.exists())
            break;
        }
        SavedataAndPredict(id,time,csvfile);//save to block and predict

//        fileOutputStream.close();
        bw.close();
        fw.close();
        br.close();
      }catch (IOException exception){
        exception.printStackTrace();
      }
    }
    public  void SavedataAndPredict(String id,String time,String csvfile){
      client.registerAssetAccount(id, time, csvfile);
    }

  }
  public static void main(String[] args) throws Exception {


//
    Thread client_connect = new Thread(new Serverclient());// 与client进行通信的线程
    client_connect.start();
    while (true) {

    }

//    while (true) {
//
//      System.out.println("请输入：");
//      String[] para;
//      String input;
//      Scanner scanner = new Scanner(System.in);
//      input = scanner.nextLine();
//      para = input.split(" ");
//
//
//      if (para.length < 1) {
//        Usage();
//      }
//      if (para[0].equals("exit"))
//        break;
//      switch (para[0]) {
//        case "deploy":
//          client.deployAssetAndRecordAddr();
//          break;
//        case "query":
//          if (para.length < 2) {
//            Usage();
//          }
//          client.queryAssetAmount(para[1]);
//          break;
//        case "register":
//          System.out.println(para[0]+para[1]+para[2]+para[3]);
//          if (para.length < 4) {
//            Usage();
//          }
//
//          //测试文件路径：/Users/dingzhengyao/fisco/ZS01/src/main/java/org/fisco/bcos/asset/client/data/datatest.csv
//          client.registerAssetAccount(para[1], para[2], para[3]);
//          break;
//
//        default: {
//          Usage();
//        }
//      }
//    }



//    System.exit(0);
  }
}
