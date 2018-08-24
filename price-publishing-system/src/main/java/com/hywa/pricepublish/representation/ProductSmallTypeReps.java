package com.hywa.pricepublish.representation;

import java.util.ArrayList;
import java.util.List;

public class ProductSmallTypeReps {

    private long total;
    private List<ProductSmallTypeRep> list;

    public ProductSmallTypeReps(long total, List<ProductSmallTypeRep> smallTypeReps) {
        this.setTotal(total);
        this.setList(smallTypeReps);
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<ProductSmallTypeRep> getList() {
        return list;
    }

    public void setList(List<ProductSmallTypeRep> list) {
        this.list = new ArrayList<>(list);
    }
}
