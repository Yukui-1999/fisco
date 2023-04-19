package org.fisco.bcos.asset.client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Split {
    public static void main(String[] args) throws IOException {
        readAndSplitFile("datatest.csv", "/Users/dingzhengyao/fisco/ZS01/src/main/java/org/fisco/bcos/asset/client/data/", 3);
    }

    public static String readAndSplitFile(String OriginalFilename, String Filepath, int block) throws IOException {
        String filename=OriginalFilename.split("\\.")[0];
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(Filepath + OriginalFilename), "UTF-8")
        );
        String line;
        boolean isTitle = true;
        String titles = "";
        List<String> li = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            //读取获取CSV文件行、列数据
            if (isTitle) {
                titles = line;
                isTitle = false;
            } else {
                li.add(line);
            }
        }
        int len = li.size() / block;
        String newfilepath=Filepath + filename + "_part.csv";
        File csvfile = new File(newfilepath);
        if(csvfile.exists()){
            csvfile.delete();
            csvfile.createNewFile();
        }


        BufferedWriter csvWriter;

        csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                csvfile), "UTF-8"));
        csvWriter.write(titles);
        csvWriter.newLine();
        for (int i = 0; i <= len; i++) {
            csvWriter.write(li.get(i));
            csvWriter.newLine();
        }
        csvWriter.flush();
        System.out.println(titles);
        return newfilepath;
    }
}
