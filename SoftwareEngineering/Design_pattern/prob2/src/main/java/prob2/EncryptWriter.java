package prob2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class EncryptWriter extends Writer {


    EncryptMode encryptMode;

    private final Writer writer;


    public EncryptWriter(FileWriter writer) {

        this.writer =writer;
    }
   public void setMode(EncryptMode encryptMode){
       this.encryptMode=encryptMode;
    }
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        try {
            char[] c=encryptMode.Encode(cbuf,off,len);

            writer.write(c, off, len);
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

