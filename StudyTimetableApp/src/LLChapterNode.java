public class LLChapterNode {
    public Chapter cargo;
    public LLChapterNode next;
    public LLChapterNode prev;

    public LLChapterNode(Chapter cargo){
        this.cargo = cargo;
        this.next = null;
        this.prev = null;
    }
}
