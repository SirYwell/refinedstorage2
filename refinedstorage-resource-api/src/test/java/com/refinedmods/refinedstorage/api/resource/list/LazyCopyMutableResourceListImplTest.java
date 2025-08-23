package com.refinedmods.refinedstorage.api.resource.list;

class LazyCopyMutableResourceListImplTest extends AbstractMutableResourceListTest {
    @Override
    protected MutableResourceList createList() {
        return LazyCopyMutableResourceListImpl.create(MutableResourceListImpl.create());
    }
}
