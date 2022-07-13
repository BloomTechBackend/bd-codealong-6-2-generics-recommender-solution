package com.amazon.ata.generics.recommender.recommender;

import com.amazon.ata.generics.recommender.MostRecentlyUsed;
import com.amazon.ata.generics.recommender.ReadOnlyDao;
import com.amazon.ata.generics.recommender.movie.PrimeVideo;

import java.util.Random;

public class GenericRecommender<T> {
    private MostRecentlyUsed<T> mostRecentlyUsed;
    private ReadOnlyDao<Long, T> readOnlyDao;
    private Random random;

    public GenericRecommender(MostRecentlyUsed<T> mostRecentlyUsed, ReadOnlyDao<Long, T> readOnlyDao, Random random) {
        this.mostRecentlyUsed = mostRecentlyUsed;
        this.readOnlyDao = readOnlyDao;
        this.random = random;
    }


    public void use(long id) {
        T item = readOnlyDao.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Item not found for id " + id);
        }
        mostRecentlyUsed.add(item);
    }

    public T getRecommendation() {
        if (mostRecentlyUsed.getSize() == 0) {
            return null;
        }
        int randomIndex = this.random.nextInt(mostRecentlyUsed.getSize());
        Long recommendedId = null;
        for (int i = randomIndex; i < mostRecentlyUsed.getSize(); i++) { // start at the randomIndex but keep trying
            // This will throw an exception if the generic type doesn't implement Item
            Item randomItem = (Item) mostRecentlyUsed.get(randomIndex);
            recommendedId = randomItem.getMostSimilarId();
            if (recommendedId == null)
                break;
            T recommendedItem = readOnlyDao.get(recommendedId);
            return recommendedItem;
        }
        for (int i = 0; i < randomIndex; i++) { // now start at the beginning
            // This will throw an exception if the generic type doesn't implement Item
            Item randomItem = (Item) mostRecentlyUsed.get(randomIndex);
            recommendedId = randomItem.getMostSimilarId();
            if (recommendedId == null)
                break;
            T recommendedItem = readOnlyDao.get(recommendedId);
            return recommendedItem;
        }
        return null; // we checked all the items and didn't find anything to recommend
    }
}