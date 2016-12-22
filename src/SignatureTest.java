import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.security.*;

/**
 * Created by pencil-box on 2016/12/18.
 */
public class SignatureTest {

    public static void main(String[] args){

        File publicFile = new File(EncryptTest.PUBLIC_KEY_FILE);
        File privateFile = new File(EncryptTest.PRIVATE_KEY_FILE);
        if(publicFile.exists()&&privateFile.exists()){

        }else {
            System.err.println("请准备好公私钥..");
            return;
        }



        PublicKey publicKey = EncryptTest.str2PublicKey(EncryptTest.readKey(EncryptTest.PUBLIC_KEY_FILE,EncryptTest.TYPE_PUBLIC_KEY));
        PrivateKey privateKey = EncryptTest.str2PrivateKey(EncryptTest.readKey(EncryptTest.PRIVATE_KEY_FILE,EncryptTest.TYPE_PRIVATE_KEY));

        String originData = "I am pencil, who are you?";
        String signEncodedData = sign(originData,privateKey);
        System.out.println("---Ready to sign----");
        System.out.println("SrcData:"+originData);
        System.out.println("SignData:"+signEncodedData);

        boolean isVerify = verify(originData,signEncodedData,publicKey);

        System.out.println("Verify is:"+isVerify);

    }

    public static final String KEY_SIGNATURE = "SHA1WithRSA";
    /**
     * 签名
     * @param data
     * @return 签名后的base64编码消息
     */
    public static String sign(String data,PrivateKey privateKey){
        try {
            Signature signature = Signature.getInstance(KEY_SIGNATURE);
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            byte[] signData = signature.sign();
            BASE64Encoder encoder = new BASE64Encoder();
            return  encoder.encode(signData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param data base64编码的签名信息
     * @param publicKey
     * @return
     */
    public static boolean verify(String data,String signEncodedData,PublicKey publicKey){
        try {
            Signature signature = Signature.getInstance(KEY_SIGNATURE);
            signature.initVerify(publicKey);
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] signData = decoder.decodeBuffer(signEncodedData);
            signature.update(data.getBytes());
            return signature.verify(signData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }
}
