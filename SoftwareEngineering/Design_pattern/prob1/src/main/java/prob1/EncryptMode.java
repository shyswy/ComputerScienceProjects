package prob1;


public interface EncryptMode  {

    char[] Encode(char[] str,int off, int len);

    char[] Decode(char[] str,int off, int len);

}
