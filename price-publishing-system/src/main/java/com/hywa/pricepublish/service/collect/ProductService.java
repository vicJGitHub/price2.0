package com.hywa.pricepublish.service.collect;

import com.hywa.pricepublish.representation.ProductMonitorReps;
import com.hywa.pricepublish.representation.ProductRep;
import com.hywa.pricepublish.representation.ProductReps;
import java.util.List;

public interface ProductService {

    ProductReps findByType(String id, String typeId, String bigTypeId, String productName,
            Integer pageNum, Integer pageSize);

    void save(ProductRep productRep, String userId);

    ProductReps findRecentUse(String userId);

    void update(ProductRep productRep, String userId);

    void delete(String productId);

    List<ProductMonitorReps> findMonitorProducts();

    List<ProductMonitorReps> findProducts();

    void deleteMonitor(String productId);

    void updateProductMonitor(List<String> productIds);
}
