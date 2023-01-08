package prob1;

import java.io.IOException;
import java.io.Reader;

public class DecryptReader extends Reader{


    private final Reader reader;
    public DecryptReader(Reader reader) {
        //read한다.
        this.reader =reader;
    }


    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int result=reader.read(cbuf, off, len);
        for(int i=off; i<off+len; i++) {
            if(cbuf[i] == ' ') continue;
            char ch = cbuf[i];
            if(ch >= 65 && ch <= 90) {   // A~ Z  65 > 88 (62)    66 89 67 90
                ch += 3;
                if(ch>90) ch-=26;


            }else if(ch >= 97 && ch <= 122) {  // a ~ z
                ch += 3;
                if(ch>122) ch-=26;
            }
            cbuf[i] = ch;
        }



        return result;


    }

    @Override
    public void close() throws IOException {
        reader.close();

    }







}
