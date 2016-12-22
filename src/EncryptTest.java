import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

/**
 * Created by pencil-box on 2016/12/22.
 */
public class EncryptTest {

    public static void main(String[] args){

        generateKey();

        String data=  FileUtils.readSrcFile(NAME_SOURCE_FILE);
        System.out.println("---Ready to encrypt----");

        Key publicKey = str2PublicKey(readKey(PUBLIC_KEY_FILE,TYPE_PUBLIC_KEY));

        String encryptData = encrypt(data,publicKey);
        System.out.println("SrcData:"+data);

        FileUtils.writeFile(NAME_ENCRYPTED_FILE,encryptData);

        System.out.println("---------------");
        System.out.println("Encrypted Data:"+encryptData);
        System.out.println("---------------");

        String encryptData1 = FileUtils.readSrcFile(NAME_ENCRYPTED_FILE);
        Key privateKey = str2PrivateKey(readKey(PRIVATE_KEY_FILE,TYPE_PRIVATE_KEY));

        String decryptData = decrypt(encryptData1,privateKey);
        System.out.println("Decrypted Data:"+decryptData);
        FileUtils.writeFile(NAME_DECRYPTED_FILE,decryptData);

    }




    public static final String NAME_SOURCE_FILE ="srcFile.txt";
    public static final String NAME_ENCRYPTED_FILE="encryptFile.txt";
    public static final String NAME_DECRYPTED_FILE = "decryptFile.txt";





    public static final int NUM_OF_BITS = 1024;
    public static final String KEY_ALGORITHM = "RSA";

    public static final String PRIVATE_KEY_FILE = "pencilPrivateKey.pem";
    public static final String PUBLIC_KEY_FILE = "pencilPublicKey.pem";

    /**
     * 格式为X.509的公钥
     */
    public static final String PUB_HEADER= "-----BEGIN PUBLIC KEY-----";
    public static final String PUB_TAIL = "-----END PUBLIC KEY------";

    /**
     * 格式为PKCS#8的私钥
     */
    public static final String PRI_HEADER="-----BEGIN PRIVATE KEY-----";
    public static final String PRI_TAIL="-----END PRIVATE KEY-----";



    /**
     * 生成RSA公钥与私钥
     */
    public static void generateKey(){

        FileOutputStream publicOutFile = null;
        FileOutputStream privateOutFile = null;

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            generator.initialize(NUM_OF_BITS);
            KeyPair keyPair = generator.generateKeyPair();

            Key publicKey = keyPair.getPublic();
            Key privateKey = keyPair.getPrivate();

            publicOutFile = new FileOutputStream(new File(PUBLIC_KEY_FILE));
            privateOutFile = new FileOutputStream(new File(PRIVATE_KEY_FILE));
            //构建头部和尾部信息
            String publicKeyStr = PUB_HEADER+"\n"+key2Base64(publicKey)+"\n"+PUB_TAIL;
            String privateKeyStr =  PRI_HEADER+"\n"+key2Base64(privateKey)+"\n"+PRI_TAIL;

            publicOutFile.write(publicKeyStr.getBytes());
            privateOutFile.write(privateKeyStr.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(privateOutFile!=null){
                try {
                    privateOutFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(publicOutFile!=null){
                try {
                    publicOutFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 加密函数
     * @param data  源数据,无编码
     * @return 加密后经过Base64编码的文本
     */
    public static String encrypt(String data,Key publicKey){
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            byte[] dataByte = data.getBytes();
            byte[] outData = cipher.doFinal(dataByte);
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(outData);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解密
     * @param encryptedData 加密后的base64编码数据
     * @param privateKey
     * @return 源文件字符串
     */
    public static String decrypt(String encryptedData,Key privateKey){
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE,privateKey);
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] data = decoder.decodeBuffer(encryptedData);
            byte[] decodedData  = cipher.doFinal(data);
            return new String(decodedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 对key进行Base64编码
     * @param key
     * @return
     */
    public static String key2Base64(Key key){
        byte[] keyBytes = key.getEncoded();
        return new BASE64Encoder().encode(keyBytes);
    }

    public static final byte TYPE_PUBLIC_KEY=1;
    public static final byte TYPE_PRIVATE_KEY=2;
    public static String readKey(String fileName,byte type){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line = "";
            StringBuffer buffer = new StringBuffer();
            while ((line=reader.readLine())!=null){
                //过滤首尾部信息
                if(TYPE_PUBLIC_KEY==type){
                    if (line.contains(PUB_HEADER) || line.contains(PUB_TAIL)) {
                        continue;
                    }}else if(TYPE_PRIVATE_KEY==type){
                    if (line.contains(PRI_HEADER) || line.contains(PRI_TAIL)) {
                        continue;
                    }}
                buffer.append(line);
            }

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
     * str转换成publickey
     * @param src
     * @return
     */
    public static PublicKey str2PublicKey(String src){
        try {
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(src);
            //注意为x.509格式
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * string转化为privateKey对象
     * @param src
     * @return
     */
    public static PrivateKey str2PrivateKey(String src){
        try {
            byte[] keyBytes = new BASE64Decoder().decodeBuffer(src);
            //注意为pkcs#8格式
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return  null;
    }


}
