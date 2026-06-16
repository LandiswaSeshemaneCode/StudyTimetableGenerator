public class Module {
    String moduleName;
    int moduleDifficulty;
    double moduleConfidence;
    LLChapterNode moduleChapterHead;
    LLChapterNode moduleChapterTail;

    public Module(String moduleName){
        this.moduleName = moduleName;
        //this.moduleDifficulty = getModuleDifficulty();
        //this.moduleConfidence = getModuleConfidence();
    }

    public String getModuleName(){
        return moduleName;
    }

    public int getModuleDifficulty(){
        LLChapterNode curhead = moduleChapterHead;

        int totalDifficulty = 0;
        int totalChapters = 0;

        while(curhead!= null){
            Chapter chapter = curhead.cargo;

            totalDifficulty += chapter.getChapterDifficulty();
            totalChapters++;
            curhead = curhead.next;
        }

        if(totalChapters == 0){
            return 0;
        }

        return (int) Math.ceil((double)totalDifficulty/totalChapters); //Takes the higher rounding off of difficulty 
    }

    public double getModuleConfidence(){
        LLChapterNode curhead = moduleChapterHead; //use it for linkedlist traversal

        double totalWeightedConfidence = 0;
        int totalWeightedDifficulty = 0;

        while(curhead!=null){
            Chapter chapter = curhead.cargo;

            totalWeightedDifficulty+= chapter.getChapterDifficulty();
            totalWeightedConfidence+= chapter.getChapterConfidence()* chapter.getChapterDifficulty();

            curhead = curhead.next; //traversing untill the end;
        }

        if(totalWeightedDifficulty==0){
            return 0;
        }

        return totalWeightedConfidence/totalWeightedDifficulty;

    }

    public void addChapter(Chapter chapter){
        LLChapterNode newNode = new LLChapterNode(chapter);

        if(moduleChapterHead == null){
            moduleChapterHead = newNode;
            moduleChapterTail = newNode;
        }
        else{
            newNode.prev = moduleChapterTail;
            moduleChapterTail.next = newNode;
            moduleChapterTail = newNode;

            //this makes it easier and faster to add a node at the end
        }
    }

    public double getModuleCompletionPercentage(){
        LLChapterNode curhead = moduleChapterHead;
        int completedChapters = 0;
        int totalChapters = 0;

        while(curhead!=null){
            Chapter chapter = curhead.cargo;

            if(chapter.isChapterComplete()){
                completedChapters++;
            }
            totalChapters++;
            curhead = curhead.next;
        }
        if(totalChapters == 0){
            return 0;
        }

        return (double)completedChapters/totalChapters;
    }

    public Chapter getChapterAt(int index){
        LLChapterNode current = moduleChapterHead;
        int count = 1; //Start at 1 as this is the number the user sees from the start of the list

        while(current != null){
            if(count == index){
                return current.cargo;
            }

            current = current.next;
            count++;
        }

        return null;
    }
        
    public void displayChapters(){
        LLChapterNode current = moduleChapterHead;
        int count = 1;
        while(current != null){
            System.out.println(count + ". " + current.cargo.getChapterName());

            current = current.next;
            count++;
        }
    }
}
