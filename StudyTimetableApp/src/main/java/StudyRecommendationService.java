import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
public class StudyRecommendationService{

    private ArrayList<Module> modules;

    public StudyRecommendationService(ArrayList<Module> modules){
        this.modules = modules;
    }

    public ArrayList<StudyRecommendation> generateGlobalRecommendations(){
        HashMap<String,StudyRecommendation> recommendationMap = new HashMap<>();
        for(int i=0;i<modules.size();i++){
            Module currentModule = modules.get(i);
            ArrayList<Assessment> moduleAssessment = currentModule.getAssessments();

            for(int k = 0;k<moduleAssessment.size();k++){

                Assessment assessed = moduleAssessment.get(k);
                ArrayList<Chapter> assessChapters = assessed.getAssessedChapters();
                for(int j=0;j<assessChapters.size();j++){
                Chapter chapter = assessChapters.get(j);
                String key = currentModule.getModuleName()+" : "+chapter.getChapterName();

                if(recommendationMap.containsKey(key)){
                    StudyRecommendation existing = recommendationMap.get(key);
                    existing.AddAssessment(assessed);
                }
                else{
                    StudyRecommendation newRecommendation = new StudyRecommendation(chapter);
                    newRecommendation.AddAssessment(assessed);
                    recommendationMap.put(key,newRecommendation);
                }
            }
            }   
        }
        ArrayList<StudyRecommendation>studyRecs = new ArrayList<>(recommendationMap.values());
        studyRecs.sort(Comparator.comparingDouble(StudyRecommendation::getFinalScore).reversed());
        return studyRecs;
    }
}