package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private final double naN = Double.NaN;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private PriceRetriever priceRetriever = new PriceRetriever();
    private Collection<Long> shopIds = Set.of(10L, 45L, 66L, 345L, 234L, 333L, 67L, 123L, 768L);


    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        var completableFutures = shopIds.stream()
                .map(shopId -> CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                        .completeOnTimeout(naN, 2900, TimeUnit.MILLISECONDS)
                        .handle((price, ex) -> ex == null ? price : naN))
                .toList();
        return completableFutures.stream()
                .mapToDouble(CompletableFuture::join)
                .filter(d -> !Double.isNaN(d))
                .min().orElse(naN);
    }
}
