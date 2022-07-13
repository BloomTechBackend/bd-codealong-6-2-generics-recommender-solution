package com.amazon.ata.generics.recommender.product;

import com.amazon.ata.generics.recommender.MostRecentlyUsed;
import com.amazon.ata.generics.recommender.ReadOnlyDao;

import java.util.Random;

public class ProductRecommender {
    private MostRecentlyUsed<Product> mostRecentlyUsed;
    private ReadOnlyDao<Long, Product> readOnlyDao;
    private Random random;

    public ProductRecommender(MostRecentlyUsed<Product> mostRecentlyUsed, ReadOnlyDao<Long, Product> readOnlyDao, Random random) {
        this.mostRecentlyUsed = mostRecentlyUsed;
        this.readOnlyDao = readOnlyDao;
        this.random = random;
    }


    public void buy(long id) {
        Product item = readOnlyDao.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Product not found for id " + id);
        }
        mostRecentlyUsed.add(item);
    }

    public Product getRecommendation() {
        if (mostRecentlyUsed.getSize() == 0) {
            return null;
        }
        int randomIndex = this.random.nextInt(mostRecentlyUsed.getSize());
        Long recommendedId = null;
        for (int i = randomIndex; i < mostRecentlyUsed.getSize(); i++) { // start at the randomIndex but keep trying
            Product randomItem = mostRecentlyUsed.get(randomIndex);
            recommendedId = randomItem.getMostSimilarId();
            if (recommendedId == null)
                break;
            Product recommendedProduct = readOnlyDao.get(recommendedId);
            return recommendedProduct;
        }
        for (int i = 0; i < randomIndex; i++) { // now start at the beginning
            Product randomItem = mostRecentlyUsed.get(randomIndex);
            recommendedId = randomItem.getMostSimilarId();
            if (recommendedId == null)
                break;
            Product recommendedProduct = readOnlyDao.get(recommendedId);
            return recommendedProduct;
        }
        return null; // we checked all the products and didn't find anything to recommend
    }

}
