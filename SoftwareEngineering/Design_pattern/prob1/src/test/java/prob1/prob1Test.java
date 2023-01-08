package prob1;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class prob1Test {
    final static int MAX = 1000;


    @Test
    public void encrypt_test() { //간단한 reverse 의 encrpt 테스트
        //Writer wr = null;
        File file = new File("EnCode.txt");
        FileReader reader=null;
        char[] act=new char[5];
        // EncryptWriter encryptWriter = null;
        String test_str="abcde";
        try {
            EncryptWriter encryptWriter = new EncryptWriter(new FileWriter(file));

            encryptWriter.write(test_str);
            encryptWriter.flush();
            encryptWriter.close();
            reader=new FileReader(file);
            reader.read(act);
            assertEquals('x',act[0]);
            assertEquals('y',act[1]);
            assertEquals('z',act[2]);
            assertEquals('a',act[3]);
            assertEquals('b',act[4]);
        } catch (IOException e) {
            System.out.println("IOException occured!");
        }
    }
    @Test
    public void Decrypt_test() { //간단한 reverse의 decrpy 테스트
        File file = new File("Encode.txt");
        File file1 = new File("Decode.txt");
        DecryptReader decryptReader = null;
        char[] buf = new char[1000];
        try {
            decryptReader = new DecryptReader(new FileReader(file));

            decryptReader.read(buf);
            decryptReader.close();

            FileWriter fileWriter=new FileWriter(file1); //read한 파일을 Decode.txt로 생성.
            fileWriter.write(buf);
            fileWriter.close();
            assertEquals('a',buf[0]);
            assertEquals('b',buf[1]);
            assertEquals('c',buf[2]);
            assertEquals('d',buf[3]);
            assertEquals('e',buf[4]);
        } catch (IOException e) {
            System.out.println("IOException occured!");
        }
    }
    @Test
    public void all_test() { //통합 테스트. 랜덤 문자열에 대해 encrpy-write 하고 다시 Decrypt-read했을 때 원래 값을 되찾는가?

        //Writer wr = null;
        File file = new File("EnCode.txt");
        File file1 = new File("Decode.txt");
        DecryptReader decryptReader = null;
        char[] buf = new char[MAX];
        char[] arr=new char[MAX];
        char[] act=new char[MAX];
        char tmp;
        // EncryptWriter encryptWriter = null;
        try {
            for(int i=0;i<MAX;i++) {
                for(int j=0;j<i;j++) {
                    if (j % 2 == 0) {
                        tmp = (char) ((Math.random() * 26) + 97);
                    } else {
                        tmp = (char) ((Math.random() * 26) + 65);
                    }
                    arr[j]=tmp;
                    act[j]=tmp;

                }


                EncryptWriter encryptWriter = new EncryptWriter(new FileWriter(file));

                encryptWriter.write(arr);
                encryptWriter.flush();
                encryptWriter.close();
                decryptReader = new DecryptReader(new FileReader(file));
                decryptReader.read(buf);
                decryptReader.close();
                FileWriter fileWriter = new FileWriter(file1); //read한 파일을 Decode.txt로 생성.
                fileWriter.write(buf);
                fileWriter.close();
                for(int j=0;j<i;j++){
                    assertEquals(act[j], buf[j]); //암호화한 뒤 해독한 문자열이 원래의 문자열로 다시 돌아오는가?
                }
            }
        } catch (IOException e) {
            System.out.println("IOException occured!");
        }
    }


}