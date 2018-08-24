package com.hywa.pricepublish.service.dict;

import com.hywa.pricepublish.representation.DictRep;
import com.hywa.pricepublish.representation.DictReps;
import com.hywa.pricepublish.representation.ProductSmallTypeReps;

public interface DictService {

    DictReps findDictListByDictType(String dictType, int pageNum, int pageSize,
            String name);

    DictReps findDictListByDictTypeId(String dictTypeId, String name, int pageNum, int pageSize);

    void add(String userId, DictRep dictRep);

    void update(String userId, DictRep dictRep);

    void delete(String dictId);

    ProductSmallTypeReps findProductSmallType(String id,String name, String bigTypeId, int pageNum, int pageSize);
}
