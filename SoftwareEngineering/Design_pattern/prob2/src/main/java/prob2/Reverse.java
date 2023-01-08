package prob2;


public class Reverse implements EncryptMode{



   @Override
   public char[] Encode(char[] str,int off, int len){

        for(int i=off; i<off+len; i++) {
            if(str[i] == ' ') continue;
            char ch = str[i];

            if(ch >= 65 && ch <= 90) {   // A~ Z
                ch += 32;
            }else if(ch >= 97 && ch <= 122) {  // a ~ z
                ch -= 32;
            }
            str[i] = ch;
        }
        return str;
    }
    @Override
    public char[] Decode(char[] str,int off, int len){
        char[] tmp;
        tmp= new char[str.length];
        for(int i=off; i<off+len; i++) {
            if(str[i] == ' ') continue;
            char ch = str[i];
            if(ch >= 65 && ch <= 90) {   // A~ Z
                ch += 32;
            }else if(ch >= 97 && ch <= 122) {  // a ~ z
                ch -= 32;
            }
            str[i] = ch;
        }
        return str;
        // return String.valueOf(tmp);
    }





}
