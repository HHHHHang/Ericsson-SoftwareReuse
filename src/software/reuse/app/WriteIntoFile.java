package software.reuse.app;

import java.io.*;

/**
 * Created by wangdechang on 2016/3/25.
 */
public class WriteIntoFile {
    private static final String pre = "D:\\";
    private static WriteIntoFile writeIntoFile;
    private WriteIntoFile(){

    }
    public static WriteIntoFile getWriteIntoFile(){
        if (writeIntoFile == null){
            writeIntoFile = new WriteIntoFile();
        }
        return writeIntoFile;
    }
    public void writeFile(String fileName,String succ,int succNum,String fail,int failNum){
        FileWriter write = null;
        try {
            write= new FileWriter(pre+fileName+".txt",true);
            write.write(succ+" = "+succNum+"\r\n");
            write.write(fail+" = "+failNum+"\r\n");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
