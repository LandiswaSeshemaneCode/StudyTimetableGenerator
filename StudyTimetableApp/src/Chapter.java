public class Chapter {
    String chapterName;
    boolean chapterComplete;

    public Chapter(String chapterName){
        this.chapterName = chapterName;
        this.chapterComplete = false;
    }

    public String getChapterName(){
        return chapterName;
    }

    public boolean isChapterComplete() {
        return chapterComplete;
    }

    public void setChapterComplete(){
        this.chapterComplete = true;
    }

}
