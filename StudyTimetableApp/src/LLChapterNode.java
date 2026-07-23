import java.io.Serializable;
public class LLChapterNode implements Serializable{
    public Chapter cargo;
    public LLChapterNode next;
    public LLChapterNode prev;

    public LLChapterNode(Chapter cargo){
        this.cargo = cargo;
        this.next = null;
        this.prev = null;
    }
}
