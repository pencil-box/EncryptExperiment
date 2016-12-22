
import java.io.*;

/**
 * Created by pencil-box on 2016/12/18.
 */
public class FileUtils {


    /**
     * 读取返回文件的内容
     * @param fileName
     * @return
     */
    public static String readSrcFile(String fileName){

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

            String line = "";
            StringBuffer buffer = new StringBuffer();
            boolean isDelete = false;
            while ((line=reader.readLine())!=null){
                buffer.append(line).append("\n");
                isDelete = true;
            }
            if(isDelete)
            buffer.deleteCharAt(buffer.length()-1);

            return buffer.toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    /**
     * 输出文件咯
     * @param fileName
     * @param data
     */
    public static void writeFile(String fileName,String data){
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(new File(fileName));
            outputStream.write(data.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }




}
