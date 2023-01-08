package prob2;

public class Caesar implements EncryptMode{



    @Override
    public char[] Encode(char[] str,int off, int len){

        for(int i=off; i<off+len; i++) {
            if(str[i] == ' ') continue;
            char ch = str[i];
            if(ch >= 65 && ch <= 90) {   // A~ Z  65 > 88 (62)    66 89 67 90
                    ch -= 3;
                    if(ch<65) ch+=26;


            }else if(ch >= 97 && ch <= 122) {  // a ~ z
                ch -= 3;
                if(ch<97) ch+=26;
            }
            str[i] = ch;
        }
        return str;
    }

    @Override
    public char[] Decode(char[] str,int off, int len){

        for(int i=off; i<off+len; i++) {
            if(str[i] == ' ') continue;
            char ch = str[i];
            if(ch >= 65 && ch <= 90) {   // A~ Z  65 > 88 (62)    66 89 67 90
                ch += 3;
                if(ch>90) ch-=26;


            }else if(ch >= 97 && ch <= 122) {  // a ~ z
                ch += 3;
                if(ch>122) ch-=26;
            }
            str[i] = ch;
        }
        return str;

    }
}
