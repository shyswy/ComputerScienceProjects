import java.io.*;

public class VisitorTester
{
   public static void main(String[] args)
   {
      DirectoryNode node = new DirectoryNode(new File("..")); //해당 경로 가진 파일 디렉토리에 추가
      String keyword="소공";
      node.accept(new FindVisitor(keyword));  //해당 keyword를 가진 file, dir만 가진 객체
      // node.accept(new PrintVisitor());//해당 visitor을 accpet 시킨다.
   }
}
