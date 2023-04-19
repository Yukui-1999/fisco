import org.tensorflow.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        try (Graph graph = new Graph()) {

            //导入图
            byte[] graphBytes = IOUtils.toByteArray(new
                    FileInputStream("/Users/dingzhengyao/fisco/tensorflow/src/main/resources/frozen_har.pb"));
            graph.importGraphDef(graphBytes);

            //根据图建立Session
            try (Session session = new Session(graph)) {

                float z = session.runner()
                        .feed("save/restore_all", Tensor.create(10.0f))
                        .fetch("y_").run().get(0).floatValue();
                System.out.println("z = " + z);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
