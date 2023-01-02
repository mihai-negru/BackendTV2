package datafetch;

public final class FilterActionFetch {
    private SortFilterFetch sort;
    private ContainsFilterFetch contains;

    public FilterActionFetch() {

    }

    public SortFilterFetch getSort() {
        return sort;
    }

    public void setSort(final SortFilterFetch newSort) {
        sort = newSort;
    }

    public ContainsFilterFetch getContains() {
        return contains;
    }

    public void setContains(final ContainsFilterFetch newContains) {
        contains = newContains;
    }
}
