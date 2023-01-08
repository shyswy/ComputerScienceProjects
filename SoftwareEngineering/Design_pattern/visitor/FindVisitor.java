public class FindVisitor implements FileSystemVisitor
{
    public FindVisitor(String keyword)
    {
        this.keyword = keyword;
    }
    public void visitFileNode(FileNode node)
    {
        String str=node.getFile().getName();
        if(str.contains(keyword)) {
            for (int i = 0; i < level; i++) System.out.print(" ");

            System.out.println(node.getFile().getName());
        }
    }

    public void visitDirectoryNode(DirectoryNode node)
    {
        String str=node.getDirectory().getName();
        if(str.contains(keyword)) {
            for (int i = 0; i < level; i++) System.out.print(" ");

            System.out.println(node.getDirectory().getName());
        }
        level++;
        for (FileSystemNode c : node.getChildren())
            c.accept(this);
        level--;
    }

    private int level = 0;
    private String keyword;
}
