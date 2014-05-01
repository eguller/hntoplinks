package jobs;

import models.StatKey;
import models.Statistic;
import models.StatisticsMgr;
import play.jobs.Every;
import play.jobs.Job;

import java.util.List;
import java.util.Map;

/**
 * User: eguller
 * Date: 5/1/14
 * Time: 7:12 PM
 */
@Every("1mn")
public class StatsUpdateJob extends Job{
    @Override
    public void doJob(){
        if(StatisticsMgr.instance().isLoaded()) {
            Map<StatKey, Statistic> statisticsMap = Statistic.getAllStatMap();
            List<Statistic> statisticList = StatisticsMgr.instance().getSnapshot();
            for(Statistic statistic : statisticList){
                Statistic statisticFromDb = statisticsMap.get(statistic.getKey());
                if(statisticFromDb == null){
                    statisticFromDb = new Statistic(statistic.getKey(), statistic.getValue());
                }
                statisticFromDb.save();
            }
        }
    }
}
