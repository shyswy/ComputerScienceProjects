package prob1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class EncryptWriter extends Writer {



    private final Writer writer;


    public EncryptWriter(FileWriter writer) {

        this.writer =writer;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        try {
            for(int i=off; i<off+len; i++) {
                if(cbuf[i] == ' ') continue;
                char ch = cbuf[i];
                if(ch >= 65 && ch <= 90) {   // A~ Z  65 > 88 (62)    66 89 67 90
                    ch -= 3;
                    if(ch<65) ch+=26;


                }else if(ch >= 97 && ch <= 122) {  // a ~ z
                    ch -= 3;
                    if(ch<97) ch+=26;
                }
                cbuf[i] = ch;
            }

            writer.write(cbuf, off, len);
        }
        catch (IOException e) {
            System.out.println("IEEEEEEEEEE!");
        }
    }




    @Override
    public void flush() throws IOException {
        writer.flush();

    }

    @Override
    public void close() throws IOException {
        writer.close();

    }



}
    /*

    Writer writer;

    public EncryptWriter (Writer writer) {
        this.writer=writer;
    }


    public String Encode(EncryptMode encryptMode,char[] str) {
        return encryptMode.Encode(str);

        // return super.Encode(CaesarEncode(str));
    }
    public String Decode(EncryptMode encryptMode,char[] str) {

        return encryptMode.Decode(str);
        // return super.Decode(CaesarDecode(str));
    }


    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }

     */

