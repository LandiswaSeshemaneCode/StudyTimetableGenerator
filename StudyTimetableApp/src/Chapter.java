public class Chapter {
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

}
