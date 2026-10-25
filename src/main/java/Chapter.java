import java.io.Serializable;
public class Chapter implements Serializable{
    private long id;
    String chapterName;
    boolean chapterComplete;
    int chapterDifficulty;
    double chapterConfidence;

    public Chapter(String chapterName,int chapterDifficulty,double chapterConfidence){
        this.chapterName = chapterName;
        this.chapterDifficulty = chapterDifficulty;
        this.chapterConfidence = chapterConfidence;
        this.chapterComplete = false;
    }

    public String getChapterName(){
        return chapterName;
    }

    public long getID(){
        return id;
    }

    public void setID(long id){
        this.id = id;
    }

    public boolean isChapterComplete() {
        return chapterComplete;
    }

    public double getChapterConfidence() {
        return chapterConfidence;
    }

    public int getChapterDifficulty(){
        return chapterDifficulty;
    }

    public void setChapterComplete(){
        this.chapterComplete = true;
    }

    public double getPriorityScore(){
        return (chapterDifficulty*20) + (100 - chapterConfidence);
        
    }

}
